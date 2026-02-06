package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.TemplateEngine;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppUnitTest {
    private static final int MAGIC_PORT_7071 = 7071;
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
    void testGetAppWithDataSource() {
        Javalin app = App.getApp(dataSource);
        assertNotNull(app);
        assertThat(app).isInstanceOf(Javalin.class);
    }

    @Test
    void testCreateTemplateEngine() {
        TemplateEngine engine = App.createTemplateEngine();
        assertNotNull(engine);
    }

    @Test
    void testGetPortDefault() {
        System.clearProperty("PORT");

        int port = App.getPort();
        assertThat(port).isEqualTo(MAGIC_PORT_7071);
    }

    @Test
    void testCreateDataSourceDefault() {
        HikariDataSource ds = App.createDataSource();
        try {
            assertThat(ds.getJdbcUrl()).contains("h2:mem:testdb");
        } finally {
            ds.close();
        }
    }

    @Test
    void testReadResourceFile() throws IOException {
        String sql = App.readResourceFile("schema.sql");
        assertThat(sql).isNotEmpty();
        assertThat(sql).contains("CREATE TABLE");
    }

}
