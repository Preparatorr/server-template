package com.matej.cshelper.db.entities;

import java.util.ArrayList;

public class ServerOrder {

    public String orderId;
    public long ticketId;
    public ArrayList<Component> components;

    public ServerOrder() {
        components = new ArrayList<>();
    }

    public ServerOrder(String orderId, long ticketId, ArrayList<Component> components) {
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.components = components;
    }
}
