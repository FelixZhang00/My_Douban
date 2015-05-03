package felixzhang.project.my_douban.model;

import java.util.ArrayList;

/**
 * Created by felix on 15/5/3.
 * 在新书网页上爬出的新书信息和请求得来的json数据的字段名不一样。
 * 故和NewBook区分开.
 */
public class Book extends BaseModel {

    public String id;
    public String url;
    public String title;
    public String summary;
    public Rating rating;
    public String image;

    //出版信息
    public String publisher;
    public String pubdate;
    public String[] author;  //作家在json中以数组存储
    public String price;


    private String description;  //拼凑所有出版信息为一个字符串

    public String getDescription() {
        String autors = "";
        String divi = author.length > 1 ? " " : "";
        for (String a : author) {
            autors += a + divi;
        }

        this.description = autors + " /" + pubdate + " /" + publisher + " /" + price;
        return description;
    }

    /**
     * 评分
     */
    public static class Rating {
        public String average;
        public String max;
        public String min;
        public String numRaters;
    }


    /**
     * 请求返回的json格式
     */
    public static class BookRequestData {
        public ArrayList<Book> books;
        public int count;
        public int start;
        public int total;

        public int getTotal() {
            return total;
        }
    }
}
