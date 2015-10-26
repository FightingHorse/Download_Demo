package com.download.service;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;

/**
 * 下载任务类
 * 
 * @author admin
 * 
 */
public class DownloadTask {
	private FileInfo mFileInfo;
	private Context mContext;
	private ThreadDAO dao;
	private int mFinished = 0;
	public boolean isPause = false;
	private int mThreadCount = 1;
	private List<DownloadThread> mThreadList;
	public static ExecutorService mExecutorService = Executors.newCachedThreadPool();

	public DownloadTask(FileInfo mFileInfo, Context mContext, int mThreadCount) {
		super();
		this.mThreadCount = mThreadCount;
		this.mFileInfo = mFileInfo;
		this.mContext = mContext;
		dao = new ThreadDAOImpl(mContext);
	}

	public void download() {
		// 读取数据库的线程信息
		List<ThreadInfo> threadInfos = dao.getThreads(mFileInfo.getUrl());
		if (threadInfos.size() == 0) {
			// 获取每个文件下载的长度
			int length = mFileInfo.getLength() / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {
				ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), length * i, (i + 1) * length - 1, 0);
				if (i == mThreadCount - 1) {
					threadInfo.setEnd(mFileInfo.getLength());
				}
				threadInfos.add(threadInfo);
				// 向数据库中插入线程信息
				dao.insertThread(threadInfo);
			}
		}
		mThreadList = new ArrayList<>();
		for (ThreadInfo info : threadInfos) {
			DownloadThread thread = new DownloadThread(info);
			DownloadTask.mExecutorService.execute(thread);
			mThreadList.add(thread);
		}
	}

	private synchronized void checkAllThreadsFinished() {
		boolean allFinished = true;
		for (DownloadThread thread : mThreadList) {
			if (!thread.isFinished) {
				allFinished = false;
				break;
			}

		}
		if (allFinished) {
			// 删除线程信息
			dao.deleteThread(mFileInfo.getUrl());
			Intent intent = new Intent(DownloadService.ACTION_FINISHED);
			intent.putExtra("fileInfo", mFileInfo);
			mContext.sendBroadcast(intent);

		}

	}

	class DownloadThread extends Thread {

		private ThreadInfo mThreadInfo = null;
		private HttpURLConnection conn;
		private RandomAccessFile raf;
		private InputStream input;
		public boolean isFinished = false;

		public DownloadThread(ThreadInfo mThreadInfo) {
			super();
			this.mThreadInfo = mThreadInfo;
		}

		public void run() {

			try {
				URL url = new URL(mThreadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(3000);
				conn.setRequestMethod("GET");
				// 设置下载位置
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				Log.i("test", "bytes" + start + "-" + mThreadInfo.getEnd());
				conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
				File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent(DownloadService.ACTION_UPDATE);
				mFinished = mThreadInfo.getFinished();
				// 开始下载
				Log.i("test", conn.getResponseCode() + "");
				if (conn.getResponseCode() == 206) {
					input = conn.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					long time = System.currentTimeMillis();
					while ((len = input.read(buffer)) != -1) {
						// 写入文件
						raf.write(buffer, 0, len);
						mFinished += len;
						// 累加每个线程完成的进度
						mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
						if (System.currentTimeMillis() - time > 1000) {
							time = System.currentTimeMillis();
							// 把下载进度发送广播给Activity
							Log.i("test", "finished" + mFinished);
							long finish = mFinished;
							intent.putExtra("finished", (int) (finish * 100 / mFileInfo.getLength()));
							intent.putExtra("id", mFileInfo.getId());
							mContext.sendBroadcast(intent);
						}
						// 下载暂停时,进度保存到数据库
						if (isPause) {
							Log.i("test", "finished" + mFinished);
							dao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mThreadInfo.getFinished());
							return;
						}

					}
					// 标示线程执行完毕
					isFinished = true;

					checkAllThreadsFinished();

				}

				// 把下载进度发送广播给activity

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					conn.disconnect();
					raf.close();
					input.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

}
