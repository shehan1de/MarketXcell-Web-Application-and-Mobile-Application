package com.example.marketxcell.model;

public class OrderModel {
    String orderId;
    String agentId;
    String cartId;
    String OrderValue;
    String clientName;
    String clientAddress;
    String orderStatus;
    String couponCode;
    String offerclamedValue;
    String totalCommission;
    String paymentMode;
    String orderDbId;

    public OrderModel(String orderId, String agentId, String cartId, String orderValue, String clientName, String clientAddress, String orderStatus, String couponCode, String offerclamedValue, String totalCommission, String paymentMode) {
        this.orderId = orderId;
        this.agentId = agentId;
        this.cartId = cartId;
        OrderValue = orderValue;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.orderStatus = orderStatus;
        this.couponCode = couponCode;
        this.offerclamedValue = offerclamedValue;
        this.totalCommission = totalCommission;
        this.paymentMode = paymentMode;
    }

    public OrderModel(String orderId, String orderValue, String orderStatus, String orderDbId) {
        this.orderId = orderId;
        OrderValue = orderValue;
        this.orderStatus = orderStatus;
        this.orderDbId = orderDbId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getOrderValue() {
        return OrderValue;
    }

    public void setOrderValue(String orderValue) {
        OrderValue = orderValue;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getOfferclamedValue() {
        return offerclamedValue;
    }

    public void setOfferclamedValue(String offerclamedValue) {
        this.offerclamedValue = offerclamedValue;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOrderDbId() {
        return orderDbId;
    }

    public void setOrderDbId(String orderDbId) {
        this.orderDbId = orderDbId;
    }
}
