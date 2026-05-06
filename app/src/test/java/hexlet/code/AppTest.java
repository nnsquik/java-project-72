package hexlet.code;

import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;

    @BeforeEach
    void setUp() throws Exception {
        app = App.getApp();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=http://google.com");
            assertThat(response.code()).isEqualTo(200);

            var url = UrlRepository.findByName("http://google.com");
            assertThat(url).isPresent();

            var urlResponse = client.get("/urls/" + url.get().getId());
            assertThat(urlResponse.code()).isEqualTo(200);
            assertThat(urlResponse.body().string()).contains("http://google.com");
        });
    }

    @Test
    void testCreateDuplicateUrl() {
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=http://google.com");
            var response = client.post("/urls", "url=http://google.com");
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
        });
    }

    @Test
    void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=not-url");
            assertThat(response.code()).isEqualTo(422);
        });
    }

    @Test
    void  testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=http://google.com");

            var url = UrlRepository.findByName("http://google.com");
            assertThat(url).isPresent();

            var response = client.get("/urls/" + url.get().getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("http://google.com");
        });
    }
}
