package com.openclassroom.cour.odim.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import static android.os.Environment.getExternalStorageDirectory;
import com.openclassroom.cour.odim.R;

public class FileChooser extends ListActivity {
	private File currentDir;
	private FileArrayAdapter adapter;
	private FileFilter fileFilter;
	private File fileSelected;
	private ArrayList<String> extensions;
	public static final String BUNDLE_EXTRA_FILE = "BUNDLE_EXTRA_FILE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntent().putExtra("filterFileExtension", ".txt");
		Bundle extras = getIntent().getExtras();

		/*if (extras != null) {

			if (extras.getStringArrayList("filterFileExtension") != null) {
				extensions = extras.getStringArrayList("filterFileExtension");
				fileFilter = pathname -> ((pathname.getName().toLowerCase().endsWith(".txt") || pathname.isDirectory()));
			}
		}*/

		currentDir = getExternalStorageDirectory().getAbsoluteFile();


		fill(currentDir);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((!currentDir.getName().equals("sdcard")) && (currentDir.getParentFile() != null)) {
				currentDir = currentDir.getParentFile();
				fill(currentDir);
			} else {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void fill(File f) {
		File[] dirs = null;
		if (fileFilter != null){
			dirs = f.listFiles(fileFilter);
		}
		else{
			dirs = f.listFiles();
		}

		this.setTitle(getString(R.string.currentDir) + ": " + f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory() && !ff.isHidden())
					dir.add(new Option(ff.getName(), getString(R.string.folder), ff
							.getAbsolutePath(), true, false));
				else {
					if (!ff.isHidden())
						fls.add(new Option(ff.getName(), getString(R.string.fileSize) + ": "
								+ ff.length(), ff.getAbsolutePath(), false, false));
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			Log.d("ODIM CRASH", "filechooser error : " + e);
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) {
			if (f.getParentFile() != null) dir.add(0, new Option("..", getString(R.string.parentDirectory), f.getParent(), false, true));
		}
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.isFolder() || o.isParent()) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
			fileSelected = new File(o.getPath());
			if (fileSelected.getName().substring(fileSelected.getName().length()-4, fileSelected.getName().length()).equals("bomb") || o.getName().substring(o.getName().length()-3, o.getName().length()).equals("txt")) {

					Intent intent = new Intent();
					intent.putExtra(BUNDLE_EXTRA_FILE, fileSelected.getAbsolutePath());
					setResult(Activity.RESULT_OK, intent);
					finish();
			}
		}
	}

	private void onFileClick(Option o) {
		Toast.makeText(this, "File Clicked: " + o.getPath(), Toast.LENGTH_SHORT)
				.show();

			SharedPreferences prefs = getSharedPreferences("bombers", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("file", ""+o.getPath());
			editor.commit();

	}
}