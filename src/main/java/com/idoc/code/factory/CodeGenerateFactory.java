package com.idoc.code.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.idoc.code.ColumnData;
import com.idoc.code.CommonPageParser;
import com.idoc.code.CreateBean;
import com.idoc.code.CreateBeanForClass;
import com.idoc.code.def.CodeResourceUtil;
import com.idoc.constant.CodeType;
import com.idoc.constant.LogConstant;
import com.idoc.model.CodeInfo;

public class CodeGenerateFactory {
	private static final Log log = LogFactory.getLog(CodeGenerateFactory.class);
	private static String url = CodeResourceUtil.URL;
	private static String username = CodeResourceUtil.USERNAME;
	private static String passWord = CodeResourceUtil.PASSWORD;

	private static String buss_package = CodeResourceUtil.bussiPackage;
	private static String projectPath = getProjectPath();

	// public static String url =
	// "jdbc:mysql://127.0.0.1:3333/lottqz?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&allowMultiQu";
	//
	// public static String username = "lott_quanzi";
	//
	// public static String passWord = "WTsyad@!6ti";

	public static synchronized void codeGenerate(String className, String tableName, String codeName,
			String controllerEntityPackage) {
		codeGenerate(className, tableName, codeName, controllerEntityPackage, controllerEntityPackage);
	}

	public static void codeGenerate(String className, String tableName, String codeName, String entityPackage,
			String controllerEntityPackage) {
		CreateBean createBean = new CreateBean();
		createBean.setMysqlInfo("com.mysql.jdbc.Driver", url, username, passWord);

		className = createBean.getTablesNameToClassName(className);
		String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());

		String srcPath = projectPath + CodeResourceUtil.source_root_package + "\\";

		String pckPath = srcPath + CodeResourceUtil.bussiPackageUrl + "\\";

		String webPath = projectPath + CodeResourceUtil.web_root_package + "\\view\\" + CodeResourceUtil.bussiPackageUrl
				+ "\\";
		String entityPath = "";

		String beanPath = entityPackage + "\\entity\\" + entityPath + className + ".java";
		String daoPath = entityPackage + "\\dao\\" + entityPath + className + "Dao.java";
		String daoImplPath = entityPackage + "\\dao\\impl\\" + entityPath + className + "DaoImpl.java";
		String servicePath = entityPackage + "\\service\\" + entityPath + className + "Service.java";
		String serviceImplPath = entityPackage + "\\service\\impl\\" + entityPath + className + "ServiceImpl.java";
		String controllerPath = entityPackage + "\\controller\\" + className + "Controller.java";
		String sqlMapperPath = entityPackage + "\\mapper\\" + entityPath + className + "Mapper.xml";
		webPath = webPath + entityPath;

		VelocityContext context = new VelocityContext();
		context.put("className", className);
		context.put("lowerName", lowerName);
		context.put("codeName", codeName);
		context.put("tableName", tableName);
		context.put("bussPackage", buss_package);
		context.put("entityPackage", entityPackage == "" ? null : entityPackage);
		context.put("controllerEntityPackage", controllerEntityPackage == "" ? null : controllerEntityPackage);
		try {
			// vo对象
			context.put("feilds", createBean.getBeanFeilds(tableName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// mapper.xml
			Map<String, Object> sqlMap = createBean.getAutoCreateSql(tableName);
			context.put("columnDatas", createBean.getColumnDatas(tableName));
			context.put("SQL", sqlMap);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		CommonPageParser.WriterPage(context, "EntityTemplate.ftl", pckPath, beanPath);
		CommonPageParser.WriterPage(context, "DaoTemplate.ftl", pckPath, daoPath);
		CommonPageParser.WriterPage(context, "ServiceTemplate.ftl", pckPath, servicePath);
		CommonPageParser.WriterPage(context, "ServiceImplTemplate.ftl", pckPath, serviceImplPath);
		CommonPageParser.WriterPage(context, "MapperTemplate.xml", pckPath, sqlMapperPath);
		CommonPageParser.WriterPage(context, "ControllerTemplate.ftl", pckPath, controllerPath);
		CommonPageParser.WriterPage(context, "DaoImplTemplate.ftl", pckPath, daoImplPath);

		log.info("----------------------------\u4EE3\u7801\u751F\u6210\u5B8C\u6BD5---------------------------");
	}

	public static synchronized List<CodeInfo> codeGenerate(String driver, String dbUrl, String dbUsername,
			String dbPassWord, String className, String tableName, String codeName, String controllerEntityPackage,
			String path) {
		return codeGenerate(driver, dbUrl, dbUsername, dbPassWord, className, tableName, codeName,
				controllerEntityPackage, controllerEntityPackage, path);
	}

	public static List<CodeInfo> codeGenerate(String driver, String dbUrl, String dbUsername, String dbPassWord,
			String className, String tableName, String codeName, String entityPackage, String controllerEntityPackage,
			String path) {
		CreateBean createBean = new CreateBean();
		createBean.setMysqlInfo(driver, dbUrl, dbUsername, dbPassWord);

		className = createBean.getTablesNameToClassName(className);
		String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());

		String srcPath = projectPath + CodeResourceUtil.source_root_package + "\\";

		String pckPath = srcPath + CodeResourceUtil.bussiPackageUrl + "\\";

		String webPath = projectPath + CodeResourceUtil.web_root_package + "\\view\\" + CodeResourceUtil.bussiPackageUrl
				+ "\\";
		String entityPath = "";

		String beanPath = path + "\\" + entityPackage + "\\entity\\" + entityPath + className + ".java";
		String daoPath = path + "\\" + entityPackage + "\\dao\\" + entityPath + className + "Dao.java";
		String daoImplPath = path + "\\" + entityPackage + "\\dao\\impl\\" + entityPath + className + "DaoImpl.java";
		String servicePath = path + "\\" + entityPackage + "\\service\\" + entityPath + className + "Service.java";
		String serviceImplPath = path + "\\" + entityPackage + "\\service\\impl\\" + entityPath + className
				+ "ServiceImpl.java";
		String controllerPath = path + "\\" + entityPackage + "\\controller\\" + className + "Controller.java";
		String sqlMapperPath = path + "\\" + entityPackage + "\\mapper\\" + entityPath + className + "Mapper.xml";
		webPath = webPath + entityPath;

		VelocityContext context = new VelocityContext();
		context.put("className", className);
		context.put("lowerName", lowerName);
		context.put("codeName", codeName);
		context.put("tableName", tableName);
		context.put("bussPackage", buss_package);
		context.put("entityPackage", entityPackage == "" ? null : entityPackage);
		context.put("controllerEntityPackage", controllerEntityPackage == "" ? null : controllerEntityPackage);
		try {
			// vo对象
			context.put("feilds", createBean.getBeanFeilds(tableName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// mapper.xml
			Map<String, Object> sqlMap = createBean.getAutoCreateSql(tableName);
			context.put("columnDatas", createBean.getColumnDatas(tableName));
			context.put("SQL", sqlMap);
		} catch (Exception e) {
			LogConstant.debugLog.info("自动生成mapper.xml时异常！");
			e.printStackTrace();
		}

		List<CodeInfo> codeInfo = new ArrayList<CodeInfo>();
		String entityCode = CommonPageParser.Writer(context, "EntityTemplate.ftl", pckPath, beanPath);
		codeInfo.add(getCodeInfo(CodeType.ENTITY, className + ".java", pckPath, beanPath, entityCode));
		String daoCode = CommonPageParser.Writer(context, "DaoTemplate.ftl", pckPath, daoPath);
		codeInfo.add(getCodeInfo(CodeType.DAO, className + "Dao.java", pckPath, beanPath, daoCode));
		String serviceCode = CommonPageParser.Writer(context, "ServiceTemplate.ftl", pckPath, servicePath);
		codeInfo.add(getCodeInfo(CodeType.SERVICE, className + "Service.java", pckPath, beanPath, serviceCode));
		String serviceImplCode = CommonPageParser.Writer(context, "ServiceImplTemplate.ftl", pckPath, serviceImplPath);
		codeInfo.add(
				getCodeInfo(CodeType.SERVICEIMPL, className + "ServiceImpl.java", pckPath, beanPath, serviceImplCode));
		String mapperCode = CommonPageParser.Writer(context, "MapperTemplate.xml", pckPath, sqlMapperPath);
		mapperCode = mapperCode.replaceAll("<", "&lt").replaceAll(">", "&gt");
		codeInfo.add(getCodeInfo(CodeType.MAPPER, className + "Mapper.xml", pckPath, beanPath, mapperCode));
		String controllerCode = CommonPageParser.Writer(context, "ControllerTemplate.ftl", pckPath, controllerPath);
		codeInfo.add(
				getCodeInfo(CodeType.CONTROLLER, className + "Controller.java", pckPath, beanPath, controllerCode));
		String daoImplCode = CommonPageParser.Writer(context, "DaoImplTemplate.ftl", pckPath, daoImplPath);
		codeInfo.add(getCodeInfo(CodeType.DAOIMPL, className + "DaoImpl.java", pckPath, beanPath, daoImplCode));

		log.info("----------------------------\u4EE3\u7801\u751F\u6210\u5B8C\u6BD5---------------------------");
		return codeInfo;
	}

	public static List<CodeInfo> codeGenerate(String packageName, String packPath, String className, String params,
			String path, Integer typeForClass) {
		String lowerName = className.toLowerCase();

		String srcPath = projectPath + CodeResourceUtil.source_root_package + "\\";

		String pckPath = srcPath + CodeResourceUtil.bussiPackageUrl + "\\";

		String webPath = projectPath + CodeResourceUtil.web_root_package + "\\view\\" + CodeResourceUtil.bussiPackageUrl
				+ "\\";
		String entityPath = "";

		String beanPath = path + "\\" + packPath + "\\entity\\" + entityPath + className + ".java";
		String daoPath = path + "\\" + packPath + "\\dao\\" + entityPath + className + "Dao.java";
		String daoImplPath = path + "\\" + packPath + "\\dao\\impl\\" + entityPath + className + "DaoImpl.java";
		String servicePath = path + "\\" + packPath + "\\service\\" + entityPath + className + "Service.java";
		String serviceImplPath = path + "\\" + packPath + "\\service\\impl\\" + entityPath + className
				+ "ServiceImpl.java";
		String controllerPath = path + "\\" + packPath + "\\controller\\" + className + "Controller.java";
		String sqlMapperPath = path + "\\" + packPath + "\\mapper\\" + entityPath + className + "Mapper.xml";
		webPath = webPath + entityPath;

		VelocityContext context = new VelocityContext();
		context.put("className", className);
		context.put("lowerName", lowerName);
		context.put("codeName", "Auto generate by idoc");
		context.put("tableName", className);
		context.put("bussPackage", buss_package);
		context.put("entityPackage", packageName == "" ? null : packageName);
		context.put("controllerEntityPackage", packageName == "" ? null : packageName);
		
		CreateBeanForClass beanForClass = new CreateBeanForClass(className, params);
		
		try {
			// vo对象
			String beanFeilds = beanForClass.getBeanFeilds();
			context.put("feilds", beanFeilds);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// mapper.xml
			Map<String, Object> sqlMap = beanForClass.getAutoCreateSql();

			context.put("columnDatas", beanForClass.getColumnDatas());
			context.put("SQL", sqlMap);
		} catch (Exception e) {
			LogConstant.debugLog.info("自动生成mapper.xml时异常！");
			e.printStackTrace();
		}

		List<CodeInfo> codeInfo = new ArrayList<CodeInfo>();
		String entityCode = CommonPageParser.Writer(context, "EntityTemplate.ftl", pckPath, beanPath);
		codeInfo.add(getCodeInfo(CodeType.ENTITY, className + ".java", pckPath, beanPath, entityCode));
		String daoCode = CommonPageParser.Writer(context, "DaoTemplate.ftl", pckPath, daoPath);
		codeInfo.add(getCodeInfo(CodeType.DAO, className + "Dao.java", pckPath, beanPath, daoCode));
		String serviceCode = CommonPageParser.Writer(context, "ServiceTemplate.ftl", pckPath, servicePath);
		codeInfo.add(getCodeInfo(CodeType.SERVICE, className + "Service.java", pckPath, beanPath, serviceCode));
		String serviceImplCode = CommonPageParser.Writer(context, "ServiceImplTemplate.ftl", pckPath, serviceImplPath);
		codeInfo.add(
				getCodeInfo(CodeType.SERVICEIMPL, className + "ServiceImpl.java", pckPath, beanPath, serviceImplCode));
		String mapperCode = CommonPageParser.Writer(context, "MapperTemplate.xml", pckPath, sqlMapperPath);
		mapperCode = mapperCode.replaceAll("<", "&lt").replaceAll(">", "&gt");
		codeInfo.add(getCodeInfo(CodeType.MAPPER, className + "Mapper.xml", pckPath, beanPath, mapperCode));
		String controllerCode = CommonPageParser.Writer(context, "ControllerTemplate.ftl", pckPath, controllerPath);
		codeInfo.add(
				getCodeInfo(CodeType.CONTROLLER, className + "Controller.java", pckPath, beanPath, controllerCode));
		String daoImplCode = CommonPageParser.Writer(context, "DaoImplTemplate.ftl", pckPath, daoImplPath);
		codeInfo.add(getCodeInfo(CodeType.DAOIMPL, className + "DaoImpl.java", pckPath, beanPath, daoImplCode));

		log.info("----------------------------\u4EE3\u7801\u751F\u6210\u5B8C\u6BD5---------------------------");
		return codeInfo;
	}

	private static CodeInfo getCodeInfo(CodeType codeType, String className, String pckPath, String filePath,
			String code) {
		CodeInfo info = new CodeInfo();
		info.setCodeType(codeType);
		info.setClassName(className);
		info.setPackagePath(pckPath);
		info.setFilePath(filePath);
		info.setCode(code);
		return info;
	}

	public static String getProjectPath() {
		String path = CodeGenerateFactory.class.getClassLoader().getResource("/").getPath().replace("\\", "/"); // System.getProperty("user.dir")
		return path;
	}
}