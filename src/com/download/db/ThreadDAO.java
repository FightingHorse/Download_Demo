package com.download.db;

import java.util.List;

import com.download.entities.ThreadInfo;

/**
 * 数据访问接口
 * 
 * @author admin
 * 
 */
public interface ThreadDAO {
	public void insertThread(ThreadInfo threadInfo);

	public void deleteThread(String url);

	public void updateThread(String url, int thread_id, int finished);

	public List<ThreadInfo> getThreads(String url);

	public boolean isExist(String url, int thread_id);
}
