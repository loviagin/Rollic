package com.loviagin.rollic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.loviagin.rollic.LikedFragment;
import com.loviagin.rollic.fragments.UserPostsFragment;
import com.loviagin.rollic.models.Post;

import java.util.LinkedList;
import java.util.List;

public class TabAccountAdapter extends FragmentStateAdapter {

    LinkedList<Post> lp;

    public TabAccountAdapter(@NonNull FragmentActivity fragmentActivity, LinkedList<Post> listPosts) {
        super(fragmentActivity);
        lp = listPosts;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new UserPostsFragment(lp);
            case 1:
                return new LikedFragment();
            default:
                return new LikedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
