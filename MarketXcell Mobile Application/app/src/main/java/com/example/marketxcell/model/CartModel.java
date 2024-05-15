package com.example.marketxcell.model;

public class CartModel {
    String productId;
    String productName;
    String productImg;
    String qty;
    String total;
    String unitPrice;
    String totalCommission;
    String cartId;

    public CartModel(String productId, String productName, String productImg, String qty, String total, String unitPrice, String totalCommission, String cartId) {
        this.productId = productId;
        this.productName = productName;
        this.productImg = productImg;
        this.qty = qty;
        this.total = total;
        this.unitPrice = unitPrice;
        this.totalCommission = totalCommission;
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
