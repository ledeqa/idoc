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
"/js/dict/checkDictHistory.js"
]
/>

	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li><a href="/dict/index.html?productId=${productId! }">${productName! }-数据字典</a></li>
				  <li class="active">查看数据字典历史信息</li>
				</ol>
			</div>
			<input type="text" hidden="true" id="dictId" value="${dictId!''}"/>
			<input type="text" hidden="true" id="version" value="${version!''}"/>
			<input type="text" hidden="true" id="productName" value="${productName!''}"/>
		</div>
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">查看数据字典
                	<small><a class="btn btn-sm btn-success" id="return_dict_history_url">返回</a></small>
                	<small><button class="btn btn-sm btn-success" onclick="revertDictVersion()">回滚到此版本</button></small>
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
									<input type="text" id="check_history_DictName" readonly="readonly" class="form-control">
								</div>
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">字典描述：</label>
								<div class="col-sm-2" style="padding-left: 0px;">
									<input type="text" id="check_history_DictDesc" readonly="readonly" class="form-control">
								</div>
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

