package com.zph.kuaichuan;

import java.util.Comparator;


/** ���� **/
public class FileComparator implements Comparator<MyFile> {

	public int compare(MyFile file1, MyFile file2) {
		// �ļ�������ǰ��
		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		// ��ͬ���Ͱ���������
		return file1.Name.toLowerCase().compareTo(file2.Name.toLowerCase());
	}
}