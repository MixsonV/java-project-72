package hexlet.code.model;

import java.time.LocalDateTime;

public final class Url {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private UrlCheck lastCheck;

    public Url() { }

    public Url(Long newId, String newName, LocalDateTime newCreatedAt) {
        this.setId(newId);
        this.setName(newName);
        this.setCreatedAt(newCreatedAt);
    }

    public Url(Long newId, String newName, LocalDateTime newCreatedAt, UrlCheck newLastCheck) {
        this.setId(newId);
        this.setName(newName);
        this.setCreatedAt(newCreatedAt);
        this.setLastCheck(newLastCheck);
    }

    public Url(String newName) {
        this.setName(newName);
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setCreatedAt(LocalDateTime newCreatedDate) {
        this.createdAt = newCreatedDate;
    }

    public void setLastCheck(UrlCheck newLastCheck) {
        this.lastCheck = newLastCheck;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public UrlCheck getLastCheck() {
        return this.lastCheck;
    }
}
