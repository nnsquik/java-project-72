package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.sql.SQLException;

@Slf4j
public class UrkCheckController {
    public static void create(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse());

        try {
            var response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();
            log.info("Status code for {}: {}", url.getName(), statusCode);

            if (statusCode >= HttpStatus.BAD_REQUEST.getCode()) {
                ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/urls/" + id);
                return;
            }

            var body = response.getBody();
            var document = Jsoup.parse(body);

            var title = document.title();
            var h1 = document.selectFirst("h1") != null
                    ? document.selectFirst("h1").text() : "";
            var metaDesc = document.selectFirst("meta[name=description]");
            var description = metaDesc != null
                    ? metaDesc.attr("content") : "";

            title = title.length() > 200 ? title.substring(0, 200) + "..." : title;
            h1 = h1.length() > 200 ? h1.substring(0, 200) + "..." : h1;
            description = description.length() > 200 ? description.substring(0, 200) + "..." : description;

            var urlCheck = new UrlCheck(statusCode, title, h1, description, id);
            UrlCheckRepository.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (Exception e) {
            log.info("Ошибка проверки URL {}: {}", url.getName(), e.getMessage());
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.redirect("/urls/" + id);
    }
}
