package felixzhang.project.my_douban.model;

/**
 * Created by felix on 15/4/27.
 */
public class NewBook extends Book {

    private String summary;

    public NewBook() {
        super();
    }

    public NewBook(String title, String description, String summary,
                   String imgurl, String id) {
        super(title, description, 0, imgurl, id);
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {

        return super.toString() + "NewBook{" +
                "summary='" + summary + '\'' +
                '}';
    }
}
