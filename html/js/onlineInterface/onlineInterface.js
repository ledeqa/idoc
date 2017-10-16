/**
 * 接口管理页面js
 */
$(document).ready(function(){
	if($('#productId').val() == ''){
		showAlertMessage('当前项目不存在，请联系管理员。');
		return;
	}
	var projectId = $('#projectId').val();
	
	$('.time').datetimepicker({
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		maxView: 4,
		forceParse: 0,
		format:'yyyy-mm-dd'
    });
	//导入word文档接口模态窗口,显示时进行一些初始化操作
	$('#uploadDocApis').on('show.bs.modal', function () {
		data = {};
		data.productId = $("#productId").val();
		$.ajax({
			url: '/idoc/onlineInter/queryOnlineInters.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode==200){
					var retContent = json.retContent;
					moduleList = retContent;
					var len = retContent.length;
					var optionStr = "<option value=\"\">-请选择模块-</option>";
					for(var i=0; i<len; i++){
						var module = retContent[i];
						optionStr += "<option value=\""+module.onlineModuleId+"\">"+module.moduleName+"</option>";
					}
					$("#onlineModule").append(optionStr);
				}
			}
		});
	});
	$("#moduleTree").jstree({
		"themes" : {
			"theme" : "default",
			"url" : "/js/common/jsTree/themes/default/style.css",
			"dots" : true,
			"icons" : false
		},
		"plugins" : [ "themes", "html_data", "ui", "crrm"]
	});
	
	$("#moduleTree").jstree("open_all");
	
	getProductUsers();
	//绑定搜索指定的条件
	//$("#search_interType option=[value='"+$("#s_interType").val()+"']").attr("selected", true);
	//$("#search_requestType option=[value='"+$("#s_requestType").val()+"']").attr("selected", true);
	if($('#interId').val() != ''){
		var interfaceId = $('#interId').val();
		//initIntefaceInfo(interfaceId);
	}
	
	$('#addModule').click(function(){
		bootbox.dialog({
			title: '添加模块',
			message:'<div class="row form-horizontal">' +
						'<div class="form-group">' +
							'<label class="col-lg-2 control-label">模块名称</label>' +
							'<div class="col-lg-9">' +
						    	'<input id="moduleName" type="text" class="form-control required" placeholder="请输入模块名称">' +
						    '</div>' +
						'</div>' +
					'</div>',
			buttons:{
				Modify:{
					label: '确定',
					className : 'btn-primary',
					callback : function() {
						var moduleName = $('#moduleName').val();
						if(!moduleName || moduleName.trim() == ''){
							$('#moduleName').after('<span style="color:red;">请输入模块名称</span>');
							return false;
						}
						
						var productId = $('#productId').val().trim();
						
						var data = {};
						data.productId = productId;
						data.moduleName = moduleName;
						
						$.ajax({
							url:'/idoc/moduleOnline/add.html',
							type: 'POST',
							data: data,
							dataType: 'json',
							success: function(json){
								if(json.retCode == 200){
									var module = json.module;
									$('#moduleTree').jstree('create', -1, 'last', {attr: {id:'module-' + module.onlineModuleId, module:module.onlineModuleId, class:'module'}, data: module.moduleName}, false, true);
									$('#module-' + module.onlineModuleId).find('a').before('<label class="chexkbox-inline">&nbsp;<input type="checkbox" name="module-checkbox" id="module-checkbox-' + module.onlineModuleId + '">&nbsp;</label>');
									$('#module-' + module.onlineModuleId).find('a').css('margin-left', '6px');
									$('#module-' + module.onlineModuleId).append('<a href="#" class="edit-module" onclick="editModule(' + module.onlineModuleId + ',' + module.moduleName +');" style="margin-left:5px;">&nbsp;<i class="glyphicon glyphicon-pencil"></i></a><a href="#" class="edit-module" onclick="deleteModule(' + module.onlineModuleId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash"></i></a>');
								}else{
									showAlertMessage('操作失败');
								}
							}
						});
						
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	$('#addPage').click(function(){
		var selectedNode = $('#moduleTree').jstree('get_selected');
		
		if(selectedNode.length == 0 || !$(selectedNode).attr('id').startsWith('module')){
			bootbox.alert({
				message: '请选择需要添加页面的模块',
				buttons:{
					ok:{
						label: '确定',
						className: 'btn-primary'
					}
				}
			});
			return;
		}
		
		bootbox.dialog({
			title: '添加页面',
			message:'<div class="row form-horizontal">' +
						'<div class="form-group">' +
							'<label class="col-lg-2 control-label">页面名称</label>' +
							'<div class="col-lg-9">' +
						    	'<input id="pageName" type="text" class="form-control required" placeholder="请输入页面名称">' +
						    '</div>' +
						'</div>' +
					'</div>',
			buttons:{
				Modify:{
					label: '确定',
					className : 'btn-primary',
					callback : function() {
						var pageName = $('#pageName').val().trim();
						if(!pageName || pageName == ''){
							$('#pageName').after('<span style="color:red;">请输入页面名称</span>');
							
							return false;
						}
						var moduleId = $(selectedNode).attr('module');
						var data = {};
						data.onlineModuleId = moduleId;
						data.pageName = pageName;
						
						$.ajax({
							url: '/idoc/pageOnline/add.html',
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									var page = json.page;
									$('#moduleTree').jstree('create', null, 'last', {attr: {id:'page-' + page.onlinePageId, page:page.onlinePageId, class:'page'}, data: page.pageName}, false, true);
									$('#page-' + page.onlinePageId).find('a').before('<label class="chexkbox-inline">&nbsp;<input type="checkbox" name="page-checkbox" id="page-checkbox-' + page.onlinePageId + '">&nbsp;</label>');
									$('#page-' + page.onlinePageId).find('a').css('margin-left', '6px');
									$('#page-' + page.onlinePageId).append('<a href="#" class="edit-page" onclick="editPage(' + page.onlinePageId + ',' + page.pageName +');">&nbsp;<i class="glyphicon glyphicon-pencil" style="margin-left:5px;"></i></a><a href="#" class="edit-page" onclick="deletePage(' + page.onlinePageId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash"></i></a>');
								}else{
									showAlertMessage('操作失败');
								}
							}
						});
						
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	$('#headBtns').delegate('#save', 'click', function(){
		var interName = $('#interName').val().trim();
		if(interName == ''){
			showAlertMessage('请填写接口名称。');
			return;
		}
		_interObj.interfaceName = interName;
		_interObj.interfaceId = $("#onlineInterfaceId").val();
		_interObj.onlinePageId = $("#onlinePageId").val();
		var interType= $('#interType').val().trim();
		if(interType == ''){
			showAlertMessage('请选择接口类型。');
			return;
		}
		_interObj.interfaceType = interType;
		
		var ftlTemplate = $("#online_inter_ftl_template").val();
		if(ftlTemplate){
			ftlTemplate = ftlTemplate.trim();
		}
		_interObj.ftlTemplate = ftlTemplate;
		
		var requestType = $('#requestType').val().trim();
		if(requestType == ''){
			showAlertMessage('请选择协议类型。');
			return;
		}
		_interObj.requestType = requestType;
		
		var url = $('#url').val().trim();
		if(url == ''){
			showAlertMessage('请填写请求url');
			return;
		}
		_interObj.url = url;
		_interObj.iterVersion = $('#lb_iterVersion').text().trim();
		
		var desc = CKEDITOR.instances.desc.getData();
		_interObj.desc = desc;
		
		_interObj.reqParams = param.getReqParam();
		_interObj.retParams = param.getRetParam();
		
		var isNeedTest = $('#isNeedInterfaceTest').val().trim();
		if(isNeedTest == ''){
			showAlertMessage('请选择是否需要测试');
			return;
		}
		_interObj.isNeedInterfaceTest = isNeedTest;
		
		var isNeedPressureTest = $('#isNeedPressureTest').val().trim();
		if(isNeedPressureTest == ''){
			showAlertMessage('请选择是否需要压力测试');
			return;
		}
		_interObj.isNeedPressureTest = isNeedPressureTest;
		
		var expectOnlineTime = $('#expectOnlineTime').val().trim();
		if(expectOnlineTime == ''){
			showAlertMessage('请选择预计上线时间。');
			return;
		}
		_interObj.expectOnlineTime = expectOnlineTime;
		
		var reqPeople = $('#reqPeople').val();
		if(!reqPeople){
			showAlertMessage('请选择需求人员。');
			return;
		}
		var reqPeopleStr = '';
		for(var i = 0; i < reqPeople.length; i++){
			reqPeopleStr += reqPeople[i] + ',';
		}
		reqPeopleStr = reqPeopleStr.substring(0, reqPeopleStr.length - 1);
		_interObj.reqPeopleIds = reqPeopleStr;
		
		var frontPeople = $('#frontPeople').val();
		if(!frontPeople){
			showAlertMessage('请选择前端开发。');
			return;
		}
		var frontPeopleStr = '';
		for(var i = 0; i < frontPeople.length; i++){
			frontPeopleStr += frontPeople[i] + ',';
		}
		frontPeopleStr = frontPeopleStr.substring(0, frontPeopleStr.length - 1);
		_interObj.frontPeopleIds = frontPeopleStr;
		
		var behindPeople = $('#behindPeople').val();
		if(!behindPeople){
			showAlertMessage('请选择后台开发。');
			return;
		}
		var behindPeopleStr = '';
		for(var i = 0; i < behindPeople.length; i++){
			behindPeopleStr += behindPeople[i] + ',';
		}
		behindPeopleStr = behindPeopleStr.substring(0, behindPeopleStr.length - 1);
		_interObj.behindPeopleIds = behindPeopleStr;
		
		var clientPeople = $('#clientPeople').val();
		if(!clientPeople){
			showAlertMessage('请选择客户端开发。');
			return;
		}
		var clientPeopleStr = '';
		for(var i = 0; i < clientPeople.length; i++){
			clientPeopleStr += clientPeople[i] + ',';
		}
		clientPeopleStr = clientPeopleStr.substring(0, clientPeopleStr.length - 1);
		_interObj.clientPeopleIds = clientPeopleStr;
		
		var testPeople = $('#testPeople').val();
		if(!testPeople){
			showAlertMessage('请选择客户端开发。');
			return;
		}
		
		bootbox.dialog({
			message:'<div style="text-align:center;height:150px;padding-top:65px;" id="spinMessage">保存中...</div>' +
				'<div id="saving"></div>',
			buttons:{
				ok:{
					label: '确定',
					className: 'hidden savingBtn'
				}
			}
		});
		var spinner = new Spinner({radius: 30, length: 0, width: 10, color: '#286090', trail: 40}).spin(document.getElementById('saving'));
		
		var testPeopleStr = '';
		for(var i = 0; i < testPeople.length; i++){
			testPeopleStr += testPeople[i] + ',';
		}
		testPeopleStr = testPeopleStr.substring(0, testPeopleStr.length - 1);
		_interObj.testPeopleIds = testPeopleStr;
		$.ajax({
			url: '/idoc/onlineInter/update.html',
			type: 'POST',
			dataType: 'json',
			contentType: "application/json",
			data: JSON.stringify(_interObj),
			success: function(json){
				if(json.retCode == 200){
					spinner.spin();
					var inter = json.inter;
					if($('#interId').val() == ''){
						$('#interId').val(inter.interfaceId);
						$('#moduleTree').jstree('create', null, 'last', {attr:{id:'inter-' + inter.interfaceId, inter:inter.interfaceId, class:'inter'}, data:inter.interfaceName}, false, true);
						$('#inter-' + inter.interfaceId + ' a').first().attr('onclick', 'initIntefaceInfo(' + inter.interfaceId + ', true);return false;');
						$('#inter-' + inter.interfaceId).append('<a href="#" class="edit-inter" onclick="deleteInterface(' + inter.interfaceId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash"></i></a>');
						$('#moduleTree').jstree('deselect_all');
						$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
					}
					refreshInterfaceInfo(9);
					setInterfaceInfoReadonly(true);
					param.setEditMode(false);
					$("#spinMessage").html('<font style="font-size:20px" color="green"><strong>保存成功!</strong></font>');
					var savingBtn = document.getElementsByClassName('savingBtn')[0];
					setTimeout(function(){
						savingBtn.click();
					}, 1000);
					
					var editVer = parseInt($('#lb_onlineVersion').text());
					if(typeof editVer === 'number')
						$('#lb_onlineVersion').text(editVer + 1);
				}else{
					$("#spinMessage").html('<font style="font-size:20px" color="red"><strong>保存失败!</strong></font>');
					var savingBtn = document.getElementsByClassName('savingBtn')[0];
					setTimeout(function(){
						savingBtn.click();
					}, 1000);
				}
			}
		});
	});
	
	$('#headBtns').delegate('#cancel', 'click', function(){
		bootbox.dialog({
			message: '确定放弃编辑该接口？',
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						$('#hint').css({'display': ''});
						$('#interMain').css({'display': 'none'});
						$('#headBtns').html('');
						
						if(window.location.search.indexOf('&') > 0){
							window.location.search = window.location.search.substring(0, window.location.search.indexOf('&'));
						}
//						window.location.reload();
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	$('#headBtns').delegate('#delete', 'click', function(){
		bootbox.dialog({
			message: '确定删除该接口？',
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						var interfaceId = $('#interId').val();
						if(interfaceId == ''){
							showAlertMessage('删除接口失败。');
							return;
						}
						
						var data = {};
						data.interfaceId = interfaceId;
						
						$.ajax({
							url: '/idoc/onlineInter/delete.html',
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									$('#moduleTree').jstree('deselect_all');
									var node = $('#inter-' + interfaceId);
									$("#moduleTree").jstree("remove", node);
									
									$('#hint').css({'display': ''});
									$('#interMain').css({'display': 'none'});
									$('#headBtns').html('');
									$('#interId').val('');
									
									showAlertMessage('删除接口成功。', function (){
										if(window.location.search.indexOf('&') > 0){
											window.location.search = window.location.search.substring(0, window.location.search.indexOf('&'));
										}
//										window.location.reload();
									});
								}else{
									showAlertMessage('删除接口失败。');
								}
							}
						});
						
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	//按钮对应的点击事件
	$('#headBtns').delegate('#reEdit', 'click', function(){ // 跳转到编辑页面
		var headBtnsHtml = '<button class="btn btn-success btn-sm" id="save">保存</button>&nbsp;&nbsp;' +
		'<button class="btn btn-warning btn-sm" id="cancel">取消</button>';
		
		$('#headBtns').html(headBtnsHtml);
		setInterfaceInfoReadonly(false);
		
		$('.chosen-select').chosen('destroy');
		setTimeout(function(){
			$(".chosen-select").chosen();
		}, 200);
		param.setEditMode(true);
	});
	
	$('#headBtns').delegate('#audit', 'click', function(){
		var msg = '确定发起审核吗？';
		var url = '/idoc/flow/audit.html';
		var data = {};
		data.interfaceId = $('#interId').val();
		data.projectId = projectId;
		operationDialog(msg, url, data, 2);
	});
	
	$('#headBtns').delegate('#revise', 'click', function(){
		bootbox.dialog({
			title: '修改建议',
			message:'<div class="row form-horizontal">' +
			'<div class="form-group">' +
			'<label class="col-lg-2 control-label">建议内容：</label>' +
			'<div class="col-lg-9">' +
			'<textarea id="proposal" rows="6" class="form-control required" placeholder="请输入修改的建议"></textarea>' +
			'</div>' +
			'</div>' +
			'</div>',
			buttons:{
				Modify:{
					label: '确定',
					className : 'btn-primary',
					callback : function() {
						var proposal = $('#proposal').val();
						if(!proposal || proposal.trim() == ''){
							$('#proposal').after('<span style="color:red;">请输入修改的建议</span>');
							return false;
						}
						
						var url = '/idoc/flow/revise.html';
						var data = {};
						data.interfaceId = $('#interId').val();
						data.projectId = projectId;
						data.proposal = proposal;
						$.ajax({
							url:url,
							type: 'POST',
							data: data,
							dataType: 'json',
							success: function(json){
								if(json.retCode == 200){
									bootbox.alert({message: '建议修改成功！',
										buttons:{
											ok:{
												label: '确定'
											}
									}});
									refreshInterfaceInfo(1);
								}else{
									bootbox.alert({message: '操作失败',
										buttons:{
											ok:{
												label: '确定'
											}
									}});
								}
							}
						});
						
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	$('#headBtns').delegate('#auditSuccess', 'click', function(){
		var url = '/idoc/flow/auditSuccess.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 3);
	});
	
	$('#headBtns').delegate('#forceBack', 'click', function(){
		var msg = '确定要强制回收吗？强制回收后将重新进入编辑状态！';
		var url = '/idoc/flow/forceBack.html';
		
		bootbox.dialog({
			title: msg,
			message:'<div class="row form-horizontal">' +
			'<div class="form-group">' +
			'<label class="col-lg-2 control-label">回收原因：</label>' +
			'<div class="col-lg-9">' +
			'<select id="reason" class="form-control">'+
			'<option value=0 selected="selected">--请输选择回收的原因--</option>' +
    		'<option value=2>产品原因</option>' +
    		'<option value=3>后台开发原因</option>' +
    		'<option value=4>客户端开发原因</option>' +
    		'<option value=5>前端开发原因</option>' +
    		'<option value=6>测试原因</option>' +
    		'<option value=7>其他</option>' +
			'</select>' +
			'</div>' +
			'</div>' +
			'</div>',
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						var reason = $('#reason').val();
						if(!reason || reason.trim() == '' || reason == 0){
							$('#reason').after('<span style="color:red;">请输选择回收的原因</span>');
							return false;
						}
						var data = {};
						data.projectId = projectId;
						data.interfaceId = $('#interId').val();
						data.reason = reason;
						$.ajax({
							url: url,
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									refreshInterfaceInfo(1);
								}else{
									bootbox.alert({message: '操作失败',
										buttons:{
											ok:{
												label: '确定'
											}
										}
									});
								}
							}
						});
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	});
	
	$('#headBtns').delegate('#submitToTest', 'click', function(){
		var url = '/idoc/flow/submitToTest.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 4);
	});
	
	$('#headBtns').delegate('#test', 'click', function(){
		var url = '/idoc/flow/test.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 5);
	});
	
	$('#headBtns').delegate('#tested', 'click', function(){
		var url = '/idoc/flow/tested.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 6);
	});
	
	$('#headBtns').delegate('#returnToTest', 'click', function(){
		var url = '/idoc/flow/returnToTest.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 5);
	});
	
	$('#headBtns').delegate('#pressure', 'click', function(){
		var url = '/idoc/flow/pressure.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 7);
	});
	
	$('#headBtns').delegate('#pressured', 'click', function(){
		var url = '/idoc/flow/pressured.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 8);
	});
	
	$('#headBtns').delegate('#returnToPressure', 'click', function(){
		var url = '/idoc/flow/returnToPressure.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 7);
	});
	
	$('#headBtns').delegate('#online', 'click', function(){
		var url = '/idoc/flow/online.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 9);
	});
	$('#moveCopy').click(function(){
		var interfaceId = $('#interId').val();
		if(interfaceId == ''){
			showAlertMessage('请先进行保存操作后，再进行移动/复制操作。');
			return;
		}
		
		var productId = $('#productId').val();
		var data = {};
		data.productId = productId;
		
		$.ajax({
			url: '/idoc/onlineInter/getModules.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					_moduleList = json.moduleList;
					if(_moduleList.length > 0){
						var firstModulePages = _moduleList[0].pageList;
						var moduleOptionsStr = '';
						for(var i = 0; i < _moduleList.length; i++){
							var module = _moduleList[i];
							moduleOptionsStr += '<option value="' + module.onlineModuleId + '">' + module.moduleName + '</option>';
						}
						
						var pageOptionStr = '';
						for(var i = 0; firstModulePages && i < firstModulePages.length; i++){
							var page = firstModulePages[i];
							pageOptionStr += '<option value="' + page.onlinePageId + '">' + page.pageName + '</option>';
						}
						
						bootbox.dialog({
							title: '移动接口',
							message:'<div class="row form-horizontal">' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">模块</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="moduleSelect" onchange="changeOnlineModule()">' +
													moduleOptionsStr +
												'</select>' +
											'</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">页面</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="pageSelect">' +
													pageOptionStr +
												'</select>' +
											'</div>' +
										'</div>' +
									'</div>',
							buttons:{
								Modify:{
									label: '确定',
									className : 'btn-primary',
									callback : function() {
										var pageId = $('#pageSelect').val();
										if(!pageId || pageId == ''){
											$('#pageSelect').after('<span style="color:red;">请选择目标页面</span>');
											
											return false;
										}

										var data = {};
										data.onlineInterfaceId = $("#onlineInterfaceId").val();
										data.onlinePageId = pageId;
										
										var action = $('input[name="moveCopyOption"]:checked').val();
										/*if(action == 'move'){*/
										$.ajax({
											url: '/idoc/onlineInter/move.html',
											type: 'POST',
											dataType: 'json',
											data: data,
											success: function(json){
												if(json.retCode == 200){
													showAlertMessage('移动接口操作成功。', function(){
														window.location.reload();
													});
												}else{
													showAlertMessage('移动接口操作失败。');
												}
											}
										});
										/*}else if(action == 'copy'){
											$.ajax({
												url: '/idoc/onlineInter/copy.html',
												type: 'POST',
												dataType: 'json',
												data: data,
												success: function(json){
													if(json.retCode == 200){
														showAlertMessage('复制接口操作成功。', function(){
															window.location.reload();
														});
													}else{
														showAlertMessage('复制接口操作失败。');
													}
												}
											});
										}*/
									}
								},
								Cancel:{
									label : '取消',
									className : "btn-warning"
								}
							}
						});
					}else{
						showAlertMessage('当前项目没有相应的模块或者页面，请先创建模块与页面。');
					}
				}else{
					showAlertMessage('获取模块列表失败。');
				}
			}
		});
	});
	
	// 导出word文档
	$('#exportInterInfo').on("click", function(){
		var interfaceIds = [];
		if($("[name='interface-checkbox']:checked").length > 0){
			checked = $('[name="interface-checkbox"]:checked');
			for(var i = 0; i < checked.length; i++){
				interfaceIds.push(checked[i].id.substring(19)); // len('interface-checkbox-') = 19
			}
			// createFileOnServer标志用于服务器上生成word文档，true生成，false返回流
			var url = '/idoc/onlineInter/exportInterInfo2Word.html?' + 'interfaceIds=' + interfaceIds.join(',') + '&createFileOnServer=false';
			window.open(url);
		}else{
			bootbox.alert('请在左侧勾选想要导出的接口！');
			return;
		}
	});
	 
	$('#exportInterInfo').poshytip({
		className: 'tip-yellow',
		content: ' <font color="#00BB00">Tips：</font>在页面左侧勾选接口，<br>点击“导出接口文档”按钮导<br>出选择的接口文档，同时可<br>以选择多个接口导出。',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		keepInViewport: true,
		offsetX: 0,
		offsetY: 5
	});
	
	$("[name='all-checkbox']").change(function(){
		if($(this).prop("checked")){
			$("[name='module-checkbox']").prop("checked",'true');
			$("[name='page-checkbox']").prop("checked",'true');
			$("[name='interface-checkbox']").prop("checked",'true');
		}else{
			$("[name='module-checkbox']").removeProp("checked");
			$("[name='page-checkbox']").removeProp("checked");
			$("[name='interface-checkbox']").removeProp("checked");
		}
	});
	
	$("[name='module-checkbox']").change(function(){
		if($(this).prop("checked")){
			$(this).parent().parent().find('[name="page-checkbox"]').prop("checked",'true');
			$(this).parent().parent().find('[name="interface-checkbox"]').prop("checked",'true');
		}else{
			$(this).parent().parent().find('[name="page-checkbox"]').removeProp("checked");
			$(this).parent().parent().find('[name="interface-checkbox"]').removeProp("checked");
		}
	});
	
	$("[name='page-checkbox']").change(function(){
		if($(this).prop("checked")){
			$(this).parent().parent().find('[name="interface-checkbox"]').prop("checked",'true');
		}else{
			$(this).parent().parent().find('[name="interface-checkbox"]').removeProp("checked");
		}
	});
});

function openOrClose(element){
	var parent = $(element).parent();
	if(parent.hasClass("jstree-closed")){
		parent.removeClass("jstree-closed");
		parent.addClass("jstree-open");
		}else{
		parent.removeClass("jstree-open");
		parent.addClass("jstree-closed");
		}
}

function displayFtlTemplate(){
	var selVal = $("#interType").val();
	if(selVal == 2){
		$("#online_ftl_template_div").show();
	}else{
		$("#online_ftl_template_div").hide();
	}
	$("#online_inter_ftl_template").val("");
}
function refreshInterfaceInfo(status){
	var status = 9;
	var statusStr = getStatus(status);
	$('#lb_interStatus').text(statusStr);
	//先移除timeline的所有complete属性
	for(var i = 0; i <= 9; i++){
		var id = '#s' + i;
		if($(id).hasClass("complete")){
			$(id).removeClass("complete");
		}
	}
	$("#s301").removeClass("complete");
	$(".s310").removeClass("complete");
	$(".s3").removeClass("complete");
	if(status == 10){
		$("#s0").addClass("complete");
	}else if(status == 310 || (status == 2&&!$('#frontPeople').val())){
		$("#lb_interStatus").text("前端审核通过");
		$(".s310").removeClass("hidden");
		$(".s310").addClass("complete");
		$(".s3").addClass("hidden");
		for(var i = 0; i <= 2; i++){
			var id = '#s' + i;
			$(id).addClass("complete");
		}
	}else if(status == 301 || (status == 2&&!$('#clientPeople').val())){
		$("#lb_interStatus").text("客户端审核通过");
		$(".s310").addClass("hidden");
		$(".s3").removeClass("hidden");
		$(".s3").removeClass("complete");
		$("#s301").addClass("complete");
		for(var i = 0; i <= 2; i++){
			if(i!=3){
				var id = '#s' + i;
				$(id).addClass("complete");
			}
		}
	}else{
		if(status>=3){
			$(".s310").addClass("complete");
			$("#s301").addClass("complete");
			$(".s3").addClass("complete");
		}
		for(var i = 0; i <= status; i++){
			var id = '#s' + i;
			$(id).addClass("complete");
		}
	}
	
	displayHeadBtns(status);
}

function operationDialog(msg, url, data, status){
	bootbox.dialog({
		message: msg,
		buttons:{
			ok:{
				label: '确定',
				className: 'btn-primary',
				callback: function(){
					$.ajax({
						url: url,
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								refreshInterfaceInfo(status);
							}else{
								bootbox.alert({message: '操作失败',
									buttons:{
										ok:{
											label: '确定'
										}
									}
								});
							}
						}
					});
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
}

function ajax(url, data, status){
	$.ajax({
		url: url,
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				refreshInterfaceInfo(status);
			}else{
				bootbox.alert({message: '操作失败',
					buttons:{
						ok:{
							label: '确定'
						}
					}
				});
			}
		}
	});
}

function displayHeadBtns(interfaceStatus){
	//根据角色显示接口详情页的按钮
	$('#headBtns').empty();
	if(!isNullOrEmpty(interfaceStatus)){
		var status = interfaceStatus;
		var role = "";
		if(!isNullOrEmpty($('#role').val())){
			role = $('#role').val();
		}else{
			$('#headBtns').append('<button class="btn btn-danger btn-sm" disabled>没有查询到角色信息，请联系管理员。</button>');
			return;
		}
		var interfaceId = "";
		if(!isNullOrEmpty($('#interId').val())){
			interfaceId = $('#interId').val();
		}else{
			$('#headBtns').append('<button class="btn btn-danger btn-sm" disabled>没有查询到接口ID，请联系管理员。</button>');
			return;
		}
		var creator = "";
		if(!isNullOrEmpty($('#creatorId').val())){
			creator = $('#creatorId').val();
		}else{
			$('#headBtns').append('<button class="btn btn-danger btn-sm" disabled>没有查询到接口创建者，请联系管理员。</button>');
			return;
		}
		var userName = $("#currUserId").val();
		var isNeedInterfaceTest = $('#isNeedInterfaceTest').val();
		var isNeedPressureTest = $('#isNeedPressureTest').val();
		switch(9){
		case 1:  // 编辑中
			var html = '';
			if(creator == userName){ //当前用户是创建者
				html += '<button class="btn btn-success btn-sm" id="reEdit">重新编辑</button> &nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="audit">发起审核</button>';
			}
			$('#headBtns').append(html);
			break;
		case 2:  // 审核中
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "前端开发负责人" || role == "客户端开发负责人" || role == "前端开发" || role == "客户端开发"){
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}
			$('#headBtns').append(html);
			break;
		case 3:  // 审核通过
			var html = '';
			if(creator == userName){
				if(isNeedInterfaceTest == 1){
					//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-success btn-sm" id="submitToTest">提交测试</button>';
				}else{
					//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
				}
			}else if((role == "测试负责人" || role == "测试人员") && isNeedInterfaceTest == 0){
				html += '<button class="btn btn-success btn-sm" id="online">上线</button>';
			}
			$('#headBtns').append(html);
			break;
		case 4:  // 已提测
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || role == "测试人员"){
				html += '<button class="btn btn-success btn-sm" id="test">开始测试</button>';
			}
			$('#headBtns').append(html);
			break;
		case 5:  // 测试中
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || role == "测试人员"){
				html += '<button class="btn btn-success btn-sm" id="tested">测试完成</button>';
			}
			$('#headBtns').append(html);
			break;
		case 6:  // 测试完成
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || role == "测试人员"){
				if(isNeedPressureTest == 1){ //需要压测
					html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-warning btn-sm" id="pressure">提交压测</button>';
				}else{
					html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-warning btn-sm" id="online">上线</button>';
				}
			}
			$('#headBtns').append(html);
			break;
		case 7:  // 压测中
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || role == "测试人员"){
				html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="pressured">压测完成</button>';
			}
			$('#headBtns').append(html);
			break;
		case 8:  // 压测完成
			var html = '';
			if(creator == userName){
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || role == "测试人员"){
				html += '<button class="btn btn-success btn-sm" id="returnToPressure">返回压测</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="online">上线</button>';
			}
			$('#headBtns').append(html);
			break;
		case 9:  // 已上线
			var html = '';
			if(role == "管理员" || role == "后台开发负责人" || role == "后台开发"){
				html += '<button class="btn btn-warning btn-sm" id="reEdit">重新编辑</button> &nbsp;';
				//html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}
			$('#headBtns').append(html);
			break;
		default:
			break;
		}
	}else{
		$('#headBtns').append('<button class="btn btn-danger btn-sm" disabled>没有查询到接口的状态，请联系管理员。</button>');
	}
}

function isNeedInterfaceTestChange(){
	var v = $('#isNeedInterfaceTest').val().trim();
	if(v == 0){
		$('#s4').css({'display': 'none'});
		$('#s5').css({'display': 'none'});
		$('#s6').css({'display': 'none'});
		$('#isNeedPressureTest').val(0);
		$('#isNeedPressureTest').change();
	}else{
		$('#s4').css({'display': ''});
		$('#s5').css({'display': ''});
		$('#s6').css({'display': ''});
	}
}

function isNeedPressureTestChange(){
	var v = $('#isNeedPressureTest').val().trim();
	if(v == 0){
		$('#s7').css({'display': 'none'});
		$('#s8').css({'display': 'none'});
	}else{
		$('#s7').css({'display': ''});
		$('#s8').css({'display': ''});
		if(v == 1){
			$('#isNeedInterfaceTest').val(1);
			$('#isNeedInterfaceTest').change();
		}
	}
}

function showAlertMessage(msg, callback){
	if(callback){
		bootbox.alert({message: msg,
			buttons:{
				ok:{
					label: '确定'
				}
			},
			callback: callback
		});
	}else{
		bootbox.alert({message: msg,
			buttons:{
				ok:{
					label: '确定'
				}
			}});
	}
}

function editModule(moduleId){
	bootbox.dialog({
		title: '编辑模块',
		message:'<div class="row form-horizontal">' +
		'<div class="form-group">' +
		'<label class="col-lg-2 control-label">模块名称</label>' +
		'<div class="col-lg-9">' +
		'<input id="moduleName" type="text" class="form-control required" placeholder="请输入模块名称">' +
		'</div>' +
		'</div>' +
		'</div>',
		buttons:{
			Modify:{
				label: '确定',
				className : 'btn-primary',
				callback : function() {
					var moduleName = $('#moduleName').val();
					if(!moduleName || moduleName.trim() == ''){
						$('#moduleName').after('<span style="color:red;">请输入模块名称</span>');
						
						return false;
					}
					
					var data = {};
					data.moduleId = moduleId;
					data.moduleName = moduleName;
					
					$.ajax({
						url:'/idoc/moduleOnline/update.html',
						type: 'POST',
						data: data,
						dataType: 'json',
						success: function(json){
							if(json.retCode == 200){
								var module = json.module;
								$('#module-' + moduleId + ' a').first().text(moduleName);
							}else{
								showAlertMessage('操作失败');
							}
						}
					});
					
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
}

function deleteModule(moduleId){
	
	var childNodes = $('#module-' + moduleId + ' ul li');
	if(childNodes.length > 0){
		showAlertMessage('该模块还有未删除的页面，无法删除该模块。');
		return;
	}
	
	
	bootbox.dialog({
		message: '确定删除该模块？',
		buttons:{
			ok:{
				label: '确定',
				className: 'btn-primary',
				callback: function(){
					var data = {};
					data.moduleId = moduleId;
					$.ajax({
						url: '/idoc/moduleOnline/delete.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								var node = $('#module-' + moduleId);
								$("#moduleTree").jstree("remove", node);
							}else{
								showAlertMessage('操作失败');
							}
						}
						
					});
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
}

function editPage(pageId){
	bootbox.dialog({
		title: '编辑页面',
		message:'<div class="row form-horizontal">' +
		'<div class="form-group">' +
		'<label class="col-lg-2 control-label">页面名称</label>' +
		'<div class="col-lg-9">' +
		'<input id="pageName" type="text" class="form-control required" placeholder="请输入页面名称">' +
		'</div>' +
		'</div>' +
		'</div>',
		buttons:{
			Modify:{
				label: '确定',
				className : 'btn-primary',
				callback : function() {
					var pageName = $('#pageName').val().trim();
					if(!pageName || pageName == ''){
						$('#pageName').after('<span style="color:red;">请输入页面名称</span>');
						
						return false;
					}
					var data = {};
					data.pageId = pageId;
					data.pageName = pageName;
					
					$.ajax({
						url: '/idoc/pageOnline/update.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								var page = json.page;
								$('#page-' + pageId + ' a').first().text(pageName);
							}else{
								showAlertMessage('操作失败');
							}
						}
					});
					
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
}

function deletePage(pageId){
	var childNodes = $('#page-' + pageId + ' ul li');
	if(childNodes.length > 0){
		showAlertMessage('该页面还有未删除的接口，无法删除该页面。');
		
		return;
	}
	bootbox.dialog({
		message: '确定删除该页面？',
		buttons:{
			ok:{
				label: '确定',
				className: 'btn-primary',
				callback: function(){
					var data = {};
					data.pageId = pageId;
					$.ajax({
						url: '/idoc/pageOnline/delete.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								var node = $('#page-' + pageId);
								$("#moduleTree").jstree("remove", node);
							}else{
								showAlertMessage('操作失败');
							}
						}
						
					});
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
}

function initIntefaceInfo(productId, interId, pageId, formOnclick){
	//var projectId = $('#projectId').val().trim();
	var productId = productId;
	var data = {};
	data.productId = productId;
	data.interfaceId = interId;
	data.pageId = pageId;
	data.productId = $("#productId").val().trim();
	$("#onlineInterfaceId").val(interId);
	$("#onlinePageId").val(pageId);
	
	if(formOnclick && $('#interId').val() === interId){
		return;
	}
	
	$.ajax({
		url: '/idoc/onlineInter/queryOnlineInter.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				//$("#moveCopy").attr("data", projectId);
				var interInfo = json.interInfo;
				if(interInfo){
					$('#lb_createTime').text(getDate(interInfo.createTime));
				}
				var inter = json.inter;
				$('#lb_creator').text(inter.creator.userName);
				_interObj = inter;
				$('#hint').css({'display': 'none'});
				$('#interMain').css({'display': ''});
				$('#interId').val(inter.interfaceId);
				$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
				
				$('#interName').val(inter.interfaceName);
				
				var statusStr = getStatus(inter.interfaceStatus);
				$('#lb_interStatus').text(statusStr);
				$('#lb_interStatus').css('color','red');
				$('#lb_interStatus').css('font-size',16);
				//$('#lb_creator').text(inter.creator.userName);
				//$('#lb_createTime').text(getDate(inter.createTime));
				$('#lb_onlineTime').text(getDate(inter.realOnlineTime));
				$('#interType').val(inter.interfaceType);
				$('#requestType').val(inter.requestType);
				$('#url').val(inter.url);
				$('#interfaceHistoryVersion').html('');
				if(inter.onlineVersion > 1){
					$('#interfaceHistoryVersion').html('<a class="btn btn-primary btn-xs" href="interfaceHistoryVersion.html?interfaceName=' + encodeURI(encodeURI(inter.interfaceName)) + '&interfaceId=' + inter.interfaceId + '&productId=' + $('#productId').val().trim() + '" target="_blank">历史版本</a>');
				}
				$('#lb_onlineVersion').text(inter.onlineVersion);
				$('#lb_iterVersion').text(inter.iterVersion);
				if(inter.interfaceType==2){
					$("#online_ftl_template_div").show();
				}else{
					$("#online_ftl_template_div").hide();
				}
				$("#online_inter_ftl_template").val(inter.ftlTemplate);
				setTimeout(function (){
					_editor.setData(inter.desc);
				}, 0);
				
				param.init(inter.reqParams, inter.retParams, false);
				
				$('#isNeedInterfaceTest').val(inter.isNeedInterfaceTest);
				$('#isNeedInterfaceTest').change();
				
				$('#isNeedPressureTest').val(inter.isNeedPressureTest);
				$('#isNeedPressureTest').change();
				
				var expectOnline = getDate(inter.expectOnlineTime);
				expectOnline = expectOnline.substring(0, 10);
				$('#expectOnlineTime').val(expectOnline);
				
				if($('#reqPeople_chosen').length > 0){
					$('.chosen-select').chosen('destroy');
				}
				$('.chosen-select option').removeAttr('selected');
				
				var reqPeople = $('#reqPeople');
				var frontPeople = $('#frontPeople');
				var behindPeople = $('#behindPeople');
				var clientPeople = $('#clientPeople');
				var testPeople = $('#testPeople');
				
				for(var i = 0; inter.reqPeoples && i < inter.reqPeoples.length; i++){
					for(j = 0; reqPeople[0].options && j < reqPeople[0].options.length; j++){
						var user = inter.reqPeoples[i];
						if(user.userId == reqPeople[0].options[j].value){
							reqPeople[0].options[j].selected = 'selected';
							break;
						}
						
					}
				}
				
				for(var i = 0; inter.frontPeoples && i < inter.frontPeoples.length; i++){
					for(j = 0; frontPeople[0].options && j < frontPeople[0].options.length; j++){
						var user = inter.frontPeoples[i];
						if(user.userId == frontPeople[0].options[j].value){
							frontPeople[0].options[j].selected = 'selected';
							break;
						}
						
					}
				}
				
				for(var i = 0; inter.behindPeoples && i < inter.behindPeoples.length; i++){
					for(j = 0; behindPeople[0].options && j < behindPeople[0].options.length; j++){
						var user = inter.behindPeoples[i];
						if(user.userId == behindPeople[0].options[j].value){
							behindPeople[0].options[j].selected = 'selected';
							break;
						}
						
					}
				}
				
				for(var i = 0; inter.clientPeoples && i < inter.clientPeoples.length; i++){
					for(j = 0; clientPeople[0].options && j < clientPeople[0].options.length; j++){
						var user = inter.clientPeoples[i];
						if(user.userId == clientPeople[0].options[j].value){
							clientPeople[0].options[j].selected = 'selected';
							break;
						}
						
					}
				}
				
				for(var i = 0; inter.testPeoples && i < inter.testPeoples.length; i++){
					for(j = 0; testPeople[0].options && j < testPeople[0].options.length; j++){
						var user = inter.testPeoples[i];
						if(user.userId == testPeople[0].options[j].value){
							testPeople[0].options[j].selected = 'selected';
							break;
						}
						
					}
				}
				setTimeout('$(".chosen-select").chosen();', 200);
				setInterfaceInfoReadonly(true);
				
				$('#creatorId').val(inter.creatorId);
				
				refreshInterfaceInfo(inter.interfaceStatus);
				$("#ulList li:first ul:first li:first ul:first li:first").attr("check", "true");
			}else{
				showAlertMessage('获取接口信息失败。');
			}
		}
	});
}

function deleteInterface(interfaceId){
	
	bootbox.dialog({
		message: '确定删除该接口？',
		buttons:{
			ok:{
				label: '确定',
				className: 'btn-primary',
				callback: function(){
					if(interfaceId == ''){
						showAlertMessage('删除接口失败。');
						return;
					}
					
					var data = {};
					data.interfaceId = interfaceId;
					
					
					$.ajax({
						url: '/idoc/onlineInter/delete.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								$('#moduleTree').jstree('deselect_all');
								var node = $('#inter-' + interfaceId);
								$("#moduleTree").jstree("remove", node);
								
								$('#hint').css({'display': ''});
								$('#interMain').css({'display': 'none'});
								$('#headBtns').html('');
								$('#interId').val('');
								
								showAlertMessage('删除接口成功。', function (){
									if(window.location.search.indexOf('&') > 0){
										window.location.search = window.location.search.substring(0, window.location.search.indexOf('&'));
									}
								});
							}else{
								showAlertMessage('删除接口失败。');
							}
						}
					});
				}
			},
			Cancel:{
				label : '取消',
				className : "btn-warning"
			}
		}
	});
	
}

function setInterfaceInfoReadonly(enable){
	$('#interName').attr("readonly",enable);
	$('#interType').attr("disabled",enable);
	$('#requestType').attr("disabled",enable);
	$('#url').attr("readonly",enable);
	_editor.setReadOnly(enable);
	$('#isNeedInterfaceTest').attr("disabled",enable);
	$('#isNeedPressureTest').attr("disabled",enable);
	$('#expectOnlineTime').attr("disabled",enable);
	$('#reqPeople').attr("disabled",enable);
	$('#frontPeople').attr("disabled",enable);
	$('#behindPeople').attr("disabled",enable);
	$('#clientPeople').attr("disabled",enable);
	$('#testPeople').attr("disabled",enable);
	$('#online_inter_ftl_template').attr("disabled",enable);
}

function getStatus(status){
	var statusStr = '';
	switch(status){
		case 10:
			statusStr = '暂存中';
			break;
		case 1:
			statusStr = '编辑中';
			break;
		case 2:
			statusStr = '审核中';
			break;
		case 310:
			statusStr = '前端审核通过';
			break;
		case 301:
			statusStr = '客户端审核通过';
			break;
		case 3:
			statusStr = '审核通过';
			break;
		case 4:
			statusStr = '已提测';
			break;
		case 5:
			statusStr = '测试中';
			break;
		case 6:
			statusStr = '测试完成';
			break;
		case 7:
			statusStr = '压测中';
			break;
		case 8:
			statusStr = '压测完成';
			break;
		case 9:
			statusStr = '已上线';
			break;
		default:
			break;
	}
	
	return statusStr;
}

function getProductUsers(){
	var productId = $('#productId').val().trim();
	var data = {};
	data.productId = productId;
	
	$.ajax({
		url: '/idoc/inter/users.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				if(json.users){
					var userMap = json.users;
					var reqPeople = $('#reqPeople');
					var frontPeople = $('#frontPeople');
					var behindPeople = $('#behindPeople');
					var clientPeople = $('#clientPeople');
					var testPeople = $('#testPeople');
					
					for(var roleName in userMap){
						var userList = userMap[roleName];
						for(var i = 0;i<userList.length;i++){
							var user = userList[i];
							if(roleName.indexOf("管理员")>=0){
								reqPeople[0].options.add(new Option(user.userName+"(管理员)", user.userId));
								frontPeople[0].options.add(new Option(user.userName+"(管理员)", user.userId));
								behindPeople[0].options.add(new Option(user.userName+"(管理员)", user.userId));
								clientPeople[0].options.add(new Option(user.userName+"(管理员)", user.userId));
								testPeople[0].options.add(new Option(user.userName+"(管理员)", user.userId));
							}
							if(roleName.indexOf("产品")>=0)
								reqPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("前端")>=0)
								frontPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("后台")>=0)
								behindPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("客户端")>=0)
								clientPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("测试")>=0)
								testPeople[0].options.add(new Option(user.userName, user.userId));
						}
					}
				}
			}else{
				showAlertMessage('获取产品成员列表失败。');
			}
		}
	});
}

function newInterfaceForm(interObj){
	var interfaceForm = {};
	interfaceForm.interfaceId = interObj.interfaceId;
	interfaceForm.onlineInterfaceId = interObj.onlineInterfaceId;
	interfaceForm.pageId = interObj.pageId;
	interfaceForm.interfaceName = interObj.interfaceName;
	interfaceForm.interfaceType = interObj.interfaceType;
	interfaceForm.iterVersion = interObj.iterVersion;
	interfaceForm.url = interObj.url;
	interfaceForm.desc = interObj.desc;
	interfaceForm.isNeedInterfaceTest = interObj.isNeedInterfaceTest;
	interfaceForm.isNeedPressureTest = interObj.isNeedPressureTest;
	interfaceForm.expectOnlineTime = interObj.expectOnlineTime;
	interfaceForm.reqPeopleIds = interObj.reqPeopleIds;
	interfaceForm.frontPeopleIds = interObj.frontPeopleIds;
	interfaceForm.behindPeopleIds = interObj.behindPeopleIds;
	interfaceForm.clientPeopleIds = interObj.clientPeopleIds;
	interfaceForm.testPeopleIds = interObj.testPeopleIds;
	interfaceForm.reqParams = interObj.reqParams;
	interfaceForm.retParams = interObj.retParams;
	
	return interfaceForm;
}

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}
function changeOnlineModule(){
	var content = _moduleList;
	var moduleId = $("#moduleSelect").val();
	if(moduleId==""){
		$("#pageSelect").empty();
	}
	var len = content.length;
	for(var i=0; i<len; i++){
		var module = content[i];
		var id = module.onlineModuleId;
		if(id == moduleId){
			var pageList = module.pageList;
			$("#pageSelect").empty();
			var pages = "";
			var pageLen = pageList.length;
			for(var j=0; j<pageLen; j++){
				var page = pageList[j];
				pages += "<option value=\""+page.onlinePageId+"\">" + page.pageName + "</>";
			}
			$("#pageSelect").append(pages);
		}
	}
}
