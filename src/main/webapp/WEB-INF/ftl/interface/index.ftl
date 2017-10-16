<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台"

otherCss=["/css/common/prism.css", "/css/common/bootstrap-datetimepicker/bootstrap-datetimepicker.css", "/css/common/chosen.css",
"/css/common/timeline.css", "/css/interface/interface.css", "/css/interface/inter.css", "/css/common/jquery.toastmessage.css", "/css/common/font-awesome/css/font-awesome.css"] 
otherJs=[ "/js/common/prism.js", "/js/common/mock.js", "/js/common/bootbox.js","/js/common/jquery.validate.js","/js/common/jsTree/jquery.jstree.js",
"/js/common/ckeditor/ckeditor.js", "/js/common/bootstrap-datetimepicker/bootstrap-datetimepicker.js", "/js/common/FileSaver.js",
"/js/common/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js", "/js/common/jquery.toastmessage.js",
"/js/common/chosen.jquery.js", "/js/common/tangram-1.2.1.js", "/js/common/tangram-fix.js", "/js/common/zclip/jquery.zclip.js",
"/js/common/uuid.js", "/js/interface/paramCore.js", "/js/interface/interface.js", "/js/interface/spin.js"]
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
				  <li><a href="/idoc/projectList.html?productId=${productId! }">${productName!}</a></li>
				  <li class="active">${projectName! }</li>
				</ol>
			</div>
		</div>
    	<div class="row">
            <div class="col-sm-12">
                <h1 class="page-header">接口编辑
                	<#if role?exists && (role == "管理员" || role == "后台开发负责人" || role == "后台开发")>
		                <button class="btn btn-primary btn-sm" id="addModule">添加模块</button>
		                <button class="btn btn-primary btn-sm" id="addPage">添加页面</button>
		                <button class="btn btn-primary btn-sm" id="addInterface">添加接口</button>
		                <button class="btn btn-primary btn-sm" id="importOnlineInterface">导入在线接口</button>
	                </#if>
                </h1>
                <input type="hidden" id="projectId" value="${projectId! }">
                <input type="hidden" id="productId" value="${productId! }">
                <input type="hidden" id="productName" value="${productName! }">
                <input type="hidden" id="currUser" value="${currUser! }">
                <input type="hidden" id="currUserName" value="${currUserName! }">
                <input type="hidden" id="currUserId" value="${currUserId!?c }">
                <input type="hidden" id="role" value="${role! }">
                <input type="hidden" id="productDomainUrl" value="${productDomainUrl!'' }">
            </div>
            <!-- /.col-sm-12 -->
            <div class="col-sm-12" style="padding-bottom:5px;margin-bottom:10px;">
            	<form id="searchForm" action="" method="post">
            		<input type="hidden" name="form_projectId" id="projectId" value="${projectId! }">
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">负责人员:</span>
						  <input id="responseInput" type="text" class="form-control" name="responsible" value="${responsible!"" }" id="search_responsible" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">接口名称:</span>
						  <input type="text" class="form-control" name="interName" value="${interName!"" }" id="search_interName" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">接口地址:</span>
						  <input type="text" class="form-control" name="interUrl" value="${interUrl!"" }" id="search_interUrl" aria-describedby="basic-addon1">
						</div>
				    </div>
				    <div class="col-sm-2" style="padding-left:1px;">
				    	<div class="input-group">
						  <span class="input-group-addon" id="basic-addon1">协议类型:</span>
	  						<select id="search_requestType" name="requestType" class="form-control edit-field">
					    		<option value="" <#if !(requestType??)>selected="selected"</#if> >-请选择协议类型-</option>
					    		<option value="1" <#if requestType?? && requestType == '1'>selected="selected"</#if> >GET</option>
					    		<option value="2"<#if requestType?? && requestType == '2'>selected="selected"</#if> >POST</option>
					    		<option value="3"<#if requestType?? && requestType == '3'>selected="selected"</#if> >PUT</option>
					    		<option value="4"<#if requestType?? && requestType == '4'>selected="selected"</#if> >DELETE</option>
					    	</select>
						</div>
				    </div>
				    <div class="col-sm-2">
				    	<div class="input-group">
						  <button class="btn btn-primary" style="margin-left:20px;" type="submit"><i class="fa fa-search"></i>&nbsp;搜索</button> &nbsp;&nbsp;&nbsp;
						</div>
				    </div>
				    <div class="col-sm-2">
				    	<div class="input-group">
						  <button class="btn btn-primary" style="margin-left:70px;" id="assign"><i class="fa fa-search"></i>&nbsp;Assign To Me</button> &nbsp;&nbsp;&nbsp;
						</div>
				    </div>
			    </form>
            </div>
        </div>
        <!-- /.row -->
		<div class="row">
			<div id="col3" class="col-sm-3" style="width: 25%;margin-bottom: 80px;overflow:hidden;">
				<div class="panel panel-info" style="overflow:hidden;">
					<#if modules?exists && modules?size gt 0>
						<span id="collapseTree" style="margin-left:12px;">
						</span>
						<span id="closeAndOpen" onclick="closeAndOpen();" status="close" style="border:1px; background:#eee;width:25px;height:60px;line-height:60px;margin-top:200px;margin-right:0px;text-align:right;vertical-align:50%;float:right;">
						 <<
						</span>
					</#if>
	                <div class="panel-body" id="moduleTree" style="min-height: 630px; margin-top:-15px;overflow:hidden;">
	                	<#if modules?exists>
	                		<ul>
		                		<#list modules as module>
		                			<li id="module-${module.moduleId?c }" module="${module.moduleId?c }" class="module"> &nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffe66f"></i>
		                				<a href="#" class="module-item" id="moduleName-${module.moduleId?c }" style="margin-left:6px;">${module.moduleName }</a>
		                				<a href="#" class="edit-module" onclick="editModule('${module.moduleId?c }', '${module.moduleName }')" style="margin-left:5px;">&nbsp;<i class="glyphicon glyphicon-pencil" style="color:green;"></i></a>
		                				<a href="#" class="edit-module" onclick="deleteModule('${module.moduleId?c }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
		                				<#if module.pageList?exists>
		                					<ul>
		                						<#list module.pageList as page>
		                							<li id="page-${page.pageId?c }" page="${page.pageId?c }" class="page"> &nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffc78e"></i>
						                				<a href="#" class="page-item" id="pageName-${page.pageId?c }" style="margin-left:6px;">${page.pageName}</a>
		                								<a href="#" class="edit-page" onclick="editPage('${page.pageId?c }', '${page.pageName }')" style="margin-left:5px;">&nbsp;<i class="glyphicon glyphicon-pencil" style="color:green;"></i></a>
		                								<a href="#" class="edit-page" onclick="deletePage('${page.pageId?c }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
		                								<#if page.interfaceList?exists>
		                									<ul>
			                									<#list page.interfaceList as inter>
			                										<li id="inter-${inter.interfaceId?c }" inter="${inter.interfaceId?c }" class="inter"> &nbsp;<i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
										                				<a href="#" class="inter-item" onclick="initInterfaceInfo('${inter.interfaceId?c }', true);return false;">${inter.interfaceName }</a>
			                											<#if currUserId?? && inter?? && ("管理员" == role || currUserId == inter.creatorId)>
			                												<a href="#" class="edit-inter" onclick="deleteInterface('${inter.interfaceId?c }')" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>
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
			
			
			<div id="col9" class="col-sm-9" style="width: 75%;margin-bottom: 80px;">
				<div class="panel panel-default">
					<div class="panel-body" style="min-height: 630px;">
						<div class="row col-sm-12">
							<h3 class="page-header" id="interHead">接口详情 &nbsp;
								<span id="interfaceHistoryVersion"></span>&nbsp;&nbsp;
								<span id="isEditableInfor" style="color:red;"></span>
								<div class="pull-right" id="headBtns">
								</div>
							</h3>
						</div>
						<div class="row col-sm-12" id="hint" style="height: 500px;position: relative;">
							<h1 style="width: 45%;  height: 45%;  overflow: auto;  margin: auto;  position: absolute;  top: 0; left: 0; bottom: 0; right: 0;color: #cccccc;">请在左侧选择需要查看的接口</h1>
						</div>
						<div id="interMain" style="display: none;">
							<input type="hidden" value="${interfaceId! }" id="interId">
							<input type="hidden" id="creatorId" value="">
							<div class="row col-sm-12">
								<div style="margin-top: 10px;margin-bottom: 20px;">
									<ul class="timeline">
							<li class="li complete" id="s0">
								<div class="status">
									<h5>创建接口</h5>
								</div>
							</li>
							<li class="li complete" id="s10">
								<div class="status">
									<h5>暂存中</h5>
								</div>
							</li>
							<li class="li" id="s1">
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
										<label for="inputEmail3" class="col-sm-2 control-label">编辑版本</label>
									    <div class="col-sm-10">
									    	<a class='btn btn-xs btn-info' disabled><label class="control-label" id="lb_editVersion"></label></a>
									    </div>
									</div>
									<div class="form-group">
										<label for="inputEmail3" class="col-sm-2 control-label">迭代版本</label>
									    <div class="col-sm-10">
									    	<a class='btn btn-xs btn-success' disabled><label class="control-label" id="lb_iterVersion"></label></a>
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">接口类型</label>
									    <div class="col-sm-10">
									    	<select id="interType" class="form-control edit-field" onChange="changeInterType()">
									    		<option value="">--请选择接口类型--</option>
									    		<option value="1" selected="selected">ajax</option>
									    		<option value="2">ftl</option>
									    		<option value="3">jsonp</option>
									    		<option value="4">action</option>
									    	</select>
									    </div>
									</div>
									<div class="form-group" id="ftl_template_div">
										<label class="col-sm-2 control-label">ftl模板名称</label>
									    <div class="col-sm-10">
									    	<input type="text" class="form-control" id="inter_ftl_template">
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
									    	<input type="text" class="form-control" id="url" value="${productDomainUrl!''}">
									    </div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">是否使用缓存</label>
									    <div class="col-sm-10">
									    	<label class="radio-inline">
									    		<input type="radio" name="redis" class="radioItem" value="0"/>否
									    	</label>
									    	<label class="radio-inline">
									    		<input type="radio" name="redis" class="radioItem" value="1"/>是
									    	</label>
									    	<button class="btn btn-success btn-sm hidden" id="showRedis">查看缓存</button>
									    	<button class="btn btn-success btn-sm hidden" id="addOneRedis" onclick="addOneRedis()">添加一个</button>
									    </div>
									</div>
									<div id="newRedis" class="form-group">
										<div style="margin-left:110px;">
											<table class="table table-striped table-bordered" style="margin-bottom:0px;" id="table_redis">
												<thead>
													<tr>
														<th style="width:30%">缓存Key</th>
														<th style="width:30%">缓存数据</th>
														<th style="width:35%">缓存策略</th>
														<th style="width:5%">操作</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
											</table>
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-1 col-sm-10">
											<button class="btn btn-default btn-sm" id="moveCopy"><i class="fa fa-copy edit-field"></i> 移动/复制接口</button>
											<button class="btn btn-success btn-sm" id="mockData">Mock数据</button>
											<button class="btn btn-warning btn-sm" id="mockHttp">模拟Http请求</button>
											<button class="btn btn-info btn-sm" id="autoGenerateInterTestCode">生成接口测试代码</button>
											<!-- <button class="btn btn-primary btn-sm" id="downloadAutoCode"><i class="fa fa-step-forward"></i></button> -->
											<!-- <button class="btn btn-primary btn-sm" id="testerLink"><i class="fa fa-step-forward"></i></button> -->
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">功能描述</label>
									</div>
									<div class="col-sm-11 col-sm-offset-1 alert alert-block alert-success">
										<button type="button" class="close" data-dismiss="alert">
										<i class="ace-icon fa fa-times"></i>
										</button>
										<i class="ace-icon fa fa-check green"></i>
										tips：建议填写内容 1.需求描述和截图（可选）  2 接口功能描述  3.相关数据库表格 
									</div>
									<div class="form-group">
										<div class="col-sm-offset-1 col-sm-11" style="margin-top: -10px">
											<div name="desc" id="desc" style="height: 550px;"></div>
										</div>
										<div class="col-sm-offset-1 col-sm-11" style="margin-top: -10px">
											<span id="desc_closeAndOpen" onclick="descCloseAndOpen()" class="cke_bottom cke_reset_all" style="text-align:center;" value="close">收起<span>
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">请求参数列表&nbsp;&nbsp;
											<i class="glyphicon glyphicon-exclamation-sign" style="color: #337ab7;" 
											data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" 
											data-content="参数中的mock字段在任何时候都可以编辑，直接点击想要编辑的mock字段进入编辑状态，编辑完后鼠标点击在编辑框外的其它地方就可以保存编辑！">
											</i> &nbsp;
											<i class="icon-bell-alt" style="color: #5cb85c;" 
											data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" 
											data-content="提示：编辑接口的参数时，可以从本接口或本项目中的其它接口中复制需要的参数，鼠标选中要复制的参数，使用快捷键 ctrl + shift + z 进行复制，在编辑的地方 ctrl + shift + v 拷贝参数。系统支持一次复制多个参数，选中一个参数，使用快捷键 alt + c，然后再选择下一个参数使用快捷键这样就可以复制多个想要的参数。">
											</i>
										</label>
									</div>
									<div class="form-group">
										<div id="reqParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">返回参数列表&nbsp;&nbsp;
											<i class="glyphicon glyphicon-exclamation-sign" style="color: #337ab7;" 
											data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" 
											data-content="参数中的mock字段在任何时候都可以编辑，直接点击想要编辑的mock字段进入编辑状态，编辑完后鼠标点击在编辑框外的其它地方就可以保存编辑！">
											</i> &nbsp;
											<i class="icon-bell-alt" style="color: #5cb85c;" 
											data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" 
											data-content="提示：编辑接口的参数时，可以从本接口或本项目中的其它接口中复制需要的参数，鼠标选中要复制的参数，使用快捷键 ctrl + shift + z 进行复制，在编辑的地方 ctrl + shift + v 拷贝参数。系统支持一次复制多个参数，选中一个参数，使用快捷键 alt + c，然后再选择下一个参数使用快捷键这样就可以复制多个想要的参数。">
											</i>
										</label>
									</div>
									<div class="form-group">
										<div id="retParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-3" style="margin-left: 4%;">返回值示例&nbsp;&nbsp;&nbsp;
											<button class="btn btn-xs btn-primary" id="mockRetParam">自动生成返回值示例</button>
										</label>
									</div>
									<div class="form-group">
										<div class="retParamClass" id="mockRet" style="margin-top: -10px; width: 94%; margin-left: 5%;">
											<textarea id="mockReturnExample" class="form-control" style="height: 450px;" readonly></textarea>
											<span id="mock_closeAndOpen" onclick="returnCloseAndOpen('mock_closeAndOpen','mockReturnExample')" class="cke_bottom cke_reset_all" style="text-align:center;" value="close">收起<span>
										</div>
										<div class="retParamClass" style="margin-top: 10px; width: 94%; margin-left: 5%;">
											<textarea id="retParamExample" class="form-control" style="height: 450px;"></textarea>
											<span id="return_closeAndOpen" onclick="returnCloseAndOpen('return_closeAndOpen','retParamExample')" class="cke_bottom cke_reset_all" style="text-align:center;" value="close">收起<span>
										</div>
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
									<div class="form-group" id="expectTestTimeDiv">
										<label class="col-sm-2 control-label">预计提测时间</label>
									    <div class="col-sm-10">
								    		<input type="text" class="form-control time edit-field" id="expectTestTime" placeholder="请填写预计提测时间" readonly="readonly">
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
									    	<select id="reqPeople" class="form-control chosen-select edit-field" onchange="reqPeopleChanged()" multiple data-placeholder="请选择该接口对应的产品">
									    	</select>
									    </div>
									    <div class="reqPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">前端开发</label>
									    <div class="col-sm-10 frontPeopleDiv">
									    	<select id="frontPeople" class="form-control chosen-select edit-field" onchange="frontPeopleChanged()" multiple data-placeholder="请选择该接口对应的前端开发人员">
									    	</select>
									    </div>
									    <div class="frontPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">后台开发</label>
									    <div class="col-sm-10 behindPeopleDiv">
									    	<select id="behindPeople" class="form-control chosen-select edit-field" onchange="behindPeopleChanged()" multiple data-placeholder="请选择该接口对应的后台开发人员">
									    	</select>
									    </div>
									    <div class="behindPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">客户端开发</label>
									    <div class="col-sm-10 clientPeopleDiv">
									    	<select id="clientPeople" class="form-control chosen-select edit-field" onchange="clientPeopleChanged()" multiple data-placeholder="请选择该接口对应的客户端开发人员">
									    	</select>
									    </div>
									    <div class="clientPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">测试人员</label>
									    <div class="col-sm-10 testPeopleDiv">
									    	<select id="testPeople" class="form-control chosen-select edit-field" onchange="testPeopleChanged()" multiple data-placeholder="请选择该接口对应的测试人员">
									    	</select>
									    </div>
									    <div class="testPeopleBtn"></div>
									</div>
									
									<div class="row col-sm-12">
										<div class="pull-right" id="footBtns">
										</div>
									</div>
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
		var _moduleList;
		var _projectList;
		var _editor = CKEDITOR.replace('desc', {height: '500px',"filebrowserUploadUrl" : "/idoc/ckeditor/upload.html"});
	</script>
<@htmlNavFoot />
<@htmlFoot/>