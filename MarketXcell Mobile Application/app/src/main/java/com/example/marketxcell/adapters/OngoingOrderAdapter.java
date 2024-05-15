package com.example.marketxcell.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketxcell.OrdersActivity;
import com.example.marketxcell.R;
import com.example.marketxcell.model.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingOrderAdapter extends RecyclerView.Adapter<OngoingOrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> list;

    public OngoingOrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OngoingOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OngoingOrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ongoingorder_list,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull OngoingOrderAdapter.ViewHolder holder, int position) {
        holder.textViewOrderid.setText(list.get(position).getOrderId());
        holder.textViewOrderValue.setText("Value: "+list.get(position).getOrderValue());
        holder.textViewOrderStatus.setText(list.get(position).getOrderStatus());
        String dbId = list.get(position).getOrderDbId();
        String status = list.get(position).getOrderStatus();


        holder.receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.equals("Processing")){
                    Toast.makeText(context, "The order did not deliver from the warehouse yet!.", Toast.LENGTH_SHORT).show();
                } else{
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("orderStatus", "Collected");
                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(dbId);
                    ordersRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Order status changed successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, OrdersActivity.class);
                                context.startActivity(intent);
                            } else{
                                Toast.makeText(context, "Something went wrong!, Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderid, textViewOrderValue, textViewOrderStatus;
        Button receiveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderid = itemView.findViewById(R.id.ongoingorder_orderid);
            textViewOrderValue = itemView.findViewById(R.id.ongoingorder_orderValue);
            textViewOrderStatus = itemView.findViewById(R.id.ongoingorder_received);
            receiveButton = itemView.findViewById(R.id.ongoingOrder_btn_receive);
        }
    }
}
