package felixzhang.project.my_douban.model;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import felixzhang.project.my_douban.dao.SearchedBookDataHelper;

/**
 * Created by felix on 15/5/3.
 * 在新书网页上爬出的新书信息和请求得来的json数据的字段名不一样。
 * 故和NewBook区分开.
 */
public class Book extends BaseModel {

    private static HashMap<String, Book> CACHE = new HashMap<>();


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
     * 将数据库中的Cursor对象转化为Book对象
     *
     * @return
     */
    public static Book fromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(SearchedBookDataHelper.SearchedBookDBInfo.COLUMN_BOOKID));

        Book book = getFromCache(id);    //先从内存中找找看，如果没有再把数据库中的json数据转换为Book对象
        if (book != null) {
            return book;
        }
        book = new Gson().fromJson(
                cursor.getString(cursor.getColumnIndex(SearchedBookDataHelper.SearchedBookDBInfo.COLUMN_JSON))
                , Book.class);

        addToCache(book);
        return book;
    }

    /**
     * 按键值对从内存中拿数据
     */
    private static Book getFromCache(String id) {
        return CACHE.get(id);
    }

    private static void addToCache(Book book) {
        CACHE.put(book.id, book);
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
        public String count;
        public String start;
        public String total;

        public int getTotal() {
            return total;
        }
    }




}
