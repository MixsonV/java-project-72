package hexlet.code.dto;

import hexlet.code.model.Url;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UrlsPageTest {

    @Test
    void testUrlsPageConstructorAndSetters() {
        Url url1 = new Url("https://site1.com");
        Url url2 = new Url("https://site2.com");
        List<Url> urls = List.of(url1, url2);

        UrlsPage page = new UrlsPage(urls);

        assertThat(page.getUrls()).isEqualTo(urls);

        List<Url> newUrls = List.of(new Url("https://newsite.com"));
        page.setUrls(newUrls);
        assertThat(page.getUrls()).isEqualTo(newUrls);
    }
}
