package com.matej.cshelper.db;


import com.matej.cshelper.db.entities.ServerOrder;

import java.lang.reflect.Array;
import java.util.ArrayList;

//Singleton class for storing server templates
public class ServerTemplates {

    private static ServerTemplates instance;

    private DBDataManager.GlobalComponentsConfig globalComponents = null;

    private ArrayList<DBDataManager.ServerTemplate> templateItems = null;

    private ArrayList<ServerOrder.OrderStep> orderSteps = null;

    private ServerTemplates() {

    }
    public static ServerTemplates getInstance() {
        if (instance == null) {
            instance = new ServerTemplates();
        }
        return instance;
    }

    protected void setGlobalComponents(DBDataManager.GlobalComponentsConfig globalComponents) {
        this.globalComponents = globalComponents;
    }

    public DBDataManager.GlobalComponentsConfig getGlobalComponents(){
            return globalComponents;
    }

    public void addBuildTemplate(DBDataManager.ServerTemplate serverTemplate) {
        if (templateItems == null) {
            templateItems = new ArrayList<>();
        }
        templateItems.add(serverTemplate);
    }

    public DBDataManager.ServerTemplate getBuildTemplate(long index) {
        if (templateItems == null) {
            return null;
        }
        return templateItems.get((int)index);
    }

    public ArrayList<DBDataManager.ServerTemplate> getBuildTemplates() {
        return templateItems;
    }

    public ArrayList<ServerOrder.OrderStep> getOrderSteps() {
        return this.orderSteps;
    }

    public void setOrderSteps(ArrayList<ServerOrder.OrderStep> orderSteps) {
        this.orderSteps = orderSteps;
    }
}
