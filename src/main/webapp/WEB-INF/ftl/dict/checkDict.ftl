<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台"
otherCss=[
"/css/common/dataTables.bootstrap.css",
"/css/common/dataTables.responsive.css",
"/css/interface/interface.css","/css/dict/dict.css"
]
otherJs=[
"/js/common/bootbox.js",
"/js/common/tangram-1.2.1.js", "/js/common/tangram-fix.js",
"/js/dict/dictCore.js",
"/js/dict/checkDict.js"
]
/>

	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li><a href="/dict/index.html?productId=${productId! }">${productName! }-数据字典</a></li>
				  <li class="active">查看数据字典</li>
				</ol>
			</div>
		</div>
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">查看数据字典
                	<small><button class="btn btn-primary" onClick="checkReturn()">返回</button></small>
                </h2>
            </div>
        </div>
        <div class="row">
        	<div class="col-lg-12">
		 		<div class="panel panel-default">
		 			<div class="panel-body">
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">字典名称：</label>
								<div class="col-sm-2" style="padding-left: 0px;">
									<input type="text" id="check_DictName" class="form-control">
								</div>
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">字典描述：</label>
								<div class="col-sm-2" style="padding-left: 0px;">
									<input type="text" id="check_DictDesc" class="form-control">
								</div>
								<a class="btn btn-sm btn-success" style="margin-left: 30px; margin-top: 3px;" onClick="generateDictModel();">生成代码</a>
								<input type="hidden" id="checkDictId" value="${dictId!''}"/>
								<input type="hidden" id="productId" value="${productId!''}" />
							</div>
			 			</div>
						<div class="row" style="padding-top:5px;">
							<div class="col-lg-12">
								<div id="editDictParamTb" >
								</div>
							</div>
						</div>
		 			</div>
		 		</div>
        	</div>
        </div>
	<!-- </div> -->
    </div>
    <!-- #page-wrapper -->

	<script type="text/javascript">
		var param = core.parameter;

	</script>
<@htmlNavFoot />
<@htmlFoot/>

