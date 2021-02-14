package com.dnake.application;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class AddAndSubView extends LinearLayout {
    protected Button bt_Add;
    protected Button bt_Del;
    protected TextView Tv_Name;
    protected TextView Tv_Num;
    protected int MaxVal=10;
    protected int MixVal=0;
    MyApp myApp;
    public int getCurVal() {
        return curVal;
    }

    protected int curVal =5;
    protected int setp=1;
    protected String name="--";
    public interface  OnChangeVal{
        public boolean  OnChangeVal(int val);
    }
    OnChangeVal onChangeVal=null;
    public   void SetOnChangeVal(OnChangeVal onChangeVal){
        this.onChangeVal=onChangeVal;
    }
    public AddAndSubView(Context context, @Nullable AttributeSet attrs)  {
        super(context, attrs);

       LayoutInflater.from(context).inflate(R.layout.layout,this);

             final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AddAndSubView, 0, 0);

        MaxVal= a.getInteger(R.styleable.AddAndSubView_Max,0);
        MixVal=a.getInteger(R.styleable.AddAndSubView_Min,2);
        name=a.getString(R.styleable.AddAndSubView_Name);
        setp=a.getInt(R.styleable.AddAndSubView_setp,1);
        a.recycle();
        Init();
     }
    public void SetName(String name)
    {
        this.name=name;
        Tv_Name.setText(name);
    }

    boolean callOnchangeVal(int val){
        if(onChangeVal!=null)
        {
           return onChangeVal.OnChangeVal(val);
        }
        return true;
    }

   public   boolean SetVal(int Val)
    {
        if(Val>MaxVal){
            Val=MaxVal;
        }else if(Val<MixVal){
            Val=MixVal;
        }
        if(curVal !=Val)
        {
            curVal =Val;
            Tv_Num.setText(String.valueOf(curVal));
             return true;
        }
        Tv_Num.setText(String.valueOf(curVal));
        return false;
    }

    protected void Init() {
        bt_Add=findViewById(R.id.bt_Add);
        bt_Del=findViewById(R.id.bt_Del);
        Tv_Name=findViewById(R.id.textView_Name);
        Tv_Num=findViewById(R.id.TextView_Num);
        Tv_Name.setText(name);
        Tv_Num.setText(String.valueOf(curVal));

        bt_Add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int OldVal=curVal;
                if(SetVal(curVal +setp))
                {
                    if(!callOnchangeVal(curVal))SetVal(OldVal);
                }

            }
        });

        bt_Del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int OldVal=curVal;
                if(SetVal(curVal -setp))
                {
                    if(!callOnchangeVal(curVal))SetVal(OldVal);
                }
          }
        });

    }

    public  void SetButEnable(boolean b){
        bt_Del.setEnabled(b);
        bt_Add.setEnabled(b);
    }
}
