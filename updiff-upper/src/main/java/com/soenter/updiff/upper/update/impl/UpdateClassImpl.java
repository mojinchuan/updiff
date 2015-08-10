/**
 * Copyright : http://www.sandpay.com.cn/ , 2007-2015
 * Project : updiff
 * $$Id$$
 * $$Revision$$
 * Last Changed by sun.mt at 2015/8/7 16:35
 * $$URL$$
 * <p/>
 * Change Log
 * Author      Change Date    Comments
 * -------------------------------------------------------------
 * sun.mt@sand.com.cn         2015/8/7        Initailized
 */
package com.soenter.updiff.upper.update.impl;

import com.soenter.updiff.upper.scan.Scaned;
import org.apache.commons.io.FileUtils;

import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName ��com.soenter.updiff.upper.update.impl.UpdateClassImpl
 * @Description : 
 * @author : sun.mt@sand.com.cn
 * @Date : 2015/8/7 16:35
 * @version 1.0.0
 *
 */
public class UpdateClassImpl extends UpdateImpl{

	private static final String CLASS_SUBPRIX = ".class";

	private List<File> oldFiles;
	private List<File> newFiles;



	public UpdateClassImpl (Scaned scaned, String backupPath) throws IOException {
		super(scaned, backupPath);

		if(scaned.isDir() || !scaned.getNewFile().getName().endsWith(FileType.CLASS.getType())){
			throw new IOException("UpdateClassImpl ֻ�ܴ�����.class��β���ļ�");
		}

		String name = scaned.getNewFile().getName();
		String filterName = name.substring(0, name.length() - CLASS_SUBPRIX.length()) + "$";

		File oldParent = scaned.getOldFile().getParentFile();
		oldFiles = genInnerClassList(oldParent, filterName);

		File newParent = scaned.getNewFile().getParentFile();
		newFiles = genInnerClassList(newParent, filterName);


	}

	public void backup () throws IOException {
		if(!scaned.isAddFile()){
			File backupDir = new File(backupPath + File.separator + scaned.getRelativePath()).getParentFile();

			if(!backupDir.mkdirs()){
				throw new IOException("���������ļ���ʧ��:" + backupDir.getAbsolutePath());
			}

			for(File f: oldFiles){
				File backupFile = new File(backupDir, f.getName());
				if(backupFile.exists()){
					throw new IOException("�����ļ��Ѿ�����:" + backupFile.getAbsolutePath());
				}
				FileUtils.copyFile(f, backupFile);
			}
		}
	}

	public void recovery () throws IOException {
		if(scaned.isAddFile()){
			//ɾ���¼��ļ�
			deleteAddFile();
		} else if(scaned.isModifyFile()){
			//ɾ���¼��ļ�
			deleteAddFile();
			//��ԭ�����ļ�
			recoveryOldFile();
		} else if(scaned.isDeleteFile()){
			//��ԭ�����ļ�
			recoveryOldFile();
		}

	}

	public void execute () throws IOException {

		if(scaned.isAddFile()){
			//�������ļ�
			File oldPathDir = scaned.getOldFile().getParentFile();
			for(File f: newFiles){
				File newFile = new File(oldPathDir, f.getName());
				if(newFile.exists()){
					throw new IOException("���ļ��Ѿ�����:" + f.getAbsolutePath());
				}
				File newFileParent = newFile.getParentFile();

				if(!newFileParent.exists() && !newFileParent.mkdirs()){
					throw new IOException("���ļ���Ŀ¼����ʧ��:" + f.getAbsolutePath());
				}
				FileUtils.copyFile(f, newFile);
			}
		} else if(scaned.isModifyFile()){
			//ɾ�����ļ�
			for(File f: oldFiles){
				if(f.delete()){
					throw new IOException("���ļ�ɾ��ʧ��:" + f.getAbsolutePath());
				}
			}
			//�������ļ�
			File oldPathDir = scaned.getOldFile().getParentFile();
			for(File f: newFiles){
				File newFile = new File(oldPathDir, f.getName());
				if(newFile.exists()){
					throw new IOException("���ļ��Ѿ�����:" + f.getAbsolutePath());
				}
				FileUtils.copyFile(f, newFile);
			}
		} else if(scaned.isDeleteFile()){
			//ɾ�����ļ�
			for(File f: oldFiles){
				if(!f.delete()){
					throw new IOException("���ļ�ɾ��ʧ��:" + f.getAbsolutePath());
				}
			}
		}


	}

	private static List<File> genInnerClassList(File file, String filterName){
		List<File> files = new ArrayList<File>();
		files.add(file);
		for(File f: file.listFiles()){
			if(f.isDirectory()) continue;

			if(f.getName().startsWith(filterName) && f.getName().endsWith(CLASS_SUBPRIX)){
				files.add(f);
			}
		}
		return files;
	}

	private void deleteAddFile() throws IOException {
		//ɾ���¼��ļ�
		File oldPathDir = scaned.getOldFile().getParentFile();
		for(File f: newFiles){
			File newFile = new File(oldPathDir, f.getName());
			if(!newFile.delete()){
				throw new IOException("[�ָ�]-ɾ���¼��ļ�ʧ�ܣ�" + f.getAbsolutePath());
			}
		}
	}

	private void recoveryOldFile() throws IOException {
		//��ԭ�����ļ�
		File backupDir = new File(backupPath + File.separator + scaned.getRelativePath()).getParentFile();
		for(File f: oldFiles){
			File backupFile = new File(backupDir, f.getName());
			FileUtils.copyFile(backupFile, f);
		}
	}
}
