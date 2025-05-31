package su.blinov.emailsender.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import su.blinov.emailsender.R;
import su.blinov.emailsender.database.SettingsViewModel;
import su.blinov.emailsender.model.Settings;


public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel viewModel;
    private EditText etEmailFromSettings, etNameFromSettings, etPasswordSettings,
            etSubjectSettings, etHostSettings, etPortSettings;
    private CheckBox checkboxAuth, checkboxTls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_edit);

        initViews();
        setupViewModel();
        setupSaveButton();
    }

    private void initViews() {
        etEmailFromSettings = findViewById(R.id.etEmailFromSettings);
        etNameFromSettings = findViewById(R.id.etNameFromSettings);
        etPasswordSettings = findViewById(R.id.etPasswordSettings);
        etSubjectSettings = findViewById(R.id.etSubjectSettings);
        etHostSettings = findViewById(R.id.etHostSettings);
        etPortSettings = findViewById(R.id.etPortSettings);
        checkboxAuth = findViewById(R.id.checkbox_auth);
        checkboxTls = findViewById(R.id.checkbox_tls);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        viewModel.getSettings().observe(this, settings -> {
            if (settings != null) {
                populateFields(settings);
            } else {
                clearFields();
            }
        });
    }

    private void populateFields(Settings settings) {
        etEmailFromSettings.setText(settings.getEmailFrom());
        etNameFromSettings.setText(settings.getNameFrom());
        etPasswordSettings.setText(settings.getPassword());
        etSubjectSettings.setText(settings.getSubject());
        etHostSettings.setText(settings.getServerHost());
        etPortSettings.setText(settings.getServerPort() != null ?
                settings.getServerPort().toString() : "");
        checkboxAuth.setChecked(settings.getSmtpAuth() != null && settings.getSmtpAuth());
        checkboxTls.setChecked(settings.getSmtpTLS() != null && settings.getSmtpTLS());
    }

    private void clearFields() {
        etEmailFromSettings.setText("");
        etNameFromSettings.setText("");
        etPasswordSettings.setText("");
        etSubjectSettings.setText("");
        etHostSettings.setText("");
        etPortSettings.setText("");
        checkboxAuth.setChecked(false);
        checkboxTls.setChecked(false);
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSaveSettings);
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String email = etEmailFromSettings.getText().toString().trim();
        if (email.isEmpty()) {
            showError("Email отправителя обязателен");
            return;
        }

        Settings newSettings = new Settings(
                email,
                etNameFromSettings.getText().toString(),
                etPasswordSettings.getText().toString(),
                etSubjectSettings.getText().toString(),
                etHostSettings.getText().toString(),
                Optional.ofNullable(
                        parsePort(Optional.of(
                                etPortSettings.getText().toString()).orElse("0")))
                        .orElse(0),
                checkboxAuth.isChecked(),
                checkboxTls.isChecked()
        );


        viewModel.createSettings(newSettings);
        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            runOnUiThread(this::finish); // Явное закрытие активити
        }).start();
    }

    private Integer parsePort(String portStr) {
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
