package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketxcell.adapters.CompleteOrderAdapter;
import com.example.marketxcell.adapters.OngoingOrderAdapter;
import com.example.marketxcell.adapters.ProductAdapter;
import com.example.marketxcell.model.OrderModel;
import com.example.marketxcell.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class OrdersActivity extends AppCompatActivity {

    Button buttonCompleteOrder, buttonOngoingOrder;
    RecyclerView recyclerViewOrders;
    OrderModel completeOrderModel;
    OrderModel ongoingOrderModel;
    CompleteOrderAdapter completeOrderAdapter;
    List<OrderModel> completeOrderModelList;
    OngoingOrderAdapter ongoingOrderAdapter;
    List<OrderModel> ongoingOrderModelList;
    GifImageView gifImageView;
    String userId;
    TextView textViewerrormessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        buttonCompleteOrder = findViewById(R.id.orderpage_completeBtn);
        buttonOngoingOrder = findViewById(R.id.orderpage_OngingBtn);
        recyclerViewOrders = findViewById(R.id.orderpage_recyclerView);
        gifImageView = findViewById(R.id.orderpage_loadingGif);
        textViewerrormessage = findViewById(R.id.cartPage_message);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("agentid", "973122584V");

        viewOngoingOrders();
        menucomponents();

        buttonOngoingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOngoingOrders();
            }
        });

        buttonCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewOrders.setVisibility(View.GONE);
                gifImageView.setVisibility(View.VISIBLE);
                viewCompleteOrders();
            }
        });

    }

    public void viewOngoingOrders(){

        textViewerrormessage.setVisibility(View.GONE);
        ongoingOrderModelList = new ArrayList<>();
        recyclerViewOrders.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String dbagentid = agentSnapshot.child("agentId").getValue(String.class);
                        if(dbagentid.equals(userId)){
                            String orderDbId = agentSnapshot.getKey().toString();
                            String orderId = agentSnapshot.child("orderId").getValue(String.class);
                            String orderValue = agentSnapshot.child("orderValue").getValue(String.class);
                            String orderStatus = agentSnapshot.child("orderStatus").getValue(String.class);
                            if(orderStatus !=null){
                                if(!orderStatus.equals("Collected")){
                                    ongoingOrderModel = new OrderModel(orderId,orderValue,orderStatus,orderDbId);
                                    ongoingOrderModelList.add(ongoingOrderModel);
                                }
                            }
                        }
                    }
                }
                
                if(ongoingOrderModelList.isEmpty()){
                    textViewerrormessage.setText("No Ongoing Orders");
                    textViewerrormessage.setVisibility(View.VISIBLE);
                }

                ongoingOrderAdapter = new OngoingOrderAdapter(OrdersActivity.this,ongoingOrderModelList);
                recyclerViewOrders.setLayoutManager( new LinearLayoutManager(OrdersActivity.this));
                recyclerViewOrders.setAdapter(ongoingOrderAdapter);
                ongoingOrderAdapter.notifyDataSetChanged();
                recyclerViewOrders.setVisibility(View.VISIBLE);
                gifImageView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void viewCompleteOrders(){

        textViewerrormessage.setVisibility(View.GONE);
        completeOrderModelList = new ArrayList<>();

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String dbagentid = agentSnapshot.child("agentId").getValue(String.class);
                        if(dbagentid.equals(userId)){
                            String orderDbId = agentSnapshot.getKey().toString();
                            String orderId = agentSnapshot.child("orderId").getValue(String.class);
                            String orderValue = agentSnapshot.child("orderValue").getValue(String.class);
                            String orderStatus = agentSnapshot.child("orderStatus").getValue(String.class);
                            if(orderStatus !=null){
                                if(orderStatus.equals("Collected")){
                                    completeOrderModel = new OrderModel(orderId,orderValue,"Still Processing",orderDbId);
                                    completeOrderModelList.add(completeOrderModel);
                                }
                            }
                        }
                    }
                }

                if(completeOrderModelList.isEmpty()){
                    textViewerrormessage.setText("No Complete Orders");
                    textViewerrormessage.setVisibility(View.VISIBLE);
                }

                completeOrderAdapter = new CompleteOrderAdapter(OrdersActivity.this, completeOrderModelList);
                recyclerViewOrders.setLayoutManager( new LinearLayoutManager(OrdersActivity.this));
                recyclerViewOrders.setAdapter(completeOrderAdapter);
                completeOrderAdapter.notifyDataSetChanged();
                recyclerViewOrders.setVisibility(View.VISIBLE);
                gifImageView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void menucomponents(){

        LinearLayout cartButton = findViewById(R.id.cartbtn);
        LinearLayout MarketButton = findViewById(R.id.marketbtn);
        LinearLayout OrdersButton = findViewById(R.id.ordersbtn);
        LinearLayout ProfileButton = findViewById(R.id.Profilebtn);

        ImageView activeIcon = OrdersButton.findViewById(R.id.imageViedw6);
        TextView activeText = OrdersButton.findViewById(R.id.texttitl2e);


        activeIcon.setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        activeText.setTextColor(getResources().getColor(R.color.PrimaryColor));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}