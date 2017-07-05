package com.sand.updiff.mvnplugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.Test;

/**
 * ADD SOME　ＴＥＳＴ ＴＥＸＴ
 * Created by sun on 2015/8/2.
 * 
 * new other line for test
 * 
 * after dev merge
 */
public class JGitTest{
	//此目录下需要有.git目录
	static final String PROJECT_PATH="e:/workspaces/workspaceTest/updiff/";

	@Test
	public void test_call() throws IOException, GitAPIException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();


		Repository repository = builder.setGitDir(new File(PROJECT_PATH+".git"))
				.readEnvironment()
				.findGitDir()
				.build();

		Git git = new Git(repository);

		ObjectReader reader = git.getRepository().newObjectReader();

		CanonicalTreeParser newTreeIterator = new CanonicalTreeParser();
		ObjectId newTree = git.getRepository().resolve("HEAD^{tree}");
		newTreeIterator.reset(reader, newTree);

		CanonicalTreeParser oldTreeIterator = new CanonicalTreeParser();
		ObjectId oldTree = git.getRepository().resolve("6525ef3^{tree}");
		oldTreeIterator.reset(reader, oldTree);

		List<DiffEntry> diffs = git.diff()
				.setNewTree(newTreeIterator)
				.setOldTree(oldTreeIterator)
				.call();
		List<String> files = new ArrayList<String>();
		for (DiffEntry diffEntry: diffs){
			System.out.println(diffEntry.getNewPath());
			if(diffEntry.getNewPath().indexOf("/src/test/java/")!=-1)
				continue;
			String filePath=diffEntry.getNewPath();
			filePath=filePath.replace("src/main/java", "target/classes").replace("src/main/resources", "target/classes").replace(".java", ".class");
			files.add(filePath);
		}
		
		zip(files);

	}
	
	static final int BUFFER = 2048;
	public static void zip (List<String> files) {
	      try {
	         BufferedInputStream origin = null;
	         FileOutputStream dest = new 
	           FileOutputStream(PROJECT_PATH+"/updiff.zip");
	         ZipOutputStream out = new ZipOutputStream(new 
	           BufferedOutputStream(dest));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         for (int i=0; i<files.size(); i++) {
	            System.out.println("Adding: "+files.get(i));
	            FileInputStream fi = new 
	              FileInputStream(PROJECT_PATH+files.get(i));
	            origin = new 
	              BufferedInputStream(fi, BUFFER);
	            String filePath=files.get(i).replace("/target/", "/WEB-INF/");
	            
	            ZipEntry entry = new ZipEntry(filePath);
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	              BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	            origin.close();
	         }
	         out.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	   }

	/**TODO 
	 * 	1.更加易使用,配置或者传参数。或者maven插件。
		2.如果更新的文件位于  packaging为jar或者maven-plugin的模块中，则不把此文件放入zip包中，
	而对于有文件更新的jar或者maven-plugin模块做标记，最后把这此JAR放入zip的WEB-INF/lib中。
		3.对于test类不打包
	*/
		
	
}
