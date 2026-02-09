package hexlet.code.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UrlModelTest {

    @Test
    void testUrlConstructorWithName() {
        String name = "https://example.com";
        Url url = new Url(name);

        assertThat(url.getName()).isEqualTo(name);
        assertThat(url.getId()).isNull();
    }

    @Test
    void testUrlConstructorWithAllFields() {
        Long id = 1L;
        String name = "https://example.com";
        LocalDateTime createdAt = LocalDateTime.now();
        UrlCheck lastCheck = new UrlCheck();

        Url url = new Url(id, name, createdAt, lastCheck);

        assertThat(url.getId()).isEqualTo(id);
        assertThat(url.getName()).isEqualTo(name);
        assertThat(url.getCreatedAt()).isEqualTo(createdAt);
        assertThat(url.getLastCheck()).isEqualTo(lastCheck);
    }

    @Test
    void testUrlSettersAndGetters() {
        Url url = new Url();
        Long id = 2L;
        String name = "https://test.com";
        LocalDateTime createdAt = LocalDateTime.now();
        UrlCheck lastCheck = new UrlCheck();

        url.setId(id);
        url.setName(name);
        url.setCreatedAt(createdAt);
        url.setLastCheck(lastCheck);

        assertThat(url.getId()).isEqualTo(id);
        assertThat(url.getName()).isEqualTo(name);
        assertThat(url.getCreatedAt()).isEqualTo(createdAt);
        assertThat(url.getLastCheck()).isEqualTo(lastCheck);
    }
}
