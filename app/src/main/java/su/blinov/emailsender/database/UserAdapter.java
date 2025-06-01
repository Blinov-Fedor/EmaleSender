package su.blinov.emailsender.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import su.blinov.emailsender.R;
import su.blinov.emailsender.model.User;
import su.blinov.emailsender.activity.EditActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private final Context context;
    private final AppDatabase db;

    public UserAdapter(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvSex.setText(user.getSex());

        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                db.userDao().deleteUser(user);
                refreshData();
            }).start();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra("email", user.getEmail());
            context.startActivity(intent);
        });
    }

    private void refreshData() {
        new Thread(() -> {
            List<User> newUsers = db.userDao().getAllUsers();
            ((Activity) context).runOnUiThread(() -> setUsers(newUsers));
        }).start();
    }

    @Override
    public int getItemCount() { return users == null ? 0 : users.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSex;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSex = itemView.findViewById(R.id.tvSex);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}