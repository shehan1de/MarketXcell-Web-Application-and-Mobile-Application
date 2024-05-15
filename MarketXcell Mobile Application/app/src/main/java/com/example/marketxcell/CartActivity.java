package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketxcell.adapters.CartAdapter;
import com.example.marketxcell.model.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    List<CartModel> cartModelList;
    RecyclerView cartRecyclerView;
    Drawable drawablebutton3, drawablebutton4;
    TextView totalTextView, commissionTextView, totalafterCouponTextView;
    LinearLayout linearLayoutmesg, linearLayoutbody;
    Button couponApplyButton, checkoutButton;
    EditText coupencodeEditText;
    CartAdapter cartAdapter;
    String userId ;
    String cardrefid;
    Float agentTotalSales;
    float totalbill,totalcommission;
    float CouponDiscountValve = 0.00f;
    String salesCondition;
    String offerValue;
    String clamedofferDBId = null;
    String cardItemId;
    String couponCode = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("agentid", "");
        String agentsales = sharedPreferences.getString("agentsales", "");
        agentTotalSales = Float.parseFloat(agentsales);
        cardrefid = sharedPreferences.getString("cartrefid", "");

        totalTextView = (TextView) findViewById(R.id.cartPage_amout);
        commissionTextView = (TextView) findViewById(R.id.cartPage_totalCommission);
        totalafterCouponTextView = (TextView) findViewById(R.id.cartPage_amoutaftercode);
        couponApplyButton = (Button) findViewById(R.id.cartPage_couponapplyBtn);
        checkoutButton = (Button) findViewById(R.id.cartPage_checkout_btn);
        coupencodeEditText = (EditText) findViewById(R.id.cartPage_txt_coupencodefield);
        linearLayoutmesg = (LinearLayout) findViewById(R.id.cartPage_message);
        linearLayoutbody = (LinearLayout) findViewById(R.id.cartPage_body);
        drawablebutton4 = getResources().getDrawable(R.drawable.custom_button4);
        drawablebutton3 = getResources().getDrawable(R.drawable.custom_button3);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("CartTotalAmount"));

        cartRecyclerView();
        menucomponents();

        couponApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                couponCode = coupencodeEditText.getText().toString();

                if(couponCode.isEmpty()){
                    Toast.makeText(CartActivity.this, "Please Enter a Coupon Code", Toast.LENGTH_SHORT).show();
                } else if (couponCode.length()<=1) {
                    Toast.makeText(CartActivity.this, "Please a Valid Coupon Code", Toast.LENGTH_SHORT).show();
                } else{
                    if(clamedofferDBId == null){
                        checkClaim(userId,couponCode);
                    }else{
                        removeOffer();
                        couponApplyButton.setText("Apply");
                        coupencodeEditText.setText("");
                        couponApplyButton.setBackground(drawablebutton3);
                    }
                }
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("TotalCommission",totalcommission);
                intent.putExtra("totalsales",totalbill);
                intent.putExtra("CouponDiscountValve", CouponDiscountValve);//0
                intent.putExtra("couponCode", couponCode);
                startActivity(intent);
            }
        });
    }


    public void cartRecyclerView(){

        cartModelList = new ArrayList<>();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cartItems").child(userId).child(cardrefid);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        cardItemId = agentSnapshot.getKey().toString();
                        String productid = agentSnapshot.child("ProductDBId").getValue(String.class);
                        String productImg = agentSnapshot.child("ProductImg").getValue(String.class);
                        String productName = agentSnapshot.child("ProductName").getValue(String.class);
                        String qty = agentSnapshot.child("qty").getValue(String.class);
                        String total = agentSnapshot.child("total").getValue(String.class);
                        String unitPrice = agentSnapshot.child("unitPrice").getValue(String.class);
                        String totalCommision = agentSnapshot.child("totalcommision").getValue(String.class);
                        CartModel cartModel = new CartModel(productid,productName,productImg,qty,total,unitPrice,totalCommision,cardItemId);
                        cartModelList.add(cartModel);
                    }


                    cartAdapter = new CartAdapter(CartActivity.this,cartModelList,userId,cardrefid);
                    cartRecyclerView = (RecyclerView) findViewById(R.id.cartRecycler);
                    cartRecyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                    cartRecyclerView.setAdapter(cartAdapter);
                    cartAdapter.notifyDataSetChanged();
                } else {
                    linearLayoutbody.setVisibility(View.GONE);
                    linearLayoutmesg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void checkClaim(String userid, String coupenCode){
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference("offer");
        couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                        String DPcouponCode = offerSnapshot.child("couponid").getValue(String.class);
                        String DPofferValue = offerSnapshot.child("offervalue").getValue(String.class);
                        if(DPcouponCode.equals(coupenCode)){
                            
                            DataSnapshot claimSnapshot = offerSnapshot.child("claim");
                            if (claimSnapshot.exists()) {
                                String claimedidNumber = claimSnapshot.child(userid).getValue(String.class);
                                if(claimedidNumber != null){
                                    coupencodeEditText.setText("");
                                    Toast.makeText(CartActivity.this, "You Already Claim This Coupen Code", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                Log.d("Claim", "Claim node does not exist");
                            }
                            salesCondition = offerSnapshot.child("totalsales").getValue(String.class);
                            offerValue = offerSnapshot.child("offervalue").getValue(String.class);
                            if(checkOfferCondition(salesCondition)){
                                Toast.makeText(CartActivity.this, "Offer Applied Successfully", Toast.LENGTH_SHORT).show();
                                clamedofferDBId = offerSnapshot.getKey().toString();
                                DatabaseReference claimRef = offerSnapshot.child("claim").getRef();
                                claimRef.updateChildren(Collections.singletonMap(userId,"true"));
                                updateTotalAmount(DPofferValue);
                                couponApplyButton.setText("Remove");
                                coupencodeEditText.setText(coupenCode);
                                couponApplyButton.setBackground(drawablebutton4);
                            }
                        } else{
                            Toast.makeText(CartActivity.this, "Invalid Coupon", Toast.LENGTH_SHORT).show();
                            coupencodeEditText.setText("");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean checkOfferCondition(String salesCondition){
        Float intSalesCondition = Float.parseFloat(salesCondition);
        if(agentTotalSales>=intSalesCondition){
            return true;
        }
        Toast.makeText(this, "You're Not Eligible For This Offer", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void removeOffer(){
        DatabaseReference removeOfferref = FirebaseDatabase.getInstance().getReference("offer").child("-Np6DW3_bdZiY8ULFI3B").child("claim");
        removeOfferref.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    CouponDiscountValve = 0.00f;
                    Toast.makeText(CartActivity.this, "Offer Removed successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, CartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else{
                    Toast.makeText(CartActivity.this, "Offer Removed failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateTotalAmount(String Value){

        float offerValue = Float.parseFloat(Value);
        
        CouponDiscountValve = totalbill * (offerValue/100);
        totalTextView.setPaintFlags(totalTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String newAmountinString = Float.toString(totalbill-CouponDiscountValve);
        totalafterCouponTextView.setText(newAmountinString);
    }


    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             totalbill = intent.getFloatExtra("totalAmount",0);
             totalcommission = intent.getFloatExtra("totalCommission",0);

            totalTextView.setText("Rs "+totalbill);
            commissionTextView.setText("Rs "+totalcommission);
        }
    };

    public void menucomponents(){

        LinearLayout cartButton = findViewById(R.id.cartbtn);
        LinearLayout MarketButton = findViewById(R.id.marketbtn);
        LinearLayout OrdersButton = findViewById(R.id.ordersbtn);
        LinearLayout ProfileButton = findViewById(R.id.Profilebtn);

        ImageView activeIcon = cartButton.findViewById(R.id.imageView6);
        TextView activeText = cartButton.findViewById(R.id.texttituyle);


        activeIcon.setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        activeText.setTextColor(getResources().getColor(R.color.PrimaryColor));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

}