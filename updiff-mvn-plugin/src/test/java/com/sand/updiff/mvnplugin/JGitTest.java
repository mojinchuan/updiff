package com.sand.updiff.mvnplugin;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by sun on 2015/8/2.
 */
public class JGitTest{


	@Test
	public void test_call() throws IOException, GitAPIException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();


		Repository repository = builder.setGitDir(new File("e:/workspaces/workspaceTest/updiff/.git"))
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
		String[] files = new String[diffs.size()];
		int i=0;
		for (DiffEntry diffEntry: diffs){
			System.out.println(diffEntry.getNewPath());
			files[i]=diffEntry.getNewPath();
			files[i]=files[i].replace("src/main/java", "target/classes").replace("src/main/resources", "target/classes").replace(".java", ".class");
			i++;
		}
		
		zip(files);

	}
	
	static final int BUFFER = 2048;
	public static void zip (String[] files) {
	      try {
	         BufferedInputStream origin = null;
	         FileOutputStream dest = new 
	           FileOutputStream("e:/workspaces/workspaceTest/updiff/updiff.zip");
	         ZipOutputStream out = new ZipOutputStream(new 
	           BufferedOutputStream(dest));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         // get a list of files from current directory
	         File f = new File("e:/workspaces/workspaceTest/updiff/");
	         
	         for (int i=0; i<files.length; i++) {
	            System.out.println("Adding: "+files[i]);
	            if(files[i].equals(".settings"))
	            	continue;
	            FileInputStream fi = new 
	              FileInputStream(f.getAbsolutePath()+"/"+files[i]);
	            origin = new 
	              BufferedInputStream(fi, BUFFER);
	            files[i]=files[i].replace("/target/", "/WEB-INF/");
	            
	            ZipEntry entry = new ZipEntry(files[i]);
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

}
