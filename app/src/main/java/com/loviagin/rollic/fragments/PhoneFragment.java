package com.loviagin.rollic.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.AuthTabAdapter;

public class PhoneFragment extends Fragment {

    private AuthTabAdapter.OnAuthTabClickListener mListener;

    public PhoneFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
        EditText editTextPhone = rootView.findViewById(R.id.etPhoneAuth);
        EditText editTextCode = rootView.findViewById(R.id.etCodeAuth);
        Button buttonSend = rootView.findViewById(R.id.bSendCodeAuth);
        Button buttonConfirm = rootView.findViewById(R.id.bConfirmCodeAuth);
        Button buttonResend = rootView.findViewById(R.id.bResendCodeAuth);
        TextView textView = rootView.findViewById(R.id.tvEnterPhoneNumber);

        buttonSend.setOnClickListener(v -> {
            if (isValidPhoneNumber(editTextPhone.getText().toString().trim())) {
                textView.setText(getResources().getString(R.string.enter_phone_str));
                textView.setTextColor(getResources().getColor(R.color.black));
                buttonSend.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.VISIBLE);
                buttonResend.setVisibility(View.VISIBLE);
                editTextCode.setVisibility(View.VISIBLE);
                mListener.OnPhoneSendClick(editTextPhone.getText().toString().trim());
            } else {
                textView.setText(R.string.enter_valid_phone_str);
                textView.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            if (editTextCode.getText().toString().trim().length() > 1){
                mListener.OnPhoneVerifyClick(editTextCode.getText().toString().trim());
            } else {
                textView.setText(R.string.enter_valid_phone_str);
                textView.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        buttonResend.setOnClickListener(v -> {
            if (editTextCode.getText().toString().trim().length() > 1){
                mListener.OnPhoneResendClick();
            } else {
                textView.setText(R.string.enter_valid_phone_str);
                textView.setTextColor(getResources().getColor(R.color.blue));
            }
        });
        return rootView;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }
}