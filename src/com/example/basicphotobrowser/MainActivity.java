package com.example.basicphotobrowser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// where to look for photos when the app is launched
	final String STARTUP_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";


	// used to change the folder in configuration changes
	final String SELECTED_FOLDER_KEY = "SELECTED_FOLDER_KEY";
	String selectedFolder;


	GridView gridview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		selectedFolder = STARTUP_DIRECTORY;

		gridview = (GridView) findViewById(R.id.gridview);
		
		// the onClick part does almost nothing currently
		// but should ideally send the photo to the PixelSense or open detail view
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
			}
		});

		// associate the gridview in the layout with an image adapter
		setUpGridView(selectedFolder);
	}

	private void setUpGridView(String selectedFolder) {
		gridview.setAdapter(new ImageAdapter(this, selectedFolder));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// setting up the folder selection actionbar item
		MenuItem selectFolderItem = menu.findItem(R.id.menu_select_folder);
		SearchView searchView = (SearchView) selectFolderItem.getActionView();

		// always show the current directory
		searchView.setQuery(selectedFolder, false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener( ) {
			@Override
			public boolean   onQueryTextChange( String newText ) {
				// not implemented
				return true;
			}

			// handles search queries that are submitted
			@Override
			public boolean   onQueryTextSubmit(String query) {
				selectedFolder = query.trim();

				// forces a configuration change, so we can load from a different folder
				recreate();

				return true;
			}
		});

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// put values in bundle to save between configuration changes (e.g. language change or rotation)
		outState.putString(SELECTED_FOLDER_KEY, selectedFolder);
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		// retrieve values from bundle
		selectedFolder = savedInstanceState.getString(SELECTED_FOLDER_KEY);
		
		
		
		// associate the gridview in the layout with an image adapter
		setUpGridView(selectedFolder);
	}

}
