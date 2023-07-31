package com.loviagin.rollic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.loviagin.rollic.fragments.PhotoAddFragment;
import com.loviagin.rollic.fragments.PostAddFragment;
import com.loviagin.rollic.fragments.VideoAddFragment;

public class AddPostTabAdapter extends FragmentStateAdapter {

    public AddPostTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public interface OnAddPostClickListener{
        void onImageClick();
        void onVideoClick();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PhotoAddFragment();
            case 1:
                return new VideoAddFragment();
            case 2:
                return new PostAddFragment();
            default:
                return new PostAddFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
