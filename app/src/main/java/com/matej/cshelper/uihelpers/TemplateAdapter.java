package com.matej.cshelper.uihelpers;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matej.cshelper.db.DBDataManager;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {

    DBDataManager.ServerTemplate template;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ViewGroup parent) {
            super(parent);
        }
    }

    @NonNull
    @Override
    public TemplateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
