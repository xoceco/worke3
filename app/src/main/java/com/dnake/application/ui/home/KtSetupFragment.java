package com.dnake.application.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dnake.application.AddAndSubView;
import com.dnake.application.ModBus;
import com.dnake.application.MyApp;
import com.dnake.application.R;
import com.dnake.application.ui.ktsetup.Fragment_Ktdisp;

import static com.dnake.application.ModBus.Dev_kt;

/**
 * A simple {@link Fragment} subclass.
 * Use te an instance of this fragment.
 */
public class KtSetupFragment extends Fragment implements View.OnClickListener {
    static final  int UpdatUIWhat=2;
    static final  int FromDateWhat =3;
    static final  int setZsWhat=5;
    MyApp myApp;
    AddAndSubView setzsV;
    Button button_mode,button_set,button_reset;
    View root;
    static final   String[] strTestMode =new String[]{"正常运行","额定制冷","最大制冷","中间制冷",
            "最小制冷","其他制冷","低温制热","额定制热","中间制热","最小制热","高温制热能力测试","A工况","B工况","C工况", "E工况"};
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case setZsWhat:
                    Dev_kt.Reg_KT_mbzs.setmVal(setzsV.getCurVal());
                    break;
                case UpdatUIWhat:
            //        UpdatUI();
                    break;
                case FromDateWhat:
                  //  if(isResumed()) myApp.ktQueryTas_setup.setTimer(10,0);
                  //  handler.sendEmptyMessageDelayed(FromDateWhat,5000);
                 //   Log.i("test","读空调状态FromDateWhat");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public KtSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_kt_setup, container, false);
        myApp=(MyApp)getActivity().getApplication();
        UiInit();
        return root;
    }
    void UiInit(){
        button_mode = root.findViewById(R.id.button_RunMOde);
        button_mode.setText("当前模式:"+ Dev_kt.Reg_KT_testMode.toString());
        button_set= root.findViewById(R.id.buttton_set);
        button_reset= root.findViewById(R.id.buttton_reset);
        button_mode.setOnClickListener(this);
        button_set.setOnClickListener(this);
        button_reset.setOnClickListener(this);
        setzsV= root.findViewById(R.id.buttonSetzs);
        setzsV.SetVal(2000);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_RunMOde:
                new AlertDialog.Builder(KtSetupFragment.this.getActivity()).setTitle("选择运行模式").setSingleChoiceItems(strTestMode,Dev_kt.Reg_KT_testMode.getmVal(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dev_kt.Reg_KT_testMode.setmVal(i);
                        myApp.ktQueryTas_setup.setDelayFrom(300);
                        ((Button) view).setText("当前模式:" + strTestMode[i] + "   代号:" + i);
                        dialogInterface.dismiss();
                    }
                }).show();
                break;
            case R.id.buttton_set:
                Dev_kt.Reg_KT_mbzs.setmVal(300);
                handler.sendEmptyMessageDelayed(setZsWhat, 1000);
                break;
            case R.id.buttton_reset:
                Dev_kt.Reg_KT_mbzs.setmVal(300);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UiInit();
    }
}