package felixzhang.project.my_douban.engine.cacheload;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;

@SuppressLint("NewApi")
public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		/**
		 * 如果有SD卡则在SD卡中建一个应用程序包名的目录存放缓存的图片 没有SD卡就放在系统的缓存目录中
		 */
		// Find the dir to save cached images
		String dirname = context.getApplicationInfo().packageName;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					dirname);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = String.valueOf(url.hashCode());
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}