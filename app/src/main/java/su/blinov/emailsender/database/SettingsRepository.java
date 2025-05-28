package su.blinov.emailsender.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import su.blinov.emailsender.model.Settings;

public class SettingsRepository {
    private final SettingsDao settingsDao;
    private final ExecutorService executor;

    public SettingsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        settingsDao = db.settingsDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<Settings> getSettings() {
        return settingsDao.getAllSettings();
    }

    public void insert(Settings settings) {
        executor.execute(() -> settingsDao.insert(settings));
    }

    public void update(Settings settings) {
        executor.execute(() -> settingsDao.update(settings));
    }
}
