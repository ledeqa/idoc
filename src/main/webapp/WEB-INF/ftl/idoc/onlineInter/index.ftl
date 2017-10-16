<!--首页 -->
<#include "../../inc/core.ftl">
<@htmHead title="接口文档管理平台"

otherCss=["/css/common/bootstrap-datetimepicker/bootstrap-datetimepicker.css", "/css/common/chosen.css","/css/common/poshytip/tip-yellow/tip-yellow.css",
"/css/common/timeline.css", "/css/interface/interface.css", "/css/interface/inter.css",
"/css/common/fileinput.css"] 
otherJs=["/js/common/bootbox.js","/js/common/jquery.validate.js","/js/common/jsTree/jquery.jstree.js",
"/js/common/ckeditor/ckeditor.js", "/js/common/bootstrap-datetimepicker/bootstrap-datetimepicker.js",
"/js/common/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js","/js/common/jquery.poshytip.min.js",
"/js/common/chosen.jquery.js", "/js/common/tangram-1.2.1.js", "/js/common/tangram-fix.js"
"/js/interface/paramCore.js", "/js/onlineInterface/onlineInterface.js",
"/js/common/fileinput.js", "/js/interface/spin.js"]
/>

<style>
.module .edit-module{
	visibility: hidden;
}

.module:hover > .edit-module{
	visibility: visible;
}

.page .edit-page{
	visibility: hidden;
}

.page:hover > .edit-page{
	visibility: visible;
}

.inter .edit-inter{
	visibility: hidden;
}

.inter:hover > .edit-inter{
	visibility: visible;
}

</style>
	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li class="active">${productName!}-在线接口</li>
				</ol>
			</div>
		</div>
    	<div class="row">
            <div class="col-sm-12">
                <h2 class="page-header">在线接口管理
                	<#if role?exists && (role == "管理员" || role == "后台开发负责人" || role == "后台开发")>
		                <button class="btn btn-primary btn-sm" id="addModule">添加模块</button>
		                <button class="btn btn-primary btn-sm" id="addPage">添加页面</button>
	                </#if>
                	<!-- <button class="btn btn-primary btn-sm" id="importDocApis" data-target="#uploadDocApis" data-toggle="modal">导入已有接口</button> -->
                </h2>
                <input type="hidden" id="productId" value="${productId! }">
                <input type="hidden" id="currUser" value="${currUser! }">
                <input type="hidden" id="currUserId" value="${currUserId! }">
                <input type="hidden" id="role" value="${role! }">
                <input type="hidden" id="onlineInterfaceId">
                <input type="hidden" id="onlinePageId">
                <input type="hidden" id="destOnlineInterId">
                <input type="hidden" id="s_interType" value="${interType!''}">
                <input type="hidden" id="s_requestType" value="${requestType!''}">
            </div>
            <!-- 导入文档的模态窗口 -->
	        <div class="modal fade" id="uploadDocApis" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">请选择模块、页面及word文档</h4>
						</div>
						<div class="modal-body">
						<form action="/idoc/import/apis.html?productId=${productId}" method="post" enctype="multipart/form-data">
							<div class="row form-horizontal">
								<div class="form-group">
									<label class="col-lg-2 control-label">所属模块:</label>
									<div class="col-lg-9">
										<select id="onlineModule" onChange="changeOnlineModule()" class="form-control chosen-select">
										</select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-lg-2 control-label">所属页面:</label>
									<div class="col-lg-9">
										<select id="onlinePage" name="onlinePageId" onChange="changeOnlinePage()" class="form-control chosen-select">
										</select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-lg-2 control-label">word文档:</label>
									<div class="col-lg-9">
										<input id="uploadDict" type="file" class="file" name="file"
										data-show-preview="false" data-allowed-file-extensions='["doc", "docx"]'
										data-max-file-count="1"
										/>							
									</div>
								</div>
							</div>
						</form>
						</div>
						<div class="modal-footer">
							<!-- <button type="button" class="btn btn-primary" data-dismiss="modal">关闭
							</button> -->
						</div>
					</div>
				</div>
			</div>
            <div class="col-sm-12" style="padding-bottom:5px;margin-bottom:10px;">
            	<form action="" method="post">
            		<input type="hidden" name="form_productId" id="productId" value="${productId! }">
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">模块名称:</span>
						  <input type="text" class="form-control" name="moduleName" id="search_moduleName" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">页面名称:</span>
						  <input type="text" class="form-control" name="pageName" id="search_pageName" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">接口名称:</span>
						  <input type="text" class="form-control" name="urlName" id="search_interName" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">接口类型:</span>
		 					<select id="search_interType" name="interType" class="form-control edit-field">
					    		<option value="" selected="selected">-请选择接口类型-</option>
					    		<option value="1">ajax</option>
					    		<option value="2">ftl</option>
					    	</select>
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">协议类型:</span>
	  						<select id="search_requestType" name="requestType" class="form-control edit-field">
					    		<option value="" selected="selected">-请选择协议类型-</option>
					    		<option value="1">GET</option>
					    		<option value="2">POST</option>
					    		<option value="3">PUT</option>
					    		<option value="4">DELETE</option>
					    	</select>
						</div>
				    </div>
				    <div class="col-sm-2">
				    	<div class="input-group">
						  <button class="btn btn-primary" style="margin-left:20px;" type="submit"><i class="fa fa-search"></i>&nbsp;搜索</button> &nbsp;&nbsp;&nbsp;
						</div>
				    </div>
			    </form>
            </div>
            <!-- /.col-sm-12 -->
        </div>
        <!-- /.row -->
		<div class="row">
			<div class="col-sm-3">
				<div class="panel panel-default">
					<#if modules?size gt 0>
						<label class="chexkbox-inline">
							&nbsp;&nbsp;<input type="checkbox" name="all-checkbox">&nbsp;&nbsp;全选
						</label>
					</#if>
					<button style="margin-left:130px; margin-top:10px" class="btn btn-success" type="button" id="exportInterInfo"><i class="fa fa-download"></i>&nbsp;导出接口文档</button>
	                <div class="panel-body" id="moduleTree" style="min-height: 650px;">
	                	<#if modules?exists>
	                		<ul id="ulList">
		                		<#list modules as module>
		                			<li id="module-${module.onlineModuleId }" module="${module.onlineModuleId }" class="module">
		                				<label class="chexkbox-inline">
											&nbsp;<input type="checkbox" name="module-checkbox" id="module-checkbox-${module.onlineModuleId}">&nbsp;
										</label>
										<i class="glyphicon glyphicon-folder-open" style="color: #ffe66f"></i>
		                				<a href="javascript:void(0)" id="moduleName-${module.onlineModuleId}"  onclick="openOrClose(this)" style="margin-left:6px;">${module.moduleName }</a>
		                				<a href="#" class="edit-module" onclick="editModule('${module.onlineModuleId }')">&nbsp;<i class="glyphicon glyphicon-pencil" style="color:green;"></i></a>
		                				<a href="#" class="edit-module" onclick="deleteModule('${module.onlineModuleId }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
		                				<#if module.pageList?exists>
		                					<ul>
		                						<#list module.pageList as page>
		                							<li id="page-${page.onlinePageId }" page="${page.onlinePageId }" class="page">
		                								<label class="chexkbox-inline">
															&nbsp;<input type="checkbox" name="page-checkbox" id="page-checkbox-${page.onlinePageId}">&nbsp;
														</label>
														&nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffc78e"></i>
		                								<a href="javascript:void(0)" id="pageName-${page.onlinePageId }" onclick="openOrClose(this)" style="margin-left:5px;">${page.pageName }</a>
		                								<a href="#" class="edit-page" onclick="editPage('${page.onlinePageId }')">&nbsp;<i class="glyphicon glyphicon-pencil" style="color:green;"></i></a>
		                								<a href="#" class="edit-page" onclick="deletePage('${page.onlinePageId }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
		                								<#if page.interfaceList?exists>
		                									<ul>
			                									<#list page.interfaceList as inter>
			                										<li id="inter-${inter.interfaceId }" inter="${inter.interfaceId }" class="inter">
			                											<label class="chexkbox-inline">
																			&nbsp;<input type="checkbox" name="interface-checkbox" id="interface-checkbox-${inter.interfaceId}">&nbsp;
																		</label>
																		&nbsp;<i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
			                											<a href="#" onclick="initIntefaceInfo(${module.productId}, ${inter.interfaceId },${page.onlinePageId }, true);return false;">${inter.interfaceName }</a>
			                											<#if currUserId?? && inter?? && currUserId == inter.creatorId>
			                												<a href="#" class="edit-inter" onclick="deleteInterface('${inter.interfaceId }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
			                											</#if>
			                										</li>
			                									</#list>
		                									</ul>
		                								</#if>
		                							</li>
		                						</#list>
		                					</ul>
		                				</#if>
		                			</li>
		                		</#list>
	                		</ul>
	                	</#if>
	                </div>
	            </div>
			</div>
			
			<div class="col-sm-9">
				<div class="panel panel-default">
					<div class="panel-body" style="min-height: 650px;">
						<div class="row col-sm-12">
							<h3 class="page-header" id="interHead">接口详情&nbsp;
								<span id="interfaceHistoryVersion"></span>&nbsp;&nbsp;
								<div class="pull-right" id="headBtns">
								</div>
							</h3>
						</div>
						<div class="row col-sm-12" id="hint" style="height: 500px;position: relative;">
							<h1 style="width: 50%;  height: 45%;  overflow: auto;  margin: auto;  position: absolute;  top: 0; left: 0; bottom: 0; right: 0;color: #cccccc;">
								请在左侧选择需要管理的在线接口</h1>
						</div>
						<div id="interMain" style="display: none;">
							<input type="hidden" value="${interfaceId! }" id="interId">
							<input type="hidden" id="creatorId" value="">
							<div class="row col-sm-12">
				                <div class="form-horizontal">
				                	<div class="form-group">
										<label class="col-sm-2 control-label">接口名称</label>
									    <div class="col-sm-10">
									    	<input type="text" class="form-control edit-field" id="interName" placeholder="请填写接口名称">
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">接口状态</label>
									    <div class="col-sm-10">
									    	<label class="control-label" id="lb_interStatus"></label>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">创建人</label>
									    <div class="col-sm-10">
									    	<label class="control-label" id="lb_creator"></label>
									    </div>
									</div>
									<div class="form-group">
										<label for="inputEmail3" class="col-sm-2 control-label">创建时间</label>
									    <div class="col-sm-10">
									    	<label class="control-label" id="lb_createTime"></label>
									    </div>
									</div>
									<div class="form-group">
										<label for="inputEmail3" class="col-sm-2 control-label">在线版本</label>
									    <div class="col-sm-10">
									    	<a class='btn btn-xs btn-info' disabled><label class="control-label" id="lb_onlineVersion"></label></a>
									    </div>
									</div>
									<div class="form-group">
										<label for="inputEmail3" class="col-sm-2 control-label">迭代版本</label>
									    <div class="col-sm-10">
									    	<a class='btn btn-xs btn-success' disabled><label class="control-label" id="lb_iterVersion"></label></a>
									    </div>
									</div>
									<div class="form-group">
										<label for="inputEmail3" class="col-sm-2 control-label">上线时间</label>
									    <div class="col-sm-10">
									    	<label class="control-label" id="lb_onlineTime"></label>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">接口类型</label>
									    <div class="col-sm-10">
									    	<select id="interType" class="form-control edit-field" onChange="displayFtlTemplate()">
									    		<option value="" selected="selected">--请选择接口类型--</option>
									    		<option value="1">ajax</option>
									    		<option value="2">ftl</option>
									    		<option value="3">jsonp</option>
									    		<option value="4">action</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group" id="online_ftl_template_div">
										<label class="col-sm-2 control-label">ftl模板名称</label>
									    <div class="col-sm-10">
									    	<input type="text" class="form-control" id="online_inter_ftl_template">
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">协议类型</label>
									    <div class="col-sm-10">
									    	<select id="requestType" class="form-control edit-field">
									    		<option value="" selected="selected">--请选择协议类型--</option>
									    		<option value="1">GET</option>
									    		<option value="2">POST</option>
									    		<option value="3">PUT</option>
									    		<option value="4">DELETE</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">请求url</label>
									    <div class="col-sm-10">
									    	<input type="text" class="form-control edit-field" id="url" placeholder="请填写接口请求url">
									    </div>
									</div>
									
									<div class="form-group">
										<div class="col-sm-offset-1 col-sm-10">
											<button class="btn btn-default btn-sm" id="moveCopy"><i class="fa fa-copy edit-field"></i> 移动接口</button>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">功能描述</label>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-1 col-sm-11">
											<div name="desc" id="desc" style="height: 550px;"></div>
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">请求参数列表</label>
									</div>
									<div class="form-group">
										<div id="reqParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">返回参数列表</label>
									</div>
									<div class="form-group">
										<div id="retParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">是否测试</label>
									    <div class="col-sm-10">
									    	<select id="isNeedInterfaceTest" class="form-control edit-field" onchange="isNeedInterfaceTestChange();return false;">
									    		<option value="" selected="selected">--请选择是否需要测试--</option>
									    		<option value="0">否</option>
									    		<option value="1">是</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">是否压测</label>
									    <div class="col-sm-10">
									    	<select id="isNeedPressureTest" class="form-control edit-field" onchange="isNeedPressureTestChange();return false;">
									    		<option value="" selected="selected">--请选择是否需要压力测试--</option>
									    		<option value="0">否</option>
									    		<option value="1">是</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">预计上线时间</label>
									    <div class="col-sm-10">
								    		<input type="text" class="form-control time edit-field" id="expectOnlineTime" placeholder="请填写预计上线时间" readonly="readonly">
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">需求人员</label>
									    <div class="col-sm-10 reqPeopleDiv">
									    	<select id="reqPeople" class="form-control chosen-select edit-field" multiple data-placeholder="请选择该接口对应的产品">
									    	<option>hello</option>
									    	<option>hello1</option>
									    	<option>hello2</option>
									    	<option>hello3</option>
									    	<option>hello4</option>
									    	<option>hello5</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">前端开发</label>
									    <div class="col-sm-10">
									    	<select id="frontPeople" class="form-control chosen-select edit-field" multiple data-placeholder="请选择该接口对应的前端开发人员">
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">后台开发</label>
									    <div class="col-sm-10">
									    	<select id="behindPeople" class="form-control chosen-select edit-field" multiple data-placeholder="请选择该接口对应的后台开发人员">
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">客户端开发</label>
									    <div class="col-sm-10">
									    	<select id="clientPeople" class="form-control chosen-select edit-field" multiple data-placeholder="请选择该接口对应的客户端开发人员">
									    	</select>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">测试人员</label>
									    <div class="col-sm-10">
									    	<select id="testPeople" class="form-control chosen-select edit-field" multiple data-placeholder="请选择该接口对应的测试人员">
									    	</select>
									    </div>
									</div>
				                </div>
							</div>
							
							<div class="row col-sm-12">
								<div style="margin-top: 40px;margin-bottom: 40px;">
									<ul class="timeline">
										<li class="li complete" id="s0">
											<div class="status">
												<h5>创建接口</h5>
											</div>
										</li>
										<li class="li complete" id="s1">
											<div class="status">
												<h5>编辑中</h5>
											</div>
										</li>
										<li class="li" id="s2">
											<div class="status">
												<h5>审核中</h5>
											</div>
										</li>
										<li class="li s310" id="s310">
											<div class="status">
												<h5>前端审核通过</h5>
											</div>
										</li>
										<li class="li" id="s301">
											<div class="status">
												<h5>客户端审核通过</h5>
											</div>
										</li>
										<li class="li hidden s3 complete" id="s310">
											<div class="status">
												<h5>前端审核通过</h5>
											</div>
										</li>
										<li class="li" id="s3">
											<div class="status">
												<h5>审核通过</h5>
											</div>
										</li>
										<li class="li" id="s4">
											<div class="status">
												<h5>已提测</h5>
											</div>
										</li>
										<li class="li" id="s5">
											<div class="status">
												<h5>测试中</h5>
											</div>
										</li>
										<li class="li" id="s6">
											<div class="status">
												<h5>测试完成</h5>
											</div>
										</li>
										<li class="li" id="s7">
											<div class="status">
												<h5>压测中</h5>
											</div>
										</li>
										<li class="li" id="s8">
											<div class="status">
												<h5>压测完成</h5>
											</div>
										</li>
										<li class="li" id="s9">
											<div class="status">
												<h5>上线</h5>
											</div>
										</li>
									</ul>
								</div>
							</div>
						</div>
	                </div>
	            </div>
			</div>
		</div>
    </div>
    <!-- #page-wrapper -->

	<script type="text/javascript">
		var param = core.parameter;
		var _interObj;
		var _editor = CKEDITOR.replace('desc', {height: '500px',"filebrowserUploadUrl" : "/idoc/ckeditor/upload.html"});
	</script>
<@htmlNavFoot />
<@htmlFoot/>
