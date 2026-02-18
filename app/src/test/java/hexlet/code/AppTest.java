package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    private static MockWebServer mockServer;
    private static HikariDataSource testDataSource;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL",
                "jdbc:h2:mem:testdb_" + System.currentTimeMillis() + ";DB_CLOSE_DELAY=-1");
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        String mockResponseBody = readFixture("index.html");
        MockResponse mockedResponse = new MockResponse()
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "text/html");
        mockServer.enqueue(mockedResponse);
        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
        if (testDataSource != null && !testDataSource.isClosed()) {
            testDataSource.close();
        }
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        String dbUrl = getDatabaseUrl();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setMaximumPoolSize(5);

        testDataSource = new HikariDataSource(hikariConfig);

        var schemaResource = App.class.getClassLoader().getResource("schema.sql");
        if (schemaResource == null) {
            throw new RuntimeException("schema.sql not found in resources");
        }
        var schemaFile = new File(schemaResource.getFile());
        var sql = Files.lines(schemaFile.toPath())
                .collect(Collectors.joining("\n"));

        try (var connection = testDataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = testDataSource;
    }

    private Javalin createTestAppForCurrentTest() throws SQLException, IOException {
        return App.getApp(testDataSource);
    }

    @Test
    void testUrlConstructor() {
        Long id = 1L;
        String name = "example";
        LocalDateTime createdAt = LocalDateTime.of(2023, 10, 30, 12, 0);

        Url url = new Url(id, name, createdAt);

        assertEquals(id, url.getId());
        assertEquals(name, url.getName());
        assertEquals(createdAt, url.getCreatedAt());
    }

    @Test
    void testUrlCheckConstructor() {
        Long urlId = 1L;
        int statusCode = 200;
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
        void testIndex() throws SQLException, IOException {
            JavalinTest.test(createTestAppForCurrentTest(), (server, client) -> {
                assertThat(client.get("/").code()).isEqualTo(200);
            });
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testIndex() throws SQLException, IOException {
            String inputUrl = "https://example.com";
            Javalin app = createTestAppForCurrentTest();

            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                var postResponse = client.post("/urls", requestBody);
                assertThat(postResponse.code()).isEqualTo(200);

                var getResponse = client.get("/urls");
                assertThat(getResponse.code()).isEqualTo(200);
                assertThat(getResponse.body().string()).contains(inputUrl);

                List<Url> urlsFromDb = UrlRepository.getEntities();
                assertTrue(urlsFromDb.stream().anyMatch(u -> u.getName().equals(inputUrl)),
                        "URL should be present in DB");
            });
        }

        @Test
        void testStore() throws SQLException, IOException {
            String inputUrl = "https://store-test.com";

            JavalinTest.test(createTestAppForCurrentTest(), (server, client) -> {
                var postResponse = client.post("/urls", "url=" + inputUrl);
                assertThat(postResponse.code()).isEqualTo(200);

                Optional<Url> savedUrlOpt = UrlRepository.findByName(inputUrl);
                assertTrue(savedUrlOpt.isPresent(), "URL should have been saved");
                Url savedUrl = savedUrlOpt.get();
                assertEquals(inputUrl, savedUrl.getName(), "Saved URL name should match input");
            });
        }
    }

    @Nested
    class UrlCheckTest {

        @Test
        void testStore() throws SQLException, IOException {
            String url = mockServer.url("/").toString().replaceAll("/$", "");
            Javalin app = createTestAppForCurrentTest();

            JavalinTest.test(app, (server, client) -> {
                var postResponse = client.post("/urls", "url=" + url);
                assertThat(postResponse.code()).isEqualTo(200);

                Optional<Url> addedUrlOpt = UrlRepository.findByName(url);
                assertTrue(addedUrlOpt.isPresent(), "URL should have been added via HTTP request");
                Long urlId = addedUrlOpt.get().getId();
                assertNotNull(urlId, "URL ID should not be null");

                var checkResponse = client.post("/urls/" + urlId + "/checks");
                assertThat(checkResponse.code()).isEqualTo(200);

                List<UrlCheck> checks = UrlCheckRepository.findAllChecksByUrlId(urlId);
                assertThat(checks.size()).isGreaterThan(0);

                UrlCheck latestCheck = checks.get(checks.size() - 1);
                assertEquals("Test page", latestCheck.getTitle(),
                        "Check title should match parsed title from mock server");
                assertEquals("Do not expect a miracle, miracles yourself!", latestCheck.getH1(),
                        "Check h1 should match parsed h1 from mock server");
                assertEquals("statements of great people", latestCheck.getDescription(),
                        "Check description should match parsed description from mock server");
                assertEquals(200, latestCheck.getStatusCode(),
                        "Check status code should match response from mock server");
            });
        }
    }

}
