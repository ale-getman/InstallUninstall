package com.android.ag.insunins; // Copyright (c) 2015 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class SelectFileDialog extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnKeyListener {
	private File _fileCurrent;
	private File[] _aFileList;
	String AppName = "FileSelector";
	private String[] _astrFileName;
	private Context _context;

	private Dialog _dlgThis;

	public SelectFileDialog(Context context) {
		_context = context;
	}

	@Override
	public void onPause() {
		if (_dlgThis != null && _dlgThis.isShowing())
			_dlgThis.dismiss();

		super.onPause();
	}

	public boolean Show(String strInitPath) {
		boolean ret;

		ret = CreateFileList(strInitPath);
		if (ret == false)
			return false;

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(_context);
		dlgBuilder.setCancelable(true);
		dlgBuilder.setOnKeyListener(this);
		dlgBuilder.setTitle(_fileCurrent.getPath() + " - " + AppName);
		dlgBuilder.setItems(_astrFileName, this);

		_dlgThis = dlgBuilder.create();
		_dlgThis.show();

		return true;
	}

	public void Close(DialogInterface dialog, File fileSelected) {
		( (onSelectFileDialogListener) _context ).onFileSelected_by_SelectFileDialog(fileSelected);
		dialog.dismiss();
		_dlgThis = null;
	}

	public void onClick(DialogInterface dialog, int which) {
		File file = _aFileList[which];

		if (file.isDirectory()) {
			Show(file.getAbsolutePath());
			dialog.dismiss();
		} else {
			Close(dialog, file);
		}
	}

	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			File fileParent;

			fileParent = _fileCurrent.getParentFile();
			if (fileParent != null) {
				Show(fileParent.getAbsolutePath());
				dialog.dismiss();
			} else {
				Close(dialog, null);
			}

			return true;
		}
		return false;
	}

	private boolean CreateFileList(String strPath) {
		File[] aFiles;

		_aFileList = null;
		_astrFileName = null;

		_fileCurrent = new File(strPath);
		if (_fileCurrent == null)
			return false;

		aFiles = _fileCurrent.listFiles();
		if (aFiles == null || aFiles.length == 0) {
			_aFileList = new File[0];
			_astrFileName = new String[0];
			return true;
		}

		int i, j, k;
		String[] astrName;

		astrName = new String[aFiles.length];

		j = 0;
		for (i = 0; i < aFiles.length; i++) {
			if (aFiles[i].isDirectory() && aFiles[i].isHidden() == false) {
				astrName[i] = aFiles[i].getName() + "/";
				j++;
			} else if (aFiles[i].isFile() && aFiles[i].isHidden() == false) {
				astrName[i] = aFiles[i].getName();
				j++;
			} else {
				astrName[i] = null;
				aFiles[i] = null;
			}
		}

		int flagNotRoot = 1;
		if (_fileCurrent.getParentFile() == null) {
			flagNotRoot = 0;
		}
		_aFileList = new File[j + flagNotRoot];
		_astrFileName = new String[j + flagNotRoot];

		FileStr fileStr[] = new FileStr[j];
		k = 0;
		for (i = 0; i < aFiles.length; i++) {
			if (aFiles[i] != null) {
				fileStr[k] = new FileStr(aFiles[i], astrName[i]);
				k++;
			}
		}
		Arrays.sort(fileStr);

		if (flagNotRoot == 1) {
			_aFileList[0] = _fileCurrent.getParentFile();
			String Name = _fileCurrent.getParentFile().getName().equals("") ? "/" : _fileCurrent.getParentFile().getPath();
			_astrFileName[0] = "../ ( " + Name + " )";
		}

		for (i = 0; i < k; i++) {
			_aFileList[i + flagNotRoot] = fileStr[i].getFile();
			if (( fileStr[i].getName().endsWith(".apk") ) || ( fileStr[i].getName().endsWith("/") )) {
				_astrFileName[i + flagNotRoot] = fileStr[i].getName();
			} else {
				_astrFileName[i + flagNotRoot] = " ( " + fileStr[i].getName() + " )";
			}
		}

		return true;
	}

	public interface onSelectFileDialogListener {
		public void onFileSelected_by_SelectFileDialog(File file);
	}
}

class FileStr implements Comparable<FileStr> {
	File file;
	String name;

	FileStr(File file, String name) {
		this.file = file;
		this.name = name;
	}

	String getName() {
		return this.name;
	}

	File getFile() {
		return this.file;
	}

	public int compareTo(FileStr f) {
		return Integer.valueOf(( this.getName() ).compareTo(f.getName()));
	}
}
