package com.dnake.application.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.dnake.application.ModBus;
import com.dnake.application.MyApp;

import java.util.ArrayList;

import static com.dnake.application.ModBus.*;


public class MainViewModel extends AndroidViewModel {

    public ArrayList<ItemList> xf2hjs;
    public ArrayList<ItemList> ktstarte;
    MyApp myApp;
    public MutableLiveData showTable=new MutableLiveData<Boolean>();
    public MainViewModel(Application myApp){
        super(myApp);
        this.myApp=(MyApp)myApp;
        xf2hjs = new ArrayList<ItemList>();
        ktstarte  = new ArrayList<ItemList>();
        xf2hjs.add(new ItemList(Dev_xf2_01.CO2, "CO2", "ppm"));
        xf2hjs.add(new ItemList(Dev_xf2_01.PM25, "PM2.5", "ppm"));
        xf2hjs.add(new ItemList(Dev_xf2_01.TVOC, "TVOC", ""));
        xf2hjs.add(new ItemList(Dev_xf2_01.WD, "温度", "度"));

        ktstarte.add(new ItemList(Dev_kt.Reg_KT_testMode, "测试模式", ""));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_mbzs, "内机电机转速", "RPM"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_602, "外机电机转速", "RPM"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_603, "压机控制频率", "Hz"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_604, "压机目标频率", "Hz"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_605, "运行电流", "mA"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_606, "母线电压", "V"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_607, "交流电源", "V"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_608, "室外环境温度", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_609, "室外冷凝温度", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_610, "排气温度", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_611, "电子膨胀阀", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_612, "室内盘管环境温度", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_613, "室内回风环境温度", "度"));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_614, "设备状态1", ""));
        ktstarte.add(new ItemList(Dev_kt.Reg_KT_615, "设备状态2", ""));
     }
   static   public class ItemList {
        public ModBus.Reg reg;
        public String name;
        public String dw;
        public ItemList(ModBus.Reg reg, String name, String dw) {
            this.reg = reg;
            this.name = name;
            this.dw = dw;
        }
    }
}
