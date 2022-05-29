package com.matej.cshelper.db.entities;

import java.util.HashMap;

public class FilledComponent {
    public String name;
    public HashMap<String,Boolean> fields;

    public FilledComponent(String name, HashMap<String,Boolean> fields) {
        this.name = name;
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
