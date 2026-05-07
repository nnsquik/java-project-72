package hexlet.code;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private Javalin app;
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        mockWebServer.close();
    }

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

    @Test
    void testCreateCheck() {
        JavalinTest.test(app, (server, client) -> {
            mockWebServer.enqueue(new MockResponse.Builder()
                    .code(200)
                    .body("<html>"
                            + "<head><title>Test Title</title>"
                            + "<meta name=\"description\" content=\"Test Description\"></head>"
                            + "<body><h1>Test H1</h1></body>"
                            + "</html>")
                    .build());

            var mockUrl = mockWebServer.url("/").toString();
            client.post("/urls", "url=" + mockUrl);

            var url = UrlRepository.findByName(
                    mockUrl.substring(0, mockUrl.length() - 1));
            assertThat(url).isPresent();

            var response = client.post("/urls/" + url.get().getId() + "/checks", "");
            assertThat(response.code()).isEqualTo(200);

            var checks = UrlCheckRepository.findByUrlId(url.get().getId());
            assertThat(checks).hasSize(1);
            assertThat(checks.get(0).getStatusCode()).isEqualTo(200);
            assertThat(checks.get(0).getTitle()).isEqualTo("Test Title");
            assertThat(checks.get(0).getH1()).isEqualTo("Test H1");
            assertThat(checks.get(0).getDescription()).isEqualTo("Test Description");
        });
    }
}
