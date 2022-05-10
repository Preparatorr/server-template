package com.matej.cshelper.uihelpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matej.cshelper.R;
import com.matej.cshelper.db.DBDataManager;

import java.util.ArrayList;

public class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.TemplatesVierwHolder> {

    private ArrayList<DBDataManager.ServerTemplate> templates;

    public static class TemplatesVierwHolder extends RecyclerView.ViewHolder {
        private final TextView templateName;

        public TemplatesVierwHolder(View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.template_name);
        }

        public TextView getTemplateName() {
            return templateName;
        }
    }

    public TemplatesAdapter(ArrayList<DBDataManager.ServerTemplate> templates) {
        this.templates = templates;
    }

    @NonNull
    @Override
    public TemplatesVierwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new TemplatesVierwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplatesVierwHolder holder, int position) {
        holder.getTemplateName().setText(this.templates.get(position).displayName);
        holder.getTemplateName().setOnClickListener(v -> {
            Toast.makeText(v.getContext(), this.templates.get(position).displayName, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return this.templates.size();
    }

}
