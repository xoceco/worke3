package com.dnake.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dnake.application.MyApp.RxTx_Uart;
import com.dnake.application.ui.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    boolean b;
    private AppBarConfiguration mAppBarConfiguration;
    MainViewModel mainViewModel;
    MyApp myApp;
    int colorRx =Color.BLUE;
    int rxingTime=0;
    int txingTime=0;
    int colorTx =Color.BLUE;
    final int wath_Rxing=0;
    final int wath_Txing=1;
    final int wath_Rxend=2;
    final int wath_Txend=3;
    TextView textViewRx;
    TextView textViewTx;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case wath_Rxing:
                    textViewRx.setBackgroundColor(Color.GREEN);
                    if(colorRx!=Color.GREEN){rxingTime=5;}
                    break;
                case wath_Rxend:
                     handler.sendEmptyMessageDelayed(wath_Rxend,100);
                     if(rxingTime!=0){rxingTime--;}
                    else {textViewRx.setBackgroundColor(colorRx);}
                    break;
                case wath_Txing:
                    textViewTx.setBackgroundColor(Color.GREEN);
                    if(colorTx!=Color.GREEN){txingTime=5;}
                    break;
                case  wath_Txend:
                    handler.sendEmptyMessageDelayed(wath_Txend,100);
                    if(txingTime!=0){txingTime--;}
                    else {textViewTx.setBackgroundColor(colorTx);}
                    break;
             }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(view -> mainViewModel.showTable.postValue(b=!b));
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_ktsetup)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        myApp=(MyApp) getApplication();
        setMyAppRxTx();
        mainViewModel= new ViewModelProvider(this).get(MainViewModel.class);

        textViewRx=findViewById(R.id.TextViewR);
        textViewTx=findViewById(R.id.TextViewT);
        myApp.netState.observe(this, new Observer<MyApp.NetState>() {

            @Override
            public void onChanged(MyApp.NetState netState) {
             if(netState== MyApp.NetState.RxNoOk){colorRx =Color.RED;}
               else if(netState== MyApp.NetState.RxOk){colorRx =Color.BLUE;}
               else if(netState== MyApp.NetState.Rxs){ handler.sendEmptyMessage(wath_Rxing);}

                if(netState== MyApp.NetState.TxNoOk){ colorTx =Color.RED;}
                else if(netState== MyApp.NetState.TxOk){colorTx =Color.BLUE;}
                else if(netState== MyApp.NetState.Txs) handler.sendEmptyMessage(wath_Txing);
            }
        });
        handler.sendEmptyMessage(wath_Rxend);
        handler.sendEmptyMessage(wath_Txend);
        MyApp.setUpChange.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setMyAppRxTx();
            }
        });
        Log.i("TEST1","MainActivity Create");
    }
    void  setMyAppRxTx()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String  strDriver= sharedPreferences.getString(getResources().getString(R.string.driver_select),getResources().getStringArray(R.array.mb)[1]);
        Log.i("TEST1","strDriver..... "+strDriver);
        try {
            if(strDriver.equals(getResources().getStringArray(R.array.mb)[1])) {
                Log.i("TEST1","strDriver= Udp");
                String   txIp = sharedPreferences.getString(getString(R.string.txIp), "192.168.4.1");
                int txPort = Integer.valueOf(sharedPreferences.getString(getString(R.string.txPort), "7000"));
                int rxPort = Integer.valueOf(sharedPreferences.getString(getString(R.string.rxPort), "7001"));
               myApp.setRxTx(new MyApp.RxTx_Udp(txIp,txPort,rxPort));
            }
            else
            {
                Log.i("TEST1","strDriver= Uart");
                int baudRate=Integer.parseInt(sharedPreferences.getString("baud_rate","9600"));
                myApp.setRxTx(new  RxTx_Uart(this,9600,(byte)8,(byte)1,(byte)0,(byte)0));
            }
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                Log.i("test1"," startActivity(new Intent(this,SettingsActivity.class));");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TEST1","MainActivity onResume");
    }

}