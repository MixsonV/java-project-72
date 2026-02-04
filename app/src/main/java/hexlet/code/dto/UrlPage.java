package hexlet.code.dto;

import hexlet.code.model.Url;

public final class UrlPage {
    private Url url;

    public UrlPage(Url newUrl) {
        this.setUrl(newUrl);
    }

    public void setUrl(Url newUrl) {
        this.url = newUrl;
    }

    public Url getUrl() {
        return this.url;
    }
}
