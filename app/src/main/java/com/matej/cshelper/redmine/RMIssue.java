package com.matej.cshelper.redmine;

import java.util.ArrayList;

public class RMIssue {

    public class CustomField {
        public String id;
        public String name;
        public String value;

        @Override
        public String toString() {
            return "CustomField{" + "id=" + id + ", name=" + name + ", value=" + value + '}';
        }
    }

    public static class OrderItem{
        public String name;
        public int count;

        public OrderItem(String name, int count){
            this.name = name;
            this.count = count;
        }

        @Override
        public String toString() {
            return "OrderItem{" + "name=" + name + ", count=" + count + '}';
        }

    }

    public String id;
    public String i6id;
    public String description;
    public ArrayList<OrderItem> orderItems;
    public ArrayList<CustomField> custom_fields;

    @Override
    public String toString()
    {
        return "RMIssue{" +
                "id='" + id + '\'' +
                ", i6id='" + i6id + '\'' +
                ", description='" + description + '\'' +
                ", custom_fields=" + custom_fields +
                '}';
    }

    public RMIssue Clone(){
        RMIssue clone = new RMIssue();
        clone.id = id;
        clone.i6id = i6id;
        clone.description = description;
        clone.orderItems = orderItems;
        clone.custom_fields = custom_fields;
        return clone;
    }
}
