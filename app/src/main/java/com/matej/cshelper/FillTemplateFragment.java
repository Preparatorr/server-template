package com.matej.cshelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FillTemplateFragment extends Fragment {

    private static final String TAG = "FillTemplateFragment";
    private static final String ARG_TEMPLATE_ID = "templateId";

    private long templateID;

    public FillTemplateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.templateID = getArguments().getLong(ARG_TEMPLATE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + this.templateID);

        return inflater.inflate(R.layout.fragment_fill_template, container, false);
    }
}