package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;


public class UrlController {
    private static final int UNPROCESSABLE_ENTITY = 422;

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flash-type");
        ctx.render("urls/index.jte", Map.of(
                "urls", urls,
                "flash", flash == null ? "" : flash,
                "flashType", flashType == null ? "" : flashType
        ));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse());
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flash-type");
        ctx.render("urls/show.jte", Map.of(
                "url", url,
                "flash", flash == null ? "" : flash,
                "flashType", flashType == null ? "" : flashType
        ));
    }

    public static void create(Context ctx) throws SQLException {
        var input = ctx.formParam("url");
        System.out.println("=== INPUT URL: " + input + " ===");
        if (input == null || input.isBlank()) {
            ctx.status(UNPROCESSABLE_ENTITY);
            ctx.render("index.jte", Map.of(
                    "flash", "Некорректный URL",
                    "flash-type", "danger"
            ));
            return;
        }

        String normalizedUrl;
        try {
            var uri = new URI(input).toURL();
            var port = uri.getPort();
            normalizedUrl = uri.getProtocol() + "://" + uri.getHost()
                    + (port != -1 ? ":" + port : "");

        } catch (Exception e) {
            ctx.status(UNPROCESSABLE_ENTITY);
            ctx.render("index.jte", Map.of(
                    "flash", "Некорректный URL",
                    "flashType", "danger"
            ));
            return;
        }

        var existingUrl = UrlRepository.findByName(normalizedUrl);
        if (existingUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect("/urls/" + existingUrl.get().getId());
            return;
        }

        var url = new Url(normalizedUrl);
        UrlRepository.save(url);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + url.getId());
    }
}
