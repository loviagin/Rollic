package com.loviagin.rollic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.loviagin.rollic.fragments.EmailFragment;
import com.loviagin.rollic.fragments.PhoneFragment;

public class AuthTabAdapter extends FragmentStateAdapter {

    public interface OnAuthTabClickListener {
        void OnEmailLoginClick(String email, String pass);
        void OnPhoneSendClick(String phone);
        void OnPhoneVerifyClick(String code);
        void OnPhoneResendClick();
    }

    public AuthTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new EmailFragment();
        } else {
            return new PhoneFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
