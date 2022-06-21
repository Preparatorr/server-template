package com.matej.cshelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.BuildDraft;
import com.matej.cshelper.db.entities.FilledComponent;
import com.matej.cshelper.db.entities.TemplateItem;
import com.matej.cshelper.uihelpers.TemplateAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FillTemplateFragment extends Fragment {

    private static final String TAG = "FillTemplateFragment";
    public static final String ARG_TEMPLATE_ID = "templateId";
    public static final String ARG_SAVED_TEMPLATE = "SavedTemplate";

    private long templateID;
    private boolean savedTemplate;
    private String orderID;
    private TemplateAdapter adapter;
    private RecyclerView componentsList;

    public FillTemplateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.templateID = getArguments().getLong(ARG_TEMPLATE_ID);
            this.orderID = getArguments().getString(ARG_SAVED_TEMPLATE, null);
            if(this.orderID == null) {
                throw new IllegalArgumentException("Order ID is null");
            }

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("drafts", Context.MODE_PRIVATE);
            HashMap<String, String> orders = (HashMap<String,String>)sharedPreferences.getAll();
            Gson gson = new Gson();
            for(Map.Entry<String, String> entry : orders.entrySet()) {
                if(entry.getKey().equals(orderID)) {
                    this.savedTemplate = true;
                    this.orderID = entry.getKey();
                    BuildDraft order = gson.fromJson(sharedPreferences.getString(orderID, ""), BuildDraft.class);
                    this.adapter = new TemplateAdapter(order);
                    ((MainActivity) getActivity()).setActionBarTitle(orderID);
                }
            }
        }
        Log.i(TAG, "onCreate " + this.templateID);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fill_template_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_template:
                saveTemplate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + this.templateID);
        setHasOptionsMenu(true);
        View parentView = inflater.inflate(R.layout.fragment_fill_template, container, false);

        if(!savedTemplate) {
            DBDataManager.ServerTemplate serverTemplate = ServerTemplates.getInstance().getBuildTemplate(this.templateID);
            this.adapter = new TemplateAdapter(serverTemplate);
            getMissingComponents(serverTemplate,adapter);
            adapter.activeDraft.orderID = this.orderID;
        }
        RecyclerView recyclerView = parentView.findViewById(R.id.components_list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        componentsList = recyclerView;
        Log.i(TAG, "onCreateView " + this.templateID + " finished");
        return parentView;
    }

    private void getMissingComponents(DBDataManager.ServerTemplate serverTemplate, TemplateAdapter adapter) {
        for (TemplateItem component : serverTemplate.server_build) {
            if (component.count == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter number of " + component.displayName + "s");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int componentCount = Integer.parseInt(input.getText().toString());
                        for (int i = 0; i < componentCount; i++) {
                            String newName = component.displayName + " " + (i + 1);
                            adapter.getComponents().add(new TemplateItem(component.name, newName, 1));
                        }
                        componentsList.setAdapter(adapter);
                    }
                });
                builder.show();
            }
        }
    }

    private void saveTemplate() {
        adapter.activeDraft.templateID = this.templateID;
        Log.i(TAG, "saveTemplate: " + adapter.activeDraft.toString());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("drafts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(adapter.activeDraft.orderID, gson.toJson(adapter.activeDraft));
        Log.i(TAG, "json: " + gson.toJson(adapter.activeDraft));
        editor.commit();
        Toast.makeText(getContext(), adapter.activeDraft.orderID + " Saved", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStackImmediate();
    }


}