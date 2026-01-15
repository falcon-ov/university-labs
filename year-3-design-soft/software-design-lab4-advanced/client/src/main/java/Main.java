
public class Main {
    public static void main(String[] args) {
        Context ctx = new Context();
        ctx.set(UserKeys.USERNAME, "Alice");
        ctx.set(UserKeys.AGE, 25);

        Operation op = context -> {
            String name = context.get(UserKeys.USERNAME);
            Integer age = context.get(UserKeys.AGE);
            System.out.println(name + " is " + age);
        };

        new OperationExecutor().run(ctx, op);

    }
}
