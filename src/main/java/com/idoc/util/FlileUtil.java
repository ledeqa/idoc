package com.idoc.util;

import java.io.File;

public class FlileUtil {

	/**
	 * 删除文件，如果是目录递归删除下面所有文件
	 * @param file
	 */
	public static void deleteFile(File file){
		if(!file.exists()){
			return;
		}
		if(file.isFile() || file.list().length == 0){
		   file.delete();
		}else{
		   File[] files = file.listFiles();
		   for(File f : files){
			   deleteFile(f);
			   f.delete();
		   }
		   file.delete();
		}
	}
}