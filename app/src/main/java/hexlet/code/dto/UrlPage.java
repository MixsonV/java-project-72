package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.util.List;

public final class UrlPage {
    private Url url;
    private List<UrlCheck> urlChecks;

    public UrlPage(Url newUrl) {
        this.setUrl(newUrl);
    }

    public UrlPage(Url newUrl, List<UrlCheck> newUrlChecks) {
        this.setUrl(newUrl);
        this.setUrlChecks(newUrlChecks);
    }

    public void setUrl(Url newUrl) {
        this.url = newUrl;
    }

    public void setUrlChecks(List<UrlCheck> newUrlChecks) {
        this.urlChecks = newUrlChecks;
    }

    public Url getUrl() {
        return this.url;
    }

    public List<UrlCheck> getUrlChecks() {
        return this.urlChecks;
    }
}
