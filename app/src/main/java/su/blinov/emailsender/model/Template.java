package su.blinov.emailsender.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "templates")
public class Template {
    @PrimaryKey
    @NonNull
    private UUID id;

    @NonNull
    private final String message;

    private final String sex;

    public Template(@NonNull UUID id, @NonNull String message, String sex) {
        this.id = id;
        this.message = message;
        this.sex = sex;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    // Геттеры
    @NonNull
    public UUID getId() {
        return id;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public String getSex() {
        return sex;
    }
}