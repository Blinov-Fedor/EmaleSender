package su.blinov.emailsender.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import su.blinov.emailsender.MailSender;
import su.blinov.emailsender.R;
import su.blinov.emailsender.database.AppDatabase;
import su.blinov.emailsender.database.UserAdapter;
import su.blinov.emailsender.model.User;

public class MainActivity extends AppCompatActivity {
    public static final int MIN_TIME_INTERVAL = 500;
    public static final int MAX_TIME_INTERVAL = 2000;
    private AppDatabase appDB;
    private UserAdapter userAdapter;

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

        buttonSend.setOnClickListener(v -> {
            new Thread(() -> {
                List<User> users = appDB.userDao().getAllUsers();
                users.stream()
                        .peek(i -> {
                            try {
                                int pause = new Random().nextInt(MAX_TIME_INTERVAL - MIN_TIME_INTERVAL + 1) + MIN_TIME_INTERVAL;
                                Thread.sleep(pause);
                                Log.i("PAUSE", ">>> Pause for  " + pause + " milliseconds");
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        })
                        .forEach(user -> {
                            MailSender mailSender = new MailSender();
                            String messageTemplate =
                                    Objects.requireNonNull(appDB.templateDao().getAllTemplates().stream()
                                            .filter(u -> u.getSex().equals(user.getSex()))
                                            .findFirst()
                                            .orElse(null)).getMessage();

                            String replacedText = createMessageFromTemplate(user, messageTemplate);

                            mailSender.sendEmail(user.getEmail(), replacedText);
                            Log.i("SENDING", ">>> Sending mail to email: " + user.getEmail());
                        });
            }).start();
        });

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            loadUsers();
            swipeRefresh.setRefreshing(false);
        });
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

}
