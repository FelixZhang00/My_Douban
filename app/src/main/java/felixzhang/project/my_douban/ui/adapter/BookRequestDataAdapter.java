package felixzhang.project.my_douban.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.engine.data.LruImageCache;
import felixzhang.project.my_douban.model.Book;
import felixzhang.project.my_douban.util.StringUtil;
import felixzhang.project.my_douban.util.VolleyUtil;

/**
 * Created by felix on 15/5/3.
 * 为SearchBookFragment使用
 */
public class BookRequestDataAdapter extends CursorAdapter {

    private ImageLoader imageLoader;
    private Context mContext;

    private LayoutInflater mLayoutInflater;
    private Resources mResource;


    public BookRequestDataAdapter(Context context) {
        super(context, null, false);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mResource = mContext.getResources();


        this.imageLoader = new ImageLoader(VolleyUtil.getQueue(MyApp.getContext()), new LruImageCache());
    }


    @Override
    public Book getItem(int position) {
        mCursor.moveToPosition(position);
        return Book.fromCursor(mCursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.booksearched_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mHolder = getHolder(view);

        Book book = Book.fromCursor(cursor);

//        view.setEnabled(!mGridView.isItemChecked(cursor.getPosition()));

        mHolder.title.setText(book.title);
        mHolder.desc.setText(book.getDescription());

        mHolder.ratingBar.setMax((int) Float.parseFloat(book.rating.max));
//            Logger.i(TAG, "max" + (int) Float.parseFloat(book.rating.max));
        mHolder.ratingBar.setRating(Float.parseFloat(book.rating.average) / 2);
//            Logger.i(TAG, "rating " + Float.parseFloat(book.rating.average));

        //异步加载图片
        ImageLoader.ImageContainer container = null;
        try {
            //如果当前ImageView上存在请求，先取消
            if (mHolder.imageView.getTag() != null) {
                container = (ImageLoader.ImageContainer) mHolder.imageView.getTag();
                container.cancelRequest();
            }

        } catch (Exception e) {
        }

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mHolder.imageView, R.drawable.book_image_default, R.drawable.book_image_default);
        container = imageLoader.get(StringUtil.preUrl(book.image), listener);

        //在ImageView上存储当前请求的Container，用于取消请求
        mHolder.imageView.setTag(container);

    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        return holder;
    }

    private ViewHolder mHolder;

    class ViewHolder {
        ImageView imageView;
        TextView title;
        RatingBar ratingBar;
        TextView desc;

        public ViewHolder(View convertView) {
            imageView = (ImageView) convertView.findViewById(R.id.book_img);
            title = (TextView) convertView.findViewById(R.id.book_title);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            desc = (TextView) convertView.findViewById(R.id.book_description);
        }
    }


}

