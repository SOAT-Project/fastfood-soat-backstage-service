package soat.fastfood.backstage.application.usecase;

public abstract class UseCase<IN, OUT> {
    public abstract OUT execute(IN anIn);
}