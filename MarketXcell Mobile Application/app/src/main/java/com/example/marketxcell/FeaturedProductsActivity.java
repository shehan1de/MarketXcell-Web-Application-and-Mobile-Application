package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class FeaturedProductsActivity extends AppCompatActivity {

    String searchQuery = "";
    String location = "specificcategory";
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    List<CategoryModel> categoryModelList;
    List<ProductModel> productModelList;
    GifImageView gifImageView;

    LinearLayout backbtn;
    TextView title;
    EditText searchField;
    ImageView searchbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_products);
        menucomponents();

        backbtn = (LinearLayout) findViewById(R.id.featuredProducts_backbtn);
        title = (TextView) findViewById(R.id.featuredproducts_title);
        searchField = (EditText) findViewById(R.id.featuredproducts_search);
        searchbtn = (ImageView) findViewById(R.id.featuredproducts_searchbtn);
        gifImageView = (GifImageView) findViewById(R.id.featuredProducts_loadingGif);

        Intent intent = getIntent();
        location = intent.getStringExtra("location");

        if(location.equals("search") || location.equals("specificcategory")) {
            searchQuery = intent.getStringExtra("searchQuery");
        }

        if(location.equals("search")){
            title.setText("Results for "+searchQuery);
            searchProductRecyclerView(searchQuery);
        } else if (location.equals("categoryviewall")) {
            title.setText("All product categories");
            viewCatRecyclerView();
        } else if (location.equals("featuredproductviewall")) {
            title.setText("All featured products");
            viewProductRecyclerView();
        } else if (location.equals("specificcategory")) {
            title.setText("All products from "+searchQuery);
            searchcategoryProductRecyclerView(searchQuery);
        }

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                searchQuery = searchField.getText().toString();
                if(searchQuery.isEmpty()){
                    Toast.makeText(FeaturedProductsActivity.this, "Search field is required", Toast.LENGTH_SHORT).show();
                } else if (searchQuery.length()<2) {
                    Toast.makeText(FeaturedProductsActivity.this, "Add more details so we can search for better matches", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(FeaturedProductsActivity.this,FeaturedProductsActivity.class);
                    intent.putExtra("location", "search");
                    intent.putExtra("searchQuery", searchQuery);
                    startActivity(intent);
                }
            }
        });
        
        

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedProductsActivity.this,MarketActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); finish();
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
                Intent intent = new Intent(FeaturedProductsActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedProductsActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedProductsActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedProductsActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    public void viewProductRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.featuredproducts_list);
        recyclerView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

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
                    if(productModelList.isEmpty()){
                        title.setText("No featured products right now");
                    }

                    productAdapter = new ProductAdapter(FeaturedProductsActivity.this,productModelList);
                    recyclerView.setLayoutManager( new GridLayoutManager(FeaturedProductsActivity.this,2));
                    recyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
//                    productgifImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.GONE);

    }

    public void viewCatRecyclerView(){
        categoryModelList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.featuredproducts_list);
        recyclerView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

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

                    if(categoryModelList.isEmpty()){
                        title.setText("No products categories right now");
                    }

                    categoryAdapter = new CategoryAdapter(FeaturedProductsActivity.this,categoryModelList);
                    recyclerView.setLayoutManager( new GridLayoutManager(FeaturedProductsActivity.this,3));
                    recyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.GONE);
    }

    public void searchProductRecyclerView(String searchQuery){

        productModelList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.featuredproducts_list);
        recyclerView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("product");
        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String dbId = agentSnapshot.getKey().toString();
                        String catname = agentSnapshot.child("categoryname").getValue(String.class);
                        String productname = agentSnapshot.child("productname").getValue(String.class);
                        String productdescription = agentSnapshot.child("productdescription").getValue(String.class);

                        if(catname.contains(searchQuery)|| productname.contains(searchQuery)
                                || productdescription.contains(searchQuery)){
                            String productcommission = agentSnapshot.child("commissionRate").getValue(String.class);
                            String featured = agentSnapshot.child("featured").getValue(String.class);
                            String cartcount = agentSnapshot.child("cartcount").getValue(String.class);
                            String minStock = agentSnapshot.child("minstocklevel").getValue(String.class);
                            String productImg = agentSnapshot.child("productIMG").getValue(String.class);
                            String productid = agentSnapshot.child("productid").getValue(String.class);
                            String productprice = agentSnapshot.child("productprice").getValue(String.class);
                            String productqty = agentSnapshot.child("productquantity").getValue(String.class);

                            ProductModel productModel = new ProductModel(catname,productcommission,featured,minStock,productImg,productdescription,productid,productname,productprice,productqty,cartcount,dbId);
                            productModelList.add(productModel);
                        }
                    }

                    if(productModelList.isEmpty()){
                        title.setText("No product results for "+ searchQuery);
                    }

                    productAdapter = new ProductAdapter(FeaturedProductsActivity.this,productModelList);
                    recyclerView.setLayoutManager( new GridLayoutManager(FeaturedProductsActivity.this,2));
                    recyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
//                    productgifImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.GONE);
    }

    public void searchcategoryProductRecyclerView(String catName){

        productModelList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.featuredproducts_list);
        recyclerView.setVisibility(View.GONE);
        gifImageView.setVisibility(View.VISIBLE);

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("product");
        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                        String dbId = agentSnapshot.getKey();
                        String catname = agentSnapshot.child("categoryname").getValue(String.class);
                        if(catname.contains(catName)){
                            String productname = agentSnapshot.child("productname").getValue(String.class);
                            String productdescription = agentSnapshot.child("productdescription").getValue(String.class);
                            String productcommission = agentSnapshot.child("commissionRate").getValue(String.class);
                            String cartcount = agentSnapshot.child("cartcount").getValue(String.class);
                            String featured = agentSnapshot.child("featured").getValue(String.class);
                            String minStock = agentSnapshot.child("minstocklevel").getValue(String.class);
                            String productImg = agentSnapshot.child("productIMG").getValue(String.class);
                            String productid = agentSnapshot.child("productid").getValue(String.class);
                            String productprice = agentSnapshot.child("productprice").getValue(String.class);
                            String productqty = agentSnapshot.child("productquantity").getValue(String.class);

                            ProductModel productModel = new ProductModel(catname,productcommission,featured,minStock,productImg,productdescription,productid,productname,productprice,productqty,cartcount,dbId);
                            productModelList.add(productModel);
                        }
                    }

                    if(productModelList.isEmpty()){
                        title.setText("No products from "+catName);
                    }

                    productAdapter = new ProductAdapter(FeaturedProductsActivity.this,productModelList);
                    recyclerView.setLayoutManager( new GridLayoutManager(FeaturedProductsActivity.this,2));
                    recyclerView.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
//                    productgifImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.GONE);
    }

}