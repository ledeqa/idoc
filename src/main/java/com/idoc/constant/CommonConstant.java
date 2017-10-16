package com.idoc.constant;

public class CommonConstant {
	
	public static final String OS_NAME = System.getProperty("os.name");//操作系统名称
	public static final String ROLE_PRODUCT_MANAGER = "产品负责人";
	public static final String ROLE_FRONT_MANAGER = "前端开发负责人";
	public static final String ROLE_APP_MANAGER = "客户端开发负责人";
	public static final String ROLE_DEVELOPER_MANAGER = "后台开发负责人";
	public static final String ROLE_TESTOR_MANAGER = "测试负责人";
	public static final String ROLE_ADMIN = "管理员";
	public static final String EXCEPTION_POPO_MESSAGE_TO_ADMIN = "exceptionPopoMessageToAdmin";
	
	public static final String PROJECT_DEMO = "Demo";
	
	public static final String PARAM_TYPE_OBJECT = "object";
	public static final String PARAM_TYPE_OBJECT_ARRAY = "array<object>";
	public static final String PARAM_TYPE_ZIDINGYI = "自定义";
	
	public static final String PEOPLE_ID_SPLIT = ",";
	
	//接口状态
	public static final int INTERFACE_STATUS_EDITING = 1;  // 编辑中
	public static final int INTERFACE_STATUS_AUDITING = 2; // 审核中
	public static final int INTERFACE_STATUS_FRONT_AUDITED = 310;//前端审核通过
	public static final int INTERFACE_STATUS_CLIENT_AUDITED = 301;//客户端审核通过
	public static final int INTERFACE_STATUS_AUDITED = 3;  // 审核通过
	public static final int INTERFACE_STATUS_TOTEST = 4;  //已提测
	public static final int INTERFACE_STATUS_TESTING = 5;  //测试中
	public static final int INTERFACE_STATUS_TESTED = 6;  //测试完成 
	public static final int INTERFACE_STATUS_PRESSURE = 7;  //压测中
	public static final int INTERFACE_STATUS_PRESSURED = 8;  //压测完成
	public static final int INTERFACE_STATUS_ONLINE = 9;  //已上线
	
	//初始化的key值，对应TB_IDOC_INI表中的key字段，名称格式INI_ + 数据库中字段的名称大写
	public static final String INI_ADMIN = "admin"; //管理员
	public static final String INI_SEND_POPO_MESSAGE_SWITCH = "sendPopoMessageSwitch"; //发送popo消息总开关，1-打开，0-关闭
	public static final String INI_CREATED_POPO_MESSAGE_TO_FRONT= "createdPopoMessageToFront"; //创建接口完成发送给前端负责人popo消息
	public static final String INI_CREATED_POPO_MESSAGE_TO_BEHIND= "createdPopoMessageToBehind"; //创建接口完成发送给后台负责人popo消息
	public static final String INI_CREATED_POPO_MESSAGE_TO_CLIENT= "createdPopoMessageToClient"; //创建接口完成发送给客户端负责人popo消息
	public static final String INI_CREATED_POPO_MESSAGE_TO_TESTER= "createdPopoMessageToTester"; //创建接口完成发送给测试负责人popo消息
	public static final String INI_AUDIT_POPO_MESSAGE_TO_FRONT= "auditPopoMessageToFront"; //发起审核发送给前端的popo消息
	public static final String INI_AUDIT_POPO_MESSAGE_TO_CLIENT= "auditPopoMessageToClient"; //发起审核发送给客户端的popo消息
	public static final String INI_REVISE_POPO_MESSAGE_TO_CREATOR= "revisePopoMessageToCreator"; //建议修改发送给接口创建者的popo消息
	public static final String INI_AUDITED_POPO_MESSAGE_TO_CREATOR= "auditedPopoMessageToCreator"; //审核通过发送给接口创建者popo消息
	public static final String INI_AUDITED_POPO_MESSAGE_TO_TESTER = "auditedPopoMessageToTester";  //审核通过发送给接口测试者popo消息
	public static final String INI_TOTEST_POPO_MESSAGE_TO_TESTER = "totestPopoMessageToTester";  //提交测试发送给接口测试者popo消息
	public static final String INI_TESTING_POPO_MESSAGE_TO_CREATOR = "testingPopoMessageToCreator";  //开始测试发送给接口创建者popo消息
	public static final String INI_TESTED_POPO_MESSAGE_TO_CREATOR = "testedPopoMessageToCreator";  //测试完成发送给接口创建者popo消息
	public static final String INI_PRESSURE_POPO_MESSAGE_TO_TESTER = "pressurePopoMessageToTester";  //提交压测发送给接口测试者popo消息
	public static final String INI_PRESSURED_POPO_MESSAGE_TO_CREATOR = "pressuredPopoMessageToCreator";  //压测完成发送给接口创建者popo消息
	public static final String INI_ONLINE_POPO_MESSAGE_TO_CREATOR = "onlinePopoMessageToCreator";  //上线发送给接口创建者popo消息
	public static final String INI_BACK_POPO_MESSAGE_TO_FRONT= "backPopoMessageToFront"; //强制回收时发送给前端负责人popo消息
	public static final String INI_BACK_POPO_MESSAGE_TO_BEHIND= "backPopoMessageToBehind"; //强制回收时发送给后台负责人popo消息
	public static final String INI_BACK_POPO_MESSAGE_TO_CLIENT= "backPopoMessageToClient"; //强制回收时发送给客户端负责人popo消息
	public static final String INI_BACK_POPO_MESSAGE_TO_TESTER= "backPopoMessageToTester"; //强制回收时发送给测试负责人popo消息
	public static final String INI_CRON_SCAN_PROJECT_SCHEDULE_POPO_MESSAGE= "cronScanProjectSchedulePopoMessage"; //强制回收时发送给测试负责人popo消息
	public static final String INI_CRON_SCAN_PROJECT_SCHEDULE_POPO_MESSAGE_SWITCH= "cronScanProjectSchedulePopoMessageSwitch"; //强制回收时发送给测试负责人popo消息开关
	public static final String INI_CDN_FILE_VERSION = "cdnFileVersion";//静态文件版本
	
	//ckeditor允许上传的文件格式后缀
	public static final String JPEG_SUFFIX = ".JPEG";
	public static final String JPG_SUFFIX = ".JPG";
	public static final String PNG_SUFFIX = ".PNG";
	public static final String GIF_SUFFIX = ".GIF";
	public static final String BPM_SUFFIX = ".BPM";

// memcache常量
	public static final int MEMCACHED_SWITCH_OPEN = 1; // 使用memcached开关打开
	public static final int MEMCACHED_SWITCH_CLOSE = 0; // 使用memcached开关关闭
	
	public static final String MEMCACHED_SWITCH_KEY = "memcached_switch_key";// memcached开关名称
	public static final String MEMCACHED_FAIL_OVER = "memFailOver";
	public static final String MEMCACHED_INI_CONN = "memInitConn";
	public static final String MEMCACHED_MIN_CONN = "memMinConn";
	public static final String MEMCACHED_MAX_CONN = "memMaxConn";
	public static final String MEMCACHED_MAX_IDLE = "memMaxIdle";
	public static final String MEMCACHED_NAGLE = "memNagle";
	public static final String MEMCACHED_SOCKET_TO = "memSocketTO";
	public static final String MEMCACHED_SOCKET_CONNECT_TO = "memSocketConnectTO";
	public static final String MEMCACHED_PRIMITIVE_AS_STRING = "memPrimitiveAsString";
	
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String MD5_PRIVATE_KEY = "30820276020100300d06092a864886f70d0101010500048202603082025c0201000281810080b335bdb0eda484c2aab214d1dd294792f42ccb32001e47b94fde6d49ff5e116379a4fc078b122b31d6a7aae3458de67f3b8107240c019ce982b7bc84417849dad5376d3949a84b08595fbf21cdc010460334e7b5fe0d7257c877a3a4494f767d71f1621a4ac4c11649a0cc89b9dde2d6c4f125ba3db80154a0e68100766cbb02030100010281803ead07c2f56305f7d185a76c14380c5e5ae6a6d9dde3c8db4d17e44e7cf6ec2cb3b10df3df088f3491f4e37a896bda0b22732c06fa3b0e9e07ea2ce895cffc6fe8e13a8bb77662275cff157dae2cf6328afbcfd5fc754d9fb6b83234ebcab4ef9687c4f650defb5503980a4010ada9ec560612c32fc2577f64e49990d909c381024100ced0604910b01a51b642979f950bf89dadf456b7ae63455d692974af1e150a71c06adae29a58d581a49a0076f0801fa6a81da0c74a4edaf511eef13ab1c5f50b0241009f4ef5a550456ab307ec37d5992f37230400aa01168fc28e1c88cd44f17f2e302f48e58d89b867eaa77363b1c0c5836067df5bd84e126b32fba925b5cfadd51102406b6e9b55d93161baa8af170c72e4711597d3a1687152682a0a02daf64cee292ac605bc06929f2ae9d993964232d49b9c7b2048f0bd8d10f8d5840613b35d5dd902402c274246133d6a193cb4e7b4b7c4324fee2810f6443e8bf9bf46db3da4814f57e3831cbc61d34e59e71740265ffb5e323617dde8d19aa1437c24a16306b07341024100ca948206de08cdbfb27a54f94714dc5196db4693154058ca77c8149f39a3aa3a7287083c217db200a44863a633c9efba5655adfc110927f67fa8604ea4bcdaee";

}