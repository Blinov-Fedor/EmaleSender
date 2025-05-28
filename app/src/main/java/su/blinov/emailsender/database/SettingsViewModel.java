package su.blinov.emailsender.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import su.blinov.emailsender.model.Settings;

public class SettingsViewModel extends AndroidViewModel {
    private final SettingsRepository repository;
    private LiveData<Settings> settings;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new SettingsRepository(application);
    }

    public LiveData<Settings> getSettings() {
        if (settings == null) {
            settings = repository.getSettings();
        }
        return settings;
    }

    public void saveSettings(Settings newSettings) {
        repository.update(newSettings);
    }

    public void createSettings(Settings newSettings) {
        repository.insert(newSettings);
    }
}
