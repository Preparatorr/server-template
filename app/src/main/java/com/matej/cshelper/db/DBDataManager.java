package com.matej.cshelper.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.matej.cshelper.db.entities.ServerOrder;
import com.matej.cshelper.db.entities.TemplateItem;

import java.util.ArrayList;
import java.util.HashMap;

public class DBDataManager {

    public class GlobalComponentsConfig
    {
        public HashMap<String, ArrayList<String>> components_config;
    }

    public class ServerTemplate
    {
        public long id;
        public String displayName;
        public ArrayList<TemplateItem> server_build;
    }

    private static final String TAG = "DBDataManager";
    private static DBDataManager instance = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean downloaded = false;

    public boolean isInitialized() {
        return downloaded;
    }

    public static DBDataManager getInstance() {
        if (instance == null) {
            instance = new DBDataManager();
        }
        return instance;
    }

    public void Init() {
        DocumentReference globalConfigDoc = db.collection("configs").document("global_config");
        globalConfigDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String config = document.getString("file");
                        saveGlobalConfig(config);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        CollectionReference templatesDoc = db.collection("server-templates");
        templatesDoc.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.i(TAG, document.getId() + " => " + document.getData());
                                saveBuildTemplate(document.getData().get("file").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        saveOrderSteps();

    }

    public void saveBuildReport(String ID, String report) {
        HashMap<String, String> entry = new HashMap<>();
        entry.put("report", report);
        db.collection("build-reports").document(ID).set(entry);
    }

    private void saveGlobalConfig(String config){
        Log.i(TAG, "Downloaded config: " + config);
        Gson gson = new Gson();
        GlobalComponentsConfig globalComponentsConfig = gson.fromJson(config, GlobalComponentsConfig.class);
        ServerTemplates.getInstance().setGlobalComponents(globalComponentsConfig);
        downloaded = true;
    }

    private void saveBuildTemplate(String templateString) {
        Log.i(TAG, "Downloaded templates: " + templateString);
        Gson gson = new Gson();
        ServerTemplate template = gson.fromJson(templateString, ServerTemplate.class);
        ServerTemplates.getInstance().addBuildTemplate(template);
    }

    private void saveOrderSteps() {
        DocumentReference globalConfigDoc = db.collection("configs").document("order_steps");
        globalConfigDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String steps = document.getString("file");
                        Gson gson = new Gson();
                        ServerOrder order = gson.fromJson(steps, ServerOrder.class);
                        ServerTemplates.getInstance().setOrderSteps(order.orderSteps);
                        Log.i(TAG, "Downloaded order steps: " + steps);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
