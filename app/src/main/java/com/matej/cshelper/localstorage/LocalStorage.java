package com.matej.cshelper.localstorage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.matej.cshelper.OrdersFragment;
import com.matej.cshelper.db.entities.ServerOrder;

import java.util.ArrayList;

//singleton class for local storage
public class LocalStorage {

    public static final String TAG = "LocalStorage";

    private static LocalStorage instance;

    private ArrayList<ServerOrder> activeOrders;
    private Activity activity;

    private LocalStorage(){
        activeOrders = new ArrayList<>();
    }

    public static LocalStorage getInstance(){
        if(instance == null){
            instance = new LocalStorage();
        }
        return instance;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public ArrayList<ServerOrder> getActiveOrders(){
        return activeOrders;
    }

    public void setActiveOrders(ArrayList<ServerOrder> activeOrders){
        this.activeOrders = activeOrders;
    }


    private void saveActiveOrders(){

        SharedPreferences sharedPreferences = this.activity.getSharedPreferences("orders", this.activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        OrdersFragment.ActiveOrdersWrap activeOrdersWrap = new OrdersFragment.ActiveOrdersWrap();
        activeOrdersWrap.activeOrders = activeOrders;
        String json = gson.toJson(activeOrdersWrap);
        Log.i(TAG, "saveOrders: " + activeOrders.size());
        editor.putString("activeOrders", json);
        editor.commit();
    }

    private void loadActiveOrders(){
        SharedPreferences sharedPreferences = this.activity.getSharedPreferences("orders", this.activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("activeOrders", "");
        Log.i(TAG, json);

        OrdersFragment.ActiveOrdersWrap savedOrders = gson.fromJson(json, OrdersFragment.ActiveOrdersWrap.class);
        if(savedOrders == null)
            activeOrders = new ArrayList<>();
        else
            activeOrders = savedOrders.activeOrders;

        //activeOrders = new ArrayList<>(Arrays.asList(savedOrders));
        Log.i(TAG, "loadOrders size : " + activeOrders.size());

        for (ServerOrder order : activeOrders){
            Log.i(TAG, "koko: "+ order.ticketId);
        }
    }

}
