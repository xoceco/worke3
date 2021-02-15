package com.dnake.application.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dnake.application.ModBus;
import com.dnake.application.MyApp;

import java.util.Observable;
import java.util.Observer;

public class HomeViewModel extends AndroidViewModel{

    static class KtKz{
      public   boolean mPower;
      public int mWD;
      public  int mFL;
      public int  mMS;
    }
    public MyApp myApp;
    private   boolean mIsUpdateUi=false;
    private MutableLiveData<String> mText;
    public  MutableLiveData<Boolean> mIsUpdateKtKzUi;
    public  MutableLiveData<Boolean> mIsUpdatexfKzUi;

    public HomeViewModel(Application application) {
        super(application);
        myApp=(MyApp) application;
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        mIsUpdateKtKzUi =new MutableLiveData<>();
        mIsUpdateKtKzUi.setValue(false);
        mIsUpdatexfKzUi =new MutableLiveData<>();
        mIsUpdatexfKzUi.setValue(false);
        myApp.ktQueryTask.setOnTsak(b ->{
            if(b){
                mIsUpdateKtKzUi.postValue(true);
            }});
        myApp.xfQueryTask.setOnTsak(b->{
            if(b) mIsUpdatexfKzUi.postValue(true);
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

}