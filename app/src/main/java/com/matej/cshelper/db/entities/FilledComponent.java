package com.matej.cshelper.db.entities;

import java.util.HashMap;

public class FilledComponent {
    public String name;
    public String displayName;
    public HashMap<String,Boolean> fields;

    public FilledComponent(String name, String displayName, HashMap<String,Boolean> fields) {
        this.name = name;
        this.displayName = displayName;
        this.fields = fields;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String key : fields.keySet()) {
            if(fields.get(key)) {
                sb.append("\n    name: " + key + ": OK");
            }
            else{
                sb.append("\n    name: " + key + ": NOT CHECKED");
            }
        }
        return sb.toString();
    }

}
