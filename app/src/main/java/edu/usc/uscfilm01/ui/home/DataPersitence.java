package edu.usc.uscfilm01.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public class DataPersitence {
    private Context ct;
    private String name;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private ArrayList<ImgItem> dataList;
    private ArrayList<String> order;


    public DataPersitence(Context ct,String name) {
        this.ct = ct;
        this.data = ct.getSharedPreferences(name,0);
        this.editor = data.edit();
        this.order = new ArrayList<>();
        String tmp = data.getString("allData","");


        if(tmp==""){
            this.dataList = new ArrayList<>();
        }else {
            this.dataList = ImgItem.strToArr(tmp);
        }
        redoOrder();
        print();
    }
    
    public void putString(String k, ImgItem v){
        dataList.add(0,v);
        order.add(k);
    }
    
    public void remove(String key){
        order.remove(key);
        ImgItem tmp = null;
        for (ImgItem item : this.dataList) {
            if(key.equals(""+item.getId())){
                tmp =item;
                break;
            }
        }
        if(tmp!=null){
            this.dataList.remove(tmp);
        }
    }

    public void commit(){
        String finalData = ImgItem.arrToString(dataList);
        editor.putString("allData",finalData);
        editor.commit();
    }

    public void redoOrder(){
        this.order.clear();
        for(ImgItem item : this.dataList){
            this.order.add(""+item.getId());
        }
    }
    public void print(){
        Log.d("data","==========data=========");
        for(ImgItem item : this.dataList){
            Log.d("data",""+item.getId());
        }

    }

    public ArrayList<ImgItem> getItemList(){
        return  dataList;
    }

    public boolean contains(String key){
        return order.contains(key);
    }

}
