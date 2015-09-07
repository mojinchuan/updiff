/**
 * Copyright : http://www.sandpay.com.cn/ , 2007-2015
 * Project : updiff
 * $$Id$$
 * $$Revision$$
 * Last Changed by sun.mt at 2015/8/10 15:36
 * $$URL$$
 * <p/>
 * Change Log
 * Author      Change Date    Comments
 * -------------------------------------------------------------
 * sun.mt@sand.com.cn         2015/8/10        Initailized
 */
package com.sand.updiff.upper.scan.impl;

import com.sand.updiff.common.DiffItem;
import com.sand.updiff.common.DiffReader;
import com.sand.updiff.upper.scan.Scanned;
import com.sand.updiff.upper.scan.Scanner;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @ClassName : com.sand.updiff.upper.scan.impl.DiffScanner
 * @Description : 
 * @author : sun.mt@sand.com.cn
 * @Date : 2015/8/10 15:36
 * @version 1.0.0
 *
 */
public class DiffScanner implements Scanner<Scanned> {

	private File oldDir;

	private File newDir;

	private File diffFile;

	private DiffReader diffReader;

	private List<Scanned> scanFiles;

	public DiffScanner (File oldDir, File newDir, File diffFile) throws IOException {
		this.oldDir = oldDir;
		this.newDir = newDir;
		this.diffFile = diffFile;

		if(!this.diffFile.exists()){
			throw new IOException("diff 不存在");
		}
		try {
			diffReader = new DiffReader(this.diffFile);
		} catch (DocumentException e) {
			throw new IOException("diff 文件格式错误");
		}
	}

	public Iterator<Scanned> iterator () {

		if(scanFiles == null){
			Iterator<DiffItem> diffIt = diffReader.readAll().iterator();
			scanFiles = new LinkedList<Scanned>();

			String packing = diffReader.getPackaging();
			boolean isWar = "war".equalsIgnoreCase(packing);
			Map<String, Boolean> needCompileMap = null;
			if(isWar){
				needCompileMap = new HashMap<String, Boolean>();
				needCompileMap.put(diffReader.getMainJavaGroup(), true);
				String[] mainResources = diffReader.getMainResourceGroups();
				for(String resource: mainResources){
					needCompileMap.put(resource, true);
				}
			}

			while(diffIt.hasNext()){
				DiffItem item = diffIt.next();

				String compliedPath = item.getCompiledNewPath();
				File oldFile = null;
				File newFile = null;
				if(isWar && needCompileMap.containsKey(item.getGroupName())){
					oldFile = new File(oldDir, "WEB-INF/classes/" + compliedPath);
					newFile = new File(newDir, "WEB-INF/classes/" + compliedPath);
				} else {
					oldFile = new File(oldDir, compliedPath);
					newFile = new File(newDir, compliedPath);
				}

				Scanned scanned = new DiffScanned(oldFile, newFile, compliedPath, item);
				scanFiles.add(scanned);
			}
		}

		return scanFiles.iterator();
	}

}
