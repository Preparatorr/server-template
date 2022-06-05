package com.matej.cshelper.db.entities;

import java.util.ArrayList;

public class ServerOrder {

    public enum StepStatus {
        DONE,
        NOT_STARTED,
        IGNORED
    }

    public class OrderStep {
        public String name;
        public StepStatus status;
        public int type;
        public boolean mandatory;

        public OrderStep(String name, StepStatus status, int type, boolean mandatory) {
            this.name = name;
            this.status = status;
            this.type = type;
            this.mandatory = mandatory;
        }

        public OrderStep Clone(){
            return new OrderStep(name, status, type, mandatory);
        }
    }

    public String orderId;
    public String ticketId;
    public boolean finished;
    public ArrayList<OrderStep> orderSteps;


    public ServerOrder(String orderId, String ticketId, boolean finished, ArrayList<OrderStep> steps) {
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.finished = finished;
        this.orderSteps = steps;
    }

    public ServerOrder() {
        orderSteps = new ArrayList<>();
    }

    public ServerOrder Clone()
    {
        ServerOrder clone = new ServerOrder();
        clone.orderId = orderId;
        clone.ticketId = ticketId;
        clone.finished = finished;
        clone.orderSteps = new ArrayList<>();
        for(OrderStep step : orderSteps)
        {
            clone.orderSteps.add(new OrderStep(step.name, step.status, step.type, step.mandatory));
        }
        return clone;
    }
}
