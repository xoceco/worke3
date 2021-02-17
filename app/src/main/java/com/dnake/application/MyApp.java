package com.dnake.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

import  static  com.dnake.application.ModBus.*;

import static com.dnake.application.ModBus.modbus_CM_0x03_RX_Check;


public class MyApp extends Application {
    static public MutableLiveData<Boolean> setUpChange=new MutableLiveData<>();
    static final int   UsbDriver=0;
    static final int   UdpDriver=1;
    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    public boolean isOpen=false;
//    static public CH34xUARTDriver driver;

    public int  baudRate;
    public byte baudRate_byte;
    public byte stopBit;
    public byte dataBit;
    public byte parity;
    public byte flowControl;
//    static public String txIp = "192.168.1.103";
//    static public int txPort = 7000;
//    static public int rxPort=7001;
    public int xfAddr;
    public int ktAddr;
    public boolean zPower = true;
    public boolean isRun = true;
    public boolean isV = true;
    RxTx rxTx=null;
    byte[] txBuf =new byte[255];

   enum NetState{ Txs,Rxs, RxOk,RxNoOk, TxOk,TxNoOk }
   public MutableLiveData<NetState> netState=new MutableLiveData<NetState>();
   private ModbusTaskS modBusTasks = new ModbusTaskS();
   public  class ModbusTaskS extends ArrayList<ModBusTimeTask> {
       private ModbusTaskS my = this;

       public void Start() {
           Timer timer = new Timer();
           timer.schedule(new TimerTask() {
               @Override
               public void run() {
                   for (ModBusTimeTask timerTask : my) {
                       timerTask.TimerRun();
                   }
               }
           }, 10, 10);
       }

       public class ModBusTimeTask_0x03 extends ModBusTimeTask {
           ModBusTimeTask my = this;

           private ModBusTimeTask_0x03(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime) {
               super(dev, RegStartAddr, Length, time, defTime);
           }

           private ModBusTimeTask_0x03(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime,int atuoDelayTiem) {
               super(dev, RegStartAddr, Length, time, defTime);
               Observer observer=new Observer() {
                   @Override
                   public void update(Observable observable, Object o) {
                       Reg.State state=  (Reg.State) o;
                       if(state==Reg.State.Set)
                       {
                           if(getDelayFrom()<atuoDelayTiem)
                               setDelayFrom(atuoDelayTiem);
                       }
                   }
               };
               if(atuoDelayTiem!=0) {
                   for(int i=0;i<Length;i++){
                       Reg reg= dev.get(RegStartAddr+i);
                       if(reg!=REG_NULL){
                           reg.addObserver(observer);
                       }
                   }
                }
           }
           @Override
           boolean RunTask() {
               netState.postValue(NetState.Rxs);
               if (my.delayFrom != 0) return false;
               int txLenth = ModBus.Modbus_CM_0x03_Tx(dev, RegStartAddr, lenth, txBuf);
               for (int j = 0; j < 3; j++) {
                   try {
                       rxTx.rxBufClear();
                       rxTx.tx(txBuf, txLenth);
                       byte[] rxBuf = rxTx.rx(5 + lenth * 2);
                       if (modbus_CM_0x03_RX_Check(dev, RegStartAddr, lenth, rxBuf)) {
                           Log.i("TEST", "RX:" + AsToHexstr(rxBuf, lenth * 2 + 5));
                           if (my.delayFrom == 0) {
                               Modbus_CM_0x03_RX_From(dev, RegStartAddr, lenth, rxBuf);
                           }
                           netState.postValue(NetState.RxOk);
                           return true;
                       } else {
                           throw new Exception("数据校验失败");
                       }
                   } catch (Exception e) {
                       //     e.printStackTrace();
                       Log.i("TEST", "查询数据失败:" + j + "原因:" + e.getMessage());
                   }
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       break;
                   }
               }
               netState.postValue(NetState.RxNoOk);
               return false;
           }
       }


       public class ModBusTimeTask_0x10 extends ModBusTimeTask {
           private ModBusTimeTask_0x10(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime) {
               super(dev, RegStartAddr, Length, time, defTime);
           }

           private ModBusTimeTask_0x10(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime,int  autoUpdateTime) {
               super(dev, RegStartAddr, Length, time, defTime);
               final Observer observer= (observable, o) -> {
                   Reg.State state=  (Reg.State) o;
                   if(state==Reg.State.Set)
                   {
                       setTimer(autoUpdateTime);
                   }
               };
               if(autoUpdateTime!=0){
                   for(int i=0;i<Length;i++){
                       Reg reg= dev.get(RegStartAddr+i);
                       if(reg!=REG_NULL){
                           reg.addObserver(observer);
                       }
                   }
               }
           }
        @Override
           boolean RunTask() {
               return Modbus_CM_0x10(dev, RegStartAddr, lenth);
           }
         }

       boolean Modbus_CM_0x10(ModBus.Dev dev, int RegStartAddr, int length) {
           netState.postValue(NetState.Txs);
           int txLength = Modbus_CM_0x10_tx(dev, RegStartAddr, length, txBuf);
           for (int j = 0; j < 3; j++) {
               try {
                   rxTx.rxBufClear();
                   rxTx.tx(txBuf, txLength);
                   Log.i("TEST", "TX:" + AsToHexstr(txBuf, txLength));
                   byte[] rxBuf = rxTx.rx(8);
                   if (Modbus_CM_0x10_check(dev, RegStartAddr, length, rxBuf)) {
                       netState.postValue(NetState.TxOk);
                       return true;
                   } else {
                       throw new Exception("数据检验失败");
                   }
               } catch (Exception e) {
                   Log.i("TEST", "发送失败" + j + "次原因:" + e.getMessage());
               }
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   break;
               }
           }
           netState.postValue(NetState.TxNoOk);
           return false;
       }
           public ModBusTimeTask AddTask_0x03(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime) {
               ModBusTimeTask_0x03 val = new ModBusTimeTask_0x03(dev, RegStartAddr, Length, time, defTime);
               add(val);
               return val;
           }

       public ModBusTimeTask AddTask_0x03(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime,int autoDelayTime) {
           ModBusTimeTask_0x03 val = new ModBusTimeTask_0x03(dev, RegStartAddr, Length, time, defTime,autoDelayTime);
           add(val);
           return val;
       }

           public ModBusTimeTask AddTask_0x10(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime) {
               ModBusTimeTask_0x10 val = new ModBusTimeTask_0x10(dev, RegStartAddr, Length, time, defTime);
               add(val);
               return val;
           }

       public ModBusTimeTask AddTask_0x10(ModBus.Dev dev, int RegStartAddr, int Length, int time, int defTime,int autoUpdateTime) {
           ModBusTimeTask_0x10 val = new ModBusTimeTask_0x10(dev, RegStartAddr, Length, time, defTime,autoUpdateTime);
           add(val);
           return val;
       }
       }

       static public abstract class ModBusTimeTask {
           protected ModBus.Dev dev;
           protected int RegStartAddr;
           protected int lenth;

           public int getDelayFrom() {
               return delayFrom;
           }

           static public interface OnTsak {
               void Tasked(boolean b);
           }

           public void setOnTsak(OnTsak onTsak) {
               this.onTsak = onTsak;
           }

           OnTsak onTsak = null;

           private int delayFrom = 0;
           private int Timer = 0;
           private int defTime;
           private boolean IsRun = false;


           abstract boolean RunTask();

           ModBusTimeTask(final ModBus.Dev dev, final int RegStartAddr, final int Length, int time, int defTime) {
               this.defTime = defTime;
               this.Timer = time;
               this.dev = dev;
               this.RegStartAddr = RegStartAddr;
               this.lenth = Length;
           }

           public synchronized void setDelayFrom(int delayFrom) {
               this.delayFrom = delayFrom;
           }

           public boolean Run() {
               if (IsRun) {
                   IsRun = false;
                   boolean b = RunTask();
                   if (onTsak != null) onTsak.Tasked(b);
                   return b;
               }
               return false;
           }

           synchronized public void RushTimer(int Timer) {
               if (this.Timer > Timer || this.Timer == 0) this.Timer = Timer;
           }

           synchronized public void setTimer(int timer) {
               this.Timer = timer;
           }
           synchronized public void setTimer(int timer,OnTsak onTsak) {
               this.Timer = timer;
               this.setOnTsak(onTsak);
           }

           synchronized public void setTimer(int timer, int defTime) {
               this.Timer = timer;
               this.defTime = defTime;
           }

           synchronized public void TimerRun() {
               if (Timer != 0) {
                   Timer--;
                   if (Timer == 0) {
                       IsRun = true;
                       Timer = defTime;
                   }
               }
               if (delayFrom != 0) {
                   delayFrom--;
               }
           }
       }
       public ModBusTimeTask xf2KzTask = modBusTasks.AddTask_0x10(Dev_xf2_01, 1, 5, 0, 0);

       public ModBusTimeTask xfUpdateTask = modBusTasks.AddTask_0x10(Dev_xf, 1, 5, 0, 0,50);

       public ModBusTimeTask xfzsKzTask = modBusTasks.AddTask_0x10(Dev_xf, 151, 17, 0, 0);

       public ModBusTimeTask ktUpdateTask = modBusTasks.AddTask_0x10(Dev_kt, 6, 5, 0, 0,50);

       public ModBusTimeTask ktGet50xTask=modBusTasks.AddTask_0x03(Dev_kt, 502, 3, 0, 0);

       public ModBusTimeTask ktQueryTask = modBusTasks.AddTask_0x03(Dev_kt, 6, 5, 0, 0,100);

       public ModBusTimeTask xf2QueryTask = modBusTasks.AddTask_0x03(Dev_xf2_01, 1, 5, 0, 0);

       public ModBusTimeTask xf2QueryTask_sw = modBusTasks.AddTask_0x03(Dev_xf2_01, 12, 3, 0, 0);

       public ModBusTimeTask ktQueryTas_setup = modBusTasks.AddTask_0x03(Dev_kt, 600, 16, 0, 0);

       public ModBusTimeTask xfQueryTask = modBusTasks.AddTask_0x03(Dev_xf, 1, 5, 0, 0,200);

       public ModBusTimeTask XfzsQTask = modBusTasks.AddTask_0x03(Dev_xf, 151, 17, 0, 0);

       public ModBusTimeTask Xf2hjQTask = modBusTasks.AddTask_0x03(Dev_xf2_01, 51, 6, 0, 200);

        public ModBusTimeTask XfsjzsQTask = modBusTasks.AddTask_0x03(Dev_xf, 71, 2, 0, 0);



       static public class HashMapRBs extends HashMap<Integer, RadioButton> {
           @Nullable
           @Override
           public RadioButton put(Integer key, RadioButton value) {
               value.setTag(key);
               return super.put(key, value);
           }

           public void setCheck(int key, boolean isCheck) {
               RadioButton b = get(key);
               if (b != null) {
                   b.setChecked(isCheck);
               }
           }

           public int getCheck() {
               for (Integer key : this.keySet()) {
                   if (get(key).isChecked()) return key;
               }
               return -1;
           }
       }
      enum KzState {xf2change, xf2From}
      public MutableLiveData<KzState> kzstate = new MutableLiveData<>();
       public MutableLiveData<Integer> zs = new MutableLiveData<Integer>();
       public MyLiveData test = new MyLiveData();

       public class MyLiveData extends LiveData<Integer> {
           int i = 0;

           public void post() {
               i++;
               super.postValue(i);
           }
       }

       void SetAtuoUpdate_Dev_kt(ModBus.Reg reg, int time) {
           if (reg.Tag == null) {
               final ModBusTimeTask task = modBusTasks.AddTask_0x10(Dev_kt, reg.getID(), 1, 0, 0);
               reg.Tag = task;
               reg.addObserver(new Observer() {
                   @Override
                   public void update(Observable observable, Object o) {
                       ModBus.Reg.State state = (ModBus.Reg.State) o;
                       if (state == ModBus.Reg.State.Set) {
                        if (ktQueryTas_setup.getDelayFrom() < (130 + time))
                               ktQueryTas_setup.setDelayFrom(130 + time);
                           task.setTimer(time, b->{
                               if (b) {
                                   ktQueryTas_setup.setDelayFrom(8);
                                   ktQueryTas_setup.setTimer(10);
                               }
                           });
                       }
                   }
               });
           }
       }

       void SetAtuoUpdate_Dev_xf2_01(ModBus.Reg reg, int time) {
           if (reg.Tag == null) {
               final ModBusTimeTask task = modBusTasks.AddTask_0x10(Dev_xf2_01, reg.getID(), 1, 0, 0);
               reg.Tag = task;
               reg.addObserver(new Observer() {
                   @Override
                   public void update(Observable observable, Object o) {
                       ModBus.Reg.State state = (ModBus.Reg.State) o;
                       if (state == ModBus.Reg.State.Set) {
                           task.setTimer(time, 0);
                           xf2QueryTask.setDelayFrom(50 + time);
                           xf2QueryTask_sw.setDelayFrom(50 + time);
                       }
                   }
               });
           }
       }

       @Override
       public void onCreate() {

         super.onCreate();
         Log.i("test","Myapp onCreate");
         modBusTasks.Start();
  /*       Observer xfControlObserver;

           ModBus.Dev_xf.Reg_XF_Power.addObserver(xfControlObserver = new Observer() {
                @Override
               public void update(Observable observable, Object o) {
                 Reg.State state=  (Reg.State) o;
                 if(state==Reg.State.Set)
                  {
                      Log.i("test"," xfKzTask.setTimer(50,UpdateOnTask);");
                      xfKzTask.setTimer(50,UpdateOnTask);
                      xfQueryTask.setDelayFrom(200);
                  }
                }
                final   ModBusTimeTask.OnTsak UpdateOnTask=b -> {
                   if(b){
                       xfQueryTask.setDelayFrom(9);
                       xfQueryTask.setTimer(10);
                   }
               };
           });
         ModBus.Dev_xf.Reg_XF_FAN.addObserver(xfControlObserver);
         ModBus.Dev_xf.Reg_XF_FR.addObserver(xfControlObserver);


         ModBusTimeTask.OnTsak ktControlUpdateOnTask=new ModBusTimeTask.OnTsak() {
               @Override
               public void Tasked(boolean b) {
                   if(b)
                   {
                       ktQueryTask.setTimer(10);
                       ktQueryTask.setDelayFrom(10);
                   }
               }
           };
           Observer ktkzObserver=new Observer() {
              @Override
              public void update(Observable o, Object arg) {
                  Reg.State state=  (Reg.State) arg;
                  if(state==Reg.State.Set)
                   //   ktControlUpdate();
                  {
                      ktQueryTask.setDelayFrom(200);
                      ktControlTask.setTimer(50,ktControlUpdateOnTask);
                  }
              }
          };
          Dev_kt.Reg_KT_Power.addObserver(ktkzObserver);
          Dev_kt.Reg_KT_WD.addObserver(ktkzObserver);
          Dev_kt.Reg_KT_MS.addObserver(ktkzObserver);
          Dev_kt.Reg_KT_FL.addObserver(ktkzObserver);
*/
           thread = null;
           isRun = true;
           thread = new Thread(runnable);
           thread.start();
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.Power, 10);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.DW, 50);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.runMode, 10);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.xhMode, 10);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.K1, 10);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.K2, 10);
           SetAtuoUpdate_Dev_xf2_01(Dev_xf2_01.K3, 10);

           //    SetAtuoUpdate_Dev_kt(Dev_kt.Reg_KT_Power,10);
           //    SetAtuoUpdate_Dev_kt(Dev_kt.Reg_KT_FSDW,100);

           SetAtuoUpdate_Dev_kt(Dev_kt.Reg_KT_mbzs, 10);
           SetAtuoUpdate_Dev_kt(Dev_kt.Reg_KT_testMode, 10);

      }

    public void setRxTx( @NonNull RxTx rxTx) throws Exception {
    //      if(this.rxTx!=null){
      //        this.rxTx.close();
      //    }
          this.rxTx=rxTx;
    }

//    public void ResetTxRx(){
//        try {
//            rxTx= getRxTx();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private RxTx getRxTx() throws Exception {
//
//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        txIp = pre.getString(getString(R.string.txIp), "192.168.4.1");
//        txPort = Integer.valueOf(pre.getString(getString(R.string.txPort), "7000"));
//        rxPort = Integer.valueOf(pre.getString(getString(R.string.rxPort), "7001"));
//        RxTx_Udp upt=new RxTx_Udp();
//        upt.init();
//        return   upt;
//    }

    @Override
       public void onTerminate() {
           if (thread != null && thread.isAlive()) thread.interrupt();
           isRun = false;
           super.onTerminate();
       }

       Thread thread;
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
               try {
                   while (isRun) {
                       if (isV && rxTx!=null) {
                           for (ModBusTimeTask modBusTimeTask : modBusTasks) {
                               modBusTimeTask.Run();
                           }
                       }
                       Thread.sleep(10);

                   }
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               Log.i("TEST", "线程停止了");
           }
       };

       public String ByteSToString(byte[] bytes, int Length) {
           StringBuilder str = new StringBuilder("");

           for (int i = 0; i < Length; i++) {
               str.append(" ");
               str.append(String.format("%02x", (bytes[i])));
           }
           return str.toString();
       }


       static public void Bind(Switch sw, ModBus.RegU16 reg) {
           reg.addObserver(new Observer() {
               @Override
               public void update(Observable observable, Object o) {
                   ModBus.Reg.State state = (ModBus.Reg.State) o;
                   if (state == ModBus.Reg.State.From) {
                       sw.setChecked(reg.getmVal() != 0);
                   }
               }
           });
       }

       static public String AsToHexstr(byte[] bs, int length) {
           StringBuilder stringBuilder = new StringBuilder();
           if (length > bs.length) length = bs.length;
           for (int i = 0; i < length; i++) {
               stringBuilder.append(String.format("%02X ", bs[i]));

           }
           return stringBuilder.toString();
       }

       interface RxTx {
           byte[] rxBuf = new byte[255];
           void rxBufClear() throws SocketException;

           void tx(byte[] txBuf, int length) throws IOException;

           byte[] rx(int length) throws IOException, InterruptedException;
            void  close();
       }
         static class  RxTx_Uart implements RxTx{
           public CH34xUARTDriver driver;
           public    RxTx_Uart(AppCompatActivity activity, int baudRate, byte dataBit, byte stopBit, byte parity, byte flowControl) throws Exception {
               {
                   driver = new CH34xUARTDriver(
                           (UsbManager) activity.getSystemService(Context.USB_SERVICE), activity,
                           ACTION_USB_PERMISSION);
                   if (! driver.UsbFeatureSupported())
                   {
                       Exception e = new Exception("NO USB HOST");
                       throw e;
                   }
                   int retval = driver.ResumeUsbList();
                   if (retval == -1) {
                        driver.CloseDevice();
                       Exception e = new Exception("NO FIND CH34X ");
                       throw e;

                   } else if (retval == 0) {
                       if (driver.UartInit()) {
                           driver.SetConfig(baudRate, dataBit, stopBit, parity,flowControl);
                           driver.SetTimeOut(1000,1000);
                       }

                   }
               }
           }



             @Override
           public void rxBufClear() throws SocketException {
               byte[] rxbuf=new byte[256];
               while (driver.ReadData(rxbuf,256)!=0){}

           }

           @Override
           public void tx(byte[] txBuf, int length) throws IOException {
               if(driver.isConnected()) {
                    driver.WriteData(txBuf,length);
               }
           }

           @Override
           public byte[] rx(int length) throws IOException, InterruptedException {
               int length2=0;
               int pos=0;
               byte[] buf=new byte[256];
               for(int i=0;i<10;i++) {
                   Thread.sleep(50);
                   if (driver.isConnected()) {
                       while (driver.ReadData(buf, 1) != 0) {
                           rxBuf[pos++] = buf[0];
                           if (pos >= length) return rxBuf;
                       }
                   }
               }
                throw(new IOException("数据长度"+pos+"小于"+length));
           }

            @Override
            public void close() {
              if(driver!=null)  {
                  driver.CloseDevice();
                  driver=null;
              }

            }
        }
          static class RxTx_Udp implements RxTx {
           static public DatagramSocket socket;
           static public DatagramPacket txPacket;
           static public DatagramPacket rxPacket;
           RxTx_Udp(String txIp,int txPort,int rxPort  ) throws Exception {
                   if(socket!=null && !socket.isClosed()) {
                     socket.close();
                     socket=null;
                   }
                   socket = new DatagramSocket(rxPort);
                   txPacket = new DatagramPacket(rxBuf, 0, 1);
                   txPacket.setAddress(InetAddress.getByName(txIp));
                   txPacket.setPort(txPort);
                   rxPacket = new DatagramPacket(rxBuf, 0, 255);
                   Log.i("TEST","eip:"+txIp+"tp"+txPort+"rp"+rxPort);
          }

           @Override
           public void rxBufClear() throws SocketException {
               rxPacket.setLength(254);
               socket.setSoTimeout(10);
               while (true) {
                   try {
                       socket.receive(rxPacket);
                   } catch (IOException e) {
                       break;
                   }
               }
           }

           @Override
           public void tx(byte[] txBuf, int length) throws IOException {
               socket.setSoTimeout(300);
               txPacket.setData(txBuf, 0, length);
               socket.send(txPacket);
           }

           @Override
           public byte[] rx(int length) throws IOException {
               socket.setSoTimeout(300);
               socket.receive(rxPacket);
               if (rxPacket.getLength() < length) {
                   throw new IOException("data Length < "+rxPacket.getLength());
               }
               return rxBuf;
           }
                @Override
               public void close() {
                   if(socket!=null && !socket.isClosed()){
                       socket.close();
                   }
               }
           }
}
