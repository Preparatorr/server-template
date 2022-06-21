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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.ServerOrder;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private static final String TAG = "OrdersFragment";

    public OrdersFragment() {
        // Required empty public constructor
    }
    public static class ActiveOrdersWrap{
        public ArrayList<ServerOrder> activeOrders;
        public ActiveOrdersWrap(ArrayList<ServerOrder> activeOrders){
            this.activeOrders = activeOrders;
        }
        public ActiveOrdersWrap(){
            this.activeOrders = new ArrayList<>();
        }
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
        LinearLayout list = view.findViewById(R.id.orders_container);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
        for(ServerOrder order : this.activeOrders){
            View activeOrder = inflater.inflate(R.layout.server_template_item, list, false);
            ((TextView)activeOrder.findViewById(R.id.component_name)).setText(order.ticketId + " " + order.orderId);
            activeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOrder(order);
                }
            });
            list.addView(activeOrder);
        }
        return view;
    }

    private void showOrder(ServerOrder order){
        Bundle args = new Bundle();
        args.putString(OrderProcessing.ARG_ORDER_ID, new Gson().toJson(order));
        NavHostFragment.findNavController(this).navigate(R.id.orderProcessing, args);
    }

    public void createOrder(){
        ServerOrder order = new ServerOrder();
        //getTicketID(order);
        for (ServerOrder.OrderStep step : ServerTemplates.getInstance().getOrderSteps()){
            order.orderSteps.add(step.Clone());
            Log.i(TAG, "createOrder: " + order.orderSteps.get(order.orderSteps.size()-1));
            //order.orderSteps.get(order.orderSteps.size()-1).mandatory = false;
        }
        activeOrders.add(order);
        getOrderID(order);

    }

    private void getOrderID(ServerOrder order){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter order number and ticket ID");
        View view = getLayoutInflater().inflate(R.layout.order_dialog, null);
        final EditText orderId = view.findViewById(R.id.edit_text_order_id);
        final EditText ticketId = view.findViewById(R.id.edit_text_ticket_id);;

        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String orderID = orderId.getText().toString();
                order.orderId = orderID;
                String ticketID = ticketId.getText().toString();
                order.ticketId = ticketID;
                saveOrders();
                Bundle args = new Bundle();
                args.putString(OrderProcessing.ARG_ORDER_ID, new Gson().toJson(order));

                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.orderProcessing, args);
                }
        });
        builder.show();
    }


    private void saveOrders(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("orders", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ActiveOrdersWrap activeOrdersWrap = new ActiveOrdersWrap();
        activeOrdersWrap.activeOrders = activeOrders;
        String json = gson.toJson(activeOrdersWrap);
        Log.i(TAG, "saveOrders: " + activeOrders.size());
        editor.putString("activeOrders", json);
        editor.commit();
    }

    private void loadOrders(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("orders", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("activeOrders", "");
        Log.i(TAG, json);

        ActiveOrdersWrap savedOrders = gson.fromJson(json, ActiveOrdersWrap.class);
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