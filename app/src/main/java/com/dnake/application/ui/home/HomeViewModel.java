package com.dnake.application.ui.home;

import android.app.Application;
import android.os.Message;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dnake.application.ModBus;
import com.dnake.application.MyApp;

public class HomeViewModel extends AndroidViewModel{
    public  MyApp myApp;
    public  MutableLiveData<Boolean> mIsUpdateKtKzUi;
    public  MutableLiveData<Boolean> mIsUpdatexfKzUi;
    public  MutableLiveData<String> m50Xmess=new MutableLiveData<>();
    public HomeViewModel(Application application) {
        super(application);
        myApp=(MyApp) application;
        mIsUpdateKtKzUi =new MutableLiveData<>();
        mIsUpdateKtKzUi.setValue(false);
        mIsUpdatexfKzUi =new MutableLiveData<>();
        mIsUpdatexfKzUi.setValue(false);
        myApp.ktQueryTask.setOnTsak(b ->{
            if(b){
                mIsUpdateKtKzUi.postValue(true);
            }
        });
        myApp.xfQueryTask.setOnTsak(b->{
            if(b) mIsUpdatexfKzUi.postValue(true);
        });
        myApp.ktUpdateTask.setOnTsak(b -> {   //设置成功，重新读回值
            if(b) {
                myApp.ktQueryTask.setDelayFrom(50);
                myApp.ktQueryTask.setTimer(50);
            }
        });
        myApp.xfUpdateTask.setOnTsak(b -> {
            if(b){
                myApp.xfQueryTask.setDelayFrom(50);
                myApp.xfQueryTask.setTimer(50);
            }
        });
       myApp.ktGet50xTask.setOnTsak(b -> {
            if (b) {
                StringBuilder str = new StringBuilder();
                str.append(ModBus.Dev_kt.Reg_KT_502.toString());
                str.append(ModBus.Dev_kt.Reg_KT_503.toString());
                str.append(ModBus.Dev_kt.Reg_KT_504.toString());
                m50Xmess.postValue(str.toString());
              }
        });
    }
 }