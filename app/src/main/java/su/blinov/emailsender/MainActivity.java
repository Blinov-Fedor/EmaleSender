package su.blinov.emailsender;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private UserAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "users-db").build();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new UserAdapter(this, db);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, EditActivity.class)));

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            loadUsers();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void loadUsers() {
        new Thread(() -> {
            List<User> users = db.userDao().getAllUsers();
            runOnUiThread(() -> adapter.setUsers(users));
        }).start();
    }

}
