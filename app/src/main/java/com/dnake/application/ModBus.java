package com.dnake.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Observable;
import java.util.Timer;

public class ModBus {
    /*新风机寄存器列表*/

    /****************新风寄存器******************/


    final public  static Dev_XF1 Dev_xf = new Dev_XF1(0x01);
    final static public  Dev_XF2 Dev_xf2_01 =new Dev_XF2( 0x11);
    final static public  Dev_KT Dev_kt =new Dev_KT(0x61);

    public static class Dev_XF2 extends  Dev
    {
        final public RegU16 Power = AddRegU16(1);
        final public RegU16 DW = AddRegU16(2);
        final public RegU16 runMode = AddRegU16(3);
        final public RegU16 xhMode = AddRegU16(4);

        final public RegU16 K1 = AddRegU16(12);
        final public RegU16 K2 = AddRegU16(13);
        final public RegU16 K3 = AddRegU16(14);
        final public RegU16 PM25 = AddRegU16(51);
        final public RegU16 CO2 = AddRegU16(52);
        final public RegU16 TVOC = AddRegU16(53);
        final public RegTempl WD = AddRegTempl(55);
        Dev_XF2(int Addr){
            super(Addr);
        }
    }

        public   static class Dev_XF1 extends Dev{

          public RegU16 Reg_XF_Power = AddRegU16(1);
          public RegU16 Reg_XF_FAN =  AddRegU16(2);
          public RegU16 Reg_XF_RunMode = AddRegU16(3);
          public RegU16 Reg_XF_XhMode =  AddRegU16(4);
          public RegU16 Reg_XF_FR =    AddRegU16(5);
          public RegU16 Reg_XFKT_MS =  AddRegU16(7);
          public RegU16 Reg_XF_ZYGY =  AddRegU16(8);

          public     RegU16 Reg_xfSjZs=AddRegU16(71);
          public    RegU16  Reg_hfSjZs=AddRegU16(72);
      public     RegU16 Reg_xfzsS[]=new  RegU16[8];
            public     RegU16 Reg_xfzshS[]=new  RegU16[8];
          Dev_XF1(int Addr) {
             super(Addr);
              for(int i=0;i<8;i++){
                  Reg_xfzsS[i] = AddRegU16(151+i);
                  Reg_xfzshS[i] = AddRegU16(160+i);
              }
         }
     }
     
    
    
    static public class  Dev extends HashMap<Integer, Reg> {
      public   byte Addr;
        public RegTempl AddRegTempl(int ID){
            RegTempl Reg = new RegTempl(ID);
            put(Reg);
            return Reg;
        }

       public   class RegU16_X10 extends RegU16{

           public RegU16_X10(int ID) {
               super(ID);
           }

           @Override
           public Integer getmVal() {
               return super.getmVal()*10;
           }
          @Override
           public  boolean setmValToInt(Integer mVal) {
                return super.setmValToInt(mVal/10);
           }
       }

       public   RegU16  AddRegU16_Mathe(int ID,final int X ,final int A ){
               RegU16 Reg=  new RegU16(ID){
                @Override
                public Integer getmVal() {
                    return super.getmVal()*X+A;
                }
            };
            put(Reg);
            return Reg;
        }


        public   <T> T AddReg_5XX(T reg){
            put((Reg) reg);
            return reg;
        }
        public   RegU16  AddRegU16_SF(int ID, int KVal,int aVal){
            RegU16 Reg=  new RegU16_SF(ID, KVal,aVal);
            put(Reg);
            return Reg;
        }
        public   RegU16  AddRegU16(int ID){
            RegU16 reg = new RegU16(ID);
            put(reg);
            return reg;
        };
        public   Reg_TestMode  AddReg_TestMode(int ID){
            Reg_TestMode reg = new Reg_TestMode(ID);
            put(reg);
            return reg;
        };

        public   RegU16  AddRegKT614(){

            RegU16 reg = new RegU16(614){
                @Override
                public String toString() {
                    StringBuilder strB=new StringBuilder();
                    int val=getmVal();
                    strB.append("机器");
                    if((val & 0x1)!=0 ) strB.append("开 "); else strB.append("关 ");
                    strB.append(";\n");
                    int val0=(mVal>>1)&0b111;
                    strB.append("外机工作模式");
                    switch (val0)
                    {
                        case 0: strB.append("其它");break;
                        case 1: strB.append("制冷");break;
                        case 2: strB.append("制热");break;
                        case 3: strB.append("除湿");break;
                        case 4: strB.append("送风");break;
                        default:
                            strB.append("未知");
                    }
                    strB.append(";\n");
                    if((val & 0x1<<4)!=0 ) strB.append("除霜中;\n");
                    if((val & 0x1<<5)!=0 ) strB.append("回油运行中;\n");
                    if((val & 0x1<<6)!=0 ) strB.append("四通阀通电;\n");else strB.append("四通阀断电;\n");
                    if((val & 0x1<<7)!=0 ) strB.append("内风机由外机控制");else strB.append("外机控制无效;\n");
                    strB.append("("+val+")");
                    return strB.toString();
                }
            };
            put(reg);
            return reg;
        };



        public   RegU16  AddRegKT615(){

            RegU16 reg = new RegU16(615){
                @Override
                public String toString() {
                    StringBuilder strB=new StringBuilder();
                    int val=getmVal();
                    strB.append("最大频率运行标志的反馈:");
                    if((val & 0x1)!=0 ) strB.append("已接收到最大7分钟运行标志 "); else strB.append("正常运行 ");
                    strB.append(";\n");
                    if((val & (0x1<<1))!=0 ) strB.append("关机保持30分钟;\n");
                    if((val & (0x1<<2))!=0 ) strB.append("电子膨胀阀复位完成 "); else strB.append("电子膨胀阀下次启动需要复位 ");
                    strB.append(";\n");
                    strB.append("外机故障，强制要求内机继续讯:");
                    if((val & (0x1<<3))!=0 ) strB.append("启用 "); else strB.append("禁用 ");
                    strB.append(";\n");
                    if((val & (0x1<<4))!=0 ) strB.append("电压限频;\n ");
                    if((val & (0x1<<5))!=0 ) strB.append("其他限频;\n ");
                    if((val & (0x1<<6))!=0 ) strB.append("压缩机保护;\n ");
                    if((val & (0x1<<7))!=0 ) strB.append("系统停机保护;\n");
                    strB.append("("+val+")");
                    return strB.toString();
                }
            };
            put(reg);
            return reg;
        };



        Dev(int Addr){
           this.Addr=(byte) Addr;
       }
        @Nullable
        @Override
        public Reg   get(@Nullable Object key) {
            if (super.containsKey(key))
                return super.get(key);
            else
                REG_NULL.setmVal(0x5A5A);
            return REG_NULL;
        }
        void put(Reg reg){
            put(reg.getID(),reg);
        }

    }

    class RegKtTemp30 extends   RegU16{

        public RegKtTemp30(int ID) {
            super(ID);
        }

        @NonNull
        @Override
        public String toString() {
            return  String.valueOf(getmVal()-30);
        }
    }
    public static class Dev_KT extends Dev {

        public RegU16 Reg_KT_Power = AddRegU16(6);
        public RegU16 Reg_KT_MS = AddRegU16(7);
        public RegU16 Reg_KT_FL = AddRegU16(8);
        public RegU16 Reg_KT_WD = AddRegU16(9);
        public RegU16 Reg_KT_FR = AddRegU16(10);
        public  Reg_502 Reg_KT_502=AddReg_5XX(new Reg_502());
        public  Reg_503 Reg_KT_503=AddReg_5XX(new Reg_503());
        public  Reg_504 Reg_KT_504=AddReg_5XX(new Reg_504());

        public Reg_TestMode Reg_KT_testMode =AddReg_TestMode(600);
     //   public RegU16_SF  Reg_KT_mbzs =(RegU16_SF)AddRegU16_SF(601,10,0);

        public RegU16 Reg_KT_mbzs =  AddRegU16_SF(601,10,0);
        public RegU16 Reg_KT_602 = AddRegU16(602);
        public RegU16 Reg_KT_603 = AddRegU16(603);
        public RegU16 Reg_KT_604 = AddRegU16(604);
        public RegU16 Reg_KT_605 = AddRegU16_Mathe(605,200,0);
        public RegU16 Reg_KT_606 = AddRegU16_Mathe(606,4,0);
        public RegU16 Reg_KT_607 = AddRegU16_Mathe(607,4,0);
        public RegU16 Reg_KT_608 = AddRegU16_Mathe(608,1,-40);
        public RegU16 Reg_KT_609 = AddRegU16_Mathe(609,1,-40);
        public RegU16 Reg_KT_610= AddRegU16(610);
        public RegU16 Reg_KT_611 = AddRegU16_Mathe(611,4,0);
        public RegU16 Reg_KT_612 = AddRegU16_Mathe(612,1,-30);
        public RegU16 Reg_KT_613 = AddRegU16_Mathe(613,1,-30);
        public RegU16 Reg_KT_614 = AddRegKT614();
        public RegU16 Reg_KT_615=  AddRegKT615();
        public Dev_KT(int Addr) {
            super(Addr);
        }

    }



   public    static class Reg_TestMode extends RegU16{
        static final  String[] strTestMode =new String[]{"正常运行","额定制冷","最大制冷","中间制冷",
                "最小制冷","其他制冷","低温制热","额定制热","中间制热","最小制热","高温制热能力测试","A工况","B工况","C工况", "E工况"};
        public Reg_TestMode(int ID) {
            super(ID);
        }

        @Override
        public String toString() {
            int val=getmVal();
            String str;
            if(strTestMode.length>val)
            {
                str=strTestMode[val]+"("+val+")";
            }
            else
            {
                str="错误模式("+val+")";
            }
            return str;
        }
    }


    public  static RegU16 REG_NULL = new RegU16(0xA5A5);
    private static final short Modbus_DieAddr_Index = 0;
    public static final short Modbus_cm_Index = 1;

public static abstract class  Reg<T> extends Observable {
        public  enum State{Set,From}
        protected int mVal;
        public Object Tag=null;
        public      abstract T getmVal();
        protected   abstract boolean setmValToInt(T mVal);

        final  public void setmVal(T mVal) {
            if( setmValToInt(mVal))
             {
                setChanged();
                notifyObservers(State.Set);
            }
         }
       protected int getToVal(){return mVal;}
        public void ToTxBuf(byte[] TxBuf,int Index) {
            int mVal =  getToVal();
            TxBuf[Index] = (byte) ((mVal >> 8) & 0x0ff);
            TxBuf[Index + 1] = (byte) ((mVal & 0x0ff));
         }

         public void FromRxBuf(byte[]RxBuf ,int Index) {
            int Val =((RxBuf[Index]&0xff)<<8)|(RxBuf[Index + 1]&0xff);
            if (mVal != Val ) {
                 this.mVal=Val;
                 setChanged();
                 notifyObservers(State.From);
             }
           }

        public int getID() {
            return ID;
        }
        protected String name;
        final private int ID;
        public Reg(int ID){
            this.ID=ID;
        }
        @NonNull
        @Override
        public String toString() {
            return (getmVal().toString());
        }
    }

    public interface RegTxRx {
        public abstract int ToTxBuf(byte[]TxBuf,  int Index);
        public abstract int FromRxBuf(byte[] Rxbuf,int Index);
    }

    public static  class  RegTempl extends Reg<Float>{
    public RegTempl(int ID) {
            super(ID);
        }
        @Override
        public Float getmVal() {
            float f;
            f=mVal&0xff;
            f=f+((mVal&0xff00)>>8)/10.0f;
            if((mVal&0xff00)!=0) f=-f;
            return f;
        }

        @Override
        public boolean setmValToInt(Float mVal) {
                this.mVal=0X5AA5;
                return false;
        }
    }

   public  static class Reg_502 extends  RegU16{
       public Reg_502() {
           super(502);
       }
      @Override
      public String toString() {
          StringBuilder str = new StringBuilder();
          int val = getmVal();
          if (val != 0) {
              str.append("故障502:("+ Integer.toHexString(val)  +")");
              if (((0x1 << 1) & val) != 0) str.append("E1（室内外机通讯故障） ");
              if (((0x1 << 2) & val) != 0) str.append("E2（T1传感器故障）");
              if (((0x1 << 3) & val) != 0) str.append("E3（T2传感器故障）");
              if (((0x1 << 4) & val) != 0) str.append("E4（T2B传感器故障）");
              if (((0x1 << 5) & val) != 0) str.append("E5（室外机故障）");
              if (((0x1 << 6) & val) != 0) str.append("E6（过零保护）");
              if (((0x1 << 7) & val) != 0) str.append("E7（EEPROM故障）");
              if (((0x1 << 8) & val) != 0) str.append("E8（风机失速故障）");
              if (((0x1 << 9) & val) != 0) str.append("E9（线控器通讯故障）");
              if (((0x1 << 0x0e) & val) != 0) str.append("EE（水位报警故障）");
          }
          return str.toString();
       }
   }
    public  static class Reg_503 extends  RegU16{
        public Reg_503() {
            super(503);
        }
        @Override
        public String toString() {
            StringBuilder str=new StringBuilder();
            int val=getmVal();
            if(val==0) return str.toString();
            str.append("故障503:("+Integer.toHexString(val)+")");
            if(((0x1<<3)&val)!=0) str.append("F3（3次过流保护不可恢复） ");
            if(((0x1<<4)&val)!=0) str.append("F4（T4故障）");
            if(((0x1<<6)&val)!=0) str.append("F6（T3故障）");
            if(((0x1<<7)&val)!=0) str.append("F7（二次测过流保护）");
            if(((0x1<<8)&val)!=0) str.append("F8（制热T2高温保护）");
            if(((0x1<<9)&val)!=0) str.append("F9（交流过压/欠压保护）");
            if(((0x1<<10)&val)!=0) str.append("H0（外机主板与驱动板通讯故障）");
            if(((0x1<<14)&val)!=0) str.append("H4（30分钟内出现3次P6保护）");
            if(((0x1<<15)&val)!=0) str.append("H5（30分钟内出现3次P2保护）");
            return str.toString();
        }
    }
    public  static class Reg_504 extends  RegU16{
        public Reg_504() {
            super(504);
        }
        @Override
        public String toString() {
            StringBuilder str=new StringBuilder();
            int val=getmVal();
            if(val==0) return str.toString();
            str.append("故障504:("+Integer.toHexString(val)+")");
            if(((0x1<<0)&val)!=0) str.append("H6（100分钟内出现3次P4保护） ");
            if(((0x1<<3)&val)!=0) str.append("H9（10分钟内出现2次P9保护）");
            if(((0x1<<7)&val)!=0) str.append("P3（一次侧过流保护）");
            if(((0x1<<8)&val)!=0) str.append("P4（排气温度过高保护）");
            if(((0x1<<9)&val)!=0) str.append("P5（T3高温保护）");
            if(((0x1<<13)&val)!=0) str.append("P9（直流风机故障）");
            return str.toString();
        }
    }
    public  static  class RegU16_SF extends RegU16{
        final int aVal;
        final int KVal;
        int ToVal;
        public RegU16_SF(int ID, int KVal,int aVal) {
            super(ID);
             this.aVal=aVal;
             this.KVal=KVal;
        }
        @Override
        public boolean setmValToInt(Integer Val) {
            int val=Val/KVal;
            if(ToVal==val) return  false;
            else{ToVal=val;   return true;}
        }
       @Override
        public Integer getmVal() {
            return (super.getmVal() *KVal+aVal) ;
        }
       @Override
        protected int getToVal() {
            return ToVal;
        }
    }



    public static class RegU16  extends Reg<Integer>{

        @Override
        public Integer getmVal() {
            return mVal;
        }
        @Override
        protected boolean setmValToInt(Integer mVal) {
            if(this.mVal==mVal)
            {
                return false;
            }else
            {
             this.mVal=mVal;
             return true;
            }
           }
     public  RegU16(int ID){
          super(ID);
      }
        @Override
        public String toString() {
            if(getmVal()>=0xfeff) return "----";
            return super.toString();
        }
    }
/*
    static abstract class ModBus_Cm {
        String Name = "名称";
        protected byte DevAddr;
        private final RegMap regMap;

        abstract  int HandleTx(byte[] Txs);

        abstract boolean HandleRx(int length);

        ModBus_Cm(int DevAddr) {
            this.DevAddr = (byte) DevAddr;
            this.regMap = DevAddr_RegMap.get(DevAddr);
        }

        ModBus_Cm(String Name, int DevAddr) {
            this.DevAddr = (byte) DevAddr;
            this.regMap = DevAddr_RegMap.get(DevAddr);
            this.Name = Name;
        }

        public RegTxRx GetReg(int RegAddr) {
            return regMap.get(RegAddr);
        }
    }
*/
    public static  int Modbus_CM_0x10_tx(Dev dev,int RegStartAddr, int Length,byte[]TxBuf){
        TxBuf[Modbus_DieAddr_Index] = (byte) dev.Addr;
        TxBuf[Modbus_cm_Index] = 0x10;
     //   RegMap regMap=dev;
        TxBuf[2] = U16HToByte(RegStartAddr);
        TxBuf[3] = U16LToByte(RegStartAddr);
        TxBuf[4] = U16HToByte(Length);
        TxBuf[5] = U16LToByte(Length);
        TxBuf[6] = (byte) (Length * 2);
        for (int i = 0; i < Length; i++) {
            dev.get(RegStartAddr + i).ToTxBuf(TxBuf,7 + i * 2);
        }
        SetCRC(TxBuf,7 + Length * 2);
        return 9 + Length * 2;
    }
  public static boolean Modbus_CM_0x10_check(Dev dev  ,int RegStartAddr, int Length,byte[] RxBuf){
        return (RxBuf.length>=8 && RxBuf[0]==dev.Addr && RxBuf[1]==0x10  && RxBuf[2]==U16HToByte(RegStartAddr)&& RxBuf[3]==U16LToByte(RegStartAddr)
                && RxBuf[4]==U16HToByte(Length)&& RxBuf[5]==U16LToByte(Length) && CheckCRC(RxBuf,6)) ;
  }
    public static int Modbus_CM_0x03_Tx(Dev dev, int RegStartAddr, int Length, byte[] TxBuf){
        TxBuf[0] = (byte) dev.Addr;
        TxBuf[1] = 0x03;
        TxBuf[2] = U16HToByte(RegStartAddr);
        TxBuf[3] = U16LToByte(RegStartAddr);
        TxBuf[4] = U16HToByte(Length);
        TxBuf[5] = U16LToByte(Length);
        SetCRC(TxBuf,6);
        return 8;
     }

    public static boolean modbus_CM_0x03_RX_Check(Dev dev, int RegStartAddr, int Length, byte[] RxBuf){
        return (dev.Addr == RxBuf[Modbus_DieAddr_Index]) && RxBuf[Modbus_cm_Index] == 0x03
                && (RxBuf[2] / 2) == Length && CheckCRC(RxBuf, 3 + Length * 2);
    }

    public static void Modbus_CM_0x03_RX_From(Dev dev, int RegStartAddr, int Length, byte[] RxBuf) {
        for (int i = 0; i < Length; i++) {
            dev.get(i + RegStartAddr).FromRxBuf(RxBuf, 3 + (i * 2));
        }
    }

    static byte U16HToByte(int i) {
        return (byte) ((i & 0xff00) >> 8);
    }

    static byte U16LToByte(int i) {
        return (byte) (i & 0xff);
    }

    static Timer timer = new Timer();

    public static void SetCRC(byte[] bytes, int Length) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < Length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        bytes[Length] = (byte) (CRC & 0xff);
        bytes[Length + 1] = (byte) (CRC >> 8 & 0Xff);
    }

    public static boolean CheckCRC(byte[] bytes, int Length) {

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < Length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return (bytes[Length] == (byte) (CRC & 0xff)) && (bytes[Length + 1] == (byte) ((CRC >> 8) & 0Xff));
    }
    public static class RegMap extends HashMap<Integer, Reg> {

    }

    static class RegU16_0x01 extends RegU16 {
        public RegU16_0x01(int Val) {
            super(Val);
        }
}

    static class KT_CS {
        public int WD;
        public int FL;

        public KT_CS(int WD, int FL) {
            this.WD = WD;
            this.FL = FL;
        }
    }
}
