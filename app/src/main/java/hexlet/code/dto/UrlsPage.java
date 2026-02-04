package hexlet.code.dto;

import hexlet.code.model.Url;
import java.util.List;

public final class UrlsPage extends BasePage {
    private List<Url> urls;

    public UrlsPage(List<Url> newUrls) {
        this.setUrls(newUrls);
    }

    public void setUrls(List<Url> newUrls) {
        this.urls = newUrls;
    }

    public List<Url> getUrls() {
        return this.urls;
    }
}
