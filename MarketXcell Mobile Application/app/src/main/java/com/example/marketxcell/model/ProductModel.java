package com.example.marketxcell.model;

import java.io.Serializable;

public class ProductModel implements Serializable {
    String categoryname;
    String commissionRate;
    String featured;
    String minstocklevel;
    String productIMG;
    String productdescription;
    String productid;
    String productname;
    String productprice;
    String productquantity;
    String cartcount;
    String productDBId;

    public ProductModel(String categoryname, String commissionRate, String featured, String minstocklevel, String productIMG, String productdescription, String productid, String productname, String productprice, String productquantity, String cartcount, String productDBId) {
        this.categoryname = categoryname;
        this.commissionRate = commissionRate;
        this.featured = featured;
        this.minstocklevel = minstocklevel;
        this.productIMG = productIMG;
        this.productdescription = productdescription;
        this.productid = productid;
        this.productname = productname;
        this.productprice = productprice;
        this.productquantity = productquantity;
        this.cartcount = cartcount;
        this.productDBId = productDBId;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(String commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getMinstocklevel() {
        return minstocklevel;
    }

    public void setMinstocklevel(String minstocklevel) {
        this.minstocklevel = minstocklevel;
    }

    public String getProductIMG() {
        return productIMG;
    }

    public void setProductIMG(String productIMG) {
        this.productIMG = productIMG;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(String productquantity) {
        this.productquantity = productquantity;
    }

    public String getCartcount() {
        return cartcount;
    }

    public void setCartcount(String cartcount) {
        this.cartcount = cartcount;
    }

    public String getProductDBId() {
        return productDBId;
    }

    public void setProductDBId(String productDBId) {
        this.productDBId = productDBId;
    }
}
