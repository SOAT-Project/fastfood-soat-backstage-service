resource "kubernetes_manifest" "backstage_namespace" {
  manifest = yamldecode(file("${path.module}/../kubernetes/namespace.yaml"))
}

resource "kubernetes_manifest" "backstage_service_account" {
  manifest = yamldecode(file("${path.module}/../kubernetes/service-account.yaml"))
}

resource "kubernetes_manifest" "backstage_service" {
  manifest = yamldecode(file("${path.module}/../kubernetes/service.yaml"))
}

resource "kubernetes_manifest" "backstage_deployment" {
  manifest = yamldecode(file("${path.module}/../kubernetes/deployment.yaml"))
}

resource "kubernetes_manifest" "backstage_http_route" {
  manifest = yamldecode(file("${path.module}/../kubernetes/http-route.yaml"))
}

resource "kubernetes_manifest" "backstage_configmap" {
  manifest = yamldecode(file("${path.module}/../kubernetes/configmap.yaml"))
}

resource "kubernetes_manifest" "backstage_hpa" {
  manifest = yamldecode(file("${path.module}/../kubernetes/hpa.yaml"))
}

# Secret com credenciais AWS (criado após o namespace)
resource "kubernetes_secret" "backstage_secret" {
  metadata {
    name      = "backstage-secret"
    namespace = "backstage"
  }

  data = {
    AWS_REGION            = var.aws_region
    AWS_ACCESS_KEY_ID     = var.aws_access_key
    AWS_SECRET_ACCESS_KEY = var.aws_secret_key
  }

  type = "Opaque"

  # Garante que o namespace existe antes de criar o secret
  depends_on = [kubernetes_manifest.backstage_namespace]
}
