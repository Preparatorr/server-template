package com.matej.cshelper.db.entities;

import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;

import java.util.HashMap;

public class BuildDraft {
    public String orderID;
    public long templateID;
    boolean finished = false;
    public HashMap<Integer,FilledComponent> components;

    public BuildDraft() {
        components = new HashMap<>();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Order id: ").append(orderID).append("\n");
        sb.append("Template name: ").append(ServerTemplates.getInstance().getBuildTemplate(templateID).displayName).append("\n");
        sb.append("Components: \n");
        for(Integer key : components.keySet()){
            sb.append("  ").append(components.get(key).name).append(": ").append(components.get(key).toString()).append("\n");
        }
        return sb.toString();
    }
}
