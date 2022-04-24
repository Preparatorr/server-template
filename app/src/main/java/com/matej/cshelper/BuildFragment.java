package com.matej.cshelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

public class BuildFragment extends Fragment {

    private View parentView;
    public BuildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        parentView = inflater.inflate(R.layout.fragment_build, container, false);
        return parentView;
    }


}