package kr.ry4nkim.objectspinner_example.model;

import kr.ry4nkim.objectspinner.ObjectSpinner;

public class Goods implements ObjectSpinner.Delegate {

    private String goodsCode;
    private String goodsName;
    private int goodsPrice;

    public Goods(String goodsCode, String goodsName, int goodsPrice) {
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
    }

    @Override
    public String getSpinnerDelegate() {
        return goodsName;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(int goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsCode='" + goodsCode + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsPrice=" + goodsPrice +
                '}';
    }
}
