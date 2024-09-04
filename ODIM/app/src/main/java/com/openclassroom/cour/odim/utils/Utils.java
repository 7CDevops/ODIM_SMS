package com.openclassroom.cour.odim.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import android.os.Environment;

import android.util.Log;

public class Utils {

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
	
	public static File whichDir(String dir1,String dir2,String dir3){
		File currentDir;
		currentDir = new File(dir1);
		if (!currentDir.exists()) {
			currentDir = new File(dir2);
			if (!currentDir.exists()) {
				currentDir = new File(dir3);
			}
		}				
		return currentDir;
	}
	
	public static void writeData(String filePath,String data,boolean append){
		try {
			File myFile = new File(filePath);
			if (!append & myFile.exists()) {
				myFile.delete();
			}
			myFile.createNewFile();
			 
			 Writer output;
			 output = new BufferedWriter(new FileWriter(filePath,true));  //clears file every time
				String[] elements = data.split("\n");
				for (int i = 0; i < elements.length; i++) {
					output.write(elements[i]+"\n");
				}
			 output.flush();
			 output.close();
		} catch (Exception e) {
			Log.e("Error", "Pb ecriture"+e.getMessage());
			Log.d("ODIM CRASH", "probleme ecriture rapport : " + e);
		}	
		
	}

	public static String getFileContent(String file) throws IOException{


		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16"));
		//BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line;
		String content = "";
		while ((line = br.readLine()) != null) {
			content += line+"\n";
		}
		return content;
	}

	public static String getFileContentRapport(String file) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		//BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line;
		String content = "";
		while ((line = br.readLine()) != null) {
			content += line+"\n";
		}
		return content;
	}

}
