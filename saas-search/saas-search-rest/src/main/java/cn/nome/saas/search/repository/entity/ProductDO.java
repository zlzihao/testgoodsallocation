package cn.nome.saas.search.repository.entity;

import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "nm_products_alias", type = "product")
public class ProductDO implements Serializable {

    private int id;
    private String name;
    private long minPrice;
//    private String fullName;
    private long maxPrice;
    private Date createdAt;
    private Date updatedAt;
    private boolean displayed;

    private String cateProdName;
    private String prodImg;

    //默认未售罄
    private int spuStockCount = 99;

//    private String cateName;
//    private String prodName;
//
//    public String getCateName() {
//        return cateName;
//    }
//
//    public void setCateName(String cateName) {
//        this.cateName = cateName;
//    }
//
//    public String getProdName() {
//        return prodName;
//    }
//
//    public void setProdName(String prodName) {
//        this.prodName = prodName;
//    }

    public String getCateProdName() {
        return cateProdName;
    }

    public void setCateProdName(String cateProdName) {
        this.cateProdName = cateProdName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public String getProdImg() {
        return prodImg;
    }

    public void setProdImg(String prodImg) {
        this.prodImg = prodImg;
    }

    public int getSpuStockCount() {
        return spuStockCount;
    }

    public void setSpuStockCount(int spuStockCount) {
        this.spuStockCount = spuStockCount;
    }

}
