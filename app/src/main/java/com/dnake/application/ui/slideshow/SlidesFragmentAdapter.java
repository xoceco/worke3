package com.dnake.application.ui.slideshow;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dnake.application.ui.home.HomeFragment;

public class SlidesFragmentAdapter extends FragmentStateAdapter {


    public SlidesFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return  HomeFragment.New();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
