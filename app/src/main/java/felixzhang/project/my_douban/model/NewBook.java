package felixzhang.project.my_douban.model;

/**
 * Created by felix on 15/4/27.
 * 在新书网页上爬出的新书信息和请求得来的json数据的字段名不一样。
 * 故和Book区分开.
 */
public class NewBook {

    public String title;
    public String description;  //出版信息
    private double rating;
    public String imgurl;
    public String id;
    private String summary;

    public NewBook() {
        super();
    }

    public NewBook(String title, String description, String summary,
                   String imgurl, String id) {

        this.title = title;
        this.description = description;
        this.imgurl = imgurl;
        this.id = id;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rate) {
        this.rating = rate;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String img) {
        this.imgurl = img;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "NewBook{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                ", imgurl='" + imgurl + '\'' +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
