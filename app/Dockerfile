# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /build

# Copy Maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copy source code
COPY src ./src

# Build application (skip tests for production build)
RUN ./mvnw clean package -DskipTests -B --no-transfer-progress && \
    mv target/*.jar app.jar

# Runtime stage
FROM eclipse-temurin:25-jre-alpine AS runtime

# Add non-root user for security
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

# Copy jar from build stage
COPY --from=build --chown=appuser:appgroup /build/app.jar ./app.jar

# Application port
ARG APPLICATION_PORT=8080
ENV APPLICATION_PORT=${APPLICATION_PORT}

EXPOSE ${APPLICATION_PORT}

# Optimized JVM settings for Java 25 with container support
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -XX:+UseCompressedOops \
               -XX:+UseCompressedClassPointers \
               -XX:+AlwaysPreTouch \
               -XX:+DisableExplicitGC \
               -Djava.security.egd=file:/dev/./urandom \
               -Dfile.encoding=UTF-8 \
               -Duser.timezone=UTC"

# Switch to non-root user
USER appuser

# Use exec form for better signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
