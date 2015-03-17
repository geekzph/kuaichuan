package com.zph.kuaichuan;

import java.util.Comparator;


/** 排序 **/
public class FileComparator implements Comparator<MyFile> {

	public int compare(MyFile file1, MyFile file2) {
		// 文件夹排在前面
		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		// 相同类型按名称排序
		return file1.Name.toLowerCase().compareTo(file2.Name.toLowerCase());
	}
}