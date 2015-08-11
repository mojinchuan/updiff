/**
 * Copyright : http://www.sandpay.com.cn/ , 2007-2015
 * Project : updiff
 * $$Id$$
 * $$Revision$$
 * Last Changed by sun.mt at 2015/8/7 13:46
 * $$URL$$
 * <p/>
 * Change Log
 * Author      Change Date    Comments
 * -------------------------------------------------------------
 * sun.mt@sand.com.cn         2015/8/7        Initailized
 */
package com.soenter.updiff.upper.scan.impl;

import com.soenter.updiff.common.FileType;
import com.soenter.updiff.common.UpiffFileUtils;
import com.soenter.updiff.upper.scan.Scaned;
import com.soenter.updiff.upper.scan.Scanner;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @ClassName ：com.soenter.updiff.upper.scan.impl.ScannerImpl
 * @Description : 
 * @author : sun.mt@sand.com.cn
 * @Date : 2015/8/7 13:46
 * @version 1.0.0
 *
 */
public class ScannerImpl implements Scanner{

	private File oldDir;

	private File newDir;


	public ScannerImpl (File oldDir, File newDir) {

		if(!oldDir.isDirectory() || !newDir.isDirectory()){
			throw new RuntimeException("Scanner 参数必须是文件夹");
		}

		this.oldDir = oldDir;
		this.newDir = newDir;
	}

	public Iterator<Scaned> iterator () {

		List<Scaned> scanFiles = new LinkedList<Scaned>();

		walkFiles(scanFiles, newDir);

		return scanFiles.iterator();
	}

	private void walkFiles(List<Scaned> scanFiles, File file){

		File[] files = file.listFiles();
		for (File f: files){
			if(f.isDirectory()){
				File realFile = UpiffFileUtils.castControlFile(f);
				scanFiles.add(new ScanedImpl(getOldFile(realFile), realFile, getRelativePath(realFile)));
				walkFiles(scanFiles, f);
			} else if(!f.getName().endsWith(FileType.DIFF.getType()) && !UpiffFileUtils.isInnerClassFile(f)){
				File realFile = UpiffFileUtils.castControlFile(f);
				scanFiles.add(new ScanedImpl(getOldFile(realFile), realFile, getRelativePath(realFile)));
			}
		}
	}

	private File getOldFile(File file){
		String oldFilePath = oldDir.getAbsolutePath() + getRelativePath(file);
		return new File(oldFilePath);
	}

	private String getRelativePath(File file){
		return file.getAbsolutePath().substring(newDir.getAbsolutePath().length());
	}
}
