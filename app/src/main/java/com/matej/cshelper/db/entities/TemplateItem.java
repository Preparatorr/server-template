package com.matej.cshelper.db.entities;

import com.matej.cshelper.db.DBDataManager;

public class TemplateItem {
    public String name;
    public String displayName;
    public int count;

    public TemplateItem(){
        this.name = "";
        this.displayName = "";
        this.count = 0;
    }
    public TemplateItem Clone()
    {
        return new TemplateItem(name, displayName, count);
    }
    public TemplateItem(String name, String displayName, int count)
    {
        this.name = name;
        this.displayName = displayName;
        this.count = count;
    }
}
