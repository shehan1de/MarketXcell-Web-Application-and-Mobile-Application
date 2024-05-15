package com.example.marketxcell.model;

public class CategoryModel {
    String categoryImg;
    String id;
    String name;

    public CategoryModel() {
    }

    public CategoryModel(String categoryImg, String id, String name) {
        this.categoryImg = categoryImg;
        this.id = id;
        this.name = name;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
