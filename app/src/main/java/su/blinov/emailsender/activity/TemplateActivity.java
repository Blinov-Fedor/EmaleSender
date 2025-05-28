package su.blinov.emailsender.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import su.blinov.emailsender.R;
import su.blinov.emailsender.database.AppDatabase;
import su.blinov.emailsender.database.TemplateAdapter;
import su.blinov.emailsender.model.Template;

public class TemplateActivity extends AppCompatActivity {

    private AppDatabase appDB;
    private TemplateAdapter templateAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        loadTemplates();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        appDB = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db").build();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewActivityTemplate);
        templateAdapter = new TemplateAdapter(this, appDB);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(templateAdapter);

        findViewById(R.id.fabAddActivityTemplate).setOnClickListener(v ->
                startActivity(new Intent(this, EditTemplateActivity.class)));

        findViewById(R.id.fabRevertActivityTemplate).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefreshActivityTemplate);
        swipeRefresh.setOnRefreshListener(() -> {
            loadTemplates();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void loadTemplates() {
        new Thread(() -> {
            List<Template> templates = appDB.templateDao().getAllTemplates();
            runOnUiThread(() -> templateAdapter.setTemplates(templates));
        }).start();
    }

}
