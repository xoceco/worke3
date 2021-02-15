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
    static HomeFragment homeFragment=null;
    static public   HomeFragment  New()
    {
        //if(homeFragment==null)
        homeFragment=new HomeFragment();
        return homeFragment;
    }

    private HomeViewModel homeViewModel;
    MyApp myApp;
    TextView textViewWMess;
    TextView textViewKTWD;
    TextView textViewXFFL;
    TextView textViewKTFL;
    SeekBar seekBarKTWD;
    SeekBar seekBarKTFL;
    SeekBar seekBarXFFL;
    Switch  swKtPower;
    Switch   swXfPower;
    Spinner spinnerKTMS;
    final int whatReadData=3;
    final int what_50x=4;
    boolean isUpdate=false;
     Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
//                case whatReadData:
//                    myApp.ktQueryTask.setTimer(2);
//                    myApp.xfQueryTask.setTimer(50);
//                    myApp.ktGet50xTask.setTimer(100);
//                    handler.sendEmptyMessageDelayed(whatReadData, 20000);
//                    break;
                 case what_50x:
                    textViewWMess.setText(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myApp=(MyApp)getActivity().getApplication();
        homeViewModel =
                new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SwipeRefreshLayout swipeRefreshLayout=root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStart();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swKtPower = root.findViewById(R.id.switchKt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swKtPower.setShowText(true);
        }
        swKtPower.setOnClickListener(v -> {
            swKtPower.setText(swKtPower.isChecked()?"空调电源 开":"空调电源 关");
            updateDataKT();
        });

       spinnerKTMS = root.findViewById(R.id.spinnerKTMS);
       spinnerKTMS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDataKT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textViewKTFL=root.findViewById(R.id.textViewKTFL);
        textViewXFFL=  root.findViewById(R.id.textViewXFFL);
        textViewKTWD = root.findViewById(R.id.textViewKtWD);
        seekBarKTWD = root.findViewById(R.id.seekBarKTWD);
        seekBarKTFL = root.findViewById(R.id.seekBarKTFL);
        seekBarXFFL = root.findViewById(R.id.seekBarXFFL);
        textViewWMess=root.findViewById(R.id.textViewWMess);
        swXfPower=root.findViewById(R.id.switch_xfpower);
        seekBarKTWD.setOnSeekBarChangeListener(this);
        seekBarXFFL.setOnSeekBarChangeListener(this);
        seekBarKTFL.setOnSeekBarChangeListener(this);
        swXfPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataXF();
            }
        });

        homeViewModel.mIsUpdatexfKzUi.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) updateUIXf();
            }
        });
        homeViewModel.mIsUpdateKtKzUi.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) updateUIKT();
            }
        });

        myApp.ktGet50xTask.setOnTsak(b -> {
            if (b) {
                StringBuilder str = new StringBuilder();
                    str.append(ModBus.Dev_kt.Reg_KT_502.toString());
                    str.append(ModBus.Dev_kt.Reg_KT_503.toString());
                    str.append(ModBus.Dev_kt.Reg_KT_504.toString());
                    Message msg = new Message();
                    msg.what = what_50x;
                    msg.obj = str;
                    handler.sendMessage(msg);
            }
        });
        homeViewModel.mIsUpdateKtKzUi.observe(getActivity(), new Observer<Boolean>() {
             @Override
             public void onChanged(Boolean aBoolean) {
                 if(aBoolean)   updateUIKT();
             }
         });
         updateUIKT();
      return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStart();
    }

    @Override
    public void onPause() {
        updateEnd();
        super.onPause();
    }

    private void updateUIKT() {
        spinnerKTMS.setSelection(intArrayOf(intKTMS,ModBus.Dev_kt.Reg_KT_MS.getmVal()));
        seekBarKTWD.setProgress(ModBus.Dev_kt.Reg_KT_WD.getmVal()-16);
        textViewKTWD.setText("空调设置温度 "+ ModBus.Dev_kt.Reg_KT_WD.getmVal()+ " 摄氏度");
        seekBarKTFL.setProgress(ModBus.Dev_kt.Reg_KT_FL.getmVal()-1);
        textViewKTFL.setText("空调风量档位 "+ (ModBus.Dev_kt.Reg_KT_FL.getmVal())+ " 档");
        swKtPower.setChecked(ModBus.Dev_kt.Reg_KT_Power.getmVal()!=0);
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
//        myApp.ktControlUpdate();
    }
    void updateDataXF() {
        if(!homeViewModel.mIsUpdatexfKzUi.getValue()) return;
        ModBus.Dev_xf.Reg_XF_Power.setmVal(swXfPower.isChecked()?1:0);
        ModBus.Dev_xf.Reg_XF_FAN.setmVal(seekBarXFFL.getProgress()+1);
     }

    void updateUIXf(){
         swXfPower.setChecked(ModBus.Dev_xf.Reg_XF_Power.getmVal()!=0);
         seekBarXFFL.setProgress(ModBus.Dev_xf.Reg_XF_FAN.getmVal()-1);
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
        myApp.xfQueryTask.setTimer(50,deftime);
   //     myApp.ktGet50xTask.setTimer(100,500);
    }
    void  updateEnd(){
        myApp.ktQueryTask.setTimer(0,0);
        myApp.xfQueryTask.setTimer(0,0);
    //    myApp.ktGet50xTask.setTimer(0,0);
    }
 }
