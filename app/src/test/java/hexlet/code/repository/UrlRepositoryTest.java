package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
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
    private static final long MAGIC_NUMBER = 999999L;
    private HikariDataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = new HikariDataSource(hikariConfig);

        UrlRepository.setDataSource(dataSource);

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
    void testSaveAndGetUrl() throws SQLException {
        Url url = new Url("https://example.com");
        UrlRepository.save(url);

        Optional<Url> found = UrlRepository.findById(url.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("https://example.com");
    }

    @Test
    void testFindAllUrls() throws SQLException {
        Url url1 = new Url("https://example1.com");
        Url url2 = new Url("https://example2.com");
        UrlRepository.save(url1);
        UrlRepository.save(url2);

        List<Url> urls = UrlRepository.getEntities();
        assertThat(urls).hasSize(2);
    }

    @Test
    void testExistsByName() throws SQLException {
        Url url = new Url("https://exists.com");
        UrlRepository.save(url);

        boolean exists = UrlRepository.existsByName("https://exists.com");
        assertThat(exists).isTrue();

        boolean notExists = UrlRepository.existsByName("https://not-exists.com");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        Optional<Url> notFound = UrlRepository.findById(MAGIC_NUMBER);
        assertThat(notFound).isEmpty();
    }
}
