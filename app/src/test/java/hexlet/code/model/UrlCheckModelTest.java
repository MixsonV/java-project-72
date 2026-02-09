package hexlet.code.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UrlCheckModelTest {
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_404 = 404;

    @Test
    void testUrlCheckConstructorWithFields() {
        Long urlId = 1L;
        String title = "Test Title";
        String h1 = "Test H1";
        String description = "Test Description";

        UrlCheck check = new UrlCheck(urlId, STATUS_CODE_200, title, h1, description);

        assertThat(check.getUrlId()).isEqualTo(urlId);
        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_200);
        assertThat(check.getTitle()).isEqualTo(title);
        assertThat(check.getH1()).isEqualTo(h1);
        assertThat(check.getDescription()).isEqualTo(description);
    }

    @Test
    void testUrlCheckSettersAndGetters() {
        UrlCheck check = new UrlCheck();
        Long id = 1L;
        Long urlId = 2L;
        String title = "New Title";
        String h1 = "New H1";
        String description = "New Description";
        LocalDateTime createdAt = LocalDateTime.now();

        check.setId(id);
        check.setUrlId(urlId);
        check.setStatusCode(STATUS_CODE_404);
        check.setTitle(title);
        check.setH1(h1);
        check.setDescription(description);
        check.setCreatedAt(createdAt);

        assertThat(check.getId()).isEqualTo(id);
        assertThat(check.getUrlId()).isEqualTo(urlId);
        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_404);
        assertThat(check.getTitle()).isEqualTo(title);
        assertThat(check.getH1()).isEqualTo(h1);
        assertThat(check.getDescription()).isEqualTo(description);
        assertThat(check.getCreatedAt()).isEqualTo(createdAt);
    }
}
