package su.blinov.emailsender.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private final String email;
    private final String name;
    private final String sex;

    public User(@NonNull String email, String name, String sex) {
        this.email = email;
        this.name = name;
        this.sex = sex;
    }

    // Геттеры
    @NonNull
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSex() { return sex; }
}