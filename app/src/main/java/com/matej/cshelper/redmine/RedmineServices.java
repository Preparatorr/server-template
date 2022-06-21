package com.matej.cshelper.redmine;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.matej.cshelper.MainActivity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class RedmineServices {

    private static final String TAG = "RedmineServices";
    private static RedmineServices instance = null;
    private String apiKey;

    public static RedmineServices getInstance() {
        if (instance == null) {
            instance = new RedmineServices();
        }
        return instance;
    }

    class NetworkThread extends Thread{
        public String responseString = "";
        public String id;

        public NetworkThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            String url = "https://clon-helpdesk.czech-server.cz/issues/" + id + ".json";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            responseString = null;
            try {
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Init()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference globalConfigDoc = db.collection("configs").document("keys");
        globalConfigDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        apiKey = document.getString("rm-api");
                        Log.d(TAG, "Api key: " + apiKey);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public RMIssue getIssue(String id) {

        /*NetworkThread thread = new NetworkThread(id);
        thread.start();
        try {
            thread.join(4000);
            Log.i(TAG, "Response: " + thread.responseString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        try{
            String response = "{ \"issue\": { \"id\": 19246, \"project\": { \"id\": 14, \"name\": \"Helpdesk\" }, \"tracker\": { \"id\": 13, \"name\": \"Montáž\" }, \"status\": { \"id\": 35, \"name\": \"Recenze žádost\" }, \"priority\": { \"id\": 2, \"name\": \"Normální\" }, \"author\": { \"id\": 34, \"name\": \"UserForImportXContacts I6toRM\" }, \"assigned_to\": { \"id\": 21, \"name\": \"Obchodni oddeleni\" }, \"subject\": \"Objednávka: 226210970\", \"description\": \"Položky: \\r\\n  1x - [192266] Dell PowerEdge R430 | 4 disky 3.5\\\" a 2 disky 2.5\\\" | 4* 1Gb Ethernet\\r\\n  1x - [191621] 2* 6- Core CPU Intel Xeon E5-2620 v3 2.40 GHz 15 MB SmartCache # CPU PassMark: 15256, dotovaná cena\\r\\n  4x - [191453] 16 GB DDR4\\r\\n  1x - [191635] Dell Perc/H730 Mini Mono Raid Controller 12G 1 GB Cache s baterií, akční cena\\r\\n  1x - [191465] Dell iDRAC Enterprise Licence\\r\\n  1x - [192267] 1x DELL zdroj 550 W\\r\\n  1x - [191987] Bez ližin\\r\\n  2x - [159239] Kingston 960GB SSD Data Centre DC500M (Mixed Use) Enterprise nový\\r\\n  4x - [191088] Adaptér disku 2.5\\\" na 3.5\\\" SATA/SAS/SSD\\r\\n  4x - [191001] Dell rámeček 3.5\\\" R/T x10 x20 x30 x40\\r\\n  1x - [180017] Bez housingu\\r\\n  1x - [700002] Dopravné\\r\\n----------\\r\\nProsím o stresstest\", \"start_date\": null, \"due_date\": null, \"done_ratio\": 0, \"is_private\": false, \"estimated_hours\": null, \"total_estimated_hours\": null, \"spent_hours\": 0.0, \"total_spent_hours\": 0.0, \"custom_fields\": [ { \"id\": 17, \"name\": \"Objednávka I6\", \"value\": \"226210970\" }, { \"id\": 19, \"name\": \"Stresstest\", \"value\": \"0\" }, { \"id\": 20, \"name\": \"Hodnocení výroby\", \"value\": \"\" }, { \"id\": 21, \"name\": \"Hodnocení obchodní\", \"value\": \"\" }, { \"id\": 22, \"name\": \"Sériová čísla\", \"value\": \"Server: HJC8SP2 Case: CN04H652FCT00837003XA00 MB: CN0CN7X8FCP0084N0049A09 Riser1: CN03G69KFCT0083100IXA01 Raid: CN0KMCCDFCP0083802ZAA09 Battery: JP037CT1PAS00823F0L9A01 PSU1: CN06V43GDED0082J0QDWA00 iDRAC: CN0X99HCFCP0084H00M6A00 ; RAM  HMA42GR7MFR4N-TF T1 AB 525: 80AD0115252433C8F4, 80AD0115252433C90C, 80AD0115252433C94B, 80AD0115252433CAA8; HDD  DC500M960GB: 50026B7282B0BDD1, 50026B7282BED6FA\" }, { \"id\": 32, \"name\": \"NBD\", \"value\": \"0\" }, { \"id\": 33, \"name\": \"Cena bez DPH\", \"value\": \"36229.27\" }, { \"id\": 34, \"name\": \"Objednávka\", \"value\": \"https://helpdesk.czech-server.cz/orders/10478\" }, { \"id\": 41, \"name\": \"Způsob platby\", \"value\": \"Zálohová faktura\" }, { \"id\": 42, \"name\": \"Zaplacená záloha\", \"value\": \"0.0\" }, { \"id\": 43, \"name\": \"Způsob dopravy\", \"value\": \"TopTrans\" }, { \"id\": 44, \"name\": \"Expedováno\", \"value\": \"1\" }, { \"id\": 45, \"name\": \"Sledování zásilky\", \"value\": \"\" }, { \"id\": 46, \"name\": \"Server\", \"value\": \"\" }, { \"id\": 52, \"name\": \"Společnost\", \"value\": \"JS-IT s.r.o.\" }, { \"id\": 54, \"name\": \"Interní poznámka\", \"value\": \"\\r\\nPotvrzovací mail z eShopu: Odeslán 18.5.2022 12:06:48\" }, { \"id\": 55, \"name\": \"Externí poznámka\", \"value\": \"Prosím o stresstest\" }, { \"id\": 57, \"name\": \"Nekompletní položky\", \"value\": \"\" }, { \"id\": 59, \"name\": \"Potvrzeno\", \"value\": \"Ne\" }, { \"id\": 60, \"name\": \"Fakturační adresa\", \"value\": \"Revoluční 1836/20\\r\\n41201\\r\\nLitoměřice\" }, { \"id\": 61, \"name\": \"Dodací adresa\", \"value\": \"\" }, { \"id\": 63, \"name\": \"VPN link\", \"value\": \"0\" } ], \"created_on\": \"2022-05-18T10:07:21Z\", \"updated_on\": \"2022-06-13T11:18:15Z\", \"closed_on\": \"2022-05-25T15:34:57Z\" } }";
            Gson gson = new Gson();
            HashMap<String,RMIssue> map = gson.fromJson(response, new TypeToken<HashMap<String,RMIssue>>(){}.getType());
            RMIssue issue = map.get("issue");
            for (RMIssue.CustomField field : issue.custom_fields)
            {
                if(field.id.equals("17"))
                    issue.i6id = field.value;
            }
            return issue;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error while parsing response", e);
            return null;
        }


        //ArrayList<String> lines = new ArrayList<>();
        //Collections.addAll(lines, issue.description.split("\r\n"));
        //lines.remove(0);
        //Log.i(TAG, "Issue: " + issue.toString());
    }
}
