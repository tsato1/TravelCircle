package com.travelcircle;

/**
 * Created by T on 2015/09/26.
 */
public class Item {
    public final static String ITEM_NAME = "name";
    public final static String ITEM_DATE = "date";
    public final static String ITEM_AREA = "area";

    private String id;
    private String name;
    private String date;
    private String area;

    public Item() {
    }

    void setName(String name) {
        this.name = name;
    }

    void setDate(String date) {
        this.date = date;
    }

    void setArea(String area) {
        this.area = area;
    }

    String getId() {
        return this.id;
    }

    String getName() {
        return this.name;
    }

    String getDate() {
        return this.date;
    }

    String getArea() {
        return this.area;
    }

}
