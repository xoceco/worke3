package com.dnake.application.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.dnake.application.R;
import com.dnake.application.ui.MainViewModel;
import com.google.android.material.tabs.TabLayout;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        MainViewModel mainViewModel=new ViewModelProvider(getActivity()).get(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        ViewPager2 viewPager2=root.findViewById(R.id.viewPager2_1);
        SlidesFragmentAdapter viewPagerAdapter=new SlidesFragmentAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        tabLayout =root.findViewById(R.id.tabLayout2);
        tabLayout.addTab(tabLayout.newTab().setText("tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("tab2"));
        tabLayout.addTab(tabLayout.newTab().setText("tab3"));
        tabLayout.addTab(tabLayout.newTab().setText("tab4"));
        tabLayout.addTab(tabLayout.newTab().setText("tab5"));
         tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.setScrollPosition(position,0,false);
            }
        });
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}