package com.dnake.application.ui.ktsetup;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dnake.application.R;
import com.dnake.application.ui.MainViewModel;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapterAir extends RecyclerView.Adapter<com.dnake.application.ui.ktsetup.MyItemRecyclerViewAdapterAir.ViewHolder> {

    private final List<MainViewModel.ItemList> mValues;
    int ColorC;

    public MyItemRecyclerViewAdapterAir(List<MainViewModel.ItemList> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_air, parent, false);
        ColorC=view.getResources().getColor( R.color.colorC);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position%2==0) holder.mView.setBackgroundColor(ColorC);
        else holder.mView.setBackgroundColor(0x00000000);
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).reg.toString());
        holder.mContentView1.setText(mValues.get(position).dw);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mContentView1;
        public MainViewModel.ItemList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mContentView1 = (TextView) view.findViewById(R.id.content1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}