package felixzhang.project.my_douban.model;

/**
 * Created by felix on 15/4/27.
 */
public class Book {



	public String title;
	public String description;
	private double rating;
	public String imgurl;
	public String id;

	public Book() {
		super();

	}

	public Book(String title, String description, double rating, String imgurl,
			String id) {
		super();
		this.title = title;
		this.description = description;
		this.rating = rating;
		this.imgurl = imgurl;
		this.id = id;
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

	@Override
	public String toString() {
		return "Book{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", rating=" + rating +
				", imgurl='" + imgurl + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
