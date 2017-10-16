package com.idoc.web.upload;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.nostransfer.exception.EcomNosException;
import com.idoc.nostransfer.impl.AsyncEcomTransferManagerImpl;
import com.idoc.web.BaseController;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

@Controller
@RequestMapping("idoc")
public class UploadFileController extends BaseController {
	
	public static final String NOS_URL_PREFIX = "http://ecom.nos.netease.com/";
	
	@Autowired
	private AsyncEcomTransferManagerImpl asyncEcomTransferManager;
	
	@RequestMapping(value = "/ckeditor/upload", method = RequestMethod.POST)
	public void uploadFileToNos(@RequestParam(value = "upload")MultipartFile file, HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/html;charset=UTF-8");
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(date);
		
		asyncEcomTransferManager.setGroupName("case/idoc/" + dateStr);
		try {
			String key = UUID.randomUUID().toString();
			String originFileName = file.getOriginalFilename().toUpperCase();
			String CKEditorFuncNum = request.getParameter("CKEditorFuncNum");
			PrintWriter out = response.getWriter();;
			if(originFileName.endsWith(CommonConstant.BPM_SUFFIX) || originFileName.endsWith(CommonConstant.GIF_SUFFIX)
					|| originFileName.endsWith(CommonConstant.JPEG_SUFFIX) || originFileName.endsWith(CommonConstant.JPG_SUFFIX)
					|| originFileName.endsWith(CommonConstant.PNG_SUFFIX)){
				
				Future<UploadResult> result = asyncEcomTransferManager.upload(key, file.getInputStream(), file.getSize(), null);
				String accessUrl = NOS_URL_PREFIX + asyncEcomTransferManager.getGroupName() + "/" + key;
				LogConstant.debugLog.info("uploadFileToNos----------success upload file,access url: " + accessUrl);
				
				String script = "<script type=\"text/javascript\">window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ", '" + accessUrl + "');</script>";
				out.print(script);
			}else{
				String script = "<script type=\"text/javascript\">window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ", '', '文件格式不正确（必须为.jpg/.jpeg/.gif/.bpm/.png文件）');</script>";
				out.print(script);
			}
	        out.flush();
	        out.close();
		} catch (IOException e) {
			LogConstant.debugLog.error("uploadFileToNos----------IOException", e);
		} catch (EcomNosException e) {
			LogConstant.debugLog.error("uploadFileToNos-----------EcomNosException", e);
		}
	}
}
