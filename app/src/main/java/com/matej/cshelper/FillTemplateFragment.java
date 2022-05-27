package com.matej.cshelper;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.db.entities.BuildDraft;
import com.matej.cshelper.db.entities.FilledComponent;
import com.matej.cshelper.db.entities.TemplateItem;
import com.matej.cshelper.uihelpers.TemplateAdapter;

import java.util.concurrent.ExecutionException;

public class FillTemplateFragment extends Fragment {

    private static final String TAG = "FillTemplateFragment";
    private static final String ARG_TEMPLATE_ID = "templateId";

    private long templateID;
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
        }
        Log.i(TAG, "onCreate " + this.templateID);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        saveTemplate();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_build_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + this.templateID);
        setHasOptionsMenu(true);
        View parentView = inflater.inflate(R.layout.fragment_fill_template, container, false);
        RecyclerView recyclerView = parentView.findViewById(R.id.components_list);
        DBDataManager.ServerTemplate serverTemplate = ServerTemplates.getInstance().getBuildTemplate(this.templateID);
        this.adapter = new TemplateAdapter(serverTemplate);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getOrderID();
        getMissingComponents(serverTemplate,adapter);
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

    private void getOrderID(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter order number ");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String orderID = input.getText().toString();
                adapter.activeDraft.orderID = orderID;
            }
        });
        builder.show();
    }

    private void saveTemplate() {
        DBDataManager.getInstance().saveBuildReport(adapter.activeDraft.orderID, adapter.activeDraft.toString());
    }

}