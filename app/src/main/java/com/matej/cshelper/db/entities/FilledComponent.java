package com.matej.cshelper.db.entities;

import java.util.HashMap;

public class FilledComponent {
    public String name;
    public HashMap<String,Boolean> fields;

    public FilledComponent(String name, HashMap<String,Boolean> fields) {
        this.name = name;
        this.fields = fields;
    }

}
