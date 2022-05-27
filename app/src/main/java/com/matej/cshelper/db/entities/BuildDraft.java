package com.matej.cshelper.db.entities;

import java.util.HashMap;

public class BuildDraft {
    public String orderID;
    public String name;
    boolean finished = false;
    public HashMap<Integer,FilledComponent> components;

    public BuildDraft() {
        components = new HashMap<>();
    }
    public BuildDraft(String id, String name) {
        this.orderID = id;
        this.name = name;
        components = new HashMap<>();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(orderID).append(";");
        sb.append(name).append(";");
        sb.append(finished).append(";");
        for(Integer key : components.keySet()){
            sb.append(key).append(";");
        }
        return sb.toString();
    }
}
