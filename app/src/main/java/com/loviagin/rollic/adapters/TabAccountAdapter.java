package com.loviagin.rollic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.loviagin.rollic.fragments.LikedFragment;
import com.loviagin.rollic.fragments.UserPostsFragment;
import com.loviagin.rollic.fragments.VideoFragment;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.Video;

import java.util.LinkedList;

public class TabAccountAdapter extends FragmentStateAdapter {

    LinkedList<Post> lp;
    LinkedList<Video> vp;
    LinkedList<Post> pv;

    public TabAccountAdapter(@NonNull FragmentActivity fragmentActivity, LinkedList<Post> listPosts, LinkedList<Video> vp, LinkedList<Post> pv) {
        super(fragmentActivity);
        this.lp = listPosts;
        this.vp = vp;
        this.pv = pv;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserPostsFragment(lp);
            case 1:
                return new VideoFragment(vp);
            default:
                return new LikedFragment(pv);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
