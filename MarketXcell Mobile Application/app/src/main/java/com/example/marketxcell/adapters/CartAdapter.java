package com.example.marketxcell.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketxcell.CartActivity;
import com.example.marketxcell.R;
import com.example.marketxcell.model.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartModel> list;
    private String userId;
    private String cartId;
    private float totalAmount = 0;
    private float totalcommission = 0;

    public CartAdapter(Context context, List<CartModel> list, String userId, String cartId) {
        this.context = context;
        this.list = list;
        this.userId = userId;
        this.cartId = cartId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getProductImg()).into(holder.cartImg);
        holder.cartTitle.setText(list.get(position).getProductName());
        holder.cartPrice.setText("Rs: "+list.get(position).getUnitPrice());
        holder.cartqty.setText("Quantity: "+list.get(position).getQty());

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cartItems").child(userId);
                cartRef.child(cartId).child(list.get(position).getCartId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                DatabaseReference productref = FirebaseDatabase.getInstance().getReference("product").child(list.get(position).getProductId());
                productref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            String cartcount = dataSnapshot.child("cartcount").getValue(String.class);
                            int cartItemqtyinInt = Integer.parseInt(list.get(position).getQty());
                            if(cartcount != null){
                                int cartcountinInt = Integer.parseInt(cartcount);
                                int updatedcartcount = cartcountinInt - cartItemqtyinInt;
                                String updatedcartcountinstring = Integer.toString(updatedcartcount);
                                Map<String, Object> cartcountmap = new HashMap<>();
                                cartcountmap.put("cartcount", updatedcartcountinstring);
                                productref.updateChildren(cartcountmap);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intent = new Intent(context, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });

        // Calculate Total Order value
        String itemTtotal = list.get(position).getTotal();
        float itemTotalInfloat = Float.parseFloat(itemTtotal);
        totalAmount = totalAmount + itemTotalInfloat;

        //Calculate Total Commission value
        String itemtotalCommission = list.get(position).getTotalCommission();
        float itemtotalCommissionInfloat = Float.parseFloat(itemtotalCommission);
        totalcommission = totalcommission + itemtotalCommissionInfloat;


        Intent intent = new Intent("CartTotalAmount");
        intent.putExtra("totalAmount",totalAmount);
        intent.putExtra("totalCommission",totalcommission);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImg, deleteImg;
        TextView cartTitle, cartPrice, cartqty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImg = itemView.findViewById(R.id.cart_img);
            cartTitle = itemView.findViewById(R.id.cart_title);
            cartPrice = itemView.findViewById(R.id.cart_price);
            cartqty = itemView.findViewById(R.id.cart_qty);
            deleteImg = itemView.findViewById(R.id.cart_delete);
        }
    }
}
