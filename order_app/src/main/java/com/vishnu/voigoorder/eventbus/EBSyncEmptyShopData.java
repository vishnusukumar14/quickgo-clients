package com.vishnu.voigoorder.eventbus;

public class EBSyncEmptyShopData {

    public String jsonData;
    public int id;


    public EBSyncEmptyShopData(String jsonData, int id) {
        this.jsonData = jsonData;
        this.id = id;
    }
}
