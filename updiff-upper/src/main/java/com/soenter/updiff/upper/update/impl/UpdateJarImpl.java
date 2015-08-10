/**
 * Copyright : http://www.sandpay.com.cn/ , 2007-2015
 * Project : updiff
 * $$Id$$
 * $$Revision$$
 * Last Changed by sun.mt at 2015/8/7 16:34
 * $$URL$$
 * <p/>
 * Change Log
 * Author      Change Date    Comments
 * -------------------------------------------------------------
 * sun.mt@sand.com.cn         2015/8/7        Initailized
 */
package com.soenter.updiff.upper.update.impl;

import com.soenter.updiff.common.DiffElement;
import com.soenter.updiff.upper.scan.Scaned;
import com.soenter.updiff.upper.scan.Scanner;
import com.soenter.updiff.upper.scan.impl.ScandDiffImpl;
import com.soenter.updiff.upper.scan.impl.ScannerDiffImpl;
import com.soenter.updiff.upper.update.Executor;
import com.soenter.updiff.upper.update.UpdateFactory;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @ClassName ��com.soenter.updiff.upper.update.impl.UpdateJarImpl
 * @Description : 
 * @author : sun.mt@sand.com.cn
 * @Date : 2015/8/7 16:34
 * @version 1.0.0
 *
 */
public class UpdateJarImpl extends UpdateImpl {

	public UpdateJarImpl (Scaned scaned, String backupPath) throws IOException {
		super(scaned, backupPath);
		if(!scaned.isJar()){
			throw new IOException("UpdateJarImpl ֻ�ܴ���jar�ļ�");
		}
	}

	@Override
	public void execute () throws IOException {

		if(scaned.isAddFile()){//������ļ�
			if(!scaned.getOldFile().getParentFile().mkdirs()){
				throw new IOException("���������ļ���ʧ�ܣ�" + scaned.getOldFile().getParentFile().getAbsolutePath());
			}
			FileUtils.copyFile(scaned.getNewFile(), scaned.getOldFile());
		} else if(scaned.hasDiff()){//����diff�ļ�
			//1.��ѹ����jar �� �����ļ����� {filename}_old
			UnArchiver unArchiverOld = new ZipUnArchiver();
			unArchiverOld.setSourceFile(backupFile);
			File unjarOld = new File(backupFile.getParent(), backupFile.getName() + "_old");
			unArchiverOld.setDestDirectory(unjarOld);
			unArchiverOld.extract();

			//2.��ѹ��jar �� �����ļ����� {filename}_new
			UnArchiver unArchiverNew = new ZipUnArchiver();
			unArchiverNew.setSourceFile(scaned.getNewFile());
			File unjarNew = new File(backupFile.getParent(), backupFile.getName() + "_new");
			unArchiverNew.setDestDirectory(unjarNew);
			unArchiverNew.extract();

			//3.����diff���ø��¡�{filename}_old���е��ļ�����{filename}_new����
			Scanner<DiffElement> diffElScanner = new ScannerDiffImpl(scaned.getDiffFile());
			Iterator<DiffElement> diffIt = diffElScanner.iterator();
			String updateJarBack = backupFile.getAbsolutePath() + "_bak";

			Executor executor = new Executor();
			while(diffIt.hasNext()){
				DiffElement element = diffIt.next();
				String compliedPath = element.getCompiledNewPath();
				File oldFile = new File(unjarOld.getAbsolutePath() + File.separator + compliedPath);
				File newFile = new File(unjarNew.getAbsolutePath() + File.separator + compliedPath);
				Scaned scaned = new ScandDiffImpl(oldFile, newFile, compliedPath, element);

				if(newFile.exists()){
					try {
						executor.execute(UpdateFactory.create(scaned, updateJarBack));
					} catch (Exception e){
						executor.recovery();
						throw new IOException(e);
					}
				}
			}


			//4.�����{filename}_old��Ϊ��jar
			JarArchiver archiver = new JarArchiver();

			archiver.addDirectory(unjarOld);
			File destFile = new File(backupFile.getParent(), backupFile.getName() + "_new.jar");
			archiver.setDestFile(destFile);
			archiver.createArchive();


			//5.����jar�滻���ļ�
			if(!scaned.getOldFile().delete()){
				throw new IOException("ɾ�����ļ�ʧ�ܣ�" + scaned.getOldFile().getAbsolutePath());
			}
			FileUtils.copyFile(destFile, scaned.getOldFile());

		}


	}
}
