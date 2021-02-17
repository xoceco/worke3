package com.dnake.application.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.dnake.application.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
  * create an instance of this fragment.
 */
public class KTHomeFragment extends Fragment {
    KTPagerAdapter ktPagerAdapter;
     public KTHomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_k_t_home, container, false);
        ViewPager2 viewPager=root.findViewById(R.id.ktHomePager);
        ktPagerAdapter=new KTPagerAdapter(this);
        viewPager.setAdapter(ktPagerAdapter);
        TabLayout tabls=root.findViewById(R.id.tabs);
        tabls.addTab(tabls.newTab().setText("控制界面"));
        tabls.addTab(tabls.newTab().setText("空调状态"));
        tabls.addTab(tabls.newTab().setText("空调工程"));
        tabls.addTab(tabls.newTab().setText("新风工程"));

        tabls.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabls.setScrollPosition(position,0,false);
            }
        });
        return root;
   }

    @Override
    public void onPause() {
         Log.i("test","KTHomeFragment  onPause");
        super.onPause();

    }
}