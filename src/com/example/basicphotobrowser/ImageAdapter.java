package com.example.basicphotobrowser;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/*
 * This class is used to load images into a GridView.
 * It currently just displays all the pictures specified in START_DIRECTORY.
 * 
 * The constants in the beginning of this class determine the start-up view.
 */
public class ImageAdapter extends BaseAdapter {

	
	// the resolution independent measurements used for the UI
	public final int CELL_SIZE_DP = 120;
	public final int PADDING_SIZE_DP = 8;

	// the initial directory to look for pictures
	public final String START_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";

	// the bitmap that is shown before the real bitmaps are loaded
	public final Bitmap PLACEHOLDER_BITMAP;



	// link back to the main activity
	private Context parentContext;

	// to be calculated based on the DP measurements
	private int cellSize;
	private int paddingSize;

	// the current directory
	private String currentDirectory;

	// the image files located in the current directory
	private String[] images = {};




	public ImageAdapter(Context context) {

		parentContext = context;

		// calculate the resolution independent measurements
		cellSize = dipsToPixels(CELL_SIZE_DP);
		paddingSize = dipsToPixels(PADDING_SIZE_DP);

		// create a placeholder bitmap for those images that are still loading
		PLACEHOLDER_BITMAP = BitmapFactory.decodeResource(parentContext.getResources(), R.drawable.ic_launcher);

		// keep track of where we are
		currentDirectory = START_DIRECTORY;

		// this does not save the full file path, just file names
		images = new File(currentDirectory).list();

	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return null; // unimplemented and not used
	}

	@Override
	public long getItemId(int position) {
		return 0; // unimplemented and not used
	}

	// this creates a new ImageView for each item referenced by the adapter
	// unless the item is being recycled (as the concertView), we create a new ImageView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;

		String filePath = currentDirectory + "/" + images[position];

		if (convertView == null) {

			// set attributes of the imageview
			imageView = new ImageView(parentContext);
			imageView.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);

		} else {

			// or recycle
			imageView = (ImageView) convertView;
		}


		// decode only to cell size for memory preservation purposes
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		// used to get the dimensions of a bitmap without decoding
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		
		// then these dimensions are used to get the most optimal size ratio for the thumbnail
		options.inSampleSize = calculateInSampleSize(options.outHeight, options.outHeight);
		options.inJustDecodeBounds = false; // <--- resetting before passing on to BitmapWorkerTask

		// finally, set the actual picture to be shown in the imageview
		// this is done by creating an asynchronous task that loads the image in the background
		// the options.inSampleSize value is used to keep memory usage down so the app doesn't crash!
		final BitmapWorkerTask task = new BitmapWorkerTask(imageView, filePath, options);
		final AsyncDrawable asyncDrawable = new AsyncDrawable(parentContext.getResources(), PLACEHOLDER_BITMAP, task);
		imageView.setImageDrawable(asyncDrawable);
		
		// the task is only executed AFTER we have created an AsyncDrawable
		// why exactly, I'm still not 100% certain...
		// but check: http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
		task.execute();

		return imageView;
	}

	// used to dynamically calculate the cell size
	private int dipsToPixels(int dips) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, parentContext.getResources().getDisplayMetrics());
	}
	
	// used to get the best thumbnail ratio for decoding each bitmap in a memory-preserving way
	// adapted from code taken from: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	private int calculateInSampleSize(int height, int width) {
		
		if (height > width) {
			return Math.round((float) height / (float) cellSize);
		} else {
			return Math.round((float) width / (float) cellSize);
		}
	}

}
