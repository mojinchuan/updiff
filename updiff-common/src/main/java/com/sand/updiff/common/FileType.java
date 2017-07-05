package com.sand.updiff.common;

/**
 * @author : sun.mt@sand.com.cn
 * @version 1.0.0
 * @ClassName ：com.sand.updiff.common.FileType
 * @Description :
 * @Date : 2015/8/11 9:09
 */
public enum FileType {
	CLASS(".class"),
	JAR(".jar"),
	DELETE(".delete"),
	DIFF(".diff"),
	ZIP(".zip"),
	TAR_GZ(".tar.gz"),
	BAK_XML(".backup.xml"),
	REDOLOG(".redolog");

	String type;
	FileType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}
}
