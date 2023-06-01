package com.loviagin.rollic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.loviagin.rollic.fragments.PhotoAddFragment;
import com.loviagin.rollic.fragments.PostAddFragment;

public class AddPostTabAdapter extends FragmentStateAdapter {

    public AddPostTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new PhotoAddFragment();
        }else {
            return new PostAddFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
