package com.webege.profause.excelsdiary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.helper.Util;
import com.webege.profause.excelsdiary.model.Measurement;

import java.util.ArrayList;

/**
 * Created by Emmanuel Mensah on 8/22/2016.
 */
public class MeasurementListAdapter extends RecyclerView.Adapter<MeasurementListAdapter.MeasuremntListViewHolder>{
    ClickListener clickListener;
View v;
    LayoutInflater inflater;
    Context context;
    ArrayList<Measurement> measurementList = new ArrayList<>();

    public MeasurementListAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    public ArrayList<Measurement> getMeasurementList() {
        return measurementList;
    }

    public void setMeasurementList(ArrayList<Measurement> measurementList) {
        this.measurementList = measurementList;
        notifyDataSetChanged();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MeasuremntListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = inflater.inflate(R.layout.measurement_list_row, parent, false);
        MeasuremntListViewHolder holder = new MeasuremntListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MeasuremntListViewHolder holder, int position) {
Measurement current =measurementList.get(position);
        holder.tvBodypart.setText(current.getName());
        switch (current.getUnit()){
            case 1:
                holder.tvMeasuredValueIn.setText(Util.FromCmToInches(current.getValue()));
                holder.tvMeasuredValue.setText(current.getValue()+":cm");
                break;
            case 2:
                holder.tvMeasuredValueIn.setText(current.getValue()+":inches");
                holder.tvMeasuredValue.setText(Util.FromInchesToCm(current.getValue()));
                break;
            default:
                holder.tvMeasuredValueIn.setText(current.getValue()+":inches");
                holder.tvMeasuredValue.setText(current.getValue()+":cm");
        }
    }

    @Override
    public int getItemCount() {
        return measurementList==null?0:measurementList.size();
    }

    public class MeasuremntListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBodypart,tvMeasuredValue,tvMeasuredValueIn;
        ImageView ivDelBtn,ivEditBtn;

        public MeasuremntListViewHolder(View itemView) {
            super(itemView);
            tvBodypart= (TextView) itemView.findViewById(R.id.tvBodypart);
            tvMeasuredValue= (TextView) itemView.findViewById(R.id.tvMeasuredValueCm);
            tvMeasuredValueIn= (TextView) itemView.findViewById(R.id.tvMeasuredValueIn);
            ivDelBtn= (ImageView) itemView.findViewById(R.id.ivDelBtn);
            ivEditBtn= (ImageView) itemView.findViewById(R.id.ivEditBtn);
            ivDelBtn.setOnClickListener(this);
            ivEditBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view,getAdapterPosition(),
                        new Measurement(measurementList.get(getAdapterPosition()).getName(),
                                measurementList.get(getAdapterPosition()).getValue(),
                                measurementList.get(getAdapterPosition()).getUnit()));
            }
        }
    }

    public interface ClickListener {
        public void itemClicked(View view, int position, Measurement m);
    }
}
