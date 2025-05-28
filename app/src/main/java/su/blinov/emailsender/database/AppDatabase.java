package su.blinov.emailsender.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import su.blinov.emailsender.model.Settings;
import su.blinov.emailsender.model.Template;
import su.blinov.emailsender.model.User;

@Database(entities = {User.class, Template.class, Settings.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract TemplateDao templateDao();

    public abstract SettingsDao settingsDao();

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db"
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }




}