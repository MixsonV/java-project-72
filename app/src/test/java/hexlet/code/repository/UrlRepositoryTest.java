package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UrlRepositoryTest {
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_NOT_FOUND = 404;
    private static final long MAGIC_NUMBER = 999999L;
    private HikariDataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = new HikariDataSource(hikariConfig);

        UrlRepository.setDataSource(dataSource);
        UrlCheckRepository.setDataSource(dataSource);

        var schema = getClass().getClassLoader().getResource("schema.sql");
        var file = new File(schema.getFile());
        var sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));

        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Test
    void testSaveUrlCheck() throws SQLException {
        Url url = new Url("https://example.com");
        UrlRepository.save(url);

        UrlCheck check = new UrlCheck(url.getId(), STATUS_CODE_OK, "Test Title",
                "Test H1", "Test Description");
        UrlCheckRepository.saveUrlCheck(check);

        Optional<UrlCheck> found = UrlCheckRepository.findLastCheckByUrlId(url.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(found.get().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void testFindLastCheckByUrlId() throws SQLException {
        Url url = new Url("https://example.com");
        UrlRepository.save(url);

        UrlCheck check1 = new UrlCheck(url.getId(), STATUS_CODE_OK, "Title 1",
                "H1 1", "Desc 1");
        UrlCheck check2 = new UrlCheck(url.getId(), STATUS_CODE_NOT_FOUND, "Title 2",
                "H1 2", "Desc 2");

        UrlCheckRepository.saveUrlCheck(check1);
        UrlCheckRepository.saveUrlCheck(check2);

        Optional<UrlCheck> lastCheck = UrlCheckRepository.findLastCheckByUrlId(url.getId());
        assertThat(lastCheck).isPresent();
        assertThat(lastCheck.get().getStatusCode()).isEqualTo(STATUS_CODE_NOT_FOUND);
    }

    @Test
    void testFindAllChecksByUrlId() throws SQLException {
        Url url = new Url("https://example.com");
        UrlRepository.save(url);

        UrlCheck check1 = new UrlCheck(url.getId(), STATUS_CODE_OK, "Title 1",
                "H1 1", "Desc 1");
        UrlCheck check2 = new UrlCheck(url.getId(), STATUS_CODE_NOT_FOUND, "Title 2",
                "H1 2", "Desc 2");

        UrlCheckRepository.saveUrlCheck(check1);
        UrlCheckRepository.saveUrlCheck(check2);

        List<UrlCheck> checks = UrlCheckRepository.findAllChecksByUrlId(url.getId());
        assertThat(checks).hasSize(2);
        assertThat(checks.get(0).getStatusCode()).isIn(STATUS_CODE_OK, STATUS_CODE_NOT_FOUND);
    }

    @Test
    void testFindLastCheckByUrlIdNotFound() throws SQLException {
        Optional<UrlCheck> notFound = UrlCheckRepository.findLastCheckByUrlId(MAGIC_NUMBER);
        assertThat(notFound).isEmpty();
    }

    @Test
    void testFindAllChecksByUrlIdNotFound() throws SQLException {
        List<UrlCheck> checks = UrlCheckRepository.findAllChecksByUrlId(MAGIC_NUMBER);
        assertThat(checks).isEmpty();
    }

    @Test
    void testGetEntitiesEmpty() throws SQLException {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM urls");
        }

        List<Url> urls = UrlRepository.getEntities();
        assertThat(urls).isEmpty();
    }

    @Test
    void testSaveUrlWithSpecialCharacters() throws SQLException {
        Url url = new Url("https://example.com/path?query=test#fragment");
        UrlRepository.save(url);

        Optional<Url> found = UrlRepository.findById(url.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).contains("https://example.com");
    }

}
