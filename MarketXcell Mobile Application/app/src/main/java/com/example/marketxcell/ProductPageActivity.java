package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.marketxcell.adapters.ProductAdapter;
import com.example.marketxcell.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProductPageActivity extends AppCompatActivity {

    String productId, productName, productImg, commissionRate, productDbId;
    LinearLayout backBtn;
    ImageView productimg;
    TextView name, categoryName, price, qty, description;
    Button addToCart, incbtn, decbtn;
    String cartcountinString, stockinString, priceinString;
    ProductModel productModel = null;

    String userId;
    String cardrefid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("agentid", "973122584V");
        cardrefid = sharedPreferences.getString("cartrefid", "test9qf-wu");

        menucomponents();

        final Object obj = getIntent().getSerializableExtra("detials");

        if (obj instanceof ProductModel) {
            productModel = (ProductModel) obj;
        }

        backBtn = (LinearLayout) findViewById(R.id.productpage_backbtn);
        productimg = findViewById(R.id.productpage_prodimg);
        name = findViewById(R.id.Productpage_prodname);
        categoryName = findViewById(R.id.productpage_catname);
        description = findViewById(R.id.productpage_description);
        price = findViewById(R.id.productpage_price);
        qty = findViewById(R.id.productpage_text_qty);
        addToCart = findViewById(R.id.productpage_btn_addtocart);
        incbtn = findViewById(R.id.productpage_btn_inc);
        decbtn = findViewById(R.id.productpage_btn_dec);


        if (productModel != null) {
            productId = productModel.getProductid();
            Glide.with(getApplicationContext()).load(productModel.getProductIMG()).into(productimg);
            productImg = productModel.getProductIMG();
            productName = productModel.getProductname();
            productDbId = productModel.getProductDBId();
            name.setText(productName);
            categoryName.setText(productModel.getCategoryname());
            price.setText(productModel.getProductprice() + " - " + productModel.getCommissionRate() + "% Commission");
            description.setText(productModel.getProductdescription());
            priceinString = productModel.getProductprice();
            commissionRate = productModel.getCommissionRate();
        }


        incbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curretValue = qty.getText().toString();
                int value = Integer.parseInt(curretValue);
                if (value >= 100) {
                    Toast.makeText(ProductPageActivity.this, "Minimum 100 Units Only", Toast.LENGTH_SHORT).show();
                    return;
                }
                value++;
                qty.setText(String.valueOf(value));
            }
        });

        decbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curretValue = qty.getText().toString();
                int value = Integer.parseInt(curretValue);
                if (value <= 1) {
                    Toast.makeText(ProductPageActivity.this, "Sorry Minimum one", Toast.LENGTH_SHORT).show();
                    return;
                }
                value--;
                qty.setText(String.valueOf(value));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference productref = FirebaseDatabase.getInstance().getReference("product");
                productref.child(productDbId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            stockinString = dataSnapshot.child("productquantity").getValue(String.class);
                            cartcountinString = dataSnapshot.child("cartcount").getValue(String.class);
                            if (stockinString != null){
                                float commissionRateInFloat = Float.parseFloat(commissionRate);

                                int cartqty = Integer.parseInt(qty.getText().toString());
                                int cartcount = Integer.parseInt(cartcountinString);
                                int stock = Integer.parseInt(stockinString);
                                float unitprice = Float.parseFloat(priceinString);
                                Float total = cartqty * unitprice;
                                int updatedcartcount = cartcount + cartqty;

                                Float TotalCommission = (commissionRateInFloat/100) * unitprice * cartqty;
                                String TotalCommissionInString = Float.toString(TotalCommission);

                                String totalinString = Float.toString(total);
                                String cartqtyinstring = Integer.toString(updatedcartcount);
                                String saleqtyinstring = Integer.toString(cartqty);

                                if ((stock - cartcount) > cartqty) {
                                    Map<String, Object> cartcountmap = new HashMap<>();
                                    cartcountmap.put("cartcount", cartqtyinstring);
                                    addItemtoCart(productId, productName, productImg, saleqtyinstring, totalinString, priceinString, TotalCommissionInString,productDbId);
                                    updatecartCount(productDbId, cartcountmap);
                                    finish();

                                } else {
                                    Toast.makeText(ProductPageActivity.this, "Insufficient Stock Level", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

    }


    public void menucomponents(){

        LinearLayout cartButton = findViewById(R.id.cartbtn);
        LinearLayout MarketButton = findViewById(R.id.marketbtn);
        LinearLayout OrdersButton = findViewById(R.id.ordersbtn);
        LinearLayout ProfileButton = findViewById(R.id.Profilebtn);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPageActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    public void updatecartCount(String id, Map<String, Object> cartcountmap){
        DatabaseReference updateStockref = FirebaseDatabase.getInstance().getReference("product");
        updateStockref.child(id).updateChildren(cartcountmap);
    }

    // Check wehter the item is already in cart
    public void addItemtoCart(String productId, String productName, String productImg, String qty, String total, String unitPrice, String totalcommission, String DbId){

        Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("ProductId", productId);
        cartMap.put("ProductName", productName);
        cartMap.put("ProductImg", productImg);
        cartMap.put("qty", qty);
        cartMap.put("totalcommision", totalcommission);
        cartMap.put("total", total);
        cartMap.put("unitPrice", unitPrice);
        cartMap.put("ProductDBId", DbId);

        DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference().child("cartItems").child(userId).child(cardrefid);
        cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                        String Dbproduct = cartItemSnapshot.child("ProductId").getValue(String.class);
                        String cartItemId = cartItemSnapshot.getKey();
                        String cartQty = cartItemSnapshot.child("qty").getValue(String.class);

                        if(cartQty !=null){
                            if(Dbproduct.equals(productId)) {
                                deletePreviousQty(DbId,cartQty);
                                updateIteminCart(cartItemId,cartMap);
                                return;
                            }
                        }
                    }
                    addNewItemtoCart(cartMap);
                } else {
                    addNewItemtoCart(cartMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void addNewItemtoCart(Map<String, Object> newcartMap){

        DatabaseReference cardRef = FirebaseDatabase.getInstance().getReference("cartItems").child(userId);
        cardRef.child(cardrefid).push().setValue(newcartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProductPageActivity.this, "Product Added to Cart Successfully", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(ProductPageActivity.this, "Something went wrong!, Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateIteminCart(String itemid,Map<String, Object> updatecartMap){

        DatabaseReference cardRef = FirebaseDatabase.getInstance().getReference("cartItems").child(userId);
        cardRef.child(cardrefid).child(itemid).updateChildren(updatecartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProductPageActivity.this, "Cart Updated Successfully", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(ProductPageActivity.this, "Something went wrong!, Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deletePreviousQty(String deductproductid,String cartItemqty){

        int cartItemqtyinInt = Integer.parseInt(cartItemqty);

        DatabaseReference productref = FirebaseDatabase.getInstance().getReference("product").child(deductproductid);
        productref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String cartcount = dataSnapshot.child("cartcount").getValue(String.class);
                    if(cartcount != null){
                        int cartcountinInt = Integer.parseInt(cartcount);
                        int updatedcartcount = cartcountinInt - cartItemqtyinInt;
                        String updatedcartcountinstring = Integer.toString(updatedcartcount);
                        Map<String, Object> cartcountmap = new HashMap<>();
                        cartcountmap.put("cartcount", updatedcartcountinstring);
                        updatecartCount(deductproductid, cartcountmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}