<!--首页 -->
<#include "../../inc/core.ftl">
<@htmHead title="接口文档管理平台"

otherCss=["/css/common/prism.css", "/css/common/bootstrap-datetimepicker/bootstrap-datetimepicker.css", "/css/common/chosen.css",
"/css/common/timeline.css", "/css/interface/interface.css", "/css/interface/inter.css", "/css/common/jquery.toastmessage.css", "/css/common/font-awesome/css/font-awesome.css"] 
otherJs=[ "/js/common/prism.js", "/js/common/mock.js", "/js/common/bootbox.js","/js/common/jquery.validate.js","/js/common/jsTree/jquery.jstree.js",
"/js/common/ckeditor/ckeditor.js", "/js/common/bootstrap-datetimepicker/bootstrap-datetimepicker.js", "/js/common/FileSaver.js",
"/js/common/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js", "/js/common/jquery.toastmessage.js",
"/js/common/chosen.jquery.js", "/js/common/tangram-1.2.1.js", "/js/common/tangram-fix.js", "/js/common/zclip/jquery.zclip.js",
"/js/interface/history/interfaceHistoryCore.js", "/js/interface/history/interfaceHistory.js"]
/>

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-sm-12">
            	<div class="col-sm-3">
                <h1 class="page-header"><!--${interfaceName!''} > --> 接口历史版本信息  </h1>
                </div>
                <div class="col-sm-9 form-inline">
			        <select id="compareSelect" class="form-control" style="width: 20%; font-size: 14px; margin: 45px -6% 0">
		                <option value="v0" selected>当前版本</option>
		                <#list interfaceVersions as version>
		                	<option value="${version.versionId!''}">${version.createTime?string("yyyy-MM-dd HH:mm:ss")!''} - 版本${version.versionNum!''}</option>
		                </#list>
	                </select>
	                <button class="btn btn-primary btn-xs form-control" onclick="compareInterVersion();" style="margin-top: 45px; margin-left: 100px;">版本比较</button>
					<i class="glyphicon glyphicon-exclamation-sign form-control" style="color: #5cb85c; margin: 45px 0 0; border: none" 
					data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" 
					data-content="提示：在左侧点击一个版本可以查看该历史版本。点击历史版本后，在下拉框中选择一个要比较的版本点击“版本比较”比较这两个版本，红色框出的是差异部分，参数部分用不同标记表示差异，其中，‘+’ 表示只在左侧选中的版本中存在，‘-’ 表示只在比较的版本中存在，‘勾’ 为两个版本一样，‘笔’ 为原版本被修改。">
					</i>
                </div>
                <input type="hidden" id="productId" value="${productId! }">
                <input type="hidden" id="projectId" value="${projectId! }">
                <input type="hidden" id="interfaceId" value="${interfaceId! }">
            </div>
            <!-- /.col-sm-12 -->
        </div>
        <!-- /.row -->
		<div class="row">
			<div class="col-sm-3" style="width: 25%;margin-bottom: 80px;">
				<div class="panel panel-info">
					<#if interfaceVersions?exists && interfaceVersions?size gt 0>
						<span id="collapseTree" style="margin-left:12px;">
						</span>
					</#if>
	                <div class="panel-body" id="interfaceHistoryTree" style="min-height: 630px; margin-top:-15px;">
	                	<#if interfaceVersions?exists>
	                		<ul>
	                			<li id="root"> &nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffe66f"></i> <a style="margin-left:6px;">历史版本</a>
	                			<ul>
			                		<#list interfaceVersions as version>
			                			<#if version_index == 0>
			                				<#if version.versionDesc?contains('迭代版本-')>
					                			<li id="version-${version.versionId }" version="${version.versionId }"> &nbsp; <i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
					                				<a href="#" class="version-item" id="${version.versionId!''}" onclick="initVersionInfo(${version.versionId!?c}, true);return false;" style="margin-left:6px;" title="描述信息：${version.versionDesc!''}">${version.createTime?string("yyyy-MM-dd HH:mm:ss")!''} - 版本${version.versionNum!''} &nbsp;&nbsp;&nbsp;&nbsp;<label class='btn btn-xs btn-success'>${version.versionDesc!''}</label></a>
					                			</li>
				                			<#else>
				                				<li id="version-${version.versionId }" version="${version.versionId }"> &nbsp; <i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
					                				<a href="#" class="version-item" id="${version.versionId!''}" onclick="initVersionInfo(${version.versionId!?c}, true);return false;" style="margin-left:6px;" title="描述信息：${version.versionDesc!''}">${version.createTime?string("yyyy-MM-dd HH:mm:ss")!''} - 版本${version.versionNum!''} &nbsp;&nbsp;&nbsp;&nbsp;<label class='btn btn-xs btn-success'>版本迭代-1</label></a>
					                			</li>
				                			</#if>
			                			<#else>
			                				<#if version.versionDesc?contains('迭代版本-')>
				                				<li id="version-${version.versionId }" version="${version.versionId }"> &nbsp; <i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
					                				<a href="#" class="version-item" id="${version.versionId!''}" onclick="initVersionInfo(${version.versionId!?c}, true);return false;" style="margin-left:6px;" title="描述信息：${version.versionDesc!''}">${version.createTime?string("yyyy-MM-dd HH:mm:ss")!''} - 版本${version.versionNum!''} <#if version.versionNum gt 9>&nbsp;&nbsp;<#else>&nbsp;&nbsp;&nbsp;&nbsp;</#if><label class='btn btn-xs btn-success'>${version.versionDesc!''}</label></a>
					                			</li>
					                		<#else>
					                			<li id="version-${version.versionId }" version="${version.versionId }"> &nbsp; <i class="glyphicon glyphicon-file" style="color: #ccff80"></i>
					                				<a href="#" class="version-item" id="${version.versionId!''}" onclick="initVersionInfo(${version.versionId!?c}, true);return false;" style="margin-left:6px;" title="描述信息：${version.versionDesc!''}">${version.createTime?string("yyyy-MM-dd HH:mm:ss")!''} - 版本${version.versionNum!''}</a>
					                			</li>
					                		</#if>
			                			</#if>
			                		</#list>
	                			</ul>
	                			</li>
	                		</ul>
	                	</#if>
	                </div>
	            </div>
			</div>
			
			<div class="col-sm-9" style="width: 75%;margin-bottom: 80px;">
				<div class="panel panel-default">
					<div class="panel-body" style="min-height: 630px;">
						<div class="row col-sm-12">
							<h3 class="page-header" id="interHead">接口详情 &nbsp;
								<span id="interfaceHistoryVersion"></span>
								<div class="pull-right" id="headBtns">
								</div>
							</h3>
						</div>
						<div class="row col-sm-12" id="hint" style="height: 500px;position: relative;">
							<h1 style="width: 45%;  height: 45%;  overflow: auto;  margin: auto;  position: absolute;  top: 0; left: 0; bottom: 0; right: 0;color: #cccccc;">请在左侧选择需要查看的接口版本</h1>
						</div>
						<div id="interMain" style="display: none;">
							<input type="hidden" value="${versionId!''}" id="versionId">
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
										<label class="col-sm-2 control-label">操作者</label>
									    <div class="col-sm-10">
									    	<label class="control-label" id="lb_operator"></label>
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
									    	<select id="interType" class="form-control edit-field">
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
										<label class="col-sm-2 control-label">功能描述</label>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-1 col-sm-11" style="margin-top: -10px" id="descDiv">
											<div name="desc" id="desc" style="height: 550px;"></div>
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">请求参数列表
										</label>
									</div>
									<div class="form-group">
										<div id="reqParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label">返回参数列表
										</label>
									</div>
									<div class="form-group">
										<div id="retParamsTb" class="col-sm-12"></div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-3" style="margin-left: 4%;">返回值示例
										</label>
									</div>
									<div class="form-group">
										<div class="retParamClass" style="margin-top: -10px; width: 94%; margin-left: 5%;">
											<textarea id="retParamExample" class="form-control" style="height: 450px;"></textarea>
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
									    <div class="col-sm-10 reqPeopleDiv" id="reqPeoples">
									    	<select id="reqPeople" class="form-control chosen-select edit-field" onchange="reqPeopleChanged()" multiple data-placeholder="请选择该接口对应的产品">
									    	</select>
									    </div>
									    <div class="reqPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">前端开发</label>
									    <div class="col-sm-10 frontPeopleDiv" id="frontPeoples">
									    	<select id="frontPeople" class="form-control chosen-select edit-field" onchange="frontPeopleChanged()" multiple data-placeholder="请选择该接口对应的前端开发人员">
									    	</select>
									    </div>
									    <div class="frontPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">后台开发</label>
									    <div class="col-sm-10 behindPeopleDiv" id="behindPeoples">
									    	<select id="behindPeople" class="form-control chosen-select edit-field" onchange="behindPeopleChanged()" multiple data-placeholder="请选择该接口对应的后台开发人员">
									    	</select>
									    </div>
									    <div class="behindPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">客户端开发</label>
									    <div class="col-sm-10 clientPeopleDiv" id="clientPeoples">
									    	<select id="clientPeople" class="form-control chosen-select edit-field" onchange="clientPeopleChanged()" multiple data-placeholder="请选择该接口对应的客户端开发人员">
									    	</select>
									    </div>
									    <div class="clientPeopleBtn"></div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label">测试人员</label>
									    <div class="col-sm-10 testPeopleDiv" id="testPeoples">
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
		var _currentInterObj;
		var _editor = CKEDITOR.replace('desc', {height: '500px',"filebrowserUploadUrl" : "/idoc/ckeditor/upload.html"});
	</script>
<@htmlNavFoot />
<@htmlFoot/>
