package su.blinov.emailsender.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import su.blinov.emailsender.model.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    List<User> getAllUsers();

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}