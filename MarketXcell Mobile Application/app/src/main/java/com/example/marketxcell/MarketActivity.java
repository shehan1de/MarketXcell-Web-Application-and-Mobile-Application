package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketxcell.adapters.CategoryAdapter;
import com.example.marketxcell.adapters.ProductAdapter;
import com.example.marketxcell.model.CategoryModel;
import com.example.marketxcell.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MarketActivity extends AppCompatActivity {

    RecyclerView catRecyclerView, productRecyclerView;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    List<CategoryModel> categoryModelList;
    List<ProductModel> productModelList;
    GifImageView catgifImageView, productgifImageView;
    TextView productViewAll, categoryViewAll, textViewagentName;
    EditText searchfield;
    ImageView searchBtn, logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("agentid", "");
        String agentName = sharedPreferences.getString("agentname", "");
        String cardrefid = sharedPreferences.getString("cartrefid", "");


        catgifImageView = (GifImageView) findViewById(R.id.cat_loadingGif);
        productgifImageView = (GifImageView) findViewById(R.id.product_loadingGif);
        textViewagentName = (TextView) findViewById(R.id.marketpage_agentName);
        productViewAll = (TextView) findViewById(R.id.market_btn_Product_viewAll);
        categoryViewAll = (TextView) findViewById(R.id.market_btn_Cat_viewAll);
        searchfield = (EditText) findViewById(R.id.MarketPage_Search_field);
        searchBtn = (ImageView) findViewById(R.id.MarketPage_Search_btn);
        logoutBtn = (ImageView) findViewById(R.id.MarketPage_logout);

        textViewagentName.setText(agentName);


        catgifImageView.setVisibility(View.VISIBLE);
        productgifImageView.setVisibility(View.VISIBLE);

        menucomponents();
        viewCatRecyclerView();
        viewProductRecyclerView();

        productViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this,FeaturedProductsActivity.class);
                intent.putExtra("location", "featuredproductviewall");
                startActivity(intent);
            }
        });

        categoryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this,FeaturedProductsActivity.class);
                intent.putExtra("location", "categoryviewall");
                startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchQuery = searchfield.getText().toString();
                if(searchQuery.isEmpty()){
                    Toast.makeText(MarketActivity.this, "Search field is required", Toast.LENGTH_SHORT).show();
                } else if (searchQuery.length()<2) {
                    Toast.makeText(MarketActivity.this, "Add more details so we can search for better matches", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(MarketActivity.this,FeaturedProductsActivity.class);
                    intent.putExtra("location", "search");
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MarketActivity.this);
                builder.setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                performLogout(userId,cardrefid);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    public void viewCatRecyclerView(){
        categoryModelList = new ArrayList<>();

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("category");
        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String name = agentSnapshot.child("name").getValue(String.class);
                        String id = agentSnapshot.child("id").getValue(String.class);
                        String categoryImg = agentSnapshot.child("categoryImg").getValue(String.class);
                        CategoryModel categoryModel = new CategoryModel(categoryImg,id,name);
                        categoryModelList.add(categoryModel);
                    }

                    categoryAdapter = new CategoryAdapter(MarketActivity.this,categoryModelList);
                    catRecyclerView = (RecyclerView) findViewById(R.id.rec_category);
                    catRecyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this,RecyclerView.HORIZONTAL, false));
                    catRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    catgifImageView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void viewProductRecyclerView(){

        productModelList = new ArrayList<>();

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("product");
        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String dbId = agentSnapshot.getKey().toString();
                        String featured = agentSnapshot.child("featured").getValue(String.class);
                        if(featured.equals("Yes")){
                            String catname = agentSnapshot.child("categoryname").getValue(String.class);
                            String productcommission = agentSnapshot.child("commissionRate").getValue(String.class);
                            String cartcount = agentSnapshot.child("cartcount").getValue(String.class);
                            String minStock = agentSnapshot.child("minstocklevel").getValue(String.class);
                            String productImg = agentSnapshot.child("productIMG").getValue(String.class);
                            String productdescription = agentSnapshot.child("productdescription").getValue(String.class);
                            String productid = agentSnapshot.child("productid").getValue(String.class);
                            String productname = agentSnapshot.child("productname").getValue(String.class);
                            String productprice = agentSnapshot.child("productprice").getValue(String.class);
                            String productqty = agentSnapshot.child("productquantity").getValue(String.class);

                            ProductModel productModel = new ProductModel(catname,productcommission,featured,minStock,productImg,productdescription,productid,productname,productprice,productqty,cartcount,dbId);
                            productModelList.add(productModel);
                        }
                    }

                    productAdapter = new ProductAdapter(MarketActivity.this,productModelList);
                    productRecyclerView = (RecyclerView) findViewById(R.id.new_product_rec);
                    productRecyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this,RecyclerView.HORIZONTAL, false));
                    productRecyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    productgifImageView.setVisibility(View.GONE);
                }
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

        ImageView activeIcon = MarketButton.findViewById(R.id.imageView7);
        TextView activeText = MarketButton.findViewById(R.id.texttitle43);

        activeIcon.setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        activeText.setTextColor(getResources().getColor(R.color.PrimaryColor));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void performLogout(String userId, String cardrefid){

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("cartItems").child(userId).child(cardrefid);
        agentRef.removeValue();

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MarketActivity.this, Welcome_pageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}