package su.blinov.emailsender.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import su.blinov.emailsender.MailSender;
import su.blinov.emailsender.R;
import su.blinov.emailsender.database.AppDatabase;
import su.blinov.emailsender.database.SettingsViewModel;
import su.blinov.emailsender.database.UserAdapter;
import su.blinov.emailsender.model.Settings;
import su.blinov.emailsender.model.User;

public class MainActivity extends AppCompatActivity {
    public static final int MIN_TIME_INTERVAL = 500;
    public static final int MAX_TIME_INTERVAL = 2000;
    private AppDatabase appDB;
    private UserAdapter userAdapter;
    private SettingsViewModel viewModel;
    private AlertDialog progressDialog;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private Thread emailThread; // Для возможности отмены

    // Показать диалог с прогрессом
    private void showProgressDialog(int totalEmails) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);

            progressBar = dialogView.findViewById(R.id.progressBar);
            tvProgress = dialogView.findViewById(R.id.tvProgress);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);

            progressBar.setMax(totalEmails);
            progressBar.setProgress(0);
            tvProgress.setText(String.format(Locale.getDefault(),
                    "Отправлено: 0 из %d", totalEmails));

            btnCancel.setOnClickListener(v -> {
                if (emailThread != null) {
                    emailThread.interrupt();
                }
                dismissProgressDialog();
            });

            builder.setView(dialogView)
                    .setCancelable(false);

            progressDialog = builder.create();
            progressDialog.show();
        });
    }

    // Обновление прогресса
    private void updateProgress(int current, int total) {
        runOnUiThread(() -> {
            if (progressBar != null && tvProgress != null) {
                progressBar.setProgress(current);
                int percent = (current * 100) / total;
                tvProgress.setText(String.format(Locale.getDefault(),
                        "Отправлено: %d из %d (%d%%)", current, total, percent));
            }
        });
    }

    // Закрыть диалог
    private void dismissProgressDialog() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appDB = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db").build();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        userAdapter = new UserAdapter(this, appDB);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        findViewById(R.id.fabAddTemplate).setOnClickListener(v ->
                startActivity(new Intent(this, TemplateActivity.class)));

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, EditActivity.class)));

        findViewById(R.id.fabAddSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        Button buttonSend = findViewById(R.id.sendButton);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        buttonSend.setOnClickListener(v ->
                viewModel.getSettings().observe(this, settings -> {
            if (settings != null) {

                new Thread(() -> {
                    List<User> users = appDB.userDao().getAllUsers();
                    AtomicInteger counter = new AtomicInteger(0);

                    runOnUiThread(() -> showProgressDialog(users.size()));

                    for (User user : users) {
                        if (Thread.currentThread().isInterrupted()) break; // Проверка на отмену

                        try {
                            // 1. Случайная задержка между отправками
                            int pause = new Random()
                                    .nextInt(MAX_TIME_INTERVAL - MIN_TIME_INTERVAL + 1) + MIN_TIME_INTERVAL;
                            Thread.sleep(pause);
                            Log.i("PAUSE", "Pause for " + pause + " ms");

                            // 2. Получаем шаблон сообщения
                            String template = appDB.templateDao().getTemplateBySex(user.getSex());
                            String messageTemplate = template != null ? template : "";

                            // 3. Отправляем письмо (сетевой вызов!)
                            // TODO: на будущее задел
                            boolean isSent = sendMail(settings, user, messageTemplate);

                            // 4. Обновляем UI (прогресс)
                            runOnUiThread(() -> {
                                counter.incrementAndGet();
                                updateProgress(counter.get(), users.size());
                            });

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    // 5. Завершение
                    runOnUiThread(() -> {
                        dismissProgressDialog();
                        Toast.makeText(this, "Отправлено: " + counter.get() + " из " + users.size(),
                                Toast.LENGTH_SHORT).show();
                    });
                }).start();
            } else {
                Toast.makeText(this, "Заполните настройки", Toast.LENGTH_SHORT).show();
            }
        }));

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            loadUsers();
            swipeRefresh.setRefreshing(false);
        });
    }


    private boolean sendMail(Settings settings, User user, String messageTemplate) {
        try {
            MailSender mailSender = new MailSender(
                    settings.getEmailFrom(),
                    settings.getNameFrom(),
                    settings.getPassword(),
                    settings.getSubject(),
                    settings.getServerHost(),
                    settings.getServerPort(),
                    settings.getSmtpAuth(),
                    settings.getSmtpTLS());
            String replacedText = createMessageFromTemplate(user, messageTemplate);
            mailSender.sendEmail(user.getEmail(), replacedText);
            Log.i("SENDING", ">>> Sending mail to email: " + user.getEmail());
            return true;
        } catch (Exception e) {
            Log.e("Email", "Ошибка отправки: " + e.getMessage());
            Toast.makeText(this, "Произошла ошибка отправки", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static String createMessageFromTemplate(User user, String messageTemplate) {
        Map<String, Object> textParameters = new HashMap<>();
        textParameters.put("name", user.getName());
        StringSubstitutor substitutor = new StringSubstitutor(s ->
                Objects.requireNonNull(textParameters.getOrDefault(s, "")).toString());
        return substitutor.replace(messageTemplate);
    }

    private void loadUsers() {
        new Thread(() -> {
            List<User> users = appDB.userDao().getAllUsers();
            runOnUiThread(() -> userAdapter.setUsers(users));
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emailThread != null && emailThread.isAlive()) {
            emailThread.interrupt();
        }
        dismissProgressDialog();
    }

}
