package com.download.adpter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.app.R;
import com.download.entities.FileInfo;
import com.download.service.DownloadService;

/**
 * 文件列表适配器
 * 
 * @author admin
 * 
 */
public class FileListAdapter extends BaseAdapter {

	private Context mContext;
	private List<FileInfo> mFileList = null;

	public FileListAdapter(Context mContext, List<FileInfo> mFileList) {
		super();
		this.mContext = mContext;
		this.mFileList = mFileList;
	}

	@Override
	public int getCount() {
		return mFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		final FileInfo fileInfo = mFileList.get(position);
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.tvFileName = (TextView) view.findViewById(R.id.tvFileName);
			holder.btStart = (Button) view.findViewById(R.id.btStart);
			holder.btStop = (Button) view.findViewById(R.id.btStop);
			holder.pbFile = (ProgressBar) view.findViewById(R.id.pbProgress);
			holder.tvFileName.setText(fileInfo.getFileName());
			holder.pbFile.setMax(100);
			holder.btStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					mContext.startService(intent);
				}
			});
			holder.btStop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DownloadService.class);
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", fileInfo);
					mContext.startService(intent);
				}
			});
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.pbFile.setProgress(fileInfo.getFinished());

		return view;
	}

	public void updateProgress(int id, int progress) {
		FileInfo fileInfo = mFileList.get(id);
		fileInfo.setFinished(progress);
		notifyDataSetChanged();

	}

	static class ViewHolder {
		TextView tvFileName;
		Button btStart, btStop;
		ProgressBar pbFile;
	}
}
