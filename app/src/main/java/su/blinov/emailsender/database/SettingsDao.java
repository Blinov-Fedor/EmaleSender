package su.blinov.emailsender.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import su.blinov.emailsender.model.Settings;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM settings LIMIT 1")
    LiveData<Settings> getAllSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Settings settings);

    @Update
    void update(Settings settings);

}
