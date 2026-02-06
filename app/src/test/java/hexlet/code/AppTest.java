package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.utils.TestUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_NOT_FOUND = 404;
    private static final int MAGIC_NUMBER_YEAR = 2023;
    private static final int MAGIC_NUMBER_MONTH = 10;
    private static final int MAGIC_NUMBER_DAY = 30;
    private static final int MAGIC_NUMBER_HOUR = 12;

    private static MockWebServer mockServer;
    private Javalin app;
    private Map<String, Object> existingUrl;
    private Map<String, Object> existingUrlCheck;
    private HikariDataSource dataSource;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse().setBody(readFixture("index.html"));
        mockServer.enqueue(mockedResponse);
        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        hikariConfig.setAutoCommit(true);

        dataSource = new HikariDataSource(hikariConfig);
        app = App.getApp(dataSource);

        var schema = AppTest.class.getClassLoader().getResource("schema.sql");
        var file = new File(schema.getFile());
        var sql = Files.lines(file.toPath()).collect(Collectors.joining("\n"));

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        String testUrl = "https://en.hexlet.io";
        TestUtils.addUrl(dataSource, testUrl);
        existingUrl = TestUtils.getUrlByName(dataSource, testUrl);

        TestUtils.addUrlCheck(dataSource, (long) existingUrl.get("id"));
        existingUrlCheck = TestUtils.getUrlCheck(dataSource, (long) existingUrl.get("id"));
    }

    @AfterEach
    public void tearDown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Test
    public void testUrlConstructor() {
        Long id = 1L;
        String name = "example";
        LocalDateTime createdAt = LocalDateTime.of(MAGIC_NUMBER_YEAR, MAGIC_NUMBER_MONTH,
                MAGIC_NUMBER_DAY, MAGIC_NUMBER_HOUR, 0);

        Url url = new Url(id, name, createdAt);

        assertEquals(id, url.getId());
        assertEquals(name, url.getName());
        assertEquals(createdAt, url.getCreatedAt());
    }

    @Test
    public void testUrlCheckConstructor() {
        Long urlId = 1L;
        int statusCode = STATUS_CODE_OK;
        String title = "example title";
        String h1 = "header 1";
        String description = "some description";

        UrlCheck urlCheck = new UrlCheck(urlId, statusCode, title, h1, description);

        assertEquals(urlId, urlCheck.getUrlId());
        assertEquals(statusCode, urlCheck.getStatusCode());
        assertEquals(title, urlCheck.getTitle());
        assertEquals(h1, urlCheck.getH1());
        assertEquals(description, urlCheck.getDescription());
    }

    @Nested
    class RootTest {
        @Test
        void testIndex() {
            JavalinTest.test(app, (server, client) -> {
                assertThat(client.get("/").code()).isEqualTo(STATUS_CODE_OK);
            });
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testIndex() {
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls");
                assertThat(response.code()).isEqualTo(STATUS_CODE_OK);
                assertThat(response.body().string())
                        .contains(existingUrl.get("name").toString())
                        .contains(existingUrlCheck.get("status_code").toString());
            });
        }

        @Test
        void testShow() {
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/" + existingUrl.get("id"));
                assertThat(response.code()).isEqualTo(STATUS_CODE_OK);
                assertThat(response.body().string())
                        .contains(existingUrl.get("name").toString())
                        .contains(existingUrlCheck.get("status_code").toString());
            });
        }

        @Test
        void testStore() {
            String inputUrl = "https://ru.hexlet.io";

            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                var response = client.post("/urls", requestBody);
                assertThat(response.code()).isEqualTo(STATUS_CODE_OK);

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage).contains("https://ru.hexlet.io");

                var actualUrl = TestUtils.getUrlByName(dataSource, "https://ru.hexlet.io");
                assertThat(actualUrl).isNotNull();
                assertThat(actualUrl.get("name").toString()).isEqualTo("https://ru.hexlet.io");
            });
        }
    }

    @Nested
    class UrlCheckTest {

        @Test
        void testStore() throws IOException {
            MockResponse mockResponse = new MockResponse().setBody(readFixture("index.html"));
            mockServer.enqueue(mockResponse);

            String url = mockServer.url("/").toString().replaceAll("/$", "");

            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + url;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(STATUS_CODE_OK);

                var actualUrl = TestUtils.getUrlByName(dataSource, url);
                assertThat(actualUrl).isNotNull();
                assertThat(actualUrl.get("name").toString()).isEqualTo(url);

                MockResponse checkResponse = new MockResponse().setBody(readFixture("index.html"));
                mockServer.enqueue(checkResponse);

                client.post("/urls/" + actualUrl.get("id") + "/checks");

                assertThat(client.get("/urls/" + actualUrl.get("id")).code()).isEqualTo(STATUS_CODE_OK);

                var actualCheck = TestUtils.getUrlCheck(dataSource, (long) actualUrl.get("id"));
                assertThat(actualCheck).isNotNull();
                assertThat(actualCheck.get("title")).isEqualTo("Test page");
                assertThat(actualCheck.get("h1")).isEqualTo("Do not expect a miracle, miracles yourself!");
                assertThat(actualCheck.get("description")).isEqualTo("statements of great people");
            });
        }
    }

    @Nested
    class UrlControllerAdditionalTests {

        @Test
        void testCreateUrlWithoutProtocol() {
            JavalinTest.test(app, (server, client) -> {
                client.post("/urls", "url=example.com");

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage).doesNotContain("example.com");
            });
        }

        @Test
        void testCreateDuplicateUrl() {
            JavalinTest.test(app, (server, client) -> {
                client.post("/urls", "url=https://duplicate.com");
                client.post("/urls", "url=https://duplicate.com");

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage.split("https://duplicate.com", -1).length - 1).isEqualTo(1);
            });
        }

        @Test
        void testCreateInvalidUrlFormat() {
            JavalinTest.test(app, (server, client) -> {
                client.post("/urls", "url=not-a-valid-url");

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage).doesNotContain("not-a-valid-url");
            });
        }

        @Test
        void testCreateUrlWithHttpProtocol() {
            JavalinTest.test(app, (server, client) -> {
                client.post("/urls", "url=http://test.com");

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage).contains("http://test.com");
            });
        }

        @Test
        void testCreateUrlWithPort() {
            JavalinTest.test(app, (server, client) -> {
                client.post("/urls", "url=https://example.com:8080");

                var urlsPage = client.get("/urls").body().string();
                assertThat(urlsPage).contains("https://example.com:8080");
            });
        }

        @Test
        void testShowNonExistentUrl() {
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/999999");
                assertThat(response.code()).isEqualTo(STATUS_CODE_NOT_FOUND);
            });
        }

        @Test
        void testCreateCheckForNonExistentUrl() {
            JavalinTest.test(app, (server, client) -> {
                var response = client.post("/urls/999999/checks");
                assertThat(response.code()).isEqualTo(STATUS_CODE_NOT_FOUND);
            });
        }

        @Test
        void testCreateCheckForValidUrl() {
            JavalinTest.test(app, (server, client) -> {
                String mockUrl = mockServer.url("/").toString().replaceAll("/$", "");

                mockServer.enqueue(new MockResponse().setBody(readFixture("index.html")));
                client.post("/urls", "url=" + mockUrl);

                var url = TestUtils.getUrlByName(dataSource, mockUrl);

                mockServer.enqueue(new MockResponse().setBody(readFixture("index.html")));
                client.post("/urls/" + url.get("id") + "/checks");

                var showPage = client.get("/urls/" + url.get("id")).body().string();
                assertThat(showPage).contains(mockUrl);
                assertThat(showPage).contains("Test page");
            });
        }
    }

}
