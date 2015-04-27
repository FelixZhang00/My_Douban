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
			String imgurl) {
		super();
		this.title = title;
		this.description = description;
		this.summary = summary;
		this.imgurl = imgurl;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		return "NewBook [title=" + title + ", description=" + description
				+ ", summary=" + summary + ", imgurl=" + imgurl + "]";
	}

}
