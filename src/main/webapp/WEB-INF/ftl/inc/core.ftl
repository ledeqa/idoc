<#compress>
<#--
	简化登录判断
	仅仅用于服务器端ftl输出页面使用
	简化cdn版本控制
-->
<#--<#assign cdnFileVersion = versionId!"0"/>-->
<#--欲定义-->
<#setting locale="zh_CN">
<#setting url_escaping_charset="UTF-8">
<#--
输出js文件 / css文件，含版本号
-->
<#macro jsFile file=[] file2=[]>
<#list file2 as x><script src="${cdnBaseUrl}${x}?v=${cdnFileVersion}"></script>
</#list>
<#--<#list file2 as x><script src="${cdnBaseUrl}${x}"></script>
</#list>-->
</#macro>
<#macro cssFile file=[] file2=[]>
<#list file2 as x><link rel="stylesheet" href="${cdnBaseUrl}${x}?v=${cdnFileVersion}"/>
</#list>
<#--<#list file2 as x><link rel="stylesheet" href="${cdnBaseUrl}${x}"/>
</#list>-->
</#macro>

<#--
文档声明/head
支持对head内容项进行修改
-->
<#macro htmHead title="" css=[] otherCss=[] js=[] otherJs=[]>
<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
	<title>${title}</title>

	<link rel="icon" type="image/x-icon" href="${cdnBaseUrl}/img/favicon.png">
	<!-- Bootstrap Core CSS -->
    <link href="${cdnBaseUrl}/css/common/bootstrap.css" rel="stylesheet">
    <link href="${cdnBaseUrl}/css/common/buttons.css" rel="stylesheet">
    <!-- MetisMenu CSS -->
    <link href="${cdnBaseUrl}/css/common/metisMenu.css?v=${cdnFileVersion}" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${cdnBaseUrl}/css/common/sb-admin-meteor.css?v=${cdnFileVersion}" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="${cdnBaseUrl}/css/common/font-awesome.css" rel="stylesheet">

    <!-- jQuery -->
    <script src="${cdnBaseUrl}/js/common/jquery-1.11.3.js"></script>
    <!-- Bootstrap Core JavaScript -->
    <script src="${cdnBaseUrl}/js/common/bootstrap.js"></script>
    <!-- Metis Menu Plugin JavaScript -->
    <script src="${cdnBaseUrl}/js/common/metisMenu.js?v=${cdnFileVersion}"></script>
    <!-- Custom Theme JavaScript -->
    <script src="${cdnBaseUrl}/js/common/sb-admin-2.js?v=${cdnFileVersion}"></script>
    <script src="${cdnBaseUrl}/js/core/core.js?v=${cdnFileVersion}"></script>
    <!-- 回到顶部插件 -->
    <script src="${cdnBaseUrl}/js/common/jquery.goup.js?v=${cdnFileVersion}"></script>
	<@cssFile file=css file2=otherCss/>
	<@jsFile file=js file2=otherJs/>
<#nested>
</head>
<body>
    <!-- navbar -->
    <nav class="navbar navbar-default navbar-static-top navbar-fixed-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
            <a class="navbar-brand"  id="logo">
         	<img style="display:inline;" src="/img/logo.png">
        		接口文档管理平台
      		</a>
        </div>
        <!-- /.navbar-header -->

		<div id="navbar" class="navbar-collapse collapse">
	        <ul class="nav navbar-nav">
		        <li class="active"><a href="${cdnBaseUrl }/idoc/index.html">首页</a></li>
		        <li class="Dropdown">
			        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">我的产品 <span class="caret"></span></a>
			        <ul class="dropdown-menu" id="my_product_list">
			        </ul>
		        </li>
		       <!-- <#if admin?exists>-->
		        	<li class="Dropdown">
				        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">系统管理 <span class="caret"></span></a>
				        <ul class="dropdown-menu">
				        	<li> <a href="${cdnBaseUrl}/idoc/roleConfig.html">角色管理</a></li>
				        	<li> <a href="${cdnBaseUrl}/ini/index.html">ini配置管理</a></li>
				        	<li> <a href="${cdnBaseUrl}/cron/cronList.html">定时任务</a></li>
				        </ul>
			        </li>
		         <!--  </#if>-->
		        <li class="Dropdown">
			        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">数据统计 <span class="caret"></span></a>
			        <ul class="dropdown-menu">
			        	<li> <a href="${cdnBaseUrl}/idoc/statistics/productStatistics.html">产品线详细数据</a></li>
			        	<li> <a href="${cdnBaseUrl}/idoc/statistics/productCompare.html">各产品线数据对比</a></li>
			        </ul>
		        </li>
		        <li class="active"><a href="${cdnBaseUrl }/code/index.html">生成代码</a></li>
	        </ul>
			<!--/.nav-collapse -->

	        <ul class="nav navbar-top-links navbar-right">
			<#if admin?exists><li><font color="red">
				<span id ="onlinePeopleCount"></span></font></li>
			</#if>
	        <#if userCorpMail?exists>
	            <li class="dropdown">
	                <a class="dropdown-toggle" data-toggle="dropdown" href="#">
	                    <i class="fa fa-user fa-fw"></i>  <i class="fa fa-caret-down"></i>
	                </a>
	                <ul class="dropdown-menu dropdown-user">
	                    <li><a href="${cdnBaseUrl}/idoc/addUserInfoPage.html"><i class="fa fa-user fa-fw"></i>${userCorpMail}</a>
	                    </li>
	                    <li class="divider"></li>
	                    <li><a href="${cdnBaseUrl}/idoc/logout.html"><i class="fa fa-sign-out fa-fw"></i>注销</a>
	                    </li>
	                </ul>
	                <!-- /.dropdown-user -->
	            </li>
	            <!-- /.dropdown -->
            <#else>
            	<li><a href="${cdnBaseUrl}/login.html">登录</a></li>
            </#if>
	        </ul>
	        <!-- /.navbar-top-links -->
        </div>
    </nav>
    <!-- /.navbar-static-side -->
</#macro>

<#macro htmlNavHead isShow=1 activeName="1">
	<#if isShow == 1>
		<!-- /.navbar-static-side -->
	</#if>
</#macro>

<#macro htmlNavFoot isShow=1>
	<#if isShow == 1>
        <footer class="footer">
			<div class="container">
				<p class="text-muted" style="text-align:center;">Powered by lede QA© 2015-<span id="year"></span> lede QA
				</p>
			</div>
	    </footer>
    </#if>
</#macro>

<#macro htmlFoot>


    </body>
</html>
</#macro>
</#compress>