<#compress>
<#--
	简化登录判断
	仅仅用于服务器端ftl输出页面使用
	简化cdn版本控制
-->
<#setting locale="zh_CN">
<#setting url_escaping_charset="UTF-8">
<#--
输出js文件 / css文件，含版本号
-->
<#macro jsFile file=[] file2=[]>
<#list file2 as x><script src="${cdnBaseUrl}${x}"></script>
</#list>
</#macro>
<#macro cssFile file=[] file2=[]>
<#list file2 as x><link rel="stylesheet" href="${cdnBaseUrl}${x}"/>
</#list>
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
    <!-- MetisMenu CSS -->
    <link href="${cdnBaseUrl}/css/common/metisMenu.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${cdnBaseUrl}/css/common/sb-admin-2.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="${cdnBaseUrl}/css/common/font-awesome.css" rel="stylesheet">
    
    <!-- jQuery -->
    <script src="${cdnBaseUrl}/js/common/jquery-1.11.3.js"></script>
    <!-- jQuery cookie 
     <script src="${cdnBaseUrl}/js/common/jquery.cookie.js"></script>
    -->
   
    <!-- Bootstrap Core JavaScript -->
    <script src="${cdnBaseUrl}/js/common/bootstrap.js"></script>
    <!-- Metis Menu Plugin JavaScript -->
    <script src="${cdnBaseUrl}/js/common/metisMenu.js"></script>
    <!-- Custom Theme JavaScript -->
    <script src="${cdnBaseUrl}/js/common/sb-admin-2.js"></script>
	<@cssFile file=css file2=otherCss/>
	<@jsFile file=js file2=otherJs/>
<#nested>
</head>
<body>
    <!-- navbar -->
    <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
            <a class="navbar-brand"  id="logo">
         	<img style="display:inline;float:left;padding-left:0px;height:30px;padding-top:0px" src="/img/logoo.png">
        		&nbsp;&nbsp;<strong>接口文档管理平台后台管理</strong>
      	</a>
        </div>
        <!-- /.navbar-header -->

		<ul class="nav navbar-top-links navbar-right">
			<!-- 友情链接添加 -->
			<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">友情链接</a>
					<ul class="dropdown-menu dropdown-user">
					</ul>
			</li>
			
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
			</li>

			<!-- /.dropdown -->
		</ul>

    </nav>
    <!-- /.navbar-static-side -->
</#macro>

<#macro htmlNavHead isShow=1 activeName="1">
	<#if isShow == 1>
		<!-- /.navbar-static-side -->
	    <div class="navbar-default sidebar" role="navigation">
	        <div class="sidebar-nav navbar-collapse">
	            <ul class="nav" id="side-menu">
	            	<#if activeName == "1">
	            	<li class="active">
	            	<#else>
	                <li>
	                </#if>
	                <li>
	                    <a href="${cdnBaseUrl}/admin.html"><i class="glyphicon glyphicon-home"></i> 首页</a>
	                </li>
	                </li>
	                
	                <#if activeName == "index">
	            	<li class="active">
	            	<#else>
	                <li>
	                </#if>
	                <li>
	                    <a href="${cdnBaseUrl}/index.html" target="_blank"><i class="glyphicon glyphicon-file"></i> 接口文档管理</a>
	                </li>
	                </li>
	                
	                <#if activeName == "role">
					<li class="active">
					<#else>
					<li>
					</#if>
						<a href="${cdnBaseUrl}/idoc/roleConfig.html">
							<i class="glyphicon glyphicon-user"></i> 角色管理
						</a>
					</li>
	                
	                <#if activeName == "ini">
					<li class="active">
					<#else>
					<li>
					</#if>
						<a href="${cdnBaseUrl}/ini/index.html">
							<i class="glyphicon glyphicon-cog"></i> ini配置管理
						</a>
					</li>
	                
	                <#if activeName == "cron">
					<li class="active">
					<#else>
					<li>
					</#if>
					 <a href="${cdnBaseUrl}/cron/cronList.html"><i
							class="fa fa-clock-o"></i> 定时任务</a>
					</li>
				
	            </ul>
	        </div>
	        <!-- /.sidebar-collapse -->
	    </div>
            
	</#if>
</#macro>

<#macro htmlNavFoot isShow=1>
	<#if isShow == 1>
        <footer class="footer">
			<div class="container">
				<p class="text-muted" style="text-align:center;">Powered by lede QA© 2015- lede QA 
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