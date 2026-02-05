package hexlet.code.model;

import java.time.LocalDateTime;

public final class UrlCheck {
    private Long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private LocalDateTime createdAt;

    public UrlCheck() { }

    public UrlCheck(int newStatusCode, String newTitle, String newH1, String newDescription, Long newUrlId) {
        this.setStatusCode(newStatusCode);
        this.setTitle(newTitle);
        this.setH1(newH1);
        this.setDescription(newDescription);
        this.setUrlId(newUrlId);
    }

    public UrlCheck(Long newId, int newStatusCode, String newTitle, String newH1, String newDescription,
                    Long newUrlId, LocalDateTime newCreatedAt) {
        this.setId(newId);
        this.setStatusCode(newStatusCode);
        this.setTitle(newTitle);
        this.setH1(newH1);
        this.setDescription(newDescription);
        this.setUrlId(newUrlId);
        this.setCreatedAt(newCreatedAt);
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public void setStatusCode(int newStatusCode) {
        this.statusCode = newStatusCode;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setH1(String newH1) {
        this.h1 = newH1;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setUrlId(Long newUrlId) {
        this.urlId = newUrlId;
    }

    public void setCreatedAt(LocalDateTime newCreatedAt) {
        this.createdAt = newCreatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getTitle() {
        return this.title;
    }

    public String getH1() {
        return this.h1;
    }

    public String getDescription() {
        return this.description;
    }

    public Long getUrlId() {
        return this.urlId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

}
