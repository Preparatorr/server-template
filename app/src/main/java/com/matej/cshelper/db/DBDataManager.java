package com.matej.cshelper.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBDataManager {

    private static final String TAG = "DBDataManager";
    private static DBDataManager instance = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean downloaded = false;
    private Map<String, ArrayList<String>> globalComponents = new HashMap<>();

    public boolean isInitialized() {
        return downloaded;
    }

    public static DBDataManager getInstance() {
        if (instance == null) {
            instance = new DBDataManager();
        }
        return instance;
    }

    public Map<String, ArrayList<String>> getGlobalComponents(){
        if (downloaded)
            return globalComponents;
        else
            return null;
    }

    public void Init() {
        DocumentReference docRef = db.collection("configs").document("global_config");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
    }

    private void saveGlobalConfig(String config){
        Map<String, Object> result = new Gson().fromJson(
                config, new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        String componentsConfig = result.get("components_config").toString();
        Log.i(TAG, "CPU = : " + (result.get("components_config")).get("cpu"));
        Log.i(TAG, "saveGlobalConfig: " + componentsConfig);


    }
}
