package felixzhang.project.my_douban.engine.cacheload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import felixzhang.project.my_douban.R;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;

	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 当进入listview时默认的图片，可换成你自己的默认图片
	final int stub_id = R.drawable.book_image_default;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			// 若没有的话则开启新线程加载图片,从文件中或者网络上
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	/**
	 * 给线程池提交任务，其中的任务类需要实现runnable多线程
	 * 
	 * @param url
	 * @param imageView
	 */
	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	// 加载图片的任务类
	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;

				Bitmap bmp = getBitmap(photoToLoad.url);

				memoryCache.put(photoToLoad.url, bmp);

				if (imageViewReused(photoToLoad))
					return;

				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	// 从文件or网络中获取图片，都是耗时操作
	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		/**
		 * 先从文件缓存中查找是否有
		 */
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;
		/**
		 * 最后从指定的url中下载图片
		 */

		// from web
		try {
			Bitmap bitmap = null;

			URL imageUrl = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);

			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);

			os.close();
			conn.disconnect();

			bitmap = decodeFile(f);

			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	/**
	 * decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	 */
	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();

			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return true 图片错位
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	/**
	 * 用于在UI线程中更新界面
	 *
	 */
	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	// 清缓存
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
