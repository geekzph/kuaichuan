package com.zph.kuaichuan;

/** 表示一个文件实体 **/
public class FileInfo {
	public String Name;
	public String Path;
	public String Size;
	public String ModifiedTime;
	public boolean IsDirectory = false;
	public int FileCount = 0;
	public int FolderCount = 0;
}