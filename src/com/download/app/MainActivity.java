package com.download.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.download.adpter.FileListAdapter;
import com.download.entities.FileInfo;
import com.download.service.DownloadService;

/**
 * 主界面
 * 
 * @author admin
 * 
 */
public class MainActivity extends Activity {
	private ListView lvFile;
	private Button btn_add;
	private List<FileInfo> mFilelist;
	private FileListAdapter mAdapter = null;

	private static int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化组件
		lvFile = (ListView) findViewById(R.id.lvFile);
		btn_add = (Button) findViewById(R.id.btn_add);
		mFilelist = new ArrayList<>();
		mAdapter = new FileListAdapter(this, mFilelist);
		lvFile.setAdapter(mAdapter);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 创建文件信息对象
				FileInfo fileInfo = new FileInfo(
						0,
						"http://scm3.evbj.easou.com/release/app-app/android/esvideo/1.30.0/esvideo_1.30.0-ad_forDev_B1.30.0-25125_1.30.0_20150921-1023_RBL/esvideo-1.30.0-bvf1328_10699_001.apk",
						"esvideo" + i++ + ".apk", 0, 0);
				mFilelist.add(fileInfo);
				mAdapter.notifyDataSetChanged();
			}
		});

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_UPDATE);
		filter.addAction(DownloadService.ACTION_FINISHED);
		registerReceiver(receiver, filter);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	};

	/**
	 * 更行UI
	 */
	BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(android.content.Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", 0);
				mAdapter.updateProgress(id, finished);
			} else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())) {
				FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				mAdapter.updateProgress(fileInfo.getId(), 100);
				Toast.makeText(MainActivity.this, fileInfo.getFileName(), 0).show();
			}
		};
	};

}
