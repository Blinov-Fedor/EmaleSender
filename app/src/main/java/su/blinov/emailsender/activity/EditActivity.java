package su.blinov.emailsender.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import su.blinov.emailsender.database.AppDatabase;
import su.blinov.emailsender.R;
import su.blinov.emailsender.model.User;

public class EditActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText etName, etEmail;
    private Spinner spSex;
    private User existingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db").build();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        spSex = findViewById(R.id.spSex);

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            loadUser(email);
            etEmail.setEnabled(false);
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveUser());
    }

    private void loadUser(String email) {
        new Thread(() -> {
            existingUser = db.userDao().getAllUsers().stream()
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);

            runOnUiThread(() -> {
                if (existingUser != null) {
                    etName.setText(existingUser.getName());
                    etEmail.setText(existingUser.getEmail());
                    spSex.setSelection(getIndex(spSex, existingUser.getSex()));
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

    private void saveUser() {
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String sex = spSex.getSelectedItem().toString();

        User user = new User(email, name, sex);

        new Thread(() -> {
            if (existingUser != null) {
                db.userDao().updateUser(user);
            } else {
                db.userDao().insertUser(user);
            }
            runOnUiThread(this::finish); // Явное закрытие активити
        }).start();
    }
}