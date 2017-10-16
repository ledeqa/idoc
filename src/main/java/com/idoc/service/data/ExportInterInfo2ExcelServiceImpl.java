package com.idoc.service.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idoc.constant.LogConstant;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.UserModel;
import com.idoc.model.data.InterfaceSimpleInfo;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.util.Config;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service("exportInterInfo2ExcelServiceImpl")
public class ExportInterInfo2ExcelServiceImpl {
	private static Configuration configuration = null;  
	private String templateFolderPath = "";
    private String outputFolderPath = "";
    
    @Autowired
    private InterfaceServiceImpl interfaceServiceImpl;
    
    @Autowired
    private ProjectDaoImpl projectDaoImpl;
    
    @Autowired
    private LoginDaoImpl loginDaoImpl;
    
    @SuppressWarnings("deprecation")
    @PostConstruct
	public void init(){  
    	configuration = new Configuration();  
        configuration.setDefaultEncoding("UTF-8");
		LogConstant.debugLog.info("====================================================");
		LogConstant.debugLog.info("初始化输入输出文件路径.......");
		String os = System.getProperty("os.name");
		String webPath = this.getClass().getResource("/").getPath();
		if (os.contains("Windows")) {
			try {
				templateFolderPath = webPath + Config.getConfig("default_template_folder_windows");
				outputFolderPath = webPath + Config.getConfig("default_output_folder_windows");
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		} else {
			try {
				templateFolderPath = webPath + Config.getConfig("default_template_folder_linux");
				outputFolderPath = webPath + Config.getConfig("default_output_folder_linux");
			} catch (IOException e) {
				LogConstant.debugLog.info("读取配置文件data.propertites出错！");
				e.printStackTrace();
			}
		}

		LogConstant.debugLog.info("初始化输入输出文件名.......");
		LogConstant.debugLog.info("templateFolderPath : " + templateFolderPath);
		LogConstant.debugLog.info("outputFolderPath : " + outputFolderPath);
		LogConstant.debugLog.info("====================================================");
    }
    
    /**
     * 生成文件
     * @param projectId
     * @param projectName
     * @param interfaceIdList
     * @return
     */
    public String exportInterInfo2Excel(String projectId, List<String> interfaceIdList){
    	String projectName = projectDaoImpl.selectProjectModelByProjectId(Long.parseLong(projectId)).getProjectName();
    	File outFile = new File(outputFolderPath + "excel_" + projectName + "_" +  UUID.randomUUID() + ".xls");
    	String[] headers = { "接口名称", "接口状态", "创建者", "请求类型", "接口类型", "接口地址" };
    	String path = "";
    	List<Interface> interfaces = interfaceServiceImpl.getInterSimpleInfo(interfaceIdList);
		if(interfaces==null || interfaces.size()<=0){
			LogConstant.debugLog.info("导出excel接口信息时，该项目下没有任何接口！");
			return null;
		}
    	try {
    		if(!outFile.exists()){
        		outFile.createNewFile();
        	}
			OutputStream out = new FileOutputStream(outFile);
			List<InterfaceSimpleInfo> interList = new ArrayList<>();
			for(Interface inter : interfaces){
	        	InterfaceSimpleInfo simpleInfo = new InterfaceSimpleInfo();
	        	simpleInfo.setInterfaceName(inter.getInterfaceName());
	        	List<UserModel> creator = loginDaoImpl.selectUserModelListByIds(inter.getCreatorId().toString());
	        	simpleInfo.setCreator(creator.get(0).getUserName());
	        	simpleInfo.setInterfaceStatus(getStatus(inter.getInterfaceStatus()));
	        	simpleInfo.setInterfaceType(getInterType(inter.getInterfaceType()));
	        	simpleInfo.setRequestType(getRequestType(inter.getRequestType()));
	        	simpleInfo.setUrl("http://idoc.qa.lede.com/idoc/inter/index.html?projectId=" + projectId +"&interfaceId=" + inter.getInterfaceId());
	        	interList.add(simpleInfo);
			}
			path = outFile.getPath();
			exportInterInfo2Excel(projectName, headers, interList, out, "yyyy-MM-dd");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    	return path;
    }
    
    /**
     * 生成字节流
     * @param projectId
     * @param projectName
     * @param interfaceIdList
     * @return
     */
    public byte[] exportInterInfo2ByteStream(String projectId, List<String> interfaceIdList) {
    	String projectName = projectDaoImpl.selectProjectModelByProjectId(Long.parseLong(projectId)).getProjectName();
    	String[] headers = { "接口名称", "接口状态", "创建者", "请求类型", "接口类型", "接口地址" };
    	List<InterfaceSimpleInfo> interList = new ArrayList<>();
		List<Interface> interfaces = interfaceServiceImpl.getInterSimpleInfo(interfaceIdList);
		if(interfaces==null || interfaces.size()<=0){
			LogConstant.debugLog.info("导出excel接口信息时，该项目下没有任何接口！");
			return null;
		}
		for(Interface inter : interfaces){
        	InterfaceSimpleInfo simpleInfo = new InterfaceSimpleInfo();
        	simpleInfo.setInterfaceName(inter.getInterfaceName());
        	List<UserModel> creator = loginDaoImpl.selectUserModelListByIds(inter.getCreatorId().toString());
        	simpleInfo.setCreator(creator.get(0).getUserName());
        	simpleInfo.setInterfaceStatus(getStatus(inter.getInterfaceStatus()));
        	simpleInfo.setInterfaceType(getInterType(inter.getInterfaceType()));
        	simpleInfo.setRequestType(getRequestType(inter.getRequestType()));
        	simpleInfo.setUrl("http://idoc.qa.lede.com/idoc/inter/index.html?projectId=" + projectId +"&interfaceId=" + inter.getInterfaceId());
        	interList.add(simpleInfo);
		}
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	exportInterInfo2Excel(projectName, headers, interList, baos, "yyyy-MM-dd");
        
		return baos.toByteArray();
	}

	@SuppressWarnings("deprecation")
    public void exportInterInfo2Excel(String title, String[] headers,Collection<InterfaceSimpleInfo> dataset, OutputStream out, String pattern){
    	// 声明一个工作薄  
        HSSFWorkbook workbook = new HSSFWorkbook();  
        // 生成一个表格  
        HSSFSheet sheet = workbook.createSheet(title);  
        // 设置表格默认列宽度为15个字节  
        sheet.setDefaultColumnWidth(20); 
        // 生成一个样式  
        HSSFCellStyle style = workbook.createCellStyle();  
        // 设置这些样式  
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);  
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
        // 生成一个字体  
        HSSFFont font = workbook.createFont();  
        font.setColor(HSSFColor.VIOLET.index);  
        font.setFontHeightInPoints((short) 12);  
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
        // 把字体应用到当前的样式  
        style.setFont(font);  
        // 生成并设置另一个样式  
        HSSFCellStyle style2 = workbook.createCellStyle();  
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);  
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
        // 生成另一个字体  
        HSSFFont font2 = workbook.createFont();  
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);  
        // 把字体应用到当前的样式  
        style2.setFont(font2);  
  
        // 声明一个画图的顶级管理器  
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();  
        // 定义注释的大小和位置,详见文档  
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,  
                0, 0, 0, (short) 4, 2, (short) 6, 5));  
        // 设置注释内容  
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));  
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.  
        comment.setAuthor("leno");  
  
        // 产生表格标题行  
        HSSFRow row = sheet.createRow(0);  
        for (short i = 0; i < headers.length; i++)  
        {  
            HSSFCell cell = row.createCell(i);  
            cell.setCellStyle(style);  
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);  
            cell.setCellValue(text);  
        }  
  
        // 遍历集合数据，产生数据行  
        Iterator<InterfaceSimpleInfo> it = dataset.iterator();  
        int index = 0;  
        while (it.hasNext())  
        {  
            index++;  
            row = sheet.createRow(index);  
            InterfaceSimpleInfo t = (InterfaceSimpleInfo) it.next();  
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值  
            Field[] fields = t.getClass().getDeclaredFields();  
            for (short i = 0; i < fields.length; i++)  
            {  
                HSSFCell cell = row.createCell(i);  
                cell.setCellStyle(style2);  
                Field field = fields[i];  
                String fieldName = field.getName();  
                String getMethodName = "get"  
                        + fieldName.substring(0, 1).toUpperCase()  
                        + fieldName.substring(1);  
                try  
                {  
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});  
                    Object value = getMethod.invoke(t, new Object[]{});  
                    // 判断值的类型后进行强制类型转换  
                    String textValue = null;  
                    // if (value instanceof Integer) {  
                    // int intValue = (Integer) value;  
                    // cell.setCellValue(intValue);  
                    // } else if (value instanceof Float) {  
                    // float fValue = (Float) value;  
                    // textValue = new HSSFRichTextString(  
                    // String.valueOf(fValue));  
                    // cell.setCellValue(textValue);  
                    // } else if (value instanceof Double) {  
                    // double dValue = (Double) value;  
                    // textValue = new HSSFRichTextString(  
                    // String.valueOf(dValue));  
                    // cell.setCellValue(textValue);  
                    // } else if (value instanceof Long) {  
                    // long longValue = (Long) value;  
                    // cell.setCellValue(longValue);  
                    // }  
                    if (value instanceof Boolean)  
                    {  
                        boolean bValue = (Boolean) value;  
                        textValue = "男";  
                        if (!bValue)  
                        {  
                            textValue = "女";  
                        }  
                    }  
                    else if (value instanceof Date)  
                    {  
                        Date date = (Date) value;  
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  
                        textValue = sdf.format(date);  
                    }  
                    else if (value instanceof byte[])  
                    {  
                        // 有图片时，设置行高为60px;  
                        row.setHeightInPoints(60);  
                        // 设置图片所在列宽度为80px,注意这里单位的一个换算  
                        sheet.setColumnWidth(i, (short) (35.7 * 80));  
                        // sheet.autoSizeColumn(i);  
                        byte[] bsValue = (byte[]) value;  
                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,  
                                1023, 255, (short) 6, index, (short) 6, index);  
                        anchor.setAnchorType(2);  
                        patriarch.createPicture(anchor, workbook.addPicture(  
                                bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));  
                    }  
                    else  
                    {  
                        // 其它数据类型都当作字符串简单处理  
                        textValue = value.toString();  
                    }  
                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成  
                    if (textValue != null)  
                    {  
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");  
                        Matcher matcher = p.matcher(textValue);  
                        if (matcher.matches())  
                        {  
                            // 是数字当作double处理  
                            cell.setCellValue(Double.parseDouble(textValue));  
                        }  
                        else  
                        {  
                            HSSFRichTextString richString = new HSSFRichTextString(  
                                    textValue);  
                            HSSFFont font3 = workbook.createFont();  
                            font3.setColor(HSSFColor.BLUE.index);  
                            richString.applyFont(font3);  
                            cell.setCellValue(richString);  
                        }  
                    }  
                }  
                catch (SecurityException e)  
                {  
                    e.printStackTrace();  
                }  
                catch (NoSuchMethodException e)  
                {  
                    e.printStackTrace();  
                }  
                catch (IllegalArgumentException e)  
                {  
                    e.printStackTrace();  
                }  
                catch (IllegalAccessException e)  
                {  
                    e.printStackTrace();  
                }  
                catch (InvocationTargetException e)  
                {  
                    e.printStackTrace();  
                }  
                finally  
                {  
                    // 清理资源  
                }  
            }  
        }  
        try  
        {  
            workbook.write(out);  
        }  
        catch (IOException e)  
        {  
            e.printStackTrace();  
        }
    }
	
	private String getStatus(Integer interfaceStatus) {
		String statusStr = "";
    	switch(interfaceStatus){
    	case 10:
    		statusStr = "暂存中";
    		break;
    	case 1:
    		statusStr = "编辑中";
    		break;
    	case 2:
    		statusStr = "审核中";
    		break;
    	case 310:
    		statusStr = "前端审核通过";
    		break;
    	case 301:
    		statusStr = "客户端审核通过";
    		break;
    	case 3:
    		statusStr = "审核通过";
    		break;
    	case 4:
    		statusStr = "已提测";
    		break;
    	case 5:
    		statusStr = "测试中";
    		break;
    	case 6:
    		statusStr = "测试完成";
    		break;
    	case 7:
    		statusStr = "压测中";
    		break;
    	case 8:
    		statusStr = "压测完成";
    		break;
    	case 9:
    		statusStr = "已上线";
    		break;
    	default:
    		statusStr = "WRONG STATUS!";
    		break;
    	}
    	return statusStr;
	}
	
	private String getRequestType(Integer requestType) {
    	String type = "";
    	switch(requestType){
    	case 1:
    		type = "GET";
			break;
		case 2:
			type = "POST";
			break;
		case 3:
			type = "PUT";
			break;
		case 4:
			type = "DELETE";
			break;
    	default:
    		type = "WRONG REQUEST TYPE!";
    		break;
    	}
    	return type;
	}
	
	private String getInterType(Integer interfaceType) {
		String interType = "";
		switch (interfaceType) {
		case 1:
			interType = "ajax";
			break;
		case 2:
			interType = "ftl";
			break;
		case 3:
			interType = "jsonp";
			break;
		case 4:
			interType = "action";
			break;

		default:
			interType = "WRONG INTER TYPE";
			break;
		}
		return interType;
	}
}
