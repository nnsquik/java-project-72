package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrkCheckController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class App {

    public static Javalin getApp() throws SQLException, IOException {
        var jdbcUrl = System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        var sql = readResourceFile("schema.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", ctx -> {
            var flash = ctx.consumeSessionAttribute("flash");
            var flashType = ctx.consumeSessionAttribute("flash-type");
            ctx.render("index.jte", Map.of(
                    "flash", flash == null ? "" : flash,
                    "flashType", flashType == null ? "" : flashType
            ));
        });

        app.get("/urls", UrlController::index);
        app.get("/urls/{id}", UrlController::show);

        app.post("/urls", UrlController::create);
        app.post("/urls/{id}/checks", UrkCheckController::create);

        return app;
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        getApp().start(getPort());
    }

    private static int getPort() {
        var port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
