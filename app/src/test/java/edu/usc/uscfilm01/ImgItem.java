package edu.usc.uscfilm01;

import org.junit.Test;

public class ImgItem {
    private  int id;
    private  String poster_path;
    private  String backdrop_path;
    private  String title;
    private  String media_type;

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
        return id+","+poster_path;
    }

    public ImgItem getInstance(String data){
        ImgItem item = new ImgItem();
        item.setId(Integer.parseInt(data.split(",")[0]));
        item.setPoster_path(data.split(",")[1]);
        return  item;
    }



    @Test
    public void strDemo() {
        ImgItem i1 = new ImgItem();
        i1.setId(1231);
        i1.setPoster_path("https://testsfsdf/sdfsdf");
        System.out.println(i1.watchListString());
        System.out.println(i1.getInstance(i1.watchListString()).getPoster_path());

    }
}
