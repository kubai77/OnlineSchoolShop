package com.zhang.ssmschoolshop.entity;

import com.zhang.ssmschoolshop.annotinon.ExportEntityMap;

import java.util.Date;
import java.util.List;

public class Goods {
    @ExportEntityMap(EnName = "goodsid", CnName = "商品ID")
    private Integer goodsid;

    @ExportEntityMap(EnName = "goodsname", CnName = "商品名称")
    private String goodsname;

    @ExportEntityMap(EnName = "price", CnName = "价格")
    private Integer price;

    @ExportEntityMap(EnName = "num", CnName = "数量")
    private Integer num;

    @ExportEntityMap(EnName = "uptime", CnName = "上架时间")
    private Date uptime;

    @ExportEntityMap(EnName = "category", CnName = "分类")
    private Integer category;

    @ExportEntityMap(EnName = "detailcate", CnName = "详细分类")
    private String detailcate;

    @ExportEntityMap(EnName = "activityid", CnName = "活动ID")
    private Integer activityid;

    @ExportEntityMap(EnName = "description", CnName = "描述")
    private String description;

    private List<ImagePath> imagePaths;

    private boolean fav;

    private Activity activity;

    private Float newPrice;

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname == null ? null : goodsname.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getDetailcate() {
        return detailcate;
    }

    public void setDetailcate(String detailcate) {
        this.detailcate = detailcate == null ? null : detailcate.trim();
    }

    public Integer getActivityid() {
        return activityid;
    }

    public void setActivityid(Integer activityid) {
        this.activityid = activityid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public List<ImagePath> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<ImagePath> imagePaths) {
        this.imagePaths = imagePaths;
    }


    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Float newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsid=" + goodsid +
                ", goodsname='" + goodsname + '\'' +
                ", price=" + price +
                ", num=" + num +
                ", uptime=" + uptime +
                ", category=" + category +
                ", detailcate='" + detailcate + '\'' +
                ", activityid=" + activityid +
                ", description='" + description + '\'' +
                ", imagePaths=" + imagePaths +
                ", fav=" + fav +
                ", activity=" + activity +
                ", newPrice=" + newPrice +
                '}';
    }
}