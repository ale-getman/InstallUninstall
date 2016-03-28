package com.android.ag.insunins; // Copyright (c) 2015 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ApkinstallerActivity extends Activity implements SelectFileDialog.onSelectFileDialogListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String pathFile = "/sdcard/TestApp/FirstApp.apk";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(pathFile)), "application/vnd.android.package-archive");
		startActivity(intent);

		String pathFile_2 = "/sdcard/TestApp/SecondApp.apk";
		Intent intent_2 = new Intent(Intent.ACTION_VIEW);
		intent_2.setDataAndType(Uri.fromFile(new File(pathFile_2)), "application/vnd.android.package-archive");
		startActivity(intent_2);

		String pathFile_3 = "/sdcard/TestApp/ThirdApp.apk";
		Intent intent_3 = new Intent(Intent.ACTION_VIEW);
		intent_3.setDataAndType(Uri.fromFile(new File(pathFile_3)), "application/vnd.android.package-archive");
		startActivity(intent_3);
		//SelectFile();
	}

	protected SelectFileDialog _dlgSelectFile;

	private void SelectFile() {
		_dlgSelectFile = new SelectFileDialog(this);
		_dlgSelectFile.Show(getString(R.string.path));
	}

	public void onFileSelected_by_SelectFileDialog(final File file) {
		if (file != null) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				Log.d("LOGI", "URI: " + Uri.fromFile(file));
				startActivity(intent);
			} catch (Exception e) {
			}
		}
		_dlgSelectFile = null;
		finish();
	}

	@Override
	public void onPause() {
		if (_dlgSelectFile != null) {
			_dlgSelectFile.onPause();
		}

		super.onPause();
	}
}
