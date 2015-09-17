package com.sand.updiff.upper.dom;

import com.sand.updiff.common.ChangeType;

import java.io.File;

/**
 *
 * @author : sun.mt
 * @date : 2015/8/14 15:20
 * @since 1.0.0
 *
 */
public class RedologItem implements Item{

	private boolean isDir;

	private ChangeType change;

	private String fromPath;

	private String toPath;

	private String backupPath;

	public RedologItem (boolean isDir, ChangeType change, String fromPath, String toPath, String backupPath) {
		this.isDir = isDir;
		this.change = change;
		this.fromPath = fromPath;
		this.toPath = toPath;
		this.backupPath = backupPath;
	}

	public RedologItem (boolean isDir, ChangeType change, File fromPath, File toPath, File backupPath) {
		this.isDir = isDir;
		this.change = change;
		this.fromPath = fromPath == null?null:fromPath.getAbsolutePath();
		this.toPath = toPath == null?null:toPath.getAbsolutePath();
		this.backupPath = backupPath == null?null:backupPath.getAbsolutePath();
	}

	public boolean isDir () {
		return isDir;
	}

	public ChangeType getChange () {
		return change;
	}

	public String getFromPath () {
		return fromPath;
	}

	public String getToPath () {
		return toPath;
	}

	public String getBackupPath () {
		return backupPath;
	}

}
