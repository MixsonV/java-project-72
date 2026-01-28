package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    private Long id;
    private String name;

    private LocalDateTime createdAt;

    public Url(String newName, LocalDateTime newCreatedAt) {
        this.name = newName;
        this.createdAt = newCreatedAt;
    }
}
