<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
		<title>这个.. 页面出错啦！！！</title>
		<link href="${cdnBaseUrl}/css/common/bootstrap.css" rel="stylesheet">
		<!-- jQuery -->
	    <script src="${cdnBaseUrl}/js/common/jquery-1.11.3.js"></script>
	    <!-- Bootstrap Core JavaScript -->
	    <script src="${cdnBaseUrl}/js/common/bootstrap.js"></script>
	    <script src="${cdnBaseUrl}/js/common/bootbox.js"></script>
	    <script src="${cdnBaseUrl}/js/fail.js"></script>
		<style type="text/css">
			body{ margin:0; padding:0; background:#efefef; font-family:Georgia, Times, Verdana, Geneva, Arial, Helvetica, sans-serif; }
			div#mother{ margin:0 auto; width:943px; height:572px; position:relative; }
			div#errorBox{ background: url(/img/404_bg.png) no-repeat top left; width:943px; height:572px; margin:auto; margin-top: 15%;}
			div#errorText{ color:#39351e; padding:146px 0 0 446px }
			div#errorText p{ width:303px; font-size:14px; line-height:26px; }
			div.link{ /*background:#f90;*/ height:50px; width:145px; float:left; }
			div#home{ margin:20px 0 0 444px;}
			div#contact{ margin:20px 0 0 25px;}
			h1{ font-size:40px; margin-bottom:35px; }
		</style>
		
	</head>
	<body>
		<div id="mother">
			<div id="errorBox">
				<div id="errorText">
					<h1>Sorry..页面出错啦！</h1>
					<p style="color:red">
						${exceptionMsg!''}
					</p>
					<!-- <#if (errorMessages![])?size gt 0>
						<p>
							<#list errorMessages as errInfo>${errInfo}<br/></#list>
						</p>
					</#if> -->
					<p>
						火星不太安全，我可以免费送你回地球
					</p>
				</div>
				<div style="height:70px;"></div>
				<a href="http://idoc.qa.lede.com/" title="返回接口文档首页">
					<div class="link" id="home"></div>
				</a>
				<a href="#" onclick="contactToAdmin()" title="给管理员发送popo消息">
					<div class="link" id="contact"></div>
				</a>
				<textarea class="hidden" id="reportMsg">"${exceptionMsg!''}"</textarea>
			</div>
		</div>
	</body>
</html>