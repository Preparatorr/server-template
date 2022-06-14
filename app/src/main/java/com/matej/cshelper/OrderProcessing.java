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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.matej.cshelper.db.entities.ServerOrder;

import java.util.ArrayList;

public class OrderProcessing extends Fragment {

    public static final String ARG_ORDER_ID = "orderId";
    private static final String TAG = "OrderProcessing";

    private ServerOrder order;
    private LinearLayout componentsList;

    public OrderProcessing() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_build_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.finish_template:
                finishOrder();
                return true;
            case R.id.upload_template:
                uploadOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String orderString = getArguments().getString(ARG_ORDER_ID);
            Gson gson = new Gson();
            order = gson.fromJson(orderString, ServerOrder.class);
            Log.i(TAG, "Order: " + order.orderSteps.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_order_processing, container, false);
        this.componentsList = view.findViewById(R.id.order_steps_container);
        TextView orderId = view.findViewById(R.id.order_order_id);
        orderId.setText("Order ID: " + order.orderId);
        TextView ticketId = view.findViewById(R.id.order_ticket_id);
        ticketId.setText("Ticket ID: " + order.ticketId);
        for (ServerOrder.OrderStep step : order.orderSteps) {
            View stepView = inflater.inflate(R.layout.check_step_item, componentsList, false);
            ((TextView)stepView.findViewById(R.id.step_name)).setText(step.name);
            CheckBox stepCheckbox = stepView.findViewById(R.id.step_done);
            stepCheckbox.setChecked(step.status == ServerOrder.StepStatus.DONE);
            Log.i("OrderProcessing", "Step: " + step.name + " " + step.status);
            stepCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    step.status = ServerOrder.StepStatus.DONE;
                } else {
                    step.status = ServerOrder.StepStatus.IGNORED;
                }
                saveOrder();
            });
            if(step.type == 1)
            {
                Button buildButton = stepView.findViewById(R.id.build_button);
                buildButton.setVisibility(View.VISIBLE);
                buildButton.setText("Start build");
                buildButton.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    
                    NavHostFragment.findNavController(this).navigate(R.id.ordersFragment);
                });
            }
            this.componentsList.addView(stepView);
        }
        return view;
    }

    private void saveOrder()
    {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("orders", getActivity().MODE_PRIVATE);
        String json = sharedPreferences.getString("activeOrders", "");

        OrdersFragment.ActiveOrdersWrap savedOrders = gson.fromJson(json, OrdersFragment.ActiveOrdersWrap.class);

        for (ServerOrder order : savedOrders.activeOrders) {
            Log.i(TAG, order.orderId + " " + order.ticketId);
            if(order.orderId.equals(this.order.orderId)) {
                order.orderSteps = new ArrayList<>();
                order.orderSteps.addAll(this.order.orderSteps);
                this.order = order;
                //save order
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Log.i(TAG + "saveOrders", gson.toJson(savedOrders));
                editor.putString("activeOrders", gson.toJson(savedOrders));

                editor.commit();
                break;
            }
        }
    }

    private void finishOrder() {
        saveOrder();
        NavHostFragment.findNavController(this).navigate(R.id.ordersFragment);
    }

    private void uploadOrder() {
        saveOrder();
        boolean allDone = true;
        StringBuilder message = new StringBuilder();
        message.append("Not done steps:\n");
        for(ServerOrder.OrderStep step : this.order.orderSteps) {
            if(step.status != ServerOrder.StepStatus.DONE && step.mandatory) {
                allDone = false;
                message.append("  - " + step.name + "\n");
            }
        }
        if(!allDone) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Some mandatory steps are not done");
            TextView view = new TextView(getContext());
            view.setPadding(50, 20, 50, 20);
            view.setText(message.toString());
            builder.setView(view);
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }
        NavHostFragment.findNavController(this).navigate(R.id.ordersFragment);
    }
}