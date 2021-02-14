package com.dnake.application.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
        ViewPager viewPager=root.findViewById(R.id.ktHomePager);
        KTPagerAdapter ktPagerAdapter=new KTPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(ktPagerAdapter);
        TabLayout tabls=root.findViewById(R.id.tabs);
        tabls.setupWithViewPager(viewPager);
        return root;

    }
}