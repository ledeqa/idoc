<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="自动生成代码"

otherCss=["/css/common/prettify/prettify.css", "/css/common/jquery.toastmessage.css", "/css/common/font-awesome/css/font-awesome.css", "/css/common/ztree/zTreeStyle/zTreeStyle.css"] 
otherJs=["/js/common/prettify/prettify.js", "/js/common/bootbox.js", "/js/common/FileSaver.js", "/js/common/jquery.toastmessage.js", 
"/js/common/lhgdialog/lhgdialog.js?skin=jtop", "/js/code/classGenerateCode.js" ,"/js/common/ztree/jquery.ztree.core.js", "/js/common/jquery.cookie.js"]
/>
	<div id="page-wrapper" style="width: 80%; margin-top: 2%; margin-left: 10%; border-left: 3px solid rgb(217,216,216); border-right: 3px solid rgb(217,216,216);">
    	<div class="row">
            <div class="col-sm-12">
                <h1 class="page-header" style="border-bottom: 2px solid #1F7D9C;">自动生成模板代码
                </h1>
            </div>
            <!-- /.col-sm-12 -->
        </div>
        <!-- /.row -->
		<div class="row">
			
			<div id="fileBrowser" style="width: 20%; min-height: 850px; float: left; margin-top: -1%;">
				<ul id="codeTree" class="ztree"></ul>
				<a id="download-btn" href="javascript:void(0);" class="button button-rounded button-flat button-action button-small" style="padding: 0px 40px;margin-left: 28%; margin-top: 30px; display: none">下载代码</a>
			</div>
			<div style="width: 78%; min-height: 850px; float: right; margin-top:-20px; margin-right:10px;">
				<pre id="codeText" class="prettyprint linenums" contenteditable="flase" style="min-height: 800px;"></pre>
			</div>
		</div>
    </div>
    <!-- #page-wrapper -->

<@htmlNavFoot />
<@htmlFoot/>