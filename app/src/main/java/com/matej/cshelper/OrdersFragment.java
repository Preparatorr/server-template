package com.matej.cshelper;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.ServerOrder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class OrdersFragment extends Fragment {

    private static final String TAG = "OrdersFragment";

    public OrdersFragment() {
        // Required empty public constructor
    }

    private ArrayList<ServerOrder> activeOrders;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadOrders();
        ((MainActivity) getActivity()).setActionBarTitle("Orders");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
        return view;
    }

    public void createOrder(){
        ServerOrder order = new ServerOrder();
        getOrderID(order);
        getTicketID(order);
        for (ServerOrder.OrderStep step : ServerTemplates.getInstance().getOrderSteps()){
            order.orderSteps.add(step.Clone());
            order.orderSteps.get(order.orderSteps.size()-1).mandatory = false;
        }
        activeOrders.add(order);
        NavHostFragment.findNavController(this).navigate(R.id.orderProcessing);
    }

    private void getOrderID(ServerOrder order){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter order number ");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String orderID = input.getText().toString();
                order.orderId = orderID;
                }
        });
        builder.show();
    }

    private void getTicketID(ServerOrder order){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter ticket number ");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String orderID = input.getText().toString();
                order.ticketId = orderID;
                saveOrders();
            }
        });
        builder.show();
    }

    private void saveOrders(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("orders", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activeOrders);
        Log.i(TAG, "saveOrders: " + activeOrders.size());
        editor.putString("activeOrders", json);
        editor.commit();
    }

    private void loadOrders(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("orders", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("activeOrders", "");
        Log.i(TAG, json);

        ServerOrder[] savedOrders = gson.fromJson(json, ServerOrder[].class);

        activeOrders = savedOrders == null ? new ArrayList<ServerOrder>() : new ArrayList<>(Arrays.asList(savedOrders));
        Log.i(TAG, "loadOrders: " + activeOrders.size());

        for (ServerOrder order : activeOrders){
            Log.i(TAG, "koko: ");
        }
    }
}