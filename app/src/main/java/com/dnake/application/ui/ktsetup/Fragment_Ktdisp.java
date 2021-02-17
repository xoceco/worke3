package com.dnake.application.ui.ktsetup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.application.AddAndSubView;
import com.dnake.application.MyApp;
import com.dnake.application.R;
import com.dnake.application.ui.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import static com.dnake.application.ModBus.Dev_kt;


public class Fragment_Ktdisp extends Fragment{
     MainViewModel viewModel;
    View root;
    MyItemRecyclerViewAdapterAir adapterAir;
    MyApp myApp;
    final  int UpdatUIWhat=2;
    final  int FromDateWhat =3;
    public Fragment_Ktdisp() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp=(MyApp) getActivity().getApplication();
        viewModel= new ViewModelProvider(getActivity()).get(MainViewModel.class);
      }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_kt_disp, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView_01);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterAir = new MyItemRecyclerViewAdapterAir(viewModel.ktstarte));
        viewModel.UpdateUi.observe(getViewLifecycleOwner(),b->{
            if(b) adapterAir.notifyDataSetChanged();
        });
         return root;
    }
 @Override
    public void onResume() {
        super.onResume();
        myApp.ktQueryTas_setup.setTimer(10,100);
    }

    @Override
    public void onPause() {
        super.onPause();
        myApp.ktQueryTas_setup.setTimer(0,0);
    }
}

