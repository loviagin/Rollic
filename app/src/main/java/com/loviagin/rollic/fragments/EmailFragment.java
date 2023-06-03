package com.loviagin.rollic.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loviagin.rollic.adapters.AuthTabAdapter;
import com.loviagin.rollic.R;

public class EmailFragment extends Fragment {

    private AuthTabAdapter.OnAuthTabClickListener mListener;

    public EmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AuthTabAdapter.OnAuthTabClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_email, container, false);
        EditText editTextEmail = rootView.findViewById(R.id.etEmailAuth);
        EditText editTextPass = rootView.findViewById(R.id.etPassAuth);
        Button button = rootView.findViewById(R.id.bLogin);
        button.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String pass = editTextPass.getText().toString().trim();
            mListener.OnEmailLoginClick(email, pass);
        });
        return rootView;
    }

}