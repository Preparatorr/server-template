package com.matej.cshelper;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.db.ServerTemplates;
import com.matej.cshelper.uihelpers.TemplatesAdapter;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuildFragment extends Fragment {

    private static final String TAG = "BuildFragment";
    private View parentView;

    public BuildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_build, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_build));
        RecyclerView recyclerView = parentView.findViewById(R.id.templates_view_group);
        recyclerView.setAdapter(new TemplatesAdapter(ServerTemplates.getInstance().getBuildTemplates()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        return parentView;
    }


}