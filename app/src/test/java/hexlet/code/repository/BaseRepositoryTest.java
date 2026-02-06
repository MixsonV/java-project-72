package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class BaseRepositoryTest {
    private HikariDataSource dataSource;

    @BeforeEach
    void setUp() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = new HikariDataSource(hikariConfig);
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Test
    void testSetAndGetDataSource() {
        BaseRepository.setDataSource(dataSource);
        DataSource retrieved = BaseRepository.getDataSource();

        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(dataSource);
    }

}
