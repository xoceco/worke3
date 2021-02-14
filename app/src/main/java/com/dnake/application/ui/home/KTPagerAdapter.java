package com.dnake.application.ui.home;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.dnake.application.ui.xfsetup.ItemFragment_xfsetup;
import com.dnake.application.ui.ktsetup.Fragment_Ktdisp;
import com.dnake.application.ui.slideshow.SlideshowFragment;



   public class KTPagerAdapter extends FragmentStatePagerAdapter {
    static String[] TABS=new String[]{"主界面","空调状态","空调设置","新风设置"};
    public KTPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)  return  HomeFragment.New();
        if(position==1)  return  new Fragment_Ktdisp();
        if(position==2)  return  new KtSetupFragment();
       if(position==3)  return  new ItemFragment_xfsetup();
        return  new SlideshowFragment();

    }

    @Override
    public int getCount() {
        return TABS.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TABS[position];
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

          return super.instantiateItem(container, position);
    }
}
