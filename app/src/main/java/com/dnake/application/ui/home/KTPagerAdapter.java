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



   public class KTPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    static String[] TABS=new String[]{"主界面","空调状态","空调设置","新风设置"};

       public KTPagerAdapter(@NonNull Fragment fragment) {
           super(fragment);
       }
     @NonNull
       @Override
       public Fragment createFragment(int position) {
           if(position==0)  return  new HomeFragment();
           if(position==1)  return  new Fragment_Ktdisp();
           if(position==2)  return  new KtSetupFragment();
           if(position==3)  return  new ItemFragment_xfsetup();
           return  new SlideshowFragment();
       }

       @Override
       public int getItemCount() {
           return 4;
       }
   }
