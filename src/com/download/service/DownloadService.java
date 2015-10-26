package com.download.service;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.download.entities.FileInfo;

public class DownloadService extends Service {
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	public static final String ACTION_FINISHED = "ACTION_FINISHED";
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/downloads/";
	public static final int MSG_INIT = 0;
	private Map<Integer, DownloadTask> mTasks = new LinkedHashMap<>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获得activity传递的参数
		if (ACTION_START.equals(intent.getAction())) {
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i("test", "start" + fileInfo);
			// 启动初始化线程
			DownloadTask.mExecutorService.execute(new InitThread(fileInfo));
		} else if (ACTION_STOP.equals(intent.getAction())) {
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i("test", "stop" + fileInfo);
			DownloadTask task = mTasks.get(fileInfo.getId());
			if (task != null) {
				task.isPause = true;

			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				FileInfo fileInfo = (FileInfo) msg.obj;
				Log.i("test", "Init" + fileInfo);
				// 启动下载任务
				DownloadTask mTask = new DownloadTask(fileInfo, DownloadService.this, 3);
				mTask.download();
				mTasks.put(fileInfo.getId(), mTask);
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 初始化的子线程
	 * 
	 */
	class InitThread extends Thread {

		private FileInfo mFileInfo = null;
		private RandomAccessFile raf;

		public InitThread(FileInfo mFileInfo) {
			super();
			this.mFileInfo = mFileInfo;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			int length = 0;
			try {
				// 连接网络文件
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() == 200) {
					// 获得文件长度
					length = conn.getContentLength();
				}
				if (length <= 0) {
					return;
				}
				File dir = new File(DOWNLOAD_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// 在本地创建文件
				File file = new File(dir, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.setLength(length);
				mFileInfo.setLength(length);
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.disconnect();
				try {
					raf.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
