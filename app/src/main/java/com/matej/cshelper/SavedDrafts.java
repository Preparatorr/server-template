package com.matej.cshelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.BuildDraft;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SavedDrafts extends Fragment {

    private static final String TAG = "SavedDrafts";

    private LinearLayout activeOrdersLayout;
    private LinearLayout finishedOrdersLayout;
    private HashMap<String, BuildDraft> ordersAll;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ordersAll = new HashMap<>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("drafts", Context.MODE_PRIVATE);
        HashMap<String, String> orders = (HashMap<String,String>)sharedPreferences.getAll();
        Gson gson = new Gson();
        for(Map.Entry<String, String> entry : orders.entrySet()) {
            this.ordersAll.put(entry.getKey(), gson.fromJson(entry.getValue(), BuildDraft.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_drafts, container, false);
        this.activeOrdersLayout = view.findViewById(R.id.active_orders_container);
        this.finishedOrdersLayout = view.findViewById(R.id.finished_orders_container);
        for(Map.Entry<String, BuildDraft> entry : ordersAll.entrySet()) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(24);
            textView.setText(entry.getKey() + " - " + ServerTemplates.getInstance().getBuildTemplate(entry.getValue().templateID).displayName);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(FillTemplateFragment.ARG_TEMPLATE_ID, entry.getValue().templateID);
                    bundle.putString(FillTemplateFragment.ARG_SAVED_TEMPLATE, entry.getValue().orderID);
                    Navigation.findNavController(v).navigate(R.id.fillTemplateFragment, bundle);

                    Toast.makeText(v.getContext(), entry.getValue().orderID, Toast.LENGTH_SHORT).show();
                }
            });
            if(entry.getValue().finished){
                this.finishedOrdersLayout.addView(textView);
            }else {
                this.activeOrdersLayout.addView(textView);
            }
        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.menu_saved));
        return view;
    }
}