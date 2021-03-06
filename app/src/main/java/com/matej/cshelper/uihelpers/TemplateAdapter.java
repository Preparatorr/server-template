package com.matej.cshelper.uihelpers;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matej.cshelper.R;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.BuildDraft;
import com.matej.cshelper.db.entities.FilledComponent;
import com.matej.cshelper.db.entities.TemplateItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private static final String TAG = "TemplateAdapter";
    private DBDataManager.ServerTemplate template;
    private ArrayList<TemplateItem> components;
    private HashMap<String,ArrayList<String>> config;
    private boolean savedDraft = false;

    public BuildDraft activeDraft;

    public class TemplateViewHolder extends RecyclerView.ViewHolder {
        private final TextView componentName;
        private final LinearLayout componentCheckList;

        public TemplateViewHolder(View itemView) {
            super(itemView);
            this.componentName = itemView.findViewById(R.id.component_name);
            this.componentCheckList = itemView.findViewById(R.id.component_checklist);
        }

        public TextView getComponentName(){
            return componentName;
        }
        public LinearLayout getComponentCheckList(){
            return componentCheckList;
        }
    }

    public TemplateAdapter(DBDataManager.ServerTemplate template) {
        this.components = new ArrayList<>();
        this.activeDraft = new BuildDraft(template.id);
        for (TemplateItem item : template.server_build) {
            for (int i = 1; i <= item.count; i++) {
                this.components.add(item.Clone());
                if(item.count > 1){
                    String newName = item.displayName + " " + i;
                    this.components.get(this.components.size()-1).displayName = newName;
                }
                Log.d(TAG, this.components.get(this.components.size()-1).displayName);
            }
        }
        this.template = template;
        config = ServerTemplates.getInstance().getGlobalComponents().components_config;
        this.savedDraft = false;
    }

    public TemplateAdapter(BuildDraft savedDraft) {
        this.components = new ArrayList<>();
        this.activeDraft = savedDraft;
        this.template = ServerTemplates.getInstance().getBuildTemplate(savedDraft.templateID);
        this.config = ServerTemplates.getInstance().getGlobalComponents().components_config;
        this.savedDraft = true;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_template_item, parent, false);
        Log.i(TAG, "onCreateViewHolder: " + components.size());
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        TemplateItem item = new TemplateItem();
        if(savedDraft){
            item.name = activeDraft.components.get(position).name;
            item.displayName = activeDraft.components.get(position).displayName;
        }
        else{
            item = components.get(position);
        }

        holder.getComponentName().setText(item.displayName);
        if(!activeDraft.components.containsKey(position)){
            FilledComponent filledComponent = new FilledComponent(item.name, item.displayName, new HashMap<>());
            ArrayList<String> checkListItems = config.get(item.name);
            for (String checkListItem : checkListItems) {
                if(!filledComponent.fields.containsKey(checkListItem))
                    filledComponent.fields.put(checkListItem, false);
            }
            activeDraft.components.put(position,filledComponent);
        }
        Log.i(TAG, "onBindViewHolder: " + item.displayName);
        LinearLayout checkList = holder.getComponentCheckList();
        checkList.removeAllViews();
        ArrayList<String> checkListItems = config.get(item.name);
        for (String checkListItem : checkListItems) {
            CheckBox checkBox = new CheckBox(checkList.getContext());
            checkBox.setText(checkListItem);
            checkBox.setChecked(activeDraft.components.get(position).fields.get(checkListItem));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                activeDraft.components.get(position).fields.put(checkListItem, isChecked);
                    });
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[] {checkBox.getResources().getColor(R.color.main_color) }
            );
            checkBox.setButtonTintList(colorStateList);
            checkList.addView(checkBox);
        }
    }

    @Override
    public int getItemCount() {
        if(!savedDraft)
            return components.size();
        else
            return this.activeDraft.components.size();
    }

    public ArrayList<TemplateItem> getComponents(){
        return components;
    }
}
