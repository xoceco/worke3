package com.dnake.application.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dnake.application.MyApp;

public class HomeViewModel extends ViewModel {

    static class KtKz{
      public   boolean mPower;
      public int mWD;
      public  int mFL;
      public int  mMS;
    }
    public MyApp myApp;
    private   boolean mIsUpdateUi=false;
    private MutableLiveData<String> mText;
    public  MutableLiveData<Integer> mKTWD;
    public  MutableLiveData<Boolean> mIsUpdateKtKzUi;
    public  MutableLiveData<Boolean> mIsUpdatexfKzUi;
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        mKTWD=new MutableLiveData<>();
        mIsUpdateKtKzUi =new MutableLiveData<>();
        mIsUpdateKtKzUi.setValue(false);
        mIsUpdatexfKzUi =new MutableLiveData<>();
        mIsUpdatexfKzUi.setValue(false);
     }
    public void updateKtkzUi(){
         mIsUpdateKtKzUi.postValue(true);
     }
    public void updatexfkzUi(){
        mIsUpdatexfKzUi.postValue(true);
    }
     public LiveData<String> getText() {
        return mText;
    }

}