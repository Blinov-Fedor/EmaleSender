package su.blinov.emailsender.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.UUID;

import su.blinov.emailsender.R;
import su.blinov.emailsender.database.AppDatabase;
import su.blinov.emailsender.model.Template;

public class EditTemplateActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText etTemplateMessage;
    private Spinner spTemplateSex;
    private Template existingTemplate;
    private UUID id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db").build();

        etTemplateMessage = findViewById(R.id.etTemplateMessage);
        spTemplateSex = findViewById(R.id.spTemplateSex);

        String message = getIntent().getStringExtra("message");
        if (message != null) {
            loadTemplate(message);
        }

        findViewById(R.id.btnTemplateSave).setOnClickListener(v -> saveTemplate());
    }

    private void loadTemplate(String message) {
        new Thread(() -> {
            existingTemplate = db.templateDao().getAllTemplates().stream()
                    .filter(u -> u.getMessage().equals(message))
                    .findFirst()
                    .orElse(null);

            runOnUiThread(() -> {
                if (existingTemplate != null) {
                    id = existingTemplate.getId();
                    etTemplateMessage.setText(existingTemplate.getMessage());
                    spTemplateSex.setSelection(getIndex(spTemplateSex, existingTemplate.getSex()));
                }
            });
        }).start();
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private void saveTemplate() {
        String message = etTemplateMessage.getText().toString();
        String sex = spTemplateSex.getSelectedItem().toString();
        Template template;

        if (id != null) {
             template = new Template(id, message, sex);
        } else {
            template = new Template(UUID.randomUUID(), message, sex);
        }

        new Thread(() -> {
            if (existingTemplate != null) {
                db.templateDao().updateTemplate(template);
            } else {
                db.templateDao().insertTemplate(template);
            }
            runOnUiThread(this::finish); // Явное закрытие активити
        }).start();
    }
}