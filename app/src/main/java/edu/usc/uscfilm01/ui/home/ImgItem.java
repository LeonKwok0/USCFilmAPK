package edu.usc.uscfilm01.ui.home;

import android.provider.ContactsContract;

import java.util.ArrayList;

public class ImgItem {
    private  int id;
    private  String poster_path;
    private  String backdrop_path;
    private  String title;
    private  String media_type;
    private  String vote_average;
    private  String date;

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String post_path) {
        this.poster_path = post_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String watchListString() {
        return id+","+poster_path+","+media_type;
    }

    public static ImgItem getInstance(String data){
        ImgItem item = new ImgItem();
        item.setId(Integer.parseInt(data.split(",")[0]));
        item.setPoster_path(data.split(",")[1]);
        item.setMedia_type(data.split(",")[2]);
        return  item;
    }

    public static String arrToString(ArrayList<ImgItem> arrData){
        StringBuilder res = new StringBuilder();
        for (ImgItem item: arrData) {
            res.append(item.watchListString()).append(";");
        }
        return res.toString();
    }

    public static  ArrayList<ImgItem> strToArr(String data){
        ArrayList<ImgItem> res = new ArrayList<ImgItem>();
        for (String str:data.split(";")) {
            res.add(ImgItem.getInstance(str));
        }
        return res;
    }





}
