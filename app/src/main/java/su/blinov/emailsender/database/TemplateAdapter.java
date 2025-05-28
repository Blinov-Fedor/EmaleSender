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
import su.blinov.emailsender.model.Template;
import su.blinov.emailsender.activity.EditTemplateActivity;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {
    private List<Template> templates;
    private final Context context;
    private final AppDatabase db;

    public TemplateAdapter(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Template template = templates.get(position);
        holder.tvTemplateMessage.setText(template.getMessage());
        holder.tvTemplateSex.setText(template.getSex());

        holder.btnTemplateDelete.setOnClickListener(v -> {
            new Thread(() -> {
                db.templateDao().deleteTemplate(template);
                refreshData();
            }).start();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTemplateActivity.class);
            intent.putExtra("message", template.getMessage());
            context.startActivity(intent);
        });
    }

    private void refreshData() {
        new Thread(() -> {
            List<Template> newTemplates = db.templateDao().getAllTemplates();
            ((Activity) context).runOnUiThread(() -> setTemplates(newTemplates));
        }).start();
    }

    @Override
    public int getItemCount() { return templates == null ? 0 : templates.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemplateMessage, tvTemplateSex;
        Button btnTemplateDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTemplateMessage = itemView.findViewById(R.id.tvTemplateMessage);
            tvTemplateSex = itemView.findViewById(R.id.tvTemplateSex);
            btnTemplateDelete = itemView.findViewById(R.id.btnTemplateDelete);
        }
    }
}