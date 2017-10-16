package com.idoc.constant;

import java.util.HashSet;
import java.util.Set;

public class FileUploadConstant {
	
	public static final String UPLOAD_TEMP_PATH = "temp";
	
	public static final int IMAGE_UPLOAD_MAX_SIZE = 10 * 1024 * 1024;// 设置上传文件最大为10M
	
	/** 允许上传的文件格式的列表 */
	@SuppressWarnings("serial")
	public static final Set<String> IMAGE_UPLOAD_ALLOW_TYPE = new HashSet<String>() {
		
		{
			add("xls");
			add("xlsx");
		}
	};
	
	/** 上传目标-CDN */
	public static final int UPLOAD_TARGET_CDN = 1;
	/** 上传目标-NGINX */
	public static final int UPLOAD_TARGET_NGINX = 2;
	
	/** 上传返回代码-成功 */
	public static final int SUCCESS = 200;
	/** 上传返回代码-参数错误 */
	public static final int PARAM_ERROR = 400;
	/** 上传返回代码-没有上传文件 */
	public static final int NO_FILE_ERROR = 401;
	/** 上传返回代码-空文件 */
	public static final int EMPTY_FILE_ERROR = 402;
	/** 上传返回代码-不支持的文件类型 */
	public static final int NOT_SUPPORT_TYPE_ERROR = 403;
	/** 上传返回代码-文件太大 */
	public static final int FILE_TOO_BIG_ERROR = 404;
	/** 上传返回代码-没有登录 */
	public static final int NOT_LOGIN_ERROR = 461;
	/** 上传返回代码-上传失败 */
	public static final int SYSTEM_ERROR = 500;
	
}
