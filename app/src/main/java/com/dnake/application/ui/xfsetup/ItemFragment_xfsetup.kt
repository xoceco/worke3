package com.dnake.application.ui.xfsetup

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dnake.application.ModBus
import com.dnake.application.MyApp
import com.dnake.application.R
import com.dnake.application.ui.home.HomeViewModel

/**
 * A fragment representing a list of Items.
 */
open class  ItemFragment_xfsetup : Fragment() {
    lateinit var  homeViewModel: HomeViewModel;
    private var columnCount = 1
    lateinit var   myapp: MyApp
    lateinit var   myAdapter: MyItemRecyclerViewAdapter
    lateinit var tVxf:TextView
    lateinit var tVhf:TextView
    lateinit var buttonDW: Button
    var dw=4;
    val what_Update=1
    val what_sjzsQuery_S=2
    val what_sjzsQuery_U=3
    val what_xfdw_U=4
    val dwStr= arrayOf("关机", "1档", "2档", "3档", "4档", "5档", "6档", "7档")

    inner class MyHandler: Handler() {
        override fun handleMessage(msg: Message) {
          when (msg.what) {
              what_Update -> {
                  myAdapter.notifyDataSetChanged()
              }
              what_sjzsQuery_U -> {
                  tVxf.setText((ModBus.Dev_xf.Reg_xfSjZs.getmVal()).toString())
                  tVhf.setText((ModBus.Dev_xf.Reg_hfSjZs.getmVal()).toString())
              }
              what_xfdw_U -> {
                  ModBus.Dev_xf.Reg_XF_Power.setmVal(if (dw == 0) 0 else 1)
                  ModBus.Dev_xf.Reg_XF_FAN.setmVal(dw)
                  buttonDW.text = dwStr[dw]
                  myAdapter.notifyDataSetChanged()
              }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }
   val hander= MyHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myapp = activity?.application as MyApp;
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        myapp.XfsjzsQTask.setOnTsak {
            hander.post {
                tVxf.setText((ModBus.Dev_xf.Reg_xfSjZs.getmVal()).toString())
                tVhf.setText((ModBus.Dev_xf.Reg_hfSjZs.getmVal()).toString()) }
        }
        homeViewModel.mIsUpdatexfKzUi.observe(this,{
            if(it){myAdapter.notifyDataSetChanged()}
        })
        myapp.XfzsQTask.setOnTsak {
            hander.post {
                if (it) {
                    for (i in 1..7) {
                        DummyContent.ITEMS[i - 1].content = ModBus.Dev_xf.Reg_xfzsS[i].getmVal();
                        DummyContent.ITEMS[i + 6].content = ModBus.Dev_xf.Reg_xfzshS[i].getmVal();
                    }
                    myAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this.context, "读取设置转速失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

        DummyContent.ITEMS.removeAll(DummyContent.ITEMS)
        val sp = activity?.getSharedPreferences("mysp", AppCompatActivity.MODE_PRIVATE)
        // Add some sample items.
        for (i in 1..7) {
            var name=StringBuffer()
            name.append("新风")
            name.append(i)
            name.append("档:")
            val str=name.toString();
            val zs= sp!!.getInt(str, 150)
            DummyContent.ITEMS.add(DummyContent.DummyItem(str, zs, ""))
        }
        for (i in 1..7) {
            var name=StringBuffer()
            name.append("回风")
            name.append(i)
            name.append("档:")
            val str=name.toString();
            val zs= sp!!.getInt(str, 125)
            DummyContent.ITEMS.add(DummyContent.DummyItem(str, zs, ""))
        }

    }

    val buttonWOnClickListener=  View.OnClickListener() {
        val sp = requireActivity().getSharedPreferences("mysp", AppCompatActivity.MODE_PRIVATE)
        // Add some sample items.
        val edit: SharedPreferences.Editor? = sp?.edit();
        edit?.let {
            for (i in 1..7) {
                var name = StringBuffer()
                name.append("新风")
                name.append(i)
                name.append("档:")
                val str = name.toString();
                val Val= DummyContent.ITEMS[i - 1].content
                ModBus.Dev_xf.Reg_xfzsS[i].setmVal(Val)
                it.putInt(str, Val)
            }
            for (i in 1..7) {
                var name = StringBuffer()
                name.append("回风")
                name.append(i)
                name.append("档:")
                val str = name.toString();
                val Val= DummyContent.ITEMS[i + 6].content
                ModBus.Dev_xf.Reg_xfzshS[i].setmVal(Val)
                it.putInt(str, Val)
            }
            it.commit();
            myapp.xfzsKzTask.setTimer(10)
         }
    }

    val buttonROnClickListener= View.OnClickListener(){
        myapp.XfzsQTask.setTimer(10)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val supview = inflater.inflate(R.layout.fragment_item_xfsetup_list, container, false)
        val view:RecyclerView=supview.findViewById(R.id.list)
        val buttonW=supview.findViewById<Button>(R.id.buttonW)
        val buttonR=supview.findViewById<Button>(R.id.buttonR)

        buttonR.setOnClickListener(buttonROnClickListener)
        buttonW.setOnClickListener(buttonWOnClickListener)
        tVxf=supview.findViewById(R.id.textView_xf)
        tVhf=supview.findViewById(R.id.textView_hf)

        buttonDW= supview.findViewById<Button>(R.id.buttonDW)
        dw = ModBus.Dev_xf.Reg_XF_FAN.getmVal()
        buttonDW.setOnClickListener {
        AlertDialog.Builder(requireContext()).
        setSingleChoiceItems(dwStr, dw,
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dw = which
                    this.hander.sendEmptyMessage(what_xfdw_U)
                    dialog.dismiss()
                }).setTitle("档位选择").

        show()
       }

        // Set the adapter

            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                myAdapter= MyItemRecyclerViewAdapter(DummyContent.ITEMS);
                adapter=myAdapter
            }
         return supview
    }

    @Override
    override fun onResume() {
        super.onResume()
        myapp.XfsjzsQTask.setTimer(10, 100)
        myapp.xfQueryTask.setTimer(20)
        Log.i("TEST", "myapp.xfQueryTask.setTimer(20)")
     //   hander.sendEmptyMessageDelayed(what_xfdw_U,1000)
        myAdapter.notifyDataSetChanged();
    }

    @Override
    override fun onPause() {
        super.onPause()
        myapp.XfsjzsQTask.setTimer(0, 0)
    }
}
