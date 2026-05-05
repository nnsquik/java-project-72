package hexlet.code;

import io.javalin.Javalin;

public class App {

    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });

        return app;
    }

    public static void main(String[] args) {
        var port = System.getenv().getOrDefault("PORT", "7070");
        getApp().start(Integer.parseInt(port));
    }
}
