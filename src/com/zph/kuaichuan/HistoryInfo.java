package com.zph.kuaichuan;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class HistoryInfo implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5322429073374962259L;
	public String fileName;
	public String fileSize;
	public String Path;
	public int current_progress;
	public String speed;
	public Drawable icon;
}
