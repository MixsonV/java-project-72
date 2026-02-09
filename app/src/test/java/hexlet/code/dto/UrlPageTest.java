package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UrlPageTest {

    @Test
    void testUrlPageConstructorWithUrlAndChecks() {
        Url url = new Url("https://example.com");
        UrlCheck check1 = new UrlCheck();
        UrlCheck check2 = new UrlCheck();
        List<UrlCheck> checks = List.of(check1, check2);

        UrlPage page = new UrlPage(url, checks);

        assertThat(page.getUrl()).isEqualTo(url);
        assertThat(page.getUrlChecks()).isEqualTo(checks);
    }

    @Test
    void testUrlPageSetters() {
        UrlPage page = new UrlPage(new Url("https://init.com"));
        Url newUrl = new Url("https://updated.com");
        List<UrlCheck> newChecks = List.of(new UrlCheck());

        page.setUrl(newUrl);
        page.setUrlChecks(newChecks);

        assertThat(page.getUrl()).isEqualTo(newUrl);
        assertThat(page.getUrlChecks()).isEqualTo(newChecks);
    }
}
