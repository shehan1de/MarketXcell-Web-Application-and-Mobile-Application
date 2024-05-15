package com.example.marketxcell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketxcell.R;
import com.example.marketxcell.model.OrderModel;

import java.util.List;

public class CompleteOrderAdapter extends RecyclerView.Adapter<CompleteOrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> list;

    public CompleteOrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CompleteOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompleteOrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.completeorder_list,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull CompleteOrderAdapter.ViewHolder holder, int position) {
        holder.textViewOrderid.setText(list.get(position).getOrderId());
        holder.textViewOrderValue.setText("Value: "+list.get(position).getOrderValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderid, textViewOrderValue;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderid = itemView.findViewById(R.id.completeorder_orderid);
            textViewOrderValue = itemView.findViewById(R.id.completeorder_ordervalue);
        }
    }
}
