package com.dnake.application.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dnake.application.ModBus;
import com.dnake.application.MyApp;
import com.dnake.application.R;

public class HomeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    public HomeFragment(){
    };
    private HomeViewModel homeViewModel;
    MyApp myApp;
    TextView textViewWMess;
    TextView textViewKTWD;
    TextView textViewXFFL;
    TextView textViewKTFL;
    SeekBar seekBarKTWD;
    SeekBar seekBarKTFL;
    SeekBar seekBarXFFL;
    Switch   swKtPower;
    Switch   swXfPower;
    Switch   swKtFR;
    Switch   swXfFR;
    Spinner spinnerKTMS;
    final int whatReadData=3;
    final int what_50x=4;
    boolean isUpdate=false;
    void uiInit(View root){
        SwipeRefreshLayout swipeRefreshLayout=root.findViewById(R.id.swipeRefreshLayout);
        swKtPower = root.findViewById(R.id.switchKt);
        spinnerKTMS = root.findViewById(R.id.spinnerKTMS);
        textViewKTFL=root.findViewById(R.id.textViewKTFL);
        textViewXFFL=  root.findViewById(R.id.textViewXFFL);
        textViewKTWD = root.findViewById(R.id.textViewKtWD);
        seekBarKTWD = root.findViewById(R.id.seekBarKTWD);
        seekBarKTFL = root.findViewById(R.id.seekBarKTFL);
        seekBarXFFL = root.findViewById(R.id.seekBarXFFL);
        textViewWMess=root.findViewById(R.id.textViewWMess);
        swXfPower=root.findViewById(R.id.switch_xfpower);
        swKtFR=root.findViewById(R.id.switchKTFR);
        swXfFR=root.findViewById(R.id.switch_xfFR);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStart();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            swKtPower.setShowText(true);
//            swKtFR.setShowText(true);
//            swXfPower.setShowText(true);
//        }
        spinnerKTMS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDataKT();
            }
         @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        seekBarKTWD.setOnSeekBarChangeListener(this);
        seekBarXFFL.setOnSeekBarChangeListener(this);
        seekBarKTFL.setOnSeekBarChangeListener(this);
        swXfPower.setOnClickListener( v -> updateDataXF());
        swKtFR.setOnCheckedChangeListener( ( buttonView,isChecked)-> {
            swKtFR.setText(isChecked?"空调辅热 开":"空调辅热 关");
            if(buttonView.isPressed()) updateDataKT();
        });
        swKtPower.setOnCheckedChangeListener( ( buttonView,isChecked)-> {
            swKtPower.setText(isChecked?"空调电源 开":"空调电源 关");
            if(buttonView.isPressed())updateDataKT();
        });
        swXfFR.setOnCheckedChangeListener( ( buttonView,isChecked)-> {
            swXfFR.setText(isChecked?"新风辅热 开":"空调辅热 关");
            if(buttonView.isPressed())updateDataXF();
        });
        swXfPower.setOnCheckedChangeListener( ( buttonView,isChecked)-> {
            swXfPower.setText(isChecked?"新风电源 开":"新风电源 关");
            if(buttonView.isPressed())updateDataXF();
        });


    }
       public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myApp=(MyApp)getActivity().getApplication();
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        uiInit(root);
        homeViewModel.mIsUpdateKtKzUi.observe(getActivity(), aBoolean -> {updateUIKT();});
        homeViewModel.mIsUpdatexfKzUi.observe(getActivity(),aBoolean -> {updateUIXf();});
        homeViewModel.m50Xmess.observe(getActivity(),str->{textViewWMess.setText(str);});
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("test","HomeFragment onResume");
        updateStart();
    }
    @Override
    public void onPause() {
        updateEnd();
        Log.i("test","HomeFragment onPause");
        super.onPause();
    }
   private void updateUIKT() {
        swKtFR.setText(swKtFR.isChecked()?"空调辅热 开":"空调辅热 关");
        swKtPower.setText(swKtPower.isChecked()?"空调电源 开":"空调电源 关");
        spinnerKTMS.setSelection(intArrayOf(intKTMS,ModBus.Dev_kt.Reg_KT_MS.getmVal()));
        seekBarKTWD.setProgress(ModBus.Dev_kt.Reg_KT_WD.getmVal()-16);
        textViewKTWD.setText("空调设置温度 "+ ModBus.Dev_kt.Reg_KT_WD.getmVal()+ " 摄氏度");
        seekBarKTFL.setProgress(ModBus.Dev_kt.Reg_KT_FL.getmVal()-1);
        textViewKTFL.setText("空调风量档位 "+ (ModBus.Dev_kt.Reg_KT_FL.getmVal())+ " 档");
        swKtPower.setChecked(ModBus.Dev_kt.Reg_KT_Power.getmVal()!=0);
        swKtFR.setChecked(ModBus.Dev_kt.Reg_KT_FR.getmVal()!=0);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch ( seekBar.getId()){
            case R.id.seekBarKTWD:
                textViewKTWD.setText("空调设置温度 "+ (progress+16)+ " 摄氏度");
                break;
            case R.id.seekBarKTFL:
                textViewKTFL.setText("空调风量档位 "+ (progress+1)+ " 档");
                break;
            case R.id.seekBarXFFL:
                textViewXFFL.setText("新风风量档位 "+ (progress+1)+ " 档");
                break;
        }
     }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        int id=seekBar.getId();
        if(id==R.id.seekBarKTWD ||id==R.id.seekBarKTFL){
            myApp.ktQueryTask.setDelayFrom(2000);
        }
        if(id==R.id.seekBarXFFL){
            myApp.xfQueryTask.setDelayFrom(2000);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id=seekBar.getId();
        if(id==R.id.seekBarKTWD ||id==R.id.seekBarKTFL){
            updateDataKT();
        }
        if(id==R.id.seekBarXFFL){
           updateDataXF();
        }
    }

    final int[] intKTMS =new int[]{3,2,1,5,6,7};
    void updateDataKT()
    {
        if(!homeViewModel.mIsUpdateKtKzUi.getValue()) return;
        ModBus.Dev_kt.Reg_KT_Power.setmVal(swKtPower.isChecked()?1:0);
        ModBus.Dev_kt.Reg_KT_FL.setmVal(seekBarKTFL.getProgress()+1);
        ModBus.Dev_kt.Reg_KT_WD.setmVal(seekBarKTWD.getProgress()+16);
        ModBus.Dev_kt.Reg_KT_MS.setmVal(intKTMS[spinnerKTMS.getSelectedItemPosition()]);
        ModBus.Dev_kt.Reg_KT_FR.setmVal(swKtFR.isChecked()?1:0);
//        myApp.ktControlUpdate();
    }
    void updateDataXF() {
        if(!homeViewModel.mIsUpdatexfKzUi.getValue()) return;
        ModBus.Dev_xf.Reg_XF_Power.setmVal(swXfPower.isChecked()?1:0);
        ModBus.Dev_xf.Reg_XF_FAN.setmVal(seekBarXFFL.getProgress()+1);
        ModBus.Dev_xf.Reg_XF_FR.setmVal(swXfFR.isChecked()?1:0);
     }

    void updateUIXf(){
        swXfFR.setText(swXfFR.isChecked()?"新风辅热 开":"新风辅热 关");
        swXfPower.setText(swXfPower.isChecked()?"新风电源 开":"新风电源 关");
        swXfPower.setChecked(ModBus.Dev_xf.Reg_XF_Power.getmVal()!=0);
        seekBarXFFL.setProgress(ModBus.Dev_xf.Reg_XF_FAN.getmVal()-1);
        swXfFR.setChecked(ModBus.Dev_xf.Reg_XF_FR.getmVal()!=0);
    }

    int intArrayOf(int[] bytes,int val ) {
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            if(bytes[i]==val) return i;
        }
        return -1;
    }

    void  updateStart(){
        final int deftime=1000;
        myApp.ktQueryTask.setTimer(2,deftime);
        myApp.xfQueryTask.setTimer(100,deftime);
 //       myApp.ktGet50xTask.setTimer(100,500);
    }
    void  updateEnd(){
        myApp.ktQueryTask.setTimer(0,0);
        myApp.xfQueryTask.setTimer(0,0);
        myApp.ktGet50xTask.setTimer(0,0);
    }
 }
