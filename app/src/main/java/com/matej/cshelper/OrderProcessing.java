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

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.matej.cshelper.db.entities.ServerOrder;
import com.matej.cshelper.redmine.RMIssue;
import com.matej.cshelper.redmine.RedmineServices;

import java.util.ArrayList;
import java.util.Collections;

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
            this.order = gson.fromJson(orderString, ServerOrder.class);
            Log.i(TAG, "Order: " + order.orderSteps.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        setHasOptionsMenu(true);

        RMIssue issue =  RedmineServices.getInstance().getIssue(order.ticketId);
        if(issue == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Wrong ticket number or no connection to Redmine");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NavHostFragment.findNavController(getParentFragment()).navigate(R.id.ordersFragment);
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        order.redmineIssue = issue.Clone();
        order.redmineIssue.orderItems = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();
        Collections.addAll(lines, issue.description.split("\r\n"));
        lines.remove(0);
        Log.i(TAG, "Lines: " + issue.description);
        order.redmineIssue.description = "";
        for (String line : lines) {
            String[] parts = line.split("\\[.*\\]");
            if (parts.length == 2) {
                int quantity = Integer.parseInt(parts[0].split("x")[0].replaceAll("\\s+","") );
                order.redmineIssue.orderItems.add(new RMIssue.OrderItem(parts[1], quantity));
            }
            else{
                order.redmineIssue.description += line + "\n";
            }
        }



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
            if(step.type == 1) {
                Button buildButton = stepView.findViewById(R.id.build_button);
                buildButton.setVisibility(View.VISIBLE);
                buildButton.setText("Start");
                buildButton.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong(FillTemplateFragment.ARG_SAVED_TEMPLATE, 0);
                    args.putString(FillTemplateFragment.ARG_SAVED_TEMPLATE, this.order.orderId);
                    NavHostFragment.findNavController(this).navigate(R.id.fillTemplateFragment, args);
                });
            }
            if(step.type == 2) {
                TextView items = stepView.findViewById(R.id.items);
                items.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                for (RMIssue.OrderItem item : this.order.redmineIssue.orderItems) {
                    sb.append(" - " + item.count + "x" + item.name + "\n");
                }
                if(order.redmineIssue.description.length() > 0)
                    sb.append("\n" + order.redmineIssue.description);
                items.setText(sb.toString());
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