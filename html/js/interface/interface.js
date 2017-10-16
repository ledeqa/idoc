/**
 * 接口管理页面js
 */
var isEditing=0;
var moduleList = '';
var moduleNameLengthToShow = 20;
var pageNameLengthToShow = 18;
var interfaceNameLengthToShow = 18;
$(document).ready(function(){
	if($('#productId').val() == ''){
		showAlertMessage('当前项目不存在，请联系管理员。');
		return;
	}
	checkProjectIsEmpty();
	
	$("[data-toggle='popover']").popover(); // 提示信息
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
	
	$('body').delegate('#mockRetParam', 'click', function(){
		if($('#mockRet').css('display') === "none")
			mockReturnParam();
		$('#mockRet').toggle(800);
	});
	
	// 截取名称较长的字段
	
	var treeWidth = $('#moduleTree').width();
	var num = parseInt(treeWidth/14.2);
	var p = 0.8; // 名称占tree的比例
	if(treeWidth > 320){
		p = 0.8;
	}else if(treeWidth > 300 && treeWidth < 320){
		p = 0.7;
	}else if(treeWidth > 250 && treeWidth < 300){
		p = 0.68;
	}else if(treeWidth > 200 && treeWidth < 250){
		p = 0.6;
	}else if(treeWidth > 150 && treeWidth < 200){
		p = 0.5;
	}else if(treeWidth > 100 && treeWidth < 150){
		p = 0.4;
	}else{
		p = 0.3;
	}
	moduleNameLengthToShow = parseInt((treeWidth * p) / 15);
	$('.module-item').each(function(){
		var text = $(this).text();
		if(text.length > moduleNameLengthToShow){
			$(this).text(text.substring(0, moduleNameLengthToShow) + '...');
			$(this).attr('title', text);
		}
	});
	pageNameLengthToShow = parseInt((treeWidth * p) / 15 - 2) > 0 ? parseInt((treeWidth * p) / 15 - 2): parseInt((treeWidth * p) / 15);
	$('.page-item').each(function(){
		var text = $(this).text();
		if(text.length > pageNameLengthToShow){
			$(this).text(text.substring(0, pageNameLengthToShow) + '...');
			$(this).attr('title', text);
		}
	});
	
	interfaceNameLengthToShow = parseInt((treeWidth * p) / 15 - 2) > 0 ? parseInt((treeWidth * p) / 15 - 2) : parseInt((treeWidth * p) / 15);
	interfaceNameLengthToShow = num - 6 -1;
	$('.inter-item').each(function(){
		var text = $(this).text();
		if(text.length > interfaceNameLengthToShow){
			$(this).text(text.substring(0, interfaceNameLengthToShow) + '...');
			$(this).attr('title', text);
		}
	});
	/*
	$('.module-item').each(function(){
		var text = $(this).text();
		$(this).attr('title', text);
	});
	$('.page-item').each(function(){
		var text = $(this).text();
		$(this).attr('title', text);
	});
	
	$('.inter-item').each(function(){
		var text = $(this).text();
		$(this).attr('title', text);
	});
	*/
	
	$("#moduleTree").jstree({
		"core" : {
			"animation" : "fast"
		},
		"themes" : {
			"theme" : "default",
			"url" : "/js/common/jsTree/themes/default/style.css",
			"dots" : true,
			"icons" : false
		},
		"plugins" : [ "themes", "html_data", "ui", "crrm"]
	});
 	//点击jstree中文字展开、折叠相应
 	$('.module-item').click(function(){
		var parent = $(this).parent();
		if(parent.hasClass("jstree-closed")){
			parent.removeClass("jstree-closed");
			parent.addClass("jstree-open");
 		}else{
			parent.removeClass("jstree-open");
			parent.addClass("jstree-closed");
 		}
 	});
 	$('.page-item').click(function(){
		var parent = $(this).parent();
		if(parent.hasClass("jstree-closed")){
			parent.removeClass("jstree-closed");
			parent.addClass("jstree-open");
 		}else{
			parent.removeClass("jstree-open");
			parent.addClass("jstree-closed");
 		}
 	});
 	
	// 展开所有和折叠所有
	var expandAllHtml = '<button type="button" id="expandAll" title="展开所有" class="btn btn-xs btn-primary"><i class="glyphicon glyphicon-plus"></i></button>';
	$('#collapseTree').html(expandAllHtml);
	
	$('#collapseTree').delegate('#expandAll', 'click', function(){
		$("#moduleTree").jstree("open_all");
		var collapeAllHtml = '<button type="button" id="collapseAll" title="收起所有" class="btn btn-xs btn-primary"><i class="glyphicon glyphicon-minus"></i></button>';
		$('#collapseTree').html(collapeAllHtml);
	});
	$('#collapseTree').delegate('#collapseAll', 'click',function(){
		$("#moduleTree").jstree("close_all");
		$('#collapseTree').html(expandAllHtml);
	});
	
	//搜索参数有不为空，表示是搜索结果，全部展开
	if(($("#search_responsible").val() != "") || ($("#search_interName").val() != "") || ($("#search_interUrl").val() != "") || ($("#search_requestType").val() != "")){
		$("#moduleTree").jstree("open_all");
		var collapeAllHtml = '<button type="button" id="collapseAll" title="收起所有" class="btn btn-xs btn-primary"><i class="glyphicon glyphicon-minus"></i></button>';
		$('#collapseTree').html(collapeAllHtml);
	}else if($('#interId').val() != ''){ //表示在显示某个接口
		var interfaceId = $('#interId').val();
		var inter_li = $("#inter-" + interfaceId);
		if(inter_li.length > 0){
			var inter_li_a = inter_li.children("a");
			inter_li_a.addClass("jstree-clicked");
			inter_li.parent().parent().removeClass("jstree-closed").addClass("jstree-open");
			inter_li.parent().parent().parent().parent().removeClass("jstree-closed").addClass("jstree-open");
		}
	}else{     //搜索参数都为空且interId不为空，表示不是搜索结果，展开最后一个
		if($('.module-item').length > 0){
			var parent = $('.module-item:last').parent();
			parent.removeClass("jstree-closed");
			parent.addClass("jstree-open");
			var chi = $('.page-item');
			chi.each(function(){
				var parent = $(this).parent();
				parent.removeClass("jstree-closed");
				parent.addClass("jstree-open");
			  });
		}
	}
	
	getProductUsers();
	
	if($('#interId').val() != ''){
		var interfaceId = $('#interId').val();
		initInterfaceInfo(interfaceId);
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
						
						var projectId = $('#projectId').val().trim();
						
						var data = {};
						data.projectId = projectId;
						data.moduleName = moduleName;
						
						$.ajax({
							url:'/idoc/module/add.html',
							type: 'POST',
							data: data,
							dataType: 'json',
							success: function(json){
								if(json.retCode == 200){
									var module = json.module;
									var moduleNameDisplay = module.moduleName.length > moduleNameLengthToShow ? module.moduleName.substring(0, moduleNameLengthToShow) + '...' : module.moduleName;
									var moduleAttr = module.moduleName.length > moduleNameLengthToShow ? {id:'module-' + module.moduleId, module:module.moduleId, class:'module', title: module.moduleName} : {id:'module-' + module.moduleId, module:module.moduleId, class:'module'};
									$('#moduleTree').jstree('create', -1, 'last', {attr: moduleAttr, data: moduleNameDisplay}, false, true);
									$('#module-' + module.moduleId).find('a').before('&nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffe66f"></i>');
									$('#module-' + module.moduleId).find('a').css('margin-left', '6px');
									$('#module-' + module.moduleId).append('<a href="#" class="edit-module" onclick="editModule(' + module.moduleId + ',' + module.moduleName +');" style="margin-left:5px; color:green;">&nbsp;<i class="glyphicon glyphicon-pencil"></i></a><a href="#" class="edit-module" onclick="deleteModule(' + module.moduleId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>');
								}else if (json.retCode == 402){
									showAlertMessage(json.retDesc);
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
		
		if(selectedNode.length == 0 || $(selectedNode).attr('id').indexOf('module')!=0){
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
						data.moduleId = moduleId;
						data.pageName = pageName;
						
						$.ajax({
							url: '/idoc/page/add.html',
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									var page = json.page;
									var pageNameDisplay = page.pageName.length > pageNameLengthToShow ? page.pageName.substring(0, pageNameLengthToShow) + '...' : page.pageName;
									var pageAttr = page.pageName.length > pageNameLengthToShow ? {id:'page-' + page.pageId, page:page.pageId, class:'page', title: page.pageName} : {id:'page-' + page.pageId, page:page.pageId, class:'page'};
									$('#moduleTree').jstree('create', null, 'last', {attr: pageAttr, data: pageNameDisplay}, false, true);
									$('#page-' + page.pageId).find('a').before('&nbsp;<i class="glyphicon glyphicon-folder-open" style="color: #ffc78e"></i>');
									$('#page-' + page.pageId).find('a').css('margin-left', '6px');
									$('#page-' + page.pageId).append('<a href="#" class="edit-page" onclick="editPage(' + page.pageId + ',' + page.pageName +');">&nbsp;<i class="glyphicon glyphicon-pencil" style="margin-left:5px; color:green;"></i></a><a href="#" class="edit-page" onclick="deletePage(' + page.pageId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>');
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
	//导入在线接口
	$("#importOnlineInterface").click(function(){
		data = {};
		data.productId = $("#productId").val();
		$.ajax({
			url: '/idoc/onlineInter/queryOnlineInters.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode==200){
					moduleList = json.retContent;
					bootbox.dialog({
						title:'请选择要导入的在线接口',
						message : '<div class="row form-horizontal">'
							+ '<div class="form-group">'
							+ '<label class="col-lg-2 control-label">所属模块:</label>'
							+ '<div class="col-lg-9">'
							+ '<select id="importModule" onChange="changeOnlineModule()" class="form-control chosen-select">'
							+ '</select>'
							+ '</div>'
							+ '</div>'
							+ '<div class="form-group">'
							+ '<label class="col-lg-2 control-label">所属页面:</label>'
							+ '<div class="col-lg-9">'
							+ '<select id="importPage" onChange="changeOnlinePage()" class="form-control chosen-select">'
							+ '</select>'
							+ '</div>'
							+ '</div>'
							+ '<div class="form-group">'
							+ '<label class="col-lg-2 control-label">接口名称:</label>'
							+ '<div class="col-lg-9">'
							+ '<select id="importInterface" class="form-control chosen-select">'
							+ '</select>'
							+ '</div>'
							+ '</div>'
							+ '</div>',
						locale : "zh_CN",
						buttons:{
							BatchImport:{
								label: '批量导入',
								className: 'btn-success',
								callback: function(){
									batchImportOnlineInterfaces();
								}
							},
							Ok:{
								label: '单个导入',
								className: 'btn-primary',
								callback: function(){
									importOnlineInterfaces();
								}
							},
							Cancel:{
								label : '取消',
								className: 'btn-warning'
							}
						}
					});
					var retContent = json.retContent;
					var len = retContent.length;
					var optionStr = "<option value=\"\">-请选择模块-</option>";
					for(var i=0; i<len; i++){
						var module = retContent[i];
						optionStr += "<option value=\""+module.onlineModuleId+"\">"+module.moduleName+"</option>";
					}
					$("#importModule").append(optionStr);
				}else{
					bootbox.alert("暂无可供导入的在线接口！");
				}
			}
		});
	});
	$('#addInterface').click(function(){
		//$("#url").val($("#productDomainUrl").val());
		var selectedNode = $('#moduleTree').jstree('get_selected');
		
		if(selectedNode.length == 0 || $(selectedNode).attr('id').indexOf('page')!=0){
			showAlertMessage('请选择需要添加接口的页面');
			return;
		}
		isEditing=1;
		$("input[name='redis'][value='0']").prop("checked",true);
		$("#newRedis").css("display","none");
		$("#showRedis").addClass("hidden");
		$("#addOneRedis").addClass("hidden");
		$("#table_redis tbody").empty();
		// 设置接口页面可编辑
		setInterfaceInfoReadonly(false);
		$('.chosen-select').chosen('destroy');
		setTimeout(function(){
			$(".chosen-select").chosen();
		}, 200);
		setMultiSelectWhenEdit();
		param.setEditMode(true);
		// 设置接口状态显示为编辑中
		for(var i = 1; i <= 9; i++){
			var id = '#s' + i;
			if($(id).hasClass("complete")){
				$(id).removeClass("complete");
			}
		}
		$("#s310").removeClass("complete");
		$("#s301").removeClass("complete");
		var pageId = $(selectedNode).attr('page');
		var data = {};
		data.pageId = pageId;
		$.ajax({
			url: '/idoc/inter/add.html',
			type: 'GET',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					var inter = json.inter;
					_interObj = inter;
					$('#hint').css({'display': 'none'});
					$('#interMain').css({'display': ''});
					var headBtnsHtml = '<button class="btn btn-success btn-sm" id="saveForNow">暂存</button>&nbsp;&nbsp;' +
									'<button class="btn btn-success btn-sm" id="save">保存</button>&nbsp;&nbsp;' +
									'<button class="btn btn-warning btn-sm" id="cancel">取消</button>';
					
					$('#interfaceHistoryVersion').html('');
					$('#headBtns').html(headBtnsHtml);
					$('#footBtns').html(headBtnsHtml);
					$('#interId').val('');
					$("#creatorId").val(_interObj.creatorId);
					$('.edit-field').val('');
					$('#inter_ftl_template').val('');
					_editor.setData('');
					// 设置创建接口时默认接口类型为ajax
					$("#interType").val("1");
					changeInterType();
					var statusStr = getStatus(inter.interfaceStatus);
					$('#lb_interStatus').text(statusStr);
					$('#lb_creator').text(inter.creator.userName);
					$('#lb_createTime').text(getDate(inter.createTime));
					$('#lb_editVersion').text(1);
					$('#lb_iterVersion').text(1);
					$('#url').val($('#productDomainUrl').val() + "/" + inter.createTime);
					
					$('#isNeedInterfaceTest').val(1);
					$('#isNeedInterfaceTest').change();
					
					$('#mockReturnExample').text('');
					$('#retParamExample').text('');
					$('#mockRet').css('display', 'none');
					
					$('#isNeedPressureTest').val(0);
					$('#isNeedPressureTest').change();
					
					if($('#reqPeople_chosen').length > 0){
						$('.chosen-select').chosen('destroy');
					}
					$('.chosen-select option').removeAttr('selected');
					
//					var reqPeople = $('#reqPeople');
//					var frontPeople = $('#frontPeople');
//					var behindPeople = $('#behindPeople');
//					var clientPeople = $('#clientPeople');
//					var testPeople = $('#testPeople');
//					
//					for(var i = 0; inter.reqPeoples && i < inter.reqPeoples.length; i++){
//						var user = inter.reqPeoples[i];
//						for(j = 0; reqPeople[0].options && j < reqPeople[0].options.length; j++){
//							if(user.userId == reqPeople[0].options[j].value){
//								reqPeople[0].options[j].selected = 'selected';
//								break;
//							}
//						}
//					}
//					
//					for(var i = 0; inter.frontPeoples && i < inter.frontPeoples.length; i++){
//						var user = inter.frontPeoples[i];
//						for(j = 0; frontPeople[0].options && j < frontPeople[0].options.length; j++){
//							if(user.userId == frontPeople[0].options[j].value){
//								frontPeople[0].options[j].selected = 'selected';
//								break;
//							}
//						}
//					}
//					
//					for(var i = 0; inter.behindPeoples && i < inter.behindPeoples.length; i++){
//						var user = inter.behindPeoples[i];
//						for(j = 0; behindPeople[0].options && j < behindPeople[0].options.length; j++){
//							if(user.userId == behindPeople[0].options[j].value){
//								behindPeople[0].options[j].selected = 'selected';
//								break;
//							}
//						}
//					}
//					
//					for(var i = 0; inter.clientPeoples && i < inter.clientPeoples.length; i++){
//						var user = inter.clientPeoples[i];
//						for(j = 0; clientPeople[0].options && j < clientPeople[0].options.length; j++){
//							if(user.userId == clientPeople[0].options[j].value){
//								clientPeople[0].options[j].selected = 'selected';
//								break;
//							}
//						}
//					}
//					
//					for(var i = 0; inter.testPeoples && i < inter.testPeoples.length; i++){
//						var user = inter.testPeoples[i];
//						for(j = 0; testPeople[0].options && j < testPeople[0].options.length; j++){
//							if(user.userId == testPeople[0].options[j].value){
//								testPeople[0].options[j].selected = 'selected';
//								break;
//							}
//						}
//					}
					setTimeout('$(".chosen-select").chosen();', 200);
					
					param.init(inter.reqParams, inter.retParams, true);
				}else{
					showAlertMessage('操作失败');
				}
			},
			error: function(){
				showAlertMessage('请检查网络是否连接。');
			}
		});
	});
	
	$("#assign").click(function(){
		var responsible = $("#currUserName").val();
		$("#responseInput").val(responsible);
		$("#searchForm").submit();
		//$("#responseInput").val("");
	});
	
	$(".radioItem").change(function(){
		var selectedValue = $("input[name='redis']:checked").val();
		if(selectedValue==0){
			$("#showRedis").addClass("hidden");
			$("#addOneRedis").addClass("hidden");
			$("#table_redis tbody").empty();
			if($("#newRedis").css("display") != "none"){
				$("#newRedis").css("display","none");
			}
		}else{
			$("#addOneRedis").removeClass("hidden");
			addOneRedis();
			if($("#newRedis").css("display")=="none"){
				$("#newRedis").toggle(800);
			}
		}
	});
	
	$("#showRedis").click(function(){
		$("#newRedis").toggle(800);
		if($(":radio").attr("disabled")!="disabled"){
			$("#addOneRedis").removeClass("hidden");
		}
	});
	
	$('#mockData').click(function(){
		_interObj.reqParams = param.getReqParam();
		_interObj.retParams = param.getRetParam();
		
		$.ajax({
			url: '/idoc/mock/queryMockData.html',
			type: 'POST',
			dataType: 'json',
			contentType: "application/json",
			data: JSON.stringify(_interObj),
			success: function(json){
				if(json.retCode == 200){
					/*
					//请求参数Mock
					var reqMockRuleObj = eval('(' + json.reqMockData + ')');
					var reqMockDataObj = Mock.mock(reqMockRuleObj);
					
					var reqMockRuleStr = JSON.stringify(reqMockRuleObj, null, 4);
					var reqMockDataStr = JSON.stringify(reqMockDataObj, null, 4);
					//用于生成postman文件的两个id
					_interObj.uuid1 = json.uuid1;
					_interObj.uuid2 = json.uuid2;
					*/
					//返回参数Mock
					var mockRuleObj = eval('(' + json.mockData + ')');
					var mockDataObj  = Mock.mock(mockRuleObj);
					
					var mockRuleStr = JSON.stringify(mockRuleObj, null, 4);
					var mockDataStr;
					if($('#interType').val() == 1 || $('#interType').val() == 3 || $('#interType').val() == 4){
						mockDataStr = JSON.stringify(mockDataObj, null, 4);
					}else if($('#interType').val() == 2){
						mockDataStr = generateFtlFakeData(mockDataObj);
					}else{
						showAlertMessage('请选择接口类型。');
						return;
					}
					
					var removeStr = "";
					if(json.mockType == "person"){
						removeStr = '<button id="removeUserMockData" class="btn btn-primary btn-xs"><i class="glyphicon glyphicon-remove"></i>&nbsp;删除个人规则</button>&nbsp;&nbsp';
					}
					
					bootbox.dialog({
						title: 'Mock数据预览',
						message: '<div class="row" id="mockDataForm">' +
										'<div class="form col-sm-12">' +
										'<div class="form-group">' +
											'<h4><strong>MockJs规则</strong></h4>' +
											'<textarea id="mockRuleTxt" class="form-control" style="height:250px;">' +
										    	mockRuleStr +
										    '</textarea>' +
										'</div>' +
										'<div class="form-group">' +
											'<h4 id="mockH4"><strong>Mock数据</strong>&nbsp;&nbsp;' +
												'<button id="refreshMockData" class="btn btn-success btn-xs"><i class="fa fa-refresh"></i>&nbsp;刷新</button>&nbsp&nbsp;' +
												'<button id="copyMockData" class="btn btn-info btn-xs"><i class="fa fa-copy"></i>&nbsp;复制</button>&nbsp;&nbsp' +
												'<button id="downloadMockData" class="btn btn-primary btn-xs"><i class="fa fa-download"></i>&nbsp;下载</button>&nbsp;&nbsp' +
												'<button id="saveUserMockData" class="btn btn-primary btn-xs"><i class="glyphicon glyphicon-saved"></i>&nbsp;保存个人规则</button>&nbsp;&nbsp' +
												removeStr +
											'</h4>' +
											'<textarea id="mockDataTxt" class="form-control" style="height:250px;">' +
										    	mockDataStr +
										    '</textarea>' +
										'</div>' +
									'</div>' +
								'</div>',
						buttons:{
							cancel:{
								label: '关闭',
								className: 'btn-default'
							}
					
						},
						callback: function(){
							setTimeout(function(){
								$($('.modal-dialog')[0]).css({'width': '900px'});
							}, 200);
						}
					});
				}else{
					showAlertMessage('获取Mock数据失败。');
				}
			}
		});
	});
	
	$('#mockHttp').click(function(){
		$.ajax({
			url: '/idoc/mock/queryReqMockData.html',
			type: 'POST',
			dataType: 'json',
			contentType: "application/json",
			data: JSON.stringify(_interObj),
			success: function(json){
				if(json.retCode == 200){
					//请求参数Mock
					var reqMockRuleObj = eval('(' + json.reqMockData + ')');
					var reqMockDataObj = Mock.mock(reqMockRuleObj);
					
					_interObj.reqMockDataObj = reqMockDataObj;
					
					var reqMockRuleStr = JSON.stringify(reqMockRuleObj, null, 4);
					var reqMockDataStr = JSON.stringify(reqMockDataObj, null, 4);
					//用于生成postman文件的两个id
					_interObj.uuid1 = json.uuid1;
					_interObj.uuid2 = json.uuid2;
					
					var requestTypeStr = $('#requestType option:selected').text();
					var dataModeStr = '';
					if($('#requestType').val() > 1){
						dataModeStr = '<div class="form-group">' +
									  	'<label class="col-sm-3 control-label">请求数据模式</label>' +
									  	'<div class="col-sm-9">' +
									  		'<label class="radio-inline">' +
									  			'<input type="radio" name="dataMode" value="urlencoded" checked>x-www-from-urlencoded' +
									  		'</label>' +
									  		'<label class="radio-inline">' +
									  			'<input type="radio" name="dataMode" value="raw">raw' +
									  		'</label>' +
									  	'</div>' +
									  '</div>';
	
					}
						
					
					bootbox.dialog({
						title: '模拟Http请求',
						message: '<div class="alert alert-success">' +
								 	'模拟Http请求的功能主要是通过生成可以在Postman导入的文件，然后通过Postman来实现模拟Http请求的功能。' +
								 	'Chrome浏览器插件Postman下载 <a href="https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=zh-CN" class="alert-link">请点击这里</a>' +
								 '</div>' +
								 '<div class="row">' +
								 	'<div class="form-horizontal col-sm-12">' +
									 	'<div class="form-group">' +
											'<label class="col-sm-3 control-label">协议类型</label>' +
											'<div class="col-sm-9">' +
									    		'<label class="control-label" id="lb_interStatus">' + requestTypeStr +'</label>' +
										    '</div>' +
									    '</div>' +
									    dataModeStr +
								 	'</div>' +
								 '</div>',
						buttons: {
							downloadPostman: {
								label: '下载Postman文件',
								className: 'btn-primary',
								callback: function(){
									var postmanObj = createPostmanObj(_interObj.uuid1, _interObj.uuid2, _interObj.reqMockDataObj);
									var postmanStr = JSON.stringify(postmanObj, null, 4);
									var interName = $('#interName').val().trim();
									saveAs(new Blob([postmanStr], {type: "text/plain;charset=" + document.characterSet}), interName + '_postman.json');
								}
							},
							cancel: {
								label: '关闭',
								className: 'btn-default'
							}
						}
					});
				}else{
					showAlertMessage('获取模拟Http请求相关数据出错。');
				}
			}
		});
	});
	
	$('#autoGenerateInterTestCode').click(function (){
		var interfaceId = $('#interId').val();
		if(interfaceId === ''){
			showAlertMessage('接口id不存在，重新登录系统试试!');
			return;
		}
		var data = {};
		data.interfaceId = interfaceId;
		$.ajax({
			url: '/idoc/inter/autoGenerateInterTestCodePage.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					var code = json.code;
					bootbox.dialog({
						title: '自动生成接口测试代码预览',
						message: '<div class="row" id="autoGenerateCodeForm">' +
										'<div class="form col-sm-12">' +
										'<textarea class="hidden" id="codeContent">' + code + '</textarea>' + 
										'<div class="form-group">' +
											'<h4><strong>接口测试代码</strong> &nbsp;&nbsp;&nbsp;' +
												'<button id="showTip" class="btn btn-success btn-xs"><i class="fa fa-bell"></i>&nbsp;提示&nbsp;</button>&nbsp;&nbsp' +
												'<button id="copyCode" class="btn btn-info btn-xs"><i class="fa fa-copy"></i>&nbsp;复制&nbsp;</button>&nbsp;&nbsp' +
												'<button id="downloadCode" class="btn btn-primary btn-xs"><i class="fa fa-download"></i>&nbsp;下载&nbsp;</button>' +
											'</h4>' +
											'<pre id="codeText" class="line-numbers" style="height:650px;" contenteditable="true"><code class="language-java">' +
									    		code +
										    '</code></pre>' +
										'</div>' +
									'</div>' +
								'</div>',
						buttons:{
							cancel:{
								label: '关闭',
								className: 'btn-warning'
							}
						},
						callback: function(){
							setTimeout(function(){
								$($('.modal-dialog')[0]).css({'width': '960px'});
							}, 200);
							setTimeout(function(){bind_zclip();}, 500);
							if(!isNullOrEmpty(code)){
								setTimeout(function(){Prism.highlightAll(true);}, 100);
							}
						}
					});
				}else{
					showAlertMessage('自动生成测试代码出错。');
				}
			}
		});
	});

	function bind_zclip(){
	    $('#copyCode').zclip({
	        path: "/js/common/zclip/ZeroClipboard.swf",
	        copy: function(){return $('#codeContent').val();},
	        afterCopy: function() { $().toastmessage('showSuccessToast', '代码已经复制到粘贴板!'); }
	    });
	}
	
	$('body').delegate('#showTip', 'click', function(){
		bootbox.dialog({
			title: '使用说明：',
			message: '<div class="row" id="autoGenerateCodeForm">' +
						'<div class="form col-sm-12">' +
							'<div class="alert alert-block alert-info">' +
								'&nbsp;&nbsp;&nbsp;&nbsp;本代码是系统根据接口信息自动生成的测试框架，代码还不能直接运行，可以通过修改添加逻辑进行接口测试。具体修改过程如下：<br>' +
								'<i class="glyphicon glyphicon-hand-right" style="color:#00ec00;"></i> &nbsp;&nbsp;&nbsp;' +
								'1. 修改测试类AutoInterfaceSmokeTest包名为你的测试工程包结构下。<br>' +
								'<i class="glyphicon glyphicon-hand-right" style="color:#00ec00;"></i> &nbsp;&nbsp;&nbsp;' +
								'2. 如果接口需要登录验证，可将程序中的SessionID用自己登录后的SessionID替换，你可<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以用自己账号登录后在浏览器调试工具请求Cookie中看到SessionID。<br>' +
								'<i class="glyphicon glyphicon-hand-right" style="color:#00ec00;"></i> &nbsp;&nbsp;&nbsp;' +
								'3. DataListProvider类是用于提供测试数据的类，需要将其拷贝到一个新的java类中保存。<br>' +
								'<i class="glyphicon glyphicon-hand-right" style="color:#00ec00;"></i> &nbsp;&nbsp;&nbsp;' +
								'4. 在AutoInterfaceSmokeTest类中引入DataListProvider的包名。<br>' +
								'<i class="glyphicon glyphicon-hand-right" style="color:#00ec00;"></i> &nbsp;&nbsp;&nbsp;' +
								'5. 如果有其他逻辑代码可以在类中添加测试。' +
							'</div> ' +
							'<div class="alert alert-block alert-danger">' +
								'注意：本测试代码需要ShockwaveTestNG支持，请确保测试环境已经引入相关文件！' +
							'</div' +
					    '</div>' +
					  '</div>',
			buttons:{
				cancel:{
					label: '关闭',
					className: 'btn-primary'
				}
			},
			callback: function(){
				setTimeout(function(){
					$($('.modal-dialog')[1]).css({'width': '660px'});
				}, 200);
			}
		});
	}).delegate('#downloadCode', 'click', function(){
		var fileType = '.java';
		var content = $('#codeContent').val();
		saveAs(new Blob([content], {type: "text/plain;charset=" + document.characterSet}), 'AutoGenerateSmokeTestCode' + fileType);
	});
	
	$('#downloadAutoCode').click(function (){
		var interfaceId = $('#interId').val();
		if(interfaceId === ''){
			showAlertMessage('接口id不存在，重新登录系统试试!');
			return;
		}
		window.open('/idoc/inter/autoGenerateInterTestCode.html?interfaceId=' + interfaceId + '&createFileOnServer=false');
	});
	
	$('#testerLink').click(function (){
		var interfaceId = $('#interId').val();
		var projectId = $('#projectId').val();
		if(interfaceId === ''){
			showAlertMessage('请先进行保存操作。');
			return;
		}
		
		window.open('/idoc/tester/index.html?interfaceId=' + interfaceId + '&projectId=' + projectId);
	});
	
	$('body').delegate('#refreshMockData', 'click', function(){
		var mockRuleObj = eval('(' + $('#mockRuleTxt').val() + ')');
		var mockDataObj  = Mock.mock(mockRuleObj);
		
		var mockDataStr;
		if($('#interType').val() == 1){
			mockDataStr = JSON.stringify(mockDataObj, null, 4);
		}else if($('#interType').val() == 2){
			mockDataStr = generateFtlFakeData(mockDataObj);
		}
		
		$('#mockDataTxt').text(mockDataStr);
	}).delegate('#copyMockData', 'click', function(){
		($('#mockDataTxt')[0]).select();
		try {
            document.execCommand('copy');
            $().toastmessage('showSuccessToast', 'Mock数据已经复制到粘贴板');
        } catch (err) {
            console.log('Oops, unable to copy');
            $().toastmessage('showWarningToast', '对不起，当前浏览器不支持复制功能');
        }
	}).delegate('#downloadMockData', 'click', function(){
		var fileType = '.json';
		if($('#interType').val() == 2){
			fileType = '.ftl';
		}
		var content = $('#mockDataTxt').text();
		saveAs(new Blob([content], {type: "text/plain;charset=" + document.characterSet}), 'fakeData' + fileType);
	}).delegate('#saveUserMockData', 'click', function(){
		var data = {};
		var interfaceId = $('#interId').val();
		if(interfaceId == ""){
			$().toastmessage('showWarningToast', '请先保存接口');
			return;
		}
		var mockRule = $('#mockRuleTxt').val();
		data.interfaceId = interfaceId;
		data.mockRule = mockRule;
		$.ajax({
			url : '/idoc/mock/saveUserMockRule.html',
			type : 'POST',
			dataType : 'json',
			data : data,
			success : function(json) {
				if (json.retCode == 200) {
					$().toastmessage('showSuccessToast', '保存个人mock规则成功');
					var eee = $("#removeUserMockData");
					if(eee.length == 0){
						$("#mockH4").append('<button id="removeUserMockData" class="btn btn-primary btn-xs"><i class="glyphicon glyphicon-remove"></i>&nbsp;删除个人规则</button>&nbsp;&nbsp');
					}
				} else {
					$().toastmessage('showWarningToast', '对不起，保存失败');
				}
			}
		});
	}).delegate('#removeUserMockData', 'click', function(){
		var data = {};
		var interfaceId = $('#interId').val();
		if(interfaceId == ""){
			$().toastmessage('showWarningToast', '尚未保存个人规则');
			return;
		}
		data.interfaceId = interfaceId;
		$.ajax({
			url : '/idoc/mock/removeUserMockRule.html',
			type : 'POST',
			dataType : 'json',
			data : data,
			success : function(json) {
				if (json.retCode == 200) {
					$().toastmessage('showSuccessToast', '删除个人mock规则成功');
					$("#removeUserMockData").remove();
					var mockRuleObj = eval('(' + json.mockRule + ')');
					var mockRuleStr = JSON.stringify(mockRuleObj, null, 4);
					$('#mockRuleTxt').text(mockRuleStr);
					var mockRuleObj = eval('(' + $('#mockRuleTxt').val() + ')');
					var mockDataObj  = Mock.mock(mockRuleObj);
					
					var mockDataStr;
					if($('#interType').val() == 1){
						mockDataStr = JSON.stringify(mockDataObj, null, 4);
					}else if($('#interType').val() == 2){
						mockDataStr = generateFtlFakeData(mockDataObj);
					}
					
					$('#mockDataTxt').text(mockDataStr);
				} else {
					$().toastmessage('showWarningToast', '对不起，删除失败');
				}
			}
		});
	}).delegate('#refreshReqMockData', 'click', function(){
		var reqMockRuleObj = eval('(' + $('#reqMockRuleTxt').val() + ')');
		var reqMockDataObj  = Mock.mock(reqMockRuleObj);
		
		var reqMockDataStr = JSON.stringify(reqMockDataObj, null, 4);
		$('#reqMockDataTxt').text(reqMockDataStr);
	}).delegate('#copyReqMockData', 'click', function(){
		($('#reqMockDataTxt')[0]).select();
		try {
            document.execCommand('copy');
            $().toastmessage('showSuccessToast', 'Mock数据已经复制到粘贴板');
        } catch (err) {
            console.log('Oops, unable to copy');
            $().toastmessage('showWarningToast', '对不起，当前浏览器不支持复制功能');
        }
	}).delegate('#downloadPostman', 'click', function(){
		var requestType = $('#requestType').val();
		if(requestType > 1){
			bootbox.dialog({
				title: '选择请求数据格式',
				message: '<div class="row form-horizontal">' +
							'<div class="form-group">' +
								'<label class="col-lg-2 control-label">参数模式</label>' +
								'<div class="col-lg-9">' +
									'<select class="form-control" id="dataMode">' +
										'<option value="urlencoded">x-www-from-urlencoded</option>' +
										'<option value="raw">raw</option>' +
									'</select>' +
								'</div>' +
							'</div>' +
						 '</div>',
				buttons:{
					ok:{
						label: '确定',
						className: 'btn-primary',
						callback: function(){
							var reqMockRuleObj = eval('(' + $('#reqMockRuleTxt').val() + ')');
							var reqMockDataObj  = Mock.mock(reqMockRuleObj);
							
							var postmanObj = createPostmanObj(_interObj.uuid1, _interObj.uuid2, reqMockDataObj);
							var postmanStr = JSON.stringify(postmanObj, null, 4);
							var interName = $('#interName').val().trim();
							saveAs(new Blob([postmanStr], {type: "text/plain;charset=" + document.characterSet}), interName + '_postman.json');
						}
					},
					cancel:{
						label: '关闭',
						className: 'btn-default'
					}
				},
				callback: function(){
					setTimeout(function(){
						$($('.modal-dialog')[1]).css({'top': '240px'});
					}, 200);
				}
			});
		}else{
			var reqMockRuleObj = eval('(' + $('#reqMockRuleTxt').val() + ')');
			var reqMockDataObj  = Mock.mock(reqMockRuleObj);
			
			var postmanObj = createPostmanObj(_interObj.uuid1, _interObj.uuid2, reqMockDataObj);
			var postmanStr = JSON.stringify(postmanObj, null, 4);
			var interName = $('#interName').val().trim();
			saveAs(new Blob([postmanStr], {type: "text/plain;charset=" + document.characterSet}), interName + '_postman.json');
		}
	});
	
	$('#headBtns').delegate('#saveForNow', 'click', function(){
		interfaceSave(0);
		isEditing=0;
	});
	$('#footBtns').delegate('#saveForNow', 'click', function(){
		interfaceSave(0);
		isEditing=0;
	});
	
	$('#headBtns').delegate('#save', 'click', function(){
		interfaceSave(1);
		isEditing=0;
	});
	$('#footBtns').delegate('#save', 'click', function(){
		interfaceSave(1);
		isEditing=0;
	});
	
	function interfaceSave(flag){
		var interName = $('#interName').val().trim();
		if(interName == ''){
			showAlertMessage('请填写接口名称。');
			return;
		}
		var interType= $('#interType').val().trim();
		if(interType == ''){
			showAlertMessage('请选择接口类型。');
			return;
		}
		var requestType = $('#requestType').val().trim();
		if(requestType == ''){
			showAlertMessage('请选择协议类型。');
			return;
		}
		var ftlTemplate = $("#inter_ftl_template").val();
		if(ftlTemplate){
			ftlTemplate = ftlTemplate.trim();
		}
		if(interType == '2' && ftlTemplate == '') {
			showAlertMessage('ftl类型接口，ftl模板名称不能为空');
			return;
		}
		var url = $('#url').val().trim();
		if(url == ''){
			showAlertMessage('请填写请求url');
			return;
		}
		var desc = CKEDITOR.instances.desc.getData();
		var reqParam = param.getReqParam();
		var retParam = param.getRetParam();
		// 判断请求参数和返回参数中是否有同名
		var paramSet = {};
		for(var i = 0; i < reqParam.length; i++){
			var p = reqParam[i];
			if(p.paramName in paramSet){
				showAlertMessage('接口的请求参数中存在同名的参数' + p.paramName + '，请修改后提交！');
				return;
			}
			paramSet[p.paramName] = p;
		}
		paramSet = {};
		for(var i = 0;  i < retParam.length; i++){
			var p = retParam[i];
			if(p.paramName in paramSet){
				showAlertMessage('接口的返回参数中存在同名的参数' + p.paramName + '，请修改后提交！');
				return;
			}
			paramSet[p.paramName] = p;
		}
		
		var isNeedTest = $('#isNeedInterfaceTest').val().trim();
		if(isNeedTest == ''){
			showAlertMessage('请选择是否需要测试');
			return;
		}
		var isNeedPressureTest = $('#isNeedPressureTest').val().trim();
		if(isNeedPressureTest == ''){
			showAlertMessage('请选择是否需要压力测试');
			return;
		}
		
		var isUseRedis = $("input[name='redis']:checked").val();
		var redisList = [];
		if(isUseRedis == 1){
			var trs = $("#table_redis tbody").find("tr");
			if(!trs){
				showAlertMessage("请添加缓存!");
				return;
			}else{
				var trCount = trs.length;
				for(var i=0;i<trCount;i++){
					var tr = trs.eq(i);
					var redisKey = tr.find("td").eq(0).find("input").val().trim();
					var redisInfo = tr.find("td").eq(1).find("input").val().trim();
					var redisTactics = tr.find("td").eq(2).find("input").val().trim();
					var redisId = tr.find("td").eq(3).find("input").val().trim();
					if(redisKey=="" || redisInfo=="" || redisTactics==""){
						showAlertMessage("请填写缓存信息!");
						redisList = null;
						return;
					}else{
						var redisObj = new Object();
						if(redisId!=""){
							redisObj.redisId = redisId;
						}
						redisObj.redisKey = redisKey;
						redisObj.redisInfo = redisInfo;
						redisObj.redisTactics = redisTactics;
						redisList.push(redisObj);
					}
				}
			}
		}else{
			redisList = null;
		}
		
		var expectTestTime = $('#expectTestTime').val().trim();
		var expectOnlineTime = $('#expectOnlineTime').val().trim();
		var reqPeople = $('#reqPeople').val();
		var behindPeople = $('#behindPeople').val();
		var frontPeople = $('#frontPeople').val();
		var clientPeople = $('#clientPeople').val();
		var testPeople = $('#testPeople').val();
		if(flag == 1){//点击保存按钮
			if(isNeedTest == 1){ // 需要测试
				if(expectTestTime == ''){
					showAlertMessage('请选择预计提测时间。');
					return;
				}
			}
			if(expectOnlineTime == ''){
				showAlertMessage('请选择预计上线时间。');
				return;
			}
			if(!reqPeople){
				showAlertMessage('请选择需求人员。');
				return;
			}
			if(!behindPeople){
				showAlertMessage('请选择后台开发。');
				return;
			}
			if(!clientPeople && !frontPeople){
				showAlertMessage('前端开发和客户端开发不能同时为空！');
				return;
			}
			if(!testPeople){
				showAlertMessage('请选择测试人员。');
				return;
			}
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
		
		var saveInterObj = new Object();
		saveInterObj.pageId = _interObj.pageId;
		saveInterObj.creatorId = _interObj.creatorId;
		saveInterObj.flag = flag;
		saveInterObj.interfaceName = interName;
		saveInterObj.redisList = redisList;
		var oldStatus = $("#lb_interStatus").html();
		if(($('#interId').val() != ''&&flag==1&&oldStatus!="暂存中")||($('#interId').val() != ''&&flag==0)){
			//不是首次保存，将修改过的数据加入_interObj中进行保存
			var interfaceId = $('#interId').val();
			saveInterObj.interfaceId = interfaceId;
			if(interType != oldInterfaceType)
				saveInterObj.interfaceType = interType;
			if(requestType != oldRequestType)
				saveInterObj.requestType = requestType;
			if(ftlTemplate != oldFtlTemplate)
				saveInterObj.ftlTemplate = ftlTemplate;
			if(url != oldRequestUrl)
				saveInterObj.url = url;
			if(desc != oldInterfaceDesc)
				saveInterObj.desc = desc;
			//if(reqParam.toString() != oldReqParam)
				saveInterObj.reqParams = reqParam;
			//if(retParam != oldRetParam)
				saveInterObj.retParams = retParam;
			if($('#retParamExample').val().trim() != oldRetParamExample)
				saveInterObj.retParamExample = $('#retParamExample').val().trim();
			//if(isNeedTest != oldNeedInterfaceTest)
				saveInterObj.isNeedInterfaceTest = isNeedTest;
			if(isNeedPressureTest != oldNeedPressureTest)
				saveInterObj.isNeedPressureTest = isNeedPressureTest;
			if(isNeedTest==1 && expectTestTime && expectTestTime!= oldExpectTestTime)
				saveInterObj.expectTestTime = expectTestTime;
			if(expectOnlineTime && expectOnlineTime != oldExpectOnlineTime)
				saveInterObj.expectOnlineTime = expectOnlineTime;
			if(oldReqPeople==null || (reqPeople&&reqPeople.toString() != oldReqPeople.toString())){
				var reqPeopleStr = '';
				for(var i = 0; reqPeople&&i < reqPeople.length; i++){
					reqPeopleStr += reqPeople[i] + ',';
				}
				reqPeopleStr = reqPeopleStr.substring(0, reqPeopleStr.length - 1);
				saveInterObj.reqPeopleIds = reqPeopleStr;
			}else if(!reqPeople){
				saveInterObj.reqPeopleIds = "";
			}
			if(oldFrontPeople==null || (frontPeople && frontPeople.toString() != oldFrontPeople.toString())){
				var frontPeopleStr = '';
				if(frontPeople){
					for(var i = 0; i < frontPeople.length; i++){
						frontPeopleStr += frontPeople[i] + ',';
					}
					frontPeopleStr = frontPeopleStr.substring(0, frontPeopleStr.length - 1);
				}
				saveInterObj.frontPeopleIds = frontPeopleStr;
			}else if(!frontPeople){
				saveInterObj.frontPeopleIds = "";
			}
			if(oldBehindPeople==null || (behindPeople&&behindPeople.toString() != oldBehindPeople.toString())){
				var behindPeopleStr = '';
				for(var i = 0; behindPeople&&i < behindPeople.length; i++){
					behindPeopleStr += behindPeople[i] + ',';
				}
				behindPeopleStr = behindPeopleStr.substring(0, behindPeopleStr.length - 1);
				saveInterObj.behindPeopleIds = behindPeopleStr;
			}else if(!behindPeople){
				saveInterObj.behindPeopleIds = "";
			}
			if(oldClientPeople==null || (clientPeople && clientPeople.toString() != oldClientPeople.toString())){
				var clientPeopleStr = '';
				if(clientPeople){
					for(var i = 0; i < clientPeople.length; i++){
						clientPeopleStr += clientPeople[i] + ',';
					}
					clientPeopleStr = clientPeopleStr.substring(0, clientPeopleStr.length - 1);
				}
				saveInterObj.clientPeopleIds = clientPeopleStr;
			}else if(!clientPeople){
				saveInterObj.clientPeopleIds = "";
			}
			if(oldTestPeople==null || (testPeople&&testPeople.toString() != oldTestPeople.toString())){
				var testPeopleStr = '';
				for(var i = 0; testPeople&&i < testPeople.length; i++){
					testPeopleStr += testPeople[i] + ',';
				}
				testPeopleStr = testPeopleStr.substring(0, testPeopleStr.length - 1);
				saveInterObj.testPeopleIds = testPeopleStr;
			}else if(!testPeople){
				saveInterObj.testPeopleIds = "";
			}
		}else{
			if($('#interId').val() != ''){
				var interfaceId = $('#interId').val();
				saveInterObj.interfaceId = interfaceId;
			}
			//首次保存（新建的接口）
			saveInterObj.interfaceName = interName;
			saveInterObj.interfaceType = interType;
			saveInterObj.requestType = requestType;
			saveInterObj.ftlTemplate = ftlTemplate;
			saveInterObj.url = url;
			saveInterObj.desc = desc;
			saveInterObj.reqParams = reqParam;
			saveInterObj.retParams = retParam;
			saveInterObj.retParamExample = $('#retParamExample').val().trim();
			saveInterObj.isNeedInterfaceTest = isNeedTest;
			saveInterObj.isNeedPressureTest = isNeedPressureTest;
			if(isNeedTest == 1 && expectTestTime!="")
				saveInterObj.expectTestTime = expectTestTime;
			if(expectOnlineTime!="")
				saveInterObj.expectOnlineTime = expectOnlineTime;
			
			var reqPeopleStr1 = '';
			if(reqPeople){
				for(var i = 0; i < reqPeople.length; i++){
					reqPeopleStr1 += reqPeople[i] + ',';
				}
				reqPeopleStr1 = reqPeopleStr1.substring(0, reqPeopleStr1.length - 1);
				saveInterObj.reqPeopleIds = reqPeopleStr1;
			}
			
			var frontPeopleStr1 = '';
			if(frontPeople){
				for(var i = 0; i < frontPeople.length; i++){
					frontPeopleStr1 += frontPeople[i] + ',';
				}
				frontPeopleStr1 = frontPeopleStr1.substring(0, frontPeopleStr1.length - 1);
				saveInterObj.frontPeopleIds = frontPeopleStr1;
			}
			
			
			var behindPeopleStr1 = '';
			if(behindPeople){
				for(var i = 0; i < behindPeople.length; i++){
					behindPeopleStr1 += behindPeople[i] + ',';
				}
				behindPeopleStr1 = behindPeopleStr1.substring(0, behindPeopleStr1.length - 1);
				saveInterObj.behindPeopleIds = behindPeopleStr1;
			}
			
			
			var clientPeopleStr1 = '';
			if(clientPeople){
				for(var i = 0; i < clientPeople.length; i++){
					clientPeopleStr1 += clientPeople[i] + ',';
				}
				clientPeopleStr1 = clientPeopleStr1.substring(0, clientPeopleStr1.length - 1);
				saveInterObj.clientPeopleIds = clientPeopleStr1;
			}
			
			
			var testPeopleStr1 = '';
			if(testPeople){
				for(var i = 0; i < testPeople.length; i++){
					testPeopleStr1 += testPeople[i] + ',';
				}
				testPeopleStr1 = testPeopleStr1.substring(0, testPeopleStr1.length - 1);
				saveInterObj.testPeopleIds = testPeopleStr1;
			}
		}
		$.ajax({
			url: '/idoc/inter/save.html',
			type: 'POST',
			dataType: 'json',
			async: false,
			contentType: "application/json",
			data: JSON.stringify(saveInterObj),
			success: function(json){
				spinner.spin();
				if(json.retCode == 200){
					var inter = json.inter;
					var afterSaveRedis = json.redisList;
					if($('#interId').val() == ''){
						$('#interId').val(inter.interfaceId);
						var interfaceNameDisplay = inter.interfaceName.length > interfaceNameLengthToShow ? inter.interfaceName.substring(0, interfaceNameLengthToShow) + '...' : inter.interfaceName;
						var interfaceAttr = inter.interfaceName.length > interfaceNameLengthToShow ? {id:'inter-' + inter.interfaceId, inter:inter.interfaceId, class:'inter', title: inter.interfaceName} : {id:'inter-' + inter.interfaceId, inter:inter.interfaceId, class:'inter'};
						$('#moduleTree').jstree('create', null, 'last', {attr: interfaceAttr, data: interfaceNameDisplay}, false, true);
						$('#inter-' + inter.interfaceId + ' a').first().attr('onclick', 'initInterfaceInfo("' + inter.interfaceId + '", true);return false;');
						$('#inter-' + inter.interfaceId).find('a').before('&nbsp;<i class="glyphicon glyphicon-file" style="color: #ccff80"></i>');
						$('#inter-' + inter.interfaceId).find('a').css('margin-left', '6px');
						$('#inter-' + inter.interfaceId).append('<a href="#" class="edit-inter" onclick="deleteInterface(' + inter.interfaceId + ');" style="margin-left:5px;"><i class="glyphicon glyphicon-trash" style="color:red;"></i></a>');
						$('#moduleTree').jstree('deselect_all');
						$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
					}
					$('#isEditableInfor').empty();
					$('#lb_editVersion').text(inter.editVersion);
					$('#lb_iterVersion').text(inter.iterVersion);
					$('#lb_interStatus').css('color','red');
					$('#lb_interStatus').css('font-size',16);
					setInterfaceInfoReadonly(true);
					initInterfaceRedis(afterSaveRedis);
					refreshInterfaceInfo(inter.interfaceStatus);
					$('#interfaceHistoryVersion').html('');
					if(inter.editVersion > 1){
						$('#interfaceHistoryVersion').html('<a class="btn btn-primary btn-xs" href="interfaceHistoryVersion.html?interfaceName=' + encodeURI(encodeURI(inter.interfaceName)) + '&interfaceId=' + inter.interfaceId + '&productId=' + $('#productId').val().trim() + '&projectId=' + $('#projectId').val().trim() + '" target="_blank">历史版本</a>');
					}
					param.setEditMode(false);
					$('#footBtns').html('');
					
					$("#spinMessage").html('<font style="font-size:20px" color="green"><strong>保存成功!</strong></font>');
					var savingBtn = document.getElementsByClassName('savingBtn')[0];
					setTimeout(function(){
						savingBtn.click();
					}, 1000);
					// 保存后存储各角色的信息
					if(reqPeople){
						reqPeoples = reqPeople;
						oldReqPeople = reqPeople;
					}else{
						oldReqPeople = null;
					}
					if(frontPeople){
						frontPeoples = frontPeople;
						oldFrontPeople = frontPeople;
					}else{
						oldFrontPeople = null;
					}
					if(behindPeople){
						behindPeoples = behindPeople;
						oldBehindPeople = behindPeople;
					}else{
						oldBehindPeople = null;
					}
					if(clientPeople){
						clientPeoples = clientPeople;
						oldClientPeople = clientPeople;
					}else{
						oldClientPeople = null;
					}
					if(testPeople){
						testPeoples = testPeople;
						oldTestPeople = testPeople;
					}else{
						oldTestPeople = null;
					}
					var intername=$("#interName").val();
					$("#inter-"+interfaceId).children().eq(2).html(intername);
					//window.location.href="/blog/window.location.href";
				}else{
					$("#spinMessage").html('<font style="font-size:20px" color="red"><strong>保存失败!</strong></font>');
					var savingBtn = document.getElementsByClassName('savingBtn')[0];
					setTimeout(function(){
						savingBtn.click();
					}, 1000);
					//showAlertMessage('保存失败。失败原因: ' + json.retDesc);
				}
			}
		});
	}
	
	
	
	$('#headBtns').delegate('#cancel', 'click', function(){
		interfaceCancel();
		
	});
	$('#footBtns').delegate('#cancel', 'click', function(){
		interfaceCancel();
	});
	
	function interfaceCancel(){
		bootbox.dialog({
			message: '确定放弃编辑该接口？',
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						var interId = $('#interId').val().trim();
						var data = {};
						data.interfaceId = interId;
						$.ajax({
							url: '/idoc/inter/cancelEdit.html',
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									isEditing=0;
//									isReEditing=0;
									var status = json.status;
									$('#isEditableInfor').empty();
									setInterfaceInfoReadonly(true);
									refreshInterfaceInfo(status);
								}else if(json.retCode == 400){
									isEditing=0;
//									isReEditing=0;
									$('#hint').css({'display': ''});
									$('#interMain').css({'display': 'none'});
									$('#headBtns').html('');
									$('#footBtns').html('');
								}else{
									$('#isEditableInfor').html("释放该接口锁失败！");
								}
							}
						});
//						var productId=$('#productId').val().trim();
						
//						if(window.location.search.indexOf('&') > 0){
//							var position = "idoc/inter/index.html?projectId="+projectId+"&interfaceId="+interId; //这两句是页面不刷新的条件下修改浏览器的url，IE应该不适用
//							//window.history.pushState({},0,'http://'+window.location.host+'/'+position);
//							window.location.reload('http://'+window.location.host+'/'+position);
//						}
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-warning"
				}
			}
		});
	}
	
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
							url: '/idoc/inter/delete.html',
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
		var data = {};
		isEditing=1;
		data.interfaceId = $("#interId").val();
		$.ajax({
			url: '/idoc/inter/getEditPeople.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				var interId = $("#interId").val();
				var currUserName = $("#currUserName").val();
				if(json.retCode == 200){
					setEditPeople(interId,currUserName);
				}else{
					var editPeople = json.editPeople;
					if(currUserName == editPeople){
						setEditPeople(interId,currUserName);
					}else{
						var isEditableInforHtml = "当前接口已被"+editPeople+"锁定，请稍后编辑。";
						$("#isEditableInfor").html(isEditableInforHtml);
					}
				}
			}
		});
	});
	
	$('#headBtns').delegate('#audit', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=1){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var data = {};
		data.interfaceId = $("#interId").val();
		$.ajax({
			url: '/idoc/inter/getEditPeople.html',
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					var msg = '确定发起审核吗？';
					var url = '/idoc/flow/audit.html';
					var data1 = {};
					data1.interfaceId = $('#interId').val();
					data1.projectId = projectId;
					operationDialog(msg, url, data1, 2);
				}else{
					var editPeople = json.editPeople;
					var isEditableInforHtml = "当前接口"+editPeople+"正在编辑中，请稍后重试。";
					$("#isEditableInfor").html(isEditableInforHtml);
				}
			}
		});
		
	});
	
	$('#headBtns').delegate('#revise', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=2 && status!=310 && status!=301){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
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
		var status = getInterStatus($('#interId').val());
		if(status == 3){
			showAlertMessage("该接口已经审核通过了！");
			refreshInterfaceInfo(status);
			return;
		}
		if(status!=2 && status!=301 && status!=310){
			showAlertMessage("接口状态发生变化，请刷新页面。");
			refreshInterfaceInfo(status);
			return;
		}
		var url = '/idoc/flow/auditSuccess.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		var role = $('#role').val();
		var currUserId = $("#currUserId").val();
		//判断当前登录用户是否在前端开发人员中
		var frontIds = $('#frontPeople').val();
		var frontFlag = 0;
		var frontIsNull = 0;
		if(!frontIds)
			frontIsNull = 1;
		for(var i=0;frontIds && i<frontIds.length;i++){
			if(currUserId == frontIds[i]){
				frontFlag = 1;
				break;
			}
		}
		//判断当前登录用户是否在客户端开发人员中
		var clientIds = $("#clientPeople").val();
		var clientFlag = 0;
		var clientIsNull = 0;
		if(!clientIds)
			clientIsNull = 1;
		for(var i=0;clientIds && i<clientIds.length;i++){
			if(currUserId == clientIds[i]){
				clientFlag = 1;
				break;
			}
		}
		if(role == "管理员" || (frontFlag==1&&clientFlag==1) ||(frontIsNull == 1&&(role == "客户端开发负责人" || (role == "客户端开发"&&clientFlag == 1))) || (clientIsNull == 1&&(role == "前端开发负责人" || (role == "前端开发"&&frontFlag == 1)))){
			data.interStatus = 3;
			ajax(url, data, 3);
		}else if((role == "前端开发负责人" || (role == "前端开发"&&frontFlag == 1))&&status!=310){
			data.interStatus = 310;
			ajax(url, data, 310);
		}else if(role == "前端开发负责人" || (role == "前端开发"&&frontFlag == 1)){
			showAlertMessage("该接口前端已经审核通过了!");
			refreshInterfaceInfo(status);
			return;
		}else if((role == "客户端开发负责人" || (role == "客户端开发"&&clientFlag == 1))&&status!=301){
			data.interStatus = 301;
			ajax(url, data, 301);
		}else if(role == "客户端开发负责人" || (role == "客户端开发"&&clientFlag == 1)){
			showAlertMessage("该接口客户端已经审核通过了!");
			refreshInterfaceInfo(status);
			return;
		}
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
			'</div><br><br><br>' +
			'<label class="col-lg-2 control-label">备注：</label>' +
			'<div class="col-lg-9">' +
			'<textarea id="remark" rows="5" class="form-control required" placeholder="选择其他时必须填写备注"></textarea>' +
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
						var remark = $('#remark').val();
						if(reason == 7 && (!remark || remark.trim() == '')){ // 当选择其他原因时，必须填写备注
							$('#remark').after('<span style="color:red;">请输填写备注</span>');
							return false;
						}
						var data = {};
						data.projectId = projectId;
						data.interfaceId = $('#interId').val();
						data.reason = reason;
						data.remark = remark;
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
	
	$('#headBtns').delegate('#iteration', 'click', function(){
		var msg = '确定要迭代该接口吗？迭代后原接口可以在历史版本中查看！';
		var url = '/idoc/flow/iteration.html';
		
		bootbox.dialog({
			title: '确定版本迭代',
			message: msg,
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						var data = {};
						data.projectId = projectId;
						data.interfaceId = $('#interId').val();
						$.ajax({
							url: url,
							type: 'POST',
							dataType: 'json',
							data: data,
							success: function(json){
								if(json.retCode == 200){
									refreshInterfaceInfo(1);
									var editVer = parseInt($('#lb_editVersion').text());
									if(typeof editVer === 'number')
										$('#lb_editVersion').text(editVer + 1);
									var iterVer = parseInt($('#lb_iterVersion').text());
									if(typeof iterVer === 'number')
										$('#lb_iterVersion').text(iterVer + 1);
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
		var status = getInterStatus($("#interId").val());
		if(status!=3){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/submitToTest.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 4);
	});
	
	$('#headBtns').delegate('#test', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=4){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/test.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 5);
	});
	
	$('#headBtns').delegate('#tested', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=5){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/tested.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 6);
	});
	
	$('#headBtns').delegate('#returnToTest', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=6){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/returnToTest.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 5);
	});
	
	$('#headBtns').delegate('#pressure', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=6){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/pressure.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 7);
	});
	
	$('#headBtns').delegate('#pressured', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=7){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/pressured.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 8);
	});
	
	$('#headBtns').delegate('#returnToPressure', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if(status!=8){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var url = '/idoc/flow/returnToPressure.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		ajax(url, data, 7);
	});
	
	$('#headBtns').delegate('#online', 'click', function(){
		var status = getInterStatus($("#interId").val());
		if($("#isNeedInterfaceTest").val() == '0'){
			if(status!=3){
				showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
				return;
			}
		}else if($("#isNeedPressureTest").val() == '0'){
			if(status!=6){
				showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
				return;
			}
		}else if(status!=8){
			showAlertMessage("接口状态发生变化，请刷新页面。", refreshInterfaceInfo(status));
			return;
		}
		var interName = $('#interName').val().trim();
		if(interName == ''){
			showAlertMessage('请填写接口名称。');
			return;
		}
		_interObj.interfaceName = interName;
		
		var interType= $('#interType').val().trim();
		if(interType == ''){
			showAlertMessage('请选择接口类型。');
			return;
		}
		_interObj.interfaceType = interType;
		
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
		
		if(isNeedTest == 1){
			var expectTestTime = $('#expectTestTime').val().trim();
			if(expectTestTime == ''){
				showAlertMessage('请选择预计提测时间。');
				return;
			}
			_interObj.expectTestTime = expectTestTime;
		}
		
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
		
		var frontPeople = $('#frontPeople').val();
		var clientPeople = $('#clientPeople').val();
		if(!clientPeople && !frontPeople){
			showAlertMessage('前端开发和客户端开发不能同时为空！');
			return;
		}
		var frontPeopleStr = '';
		for(var i = 0; frontPeople&&i < frontPeople.length; i++){
			frontPeopleStr += frontPeople[i] + ',';
		}
		frontPeopleStr = frontPeopleStr.substring(0, frontPeopleStr.length - 1);
		_interObj.frontPeopleIds = frontPeopleStr;
		var clientPeopleStr = '';
		for(var i = 0; clientPeople&&i < clientPeople.length; i++){
			clientPeopleStr += clientPeople[i] + ',';
		}
		clientPeopleStr = clientPeopleStr.substring(0, clientPeopleStr.length - 1);
		_interObj.clientPeopleIds = clientPeopleStr;
		
		var testPeople = $('#testPeople').val();
		if(!testPeople){
			showAlertMessage('请选择测试人员。');
			return;
		}
		
		var testPeopleStr = '';
		for(var i = 0; i < testPeople.length; i++){
			testPeopleStr += testPeople[i] + ',';
		}
		testPeopleStr = testPeopleStr.substring(0, testPeopleStr.length - 1);
		_interObj.testPeopleIds = testPeopleStr;
		_interObj.projectId = projectId;
		_interObj.interfaceId = $('#interId').val();
		var url = '/idoc/flow/online.html';
		var data = {};
		data.projectId = projectId;
		data.interfaceId = $('#interId').val();
		data.interfaceForm = newInterfaceForm(_interObj);
		//ajax(url, data, 9);
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			contentType: "application/json",
			data: JSON.stringify(_interObj),
			success: function(json){
				if(json.retCode == 200){
					refreshInterfaceInfo(9);
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
	});

	//移动/复制接口按钮
	/* 修改成项目间可以复制移动前代码
	$('#moveCopy').click(function(){
		var interfaceId = $('#interId').val();
		if(interfaceId == ''){
			showAlertMessage('请先进行保存操作后，再进行移动/复制操作。');
			return;
		}
		
		var projectId = $('#projectId').val();
		var data = {};
		data.projectId = projectId;
		
		var productId = $('#productId').val();
		data.productId = productId;
		
		$.ajax({
			url: '/idoc/inter/getProjects.html',
			type: 'GET',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					_projectList = json.projectList;
					if(_projectList.length > 0){
						//var firstProjectModules = _projectList[0].moduleList;
						var firstProjectModules = null;
						var projectOptionStr = '';
						var nowProjectOptionStr = '';
						for(var i = 0; i< _projectList.length; i++){
							var project = _projectList[i];
							if(project.projectId == projectId){
								firstProjectModules = _projectList[i].moduleList;
								nowProjectOptionStr = '<option value="' + project.projectId + '">' + project.projectName + '</option>';
							}else{
								projectOptionStr += '<option value="' + project.projectId + '">' + project.projectName + '</option>';
							}
						}
						projectOptionStr = nowProjectOptionStr + projectOptionStr;
						
						var moduleOptionsStr = '';
						for(var i = 0; firstProjectModules && i < firstProjectModules.length; i++){
							var module = firstProjectModules[i];
							moduleOptionsStr += '<option value="' + module.moduleId + '">' + module.moduleName + '</option>';
						}
						
						var pageOptionStr = '';
						for(var i = 0; firstProjectModules && firstProjectModules.length > 0 && firstProjectModules[0].pageList && i < firstProjectModules[0].pageList.length; i++){
							var page = firstProjectModules[0].pageList[i];
							pageOptionStr += '<option value="' + page.pageId + '">' + page.pageName + '</option>';
						}
						
						bootbox.dialog({
							title: '移动/复制接口',
							message:'<div class="row form-horizontal">' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">移动/复制</label>' +
											'<div class="col-lg-9">' +
												'<label class="radio-inline">' +
	                                                '<input type="radio" name="moveCopyOption" id="moveOption" value="move" checked="checked">移动' +
	                                            '</label>' +
	                                            '<label class="radio-inline">' +
	                                            	'<input type="radio" name="moveCopyOption" id="copyOption" value="copy">复制' +
	                                            '</label>' +
                                            '</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">项目</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="projectSelect" onchange="projectSelectChange();return false;">' +
												    projectOptionStr +
												'</select>' +
											'</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">模块</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="moduleSelect" onchange="moduleSelectChange();return false;">' +
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
										data.interfaceId = interfaceId;
										data.pageId = pageId;
										
										var action = $('input[name="moveCopyOption"]:checked').val();
										if(action == 'move'){
											$.ajax({
												url: '/idoc/inter/move.html',
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
										}else if(action == 'copy'){
											$.ajax({
												url: '/idoc/inter/copy.html',
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
										}
									}
								},
								Cancel:{
									label : '取消',
									className : "btn-warning"
								}
							}
						});
					}else{
						showAlertMessage('当前产品没有相应的项目、模块和页面，请先创建项目与模块和页面。');
					}
				}else{
					showAlertMessage('获取项目列表失败。');
				}
			}
		});
	});
	*/
	//移动/复制接口按钮
	$('#moveCopy').click(function(){
		var interfaceId = $('#interId').val();
		if(interfaceId == ''){
			showAlertMessage('请先进行保存操作后，再进行移动/复制操作。');
			return;
		}
		
		var projectId = $('#projectId').val();
		var data = {};
		data.projectId = projectId;
		
		var productId = $('#productId').val();
		data.productId = productId;
		
		$.ajax({
			url: '/idoc/inter/getProjects.html',
			type: 'GET',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					_projectList = json.projectList;
					if(_projectList.length > 0){
						//var firstProjectModules = _projectList[0].moduleList;
						var firstProjectModules = null;
						var projectOptionStr = '';
						var productOptionStr = '';
						
						$.ajax({
							url: '/idoc/getMyProduct.html',
							type: 'GET',
							dataType: 'json',
							async: false,
							success: function(res){
								if(res.retCode == 200){
									var productList = res.retContent;
									if(productList.length > 0){
										var firstProductStr = '';
										var nextProductStr = '';
										for(var i = 0; i < productList.length; i++){
											var product = productList[i];
											if(product.productId == productId){
												firstProductStr = '<option value="' + product.productId + '">' + product.productName + '</option>';
											}else{
												nextProductStr += '<option value="' + product.productId + '">' + product.productName + '</option>';
											}
										}
										productOptionStr = firstProductStr + nextProductStr;
									}
								}else{
									productOptionStr = '<option value="">查询产品出错！</option>';
								}
							}
						});
						
						var nowProjectOptionStr = '';
						for(var i = 0; i< _projectList.length; i++){
							var project = _projectList[i];
							if(project.projectId == projectId){
								firstProjectModules = _projectList[i].moduleList;
								nowProjectOptionStr = '<option value="' + project.projectId + '">' + project.projectName + '</option>';
							}else{
								projectOptionStr += '<option value="' + project.projectId + '">' + project.projectName + '</option>';
							}
						}
						projectOptionStr = nowProjectOptionStr + projectOptionStr;
						
						var moduleOptionsStr = '';
						for(var i = 0; firstProjectModules && i < firstProjectModules.length; i++){
							var module = firstProjectModules[i];
							moduleOptionsStr += '<option value="' + module.moduleId + '">' + module.moduleName + '</option>';
						}
						
						var pageOptionStr = '';
						for(var i = 0; firstProjectModules && firstProjectModules.length > 0 && firstProjectModules[0].pageList && i < firstProjectModules[0].pageList.length; i++){
							var page = firstProjectModules[0].pageList[i];
							pageOptionStr += '<option value="' + page.pageId + '">' + page.pageName + '</option>';
						}
						
						bootbox.dialog({
							title: '移动/复制接口',
							message:'<div class="row form-horizontal">' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">移动/复制</label>' +
											'<div class="col-lg-9">' +
												'<label class="radio-inline">' +
	                                                '<input type="radio" name="moveCopyOption" id="moveOption" value="move" checked="checked">移动' +
	                                            '</label>' +
	                                            '<label class="radio-inline">' +
	                                            	'<input type="radio" name="moveCopyOption" id="copyOption" value="copy">复制' +
	                                            '</label>' +
                                            '</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">产品</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="productSelect" onchange="productSelectChange();return false;">' +
												    productOptionStr +
												'</select>' +
											'</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">项目</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="projectSelect" onchange="projectSelectChange();return false;">' +
												    projectOptionStr +
												'</select>' +
											'</div>' +
										'</div>' +
										'<div class="form-group">' +
											'<label class="col-lg-2 control-label">模块</label>' +
											'<div class="col-lg-9">' +
												'<select class="form-control" id="moduleSelect" onchange="moduleSelectChange();return false;">' +
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
										var url_projectId = $('#projectSelect').val();
										if(!pageId || pageId == ''){
											$('#pageSelect').after('<span style="color:red;">请选择目标页面</span>');
											
											return false;
										}

										var data = {};
										data.interfaceId = interfaceId;
										data.pageId = pageId;
										
										var action = $('input[name="moveCopyOption"]:checked').val();
										if(action == 'move'){
											$.ajax({
												url: '/idoc/inter/move.html',
												type: 'POST',
												dataType: 'json',
												data: data,
												success: function(json){
													if(json.retCode == 200){
														showAlertMessage('移动接口操作成功。', function(){
															//window.location.reload();
															var url_interfaceId = interfaceId;
															window.location.href='/idoc/inter/index.html?projectId=' + url_projectId + '&interfaceId=' + url_interfaceId;
														});
													}else{
														showAlertMessage('移动接口操作失败。');
													}
												}
											});
										}else if(action == 'copy'){
											$.ajax({
												url: '/idoc/inter/copy.html',
												type: 'POST',
												dataType: 'json',
												data: data,
												success: function(json){
													if(json.retCode == 200){
														showAlertMessage('复制接口操作成功。', function(){
															//window.location.reload();
															var interf = json.inter;
															var url_interfaceId = interf.interfaceId;
															window.location.href='/idoc/inter/index.html?projectId=' + url_projectId + '&interfaceId=' + url_interfaceId;
														});
													}else{
														showAlertMessage('复制接口操作失败。');
													}
												}
											});
										}
									}
								},
								Cancel:{
									label : '取消',
									className : "btn-warning"
								}
							}
						});
					}else{
						showAlertMessage('当前产品没有相应的项目、模块和页面，请先创建项目与模块和页面。');
					}
				}else{
					showAlertMessage('获取项目列表失败。');
				}
			}
		});
	});
	
	$('.reqPeopleBtn').delegate('#saveReqPeople', 'click', function(){
		var url = '/idoc/flow/savePeopleInfo.html';
		var data = {};
		data.type = 1;
		data.interfaceId = $('#interId').val();
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
		data.peopleIds = reqPeopleStr;
		
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					bootbox.alert({message: '保存成功',
						buttons:{
							ok:{
								label: '确定'
							}
						}
					});
					$(".reqPeopleDiv").css("width","83%");
					$("#reqPeople_chosen").css("width","100%");
					$('.reqPeopleBtn').empty();
					reqPeoples = reqPeople;
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
	});
	
	$('.reqPeopleBtn').delegate('#cancelReqPeople', 'click', function(){
		$(".reqPeopleDiv").css("width","83%");
		$("#reqPeople_chosen").css("width","100%");
		$('.reqPeopleBtn').empty();
		
		// 绑定取消前的人员
		$('#reqPeople').chosen('destroy');
		var reqPeople = $('#reqPeople');
		$('#reqPeople').val("");
		for(var i = 0; reqPeoples && i < reqPeoples.length; i++){
			for(j = 0; reqPeople[0].options && j < reqPeople[0].options.length; j++){
				var user = reqPeoples[i];
				if(user == reqPeople[0].options[j].value){
					reqPeople[0].options[j].selected = 'selected';
					break;
				}
			}
		}
		$("#reqPeople").chosen();
	});
	
	$('.frontPeopleBtn').delegate('#saveFrontPeople', 'click', function(){
		var url = '/idoc/flow/savePeopleInfo.html';
		var data = {};
		data.type = 2;
		data.interfaceId = $('#interId').val();
		var frontPeople = $('#frontPeople').val();
		var clientPeople = $('#clientPeople').val();
		if(!clientPeople && !frontPeople){
 			showAlertMessage('前端开发和客户端开发不能同时为空！');
 			return;
 		}
		/*if(!frontPeople){
			showAlertMessage('请选择前端开发。');
			return;
		}*/
		var frontPeopleStr = '';
		if(frontPeople){
			for(var i = 0; i < frontPeople.length; i++){
				frontPeopleStr += frontPeople[i] + ',';
			}
			frontPeopleStr = frontPeopleStr.substring(0, frontPeopleStr.length - 1);
		}
		data.peopleIds = frontPeopleStr;
		
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					bootbox.alert({message: '保存成功',
						buttons:{
							ok:{
								label: '确定'
							}
						}
					});
					$(".frontPeopleDiv").css("width","100%");
					$("#frontPeople_chosen").css("width","100%");
					$('.frontPeopleBtn').empty();
					frontPeoples = frontPeople;
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
	});
	
	$('.frontPeopleBtn').delegate('#cancelFrontPeople', 'click', function(){
		var clientPeople = $("#clientPeople").val();
		if((!frontPeoples || frontPeoples.length==0) && !clientPeople){
			bootbox.alert("前端开发和客户端开发不能同时为空！");
			return;
		}
		$(".frontPeopleDiv").css("width","83%");
		$("#frontPeople_chosen").css("width","100%");
		$('.frontPeopleBtn').empty();
		// 绑定取消前的人员
		$('#frontPeople').chosen('destroy');
		var frontPeople = $('#frontPeople');
		$('#frontPeople').val("");
		for(var i = 0; frontPeoples && i < frontPeoples.length; i++){
			for(j = 0; frontPeople[0].options && j < frontPeople[0].options.length; j++){
				var user = frontPeoples[i];
				if(user == frontPeople[0].options[j].value){
					frontPeople[0].options[j].selected = 'selected';
					break;
				}
			}
		}
		$("#frontPeople").chosen();
		
	});
	
	$('.behindPeopleBtn').delegate('#saveBehindPeople', 'click', function(){
		var url = '/idoc/flow/savePeopleInfo.html';
		var data = {};
		data.type = 5;
		data.interfaceId = $('#interId').val();
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
		data.peopleIds = behindPeopleStr;
		
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					bootbox.alert({message: '保存成功',
						buttons:{
							ok:{
								label: '确定'
							}
						}
					});
					$(".behindPeopleDiv").css("width","83%");
					$("#behindPeople_chosen").css("width","100%");
					$('.behindPeopleBtn').empty();
					behindPeoples = behindPeople;
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
	});
	
	$('.behindPeopleBtn').delegate('#cancelBehindPeople', 'click', function(){
		$(".behindPeopleDiv").css("width","83%");
		$("#behindPeople_chosen").css("width","100%");
		$('.behindPeopleBtn').empty();
		
		// 绑定取消前的人员
		$('#behindPeople').chosen('destroy');
		var behindPeople = $('#behindPeople');
		$('#behindPeople').val("");
		for(var i = 0; behindPeoples && i < behindPeoples.length; i++){
			for(j = 0; behindPeople[0].options && j < behindPeople[0].options.length; j++){
				var user = behindPeoples[i];
				if(user == behindPeople[0].options[j].value){
					behindPeople[0].options[j].selected = 'selected';
					break;
				}
			}
		}
		$("#behindPeople").chosen();
	});
	
	$('.clientPeopleBtn').delegate('#saveClientPeople', 'click', function(){
		var url = '/idoc/flow/savePeopleInfo.html';
		var data = {};
		data.type = 3;
		data.interfaceId = $('#interId').val();
		var clientPeople = $('#clientPeople').val();
		var frontPeople = $('#frontPeople').val();
		if(!frontPeople && !clientPeople){
 			showAlertMessage('客户端开发和前端开发不能同时为空！');
 			return;
 		}
		/*if(!clientPeople){
			showAlertMessage('请选择客户端开发。');
			return;
		}*/
		var clientPeopleStr = '';
		if(clientPeople){
			for(var i = 0; i < clientPeople.length; i++){
				clientPeopleStr += clientPeople[i] + ',';
			}
			clientPeopleStr = clientPeopleStr.substring(0, clientPeopleStr.length - 1);
		}
		data.peopleIds = clientPeopleStr;
		
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					bootbox.alert({message: '保存成功',
						buttons:{
							ok:{
								label: '确定'
							}
						}
					});
					$(".clientPeopleDiv").css("width","83%");
					$("#clientPeople_chosen").css("width","100%");
					$('.clientPeopleBtn').empty();
					clientPeoples = clientPeople;
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
	});
	
	$('.clientPeopleBtn').delegate('#cancelClientPeople', 'click', function(){
		var frontPeople = $("#frontPeople").val();
		if((!clientPeoples||clientPeoples.length==0) && !frontPeople){
			bootbox.alert("前端开发和客户端开发不能同时为空！");
			return;
		}
		$(".clientPeopleDiv").css("width","83%");
		$("#clientPeople_chosen").css("width","100%");
		$('.clientPeopleBtn').empty();
		
		$('#clientPeople').chosen('destroy');
		var clientPeople = $('#clientPeople');
		$('#clientPeople').val("");
		for(var i = 0; clientPeoples && i < clientPeoples.length; i++){
			for(j = 0; clientPeople[0].options && j < clientPeople[0].options.length; j++){
				var user = clientPeoples[i];
				if(user == clientPeople[0].options[j].value){
					clientPeople[0].options[j].selected = 'selected';
					break;
				}
			}
		}
		$("#clientPeople").chosen();
	});
	
	$('.testPeopleBtn').delegate('#saveTestPeople', 'click', function(){
		var url = '/idoc/flow/savePeopleInfo.html';
		var data = {};
		data.type = 4;
		data.interfaceId = $('#interId').val();
		var testPeople = $('#testPeople').val();
		if(!testPeople){
			showAlertMessage('请选择测试人员。');
			return;
		}
		var testPeopleStr = '';
		for(var i = 0; i < testPeople.length; i++){
			testPeopleStr += testPeople[i] + ',';
		}
		testPeopleStr = testPeopleStr.substring(0, testPeopleStr.length - 1);
		data.peopleIds = testPeopleStr;
		
		$.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					bootbox.alert({message: '保存成功',
						buttons:{
							ok:{
								label: '确定'
							}
						}
					});
					$(".testPeopleDiv").css("width","83%");
					$("#testPeople_chosen").css("width","100%");
					$('.testPeopleBtn').empty();
					testPeoples = testPeople;
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
	});
	
	$('.testPeopleBtn').delegate('#cancelTestPeople', 'click', function(){
		$(".testPeopleDiv").css("width","83%");
		$("#testPeople_chosen").css("width","100%");
		$('.testPeopleBtn').empty();
		
		$('#testPeople').chosen('destroy');
		var testPeople = $('#testPeople');
		$('#testPeople').val("");
		for(var i = 0; testPeoples && i < testPeoples.length; i++){
			for(j = 0; testPeople[0].options && j < testPeople[0].options.length; j++){
				var user = testPeoples[i];
				if(user == testPeople[0].options[j].value){
					testPeople[0].options[j].selected = 'selected';
					break;
				}
			}
		}
		$("#testPeople").chosen();
	});
	
	$('#inter_ftl_template').blur(function(){
		var status = $("#inter_ftl_template").attr("readonly");
		if(status == "readonly"){
			return;
		}
		$.ajax({
			url: '/idoc/inter/checkInterfaceFtl.html',
			type: 'POST',
			dataType: 'json',
			data: {ftlTemplate:$('#inter_ftl_template').val()},
			success: function(json){
				if(json.retCode != 200){
					showAlertMessage('格式错误: ' + json.retDesc);
					$("#inter_ftl_template").val("");
				}
			}
		});
	});
	$('#url').blur(function(){
		var status = $("#url").attr("readonly");
		if(status == "readonly"){
			return;
		}
		$.ajax({
			url: '/idoc/inter/checkInterfaceUrl.html',
			type: 'POST',
			dataType: 'json',
			data: {url:$('#url').val(),interfaceId:$('#interId').val()},
			success: function(json){
				if(json.retCode == 400){
					showAlertMessage(json.retDesc);
					/**
					 * 对于重复的url，做置空处理
					 * */
					$("#url").val("");
				}else if(json.retCode == 402){
					showAlertMessage('<font style="font-size:14px" color="red">该url已经存在，同一个产品下不允许有多个相同的url地址，请重新选择一个url，如果该接口和同名url是同一个接口，请在原接口基础上做操作。</font><br>后台信息: ' + json.retDesc);
					$("#url").val("");
				}else if(json.retCode == 500){
					showAlertMessage('后台错误: ' + json.retDesc);
					$("#url").val("");
				}
			}
		});
	});
	
});

function checkProjectIsEmpty(){
	var inters = $("li[id^='inter-']");
	if(inters.length == 0){
		$().toastmessage('showToast', {
		    text     : '没有查询到相关接口!',
		    sticky   :  false,
		    position : 'middle-center',
		    type     : 'warning',
		    stayTime :  2000
		}); 
	}
}

function addOneRedis(){
	if($("#newRedis").css("display")=="none"){
		$("#newRedis").toggle(800);
	}
	var str = "<tr>" +
		"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存Key'></td>" +
		"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存数据'></td>" +
		"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存策略'></td>" +
		"<td><button class='btn btn-warning btn-sm' onclick='deleteOneRedis(null,this)'>删除</button>" +
		"<input type='hidden' value=''>" +
		"</td>" +
		"</tr>";
	$("#table_redis tbody").append(str);
}

function deleteOneRedis(redisId,item){
	var data = {};	
	data.redisId = redisId;
	bootbox.dialog({
		message : " ",
		title : "您确定删除该缓存？",
		buttons : {
			OK : {
				label : "确认",
				className : "btn-primary",
				callback : function() {
					if(redisId!=null && redisId!=""){
						$.ajax({
							url:'/idoc/inter/deleteInterfaceRedis.html',
							type:'GET',
							data:data,
							dataType:'json',
							success:function(json){
								if(json.retCode == 200){				
									$().toastmessage('showToast', {
			    					    text     : '删除成功!',
			    					    sticky   :  false,
			    					    position : 'middle-center',
			    					    type     : 'success',
			    					    stayTime :  1500
			    					}); 
							        var tr = $(item).parent().parent();
							        tr.remove();
								}else{
									bootbox.alert("操作失败！");
								}
							}
						});
					}else{
						$().toastmessage('showToast', {
    					    text     : '删除成功!',
    					    sticky   :  false,
    					    position : 'top-right',
    					    type     : 'success',
    					    stayTime :  1000
    					});
						var tr = $(item).parent().parent();
				        tr.remove();
					}
				}
			},
			Cancel : {
				label : "取消",
				className : "btn-danger",
				callback : function() {
				}
			}
		}
	});
}

function refreshInterfaceInfo(status){
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
	data.flag=1;
	$.ajax({
		url: url,
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				if(json.status)
					status = json.status;
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
		var currUserId = $("#currUserId").val();
		var isNeedInterfaceTest = $('#isNeedInterfaceTest').val();
		var isNeedPressureTest = $('#isNeedPressureTest').val();
		// 各个角色的负责人可以修改其角色的成员
		if(role == "管理员" || role == "产品负责人"){
			$('#reqPeople').attr("disabled",false);
			$('#reqPeople').chosen('destroy');
			setTimeout(function(){
				$("#reqPeople").chosen();
			}, 200);
			reqPeopleEditable = true;
		}
		if(role == "管理员" || role == "前端开发负责人"){
			$('#frontPeople').attr("disabled",false);
			$('#frontPeople').chosen('destroy');
			setTimeout(function(){
				$("#frontPeople").chosen();
			}, 200);
			frontPeopleEditable = true;
		}
		if(role == "管理员" || role == "后台开发负责人"){
			$('#behindPeople').attr("disabled",false);
			$('#behindPeople').chosen('destroy');
			setTimeout(function(){
				$("#behindPeople").chosen();
			}, 200);
			behindPeopleEditable = true;
		}
		if(role == "管理员" || role == "客户端开发负责人"){
			$('#clientPeople').attr("disabled",false);
			$('#clientPeople').chosen('destroy');
			setTimeout(function(){
				$("#clientPeople").chosen();
			}, 200);
			clientPeopleEditable = true;
		}
		if(role == "管理员" || role == "测试负责人"){
			$('#testPeople').attr("disabled",false);
			$('#testPeople').chosen('destroy');
			setTimeout(function(){
				$("#testPeople").chosen();
			}, 200);
			testPeopleEditable = true;
		}
		
		switch(status){
		case 10:  // 暂存中
			var html = '';
			if(role == "管理员" || role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-success btn-sm" id="reEdit">重新编辑</button> &nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="audit">发起审核</button>';
			}
			$('#headBtns').append(html);
			break;
		case 1:  // 编辑中
			var html = '';
			if(role == "管理员" || role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-success btn-sm" id="reEdit">重新编辑</button> &nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="audit">发起审核</button>';
			}
			$('#headBtns').append(html);
			break;
		case 2:  // 审核中
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "前端开发负责人" || role == "客户端开发负责人" || (role == "前端开发") || (role == "客户端开发")){
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}
			$('#headBtns').append(html);
			break;
		case 310:  // 前端审核通过
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "客户端开发负责人" || (role == "客户端开发")){
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}else if(role == "前端开发负责人" || (role == "前端开发")){
				html += '<button class="btn btn-warning btn-sm" disabled="disabled">等待客户端审核中</button>';
			}
			$('#headBtns').append(html);
			break;
		case 301:  // 客户端审核通过
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "客户端开发负责人" || (role == "客户端开发")){
				html += '<button class="btn btn-warning btn-sm" disabled="disabled">等待前端审核中</button>';
			}else if(role == "前端开发负责人" || (role == "前端开发")){
				html += '<button class="btn btn-success btn-sm" id="revise">建议修改</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="auditSuccess">审核通过</button>';
			}
			$('#headBtns').append(html);
			break;
		case 3:  // 审核通过
			var html = '';
			if(role == "管理员"){
				if(isNeedInterfaceTest == 1){
					html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-success btn-sm" id="submitToTest">提交测试</button>';
				}else{
					html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-success btn-sm" id="online">上线</button>';
				}
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				if(isNeedInterfaceTest == 1){
					html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-success btn-sm" id="submitToTest">提交测试</button>';
				}else{
					html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
				}
			}else if((role == "测试负责人" || (role == "测试人员")) && isNeedInterfaceTest == 0){
				html += '<button class="btn btn-success btn-sm" id="online">上线</button>';
			}
			$('#headBtns').append(html);
			break;
		case 4:  // 已提测
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="test">开始测试</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || (role == "测试人员")){
				html += '<button class="btn btn-success btn-sm" id="test">开始测试</button>';
			}
			$('#headBtns').append(html);
			break;
		case 5:  // 测试中
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="tested">测试完成</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || (role == "测试人员")){
				html += '<button class="btn btn-success btn-sm" id="tested">测试完成</button>';
			}
			$('#headBtns').append(html);
			break;
		case 6:  // 测试完成
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				if(isNeedPressureTest == 1){ //需要压测
					html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-warning btn-sm" id="pressure">提交压测</button>';
				}else{
					html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
					html += '<button class="btn btn-warning btn-sm" id="online">上线</button>';
				}
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || (role == "测试人员")){
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
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="pressured">压测完成</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || (role == "测试人员")){
				html += '<button class="btn btn-success btn-sm" id="returnToTest">返回测试</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="pressured">压测完成</button>';
			}
			$('#headBtns').append(html);
			break;
		case 8:  // 压测完成
			var html = '';
			if(role == "管理员"){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-success btn-sm" id="returnToPressure">返回压测</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="online">上线</button>';
			}else if(role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
			}else if(role == "测试负责人" || (role == "测试人员")){
				html += '<button class="btn btn-success btn-sm" id="returnToPressure">返回压测</button> &nbsp;&nbsp;';
				html += '<button class="btn btn-warning btn-sm" id="online">上线</button>';
			}
			$('#headBtns').append(html);
			break;
		case 9:  // 已上线
			var html = '';
			if(role == "管理员" || role == "后台开发负责人" || (role == "后台开发")){
				html += '<button class="btn btn-info btn-sm" id="iteration">版本迭代</button> &nbsp;&nbsp';
				html += '<button class="btn btn-danger btn-sm" id="forceBack">强制回收</button>';
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

var reqPeopleEditable = false; // 如果接口处于‘编辑’状态为false
var reqPeoples = []; // 用于存储多选下拉框改变前的状态
function reqPeopleChanged(){ 
	if(reqPeopleEditable){
		$(".reqPeopleDiv").css("width","73%");
		$("#reqPeople_chosen").css("width","100%");
		var html = '<button class="btn btn-info btn-sm" id="saveReqPeople">保存</button> ' +
			'<button class="btn btn-danger btn-sm" id="cancelReqPeople">取消</button>';
		$('.reqPeopleBtn').html(html);
	}
}

var frontPeopleEditable = false;
var frontPeoples = [];
function frontPeopleChanged(){ 
	if(frontPeopleEditable){
		$(".frontPeopleDiv").css("width","73%");
		$("#frontPeople_chosen").css("width","100%");
		var html = '<button class="btn btn-info btn-sm" id="saveFrontPeople">保存</button> ' +
			'<button class="btn btn-danger btn-sm" id="cancelFrontPeople">取消</button>';
		$('.frontPeopleBtn').html(html);
	}
}

var behindPeopleEditable = false;
var behindPeoples = [];
function behindPeopleChanged(){ 
	if(behindPeopleEditable){
		$(".behindPeopleDiv").css("width","73%");
		$("#behindPeople_chosen").css("width","100%");
		var html = '<button class="btn btn-info btn-sm" id="saveBehindPeople">保存</button> ' +
			'<button class="btn btn-danger btn-sm" id="cancelBehindPeople">取消</button>';
		$('.behindPeopleBtn').html(html);
	}
}

var clientPeopleEditable = false; 
var clientPeoples = [];
function clientPeopleChanged(){ 
	if(clientPeopleEditable){
		$(".clientPeopleDiv").css("width","73%");
		$("#clientPeople_chosen").css("width","100%");
		var html = '<button class="btn btn-info btn-sm" id="saveClientPeople">保存</button> ' +
			'<button class="btn btn-danger btn-sm" id="cancelClientPeople">取消</button>';
		$('.clientPeopleBtn').html(html);
	}
}

var testPeopleEditable = false;
var testPeoples = [];
function testPeopleChanged(){ 
	if(testPeopleEditable){
		$(".testPeopleDiv").css("width","73%");
		$("#testPeople_chosen").css("width","100%");
		var html = '<button class="btn btn-info btn-sm" id="saveTestPeople">保存</button> ' +
			'<button class="btn btn-danger btn-sm" id="cancelTestPeople">取消</button>';
		$('.testPeopleBtn').html(html);
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
		$('#expectTestTimeDiv').hide();
	}else{
		$('#s4').css({'display': ''});
		$('#s5').css({'display': ''});
		$('#s6').css({'display': ''});
		$('#expectTestTimeDiv').show();
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

function editModule(moduleId, oldModuleName){
	bootbox.dialog({
		title: '编辑模块',
		message:'<div class="row form-horizontal">' +
		'<div class="form-group has-feedback">' +
		'<label class="col-lg-2 control-label">模块名称</label>' +
		'<div class="col-lg-9">' +
		'<input id="moduleName" type="text" class="form-control required" value="' + oldModuleName + '" placeholder="请输入新的模块名称">' +
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
					if(oldModuleName == moduleName){
						$('#moduleName').after('<span style="color:red;">输入的模块名称与原来相同！</span>');
						return false;
					}
					
					var data = {};
					data.moduleId = moduleId;
					data.moduleName = moduleName;
					var theModuleName = moduleName;
					$.ajax({
						url:'/idoc/module/update.html',
						type: 'POST',
						data: data,
						dataType: 'json',
						success: function(json){
							if(json.retCode == 200){
								var module = json.module;
								$('#module-' + moduleId + ' a').first().attr('title', moduleName);
								if(moduleName.length > moduleNameLengthToShow){
									moduleName = moduleName.substring(0, moduleNameLengthToShow) + '...';
								}
								$('#module-' + moduleId + ' a').first().text(moduleName);
								$('#module-' + moduleId + ' a').eq(1).attr('onclick', 'editModule("' + moduleId + '","' + theModuleName + '")');
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
						url: '/idoc/module/delete.html',
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

function editPage(pageId, oldPageName){
	bootbox.dialog({
		title: '编辑页面',
		message:'<div class="row form-horizontal">' +
		'<div class="form-group">' +
		'<label class="col-lg-2 control-label">页面名称</label>' +
		'<div class="col-lg-9">' +
		'<input id="pageName" type="text" class="form-control required" value="' + oldPageName + '" placeholder="请输入新的页面名称">' +
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
					if(oldPageName == pageName){
						$('#pageName').after('<span style="color:red;">输入的页面名称和原来相同！</span>');
						return false;
					}
					var data = {};
					data.pageId = pageId;
					data.pageName = pageName;
					var thePageName = pageName;
					
					$.ajax({
						url: '/idoc/page/update.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								var page = json.page;
								$('#page-' + pageId + ' a').first().attr('title', pageName);
								if(pageName.length > pageNameLengthToShow){
									pageName = pageName.substring(0, pageNameLengthToShow) + '...';
								}
								$('#page-' + pageId + ' a').first().text(pageName);
								$('#page-' + pageId + ' a').eq(1).attr('onclick', "editPage('" + pageId +"','" + thePageName + "')");
								
							}else{
								showAlertMessage('操作失败')
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
						url: '/idoc/page/delete.html',
						type: 'POST',
						dataType: 'json',
						data: data,
						success: function(json){
							if(json.retCode == 200){
								var node = $('#page-' + pageId);
								$("#moduleTree").jstree("remove", node);
							}else{
								showAlertMessage('操作失败')
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

var oldInterfaceName;
var oldInterfaceType;
var oldFtlTemplate;
var oldRequestType;
var oldRequestUrl;
var oldInterfaceDesc;
var oldReqParam;
var oldRetParam;
var oldNeedInterfaceTest;
var oldNeedPressureTest;
var oldExpectTestTime;
var oldExpectOnlineTime;
var oldReqPeople;
var oldFrontPeople;
var oldBehindPeople;
var oldClientPeople;
var oldTestPeople;
var oldRetParamExample;
function initInterfaceInfo(interId,formOnclick){
	if(isEditing==1){
		bootbox.alert("请先保存或取消您编辑的接口！");
		$('#interName').focus();
		return;
	}
	var projectId = $('#projectId').val().trim();
	var productId = $('#productId').val().trim();
	var data = {};
	//data.productId=productId;
	data.projectId = projectId;
	data.interfaceId = interId;
	if (!window.ActiveXObject){
		var position = "idoc/inter/index.html?projectId="+projectId+"&interfaceId="+interId; //这两句是页面不刷新的条件下修改浏览器的url，IE应该不适用
		window.history.pushState({},0,'http://'+window.location.host+'/'+position);
	}
	if(formOnclick && $('#interId').val() === interId){
		return;
	}
	
	$.ajax({
		url: '/idoc/inter/edit.html',
		type: 'GET',
		dataType: 'json',
		data: data,
		error : function( req, status, err) {
			 var auth = req.getResponseHeader("REQUIRES_AUTH");
			 var auth_url = req.getResponseHeader("REQUIRES_AUTH_URL");
			 if(auth == 1 && auth_url){
				 window.location = auth_url;
			}
		},
		success: function(json){
			if(json.retCode == 200){
				var inter = json.inter;
				var lastInter;
				_interObj = inter;
				if(inter.editVersion > 1){
					var data = {};
					data.interfaceId = inter.interfaceId;
					data.versionNum = inter.editVersion - 1;
					$.ajax({
						url: '/idoc/inter/searchLastInterfaceVersion.html',
						type: 'POST',
						async: false,
						dataType: 'json',
						data: data,
						success: function(json1){
							if(json1.retCode == 200){
								var snapshot = json1.version.snapshot;
								if(!isNullOrEmpty(snapshot)){
									lastInter = JSON.parse(snapshot);
								}
							}
						}
					});
				}
				$("#isEditableInfor").empty();
				$('#hint').css({'display': 'none'});
				$('#footBtns').html('');
				$('#interMain').css({'display': ''});
				$('#interId').val(inter.interfaceId);
				$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
				
				$('#interName').val(inter.interfaceName);
				oldInterfaceName = inter.interfaceName;
				
				var statusStr = getStatus(inter.interfaceStatus);
				$('#lb_interStatus').text(statusStr);
				$('#lb_interStatus').css('color','red');
				$('#lb_interStatus').css('font-size',16);
				$('#lb_creator').text(inter.creator.userName);
				$('#lb_createTime').text(getDate(inter.createTime));
				$('#lb_editVersion').text(inter.editVersion);
				$('#lb_iterVersion').text(inter.iterVersion);
				
				$('#interType').val(inter.interfaceType);
				oldInterfaceType = inter.interfaceType;
				$('#requestType').val(inter.requestType);
				oldRequestType = inter.requestType;
				$('#url').val(inter.url);
				oldRequestUrl = inter.url;
				
				$('#interfaceHistoryVersion').html('');
				if(inter.editVersion > 1){
					$('#interfaceHistoryVersion').html('<a class="btn btn-primary btn-xs" href="interfaceHistoryVersion.html?interfaceName=' + encodeURI(encodeURI(inter.interfaceName)) + '&interfaceId=' + inter.interfaceId + '&productId=' + $('#productId').val().trim() + '&projectId=' + $('#projectId').val().trim() + '" target="_blank">历史版本</a>');
				}
				//控制ftl模板显示还是隐藏
				if(inter.interfaceType==2){
					$("#ftl_template_div").show();
					$("#inter_ftl_template").val(inter.ftlTemplate);
					oldFtlTemplate = inter.ftlTemplate;
				}else{
					$("#ftl_template_div").hide();
					$("#inter_ftl_template").val("");
					oldFtlTemplate = "";
				}
				
				var redisList = json.redis;
				initInterfaceRedis(redisList);
				
				param.init(inter.reqParams, inter.retParams, false);
				if(lastInter){
					var lastReqParams = lastInter.reqParams;
					var lastRetParams = lastInter.retParams;
					initParams(inter.reqParams, inter.retParams,lastReqParams,lastRetParams);
				}
				oldReqParam = inter.reqParams.toString();
				oldRetParam = inter.retParams.toString();
				//mockReturnParam();
				$('#mockRet').css('display', 'none');
				oldInterfaceDesc = inter.desc;
				
				if(inter.retParamExample == null)
					$('#retParamExample').text('');
				else{
					$('#retParamExample').text(inter.retParamExample);
					oldRetParamExample = inter.retParamExample;
				}
				
				$('#isNeedInterfaceTest').val(inter.isNeedInterfaceTest);
				oldNeedInterfaceTest = inter.isNeedInterfaceTest;
				$('#isNeedInterfaceTest').change();
				
				$('#isNeedPressureTest').val(inter.isNeedPressureTest);
				oldNeedPressureTest = inter.isNeedPressureTest;
				$('#isNeedPressureTest').change();
				
				if(!isNullOrEmpty(inter.expectTestTime)){
					var expectTestTime = getDate(inter.expectTestTime);
					expectTestTime = expectTestTime.substring(0, 10);
					$('#expectTestTime').val(expectTestTime);
					oldExpectTestTime = expectTestTime;
				}else{
					$('#expectTestTime').val("");
				}
				if(!isNullOrEmpty(inter.expectOnlineTime)){
					var expectOnline = getDate(inter.expectOnlineTime);
					expectOnline = expectOnline.substring(0, 10);
					$('#expectOnlineTime').val(expectOnline);
					oldExpectOnlineTime = expectOnline;
				}else{
					$('#expectOnlineTime').val("");
				}
				
				if($('#reqPeople_chosen').length > 0){
					$('.chosen-select').chosen('destroy');
				}
				$('.chosen-select option').removeAttr('selected');
				
				var reqPeople = $('#reqPeople');
				var frontPeople = $('#frontPeople');
				var behindPeople = $('#behindPeople');
				var clientPeople = $('#clientPeople');
				var testPeople = $('#testPeople');
				
				
				var k = 0;
				for(var i = 0; inter.reqPeoples && i < inter.reqPeoples.length; i++){
					var user = inter.reqPeoples[i];
					for(j = 0; reqPeople[0].options && j < reqPeople[0].options.length; j++){
						if(user.userId == reqPeople[0].options[j].value){
							reqPeople[0].options[j].selected = 'selected';
							reqPeoples[k++] = user.userId;
							break;
						}
						
					}
				}
				
				k = 0;
				for(var i = 0; inter.frontPeoples && i < inter.frontPeoples.length; i++){
					var user = inter.frontPeoples[i];
					for(j = 0; frontPeople[0].options && j < frontPeople[0].options.length; j++){
						if(user.userId == frontPeople[0].options[j].value){
							frontPeople[0].options[j].selected = 'selected';
							frontPeoples[k++] = user.userId;
							break;
						}
					}
				}
				
				k = 0;
				for(var i = 0; inter.behindPeoples && i < inter.behindPeoples.length; i++){
					var user = inter.behindPeoples[i];
					for(j = 0; behindPeople[0].options && j < behindPeople[0].options.length; j++){
						if(user.userId == behindPeople[0].options[j].value){
							behindPeople[0].options[j].selected = 'selected';
							behindPeoples[k++] = user.userId;
							break;
						}
					}
				}
				
				k = 0;
				for(var i = 0; inter.clientPeoples && i < inter.clientPeoples.length; i++){
					var user = inter.clientPeoples[i];
					for(j = 0; clientPeople[0].options && j < clientPeople[0].options.length; j++){
						if(user.userId == clientPeople[0].options[j].value){
							clientPeople[0].options[j].selected = 'selected';
							clientPeoples[k++] = user.userId;
							break;
						}
					}
				}
				
				k = 0;
				for(var i = 0; inter.testPeoples && i < inter.testPeoples.length; i++){
					var user = inter.testPeoples[i];
					for(j = 0; testPeople[0].options && j < testPeople[0].options.length; j++){
						if(user.userId == testPeople[0].options[j].value){
							testPeople[0].options[j].selected = 'selected';
							testPeoples[k++] = user.userId;
							break;
						}
					}
				}
				oldReqPeople = $('#reqPeople').val();
				oldFrontPeople = $('#frontPeople').val();
				oldBehindPeople = $('#behindPeople').val();
				oldClientPeople = $('#clientPeople').val();
				oldTestPeople = $('#testPeople').val();
				setTimeout(function(){
					_editor.setData(inter.desc);
					$(".chosen-select").chosen();
					setInterfaceInfoReadonly(true);
					refreshInterfaceInfo(inter.interfaceStatus);
				}, 500);
				
				$('#creatorId').val(inter.creatorId);
				
				
			}else{
				showAlertMessage('获取接口信息失败。');
			}
		}
	});
	
}

function mockReturnParam(){
	// 自动生成mock返回值示例
	_interObj.reqParams = param.getReqParam();
	_interObj.retParams = param.getRetParam();
	$.ajax({
		url: '/idoc/mock/queryMockData.html',
		type: 'POST',
		dataType: 'json',
		contentType: "application/json",
		data: JSON.stringify(_interObj),
		success: function(json){
			if(json.retCode == 200){
				//返回参数Mock
				var mockRuleObj = eval('(' + json.mockData + ')');
				var mockDataObj  = Mock.mock(mockRuleObj);
				
				var mockDataStr;
				if($('#interType').val() == 1 || $('#interType').val() == 3 || $('#interType').val() == 4){
					mockDataStr = JSON.stringify(mockDataObj, null, 4);
				}else if($('#interType').val() == 2){
					mockDataStr = generateFtlFakeData(mockDataObj);
				}else{
					showAlertMessage('请选择接口类型。');
					return;
				}
				$('#mockReturnExample').text(mockDataStr);
			}else{
				showAlertMessage('自动生成返回值示例失败。');
			}
		}
	});
	//$('#mockRet').css('display', 'none');
}

//改变接口类型，是否显示ftl模板信息
function changeInterType(){
	var interType = $("#interType").val();
	if(interType == 2){
		$("#ftl_template_div").show();
	}else{
		$("#ftl_template_div").hide();
	}
	$("#inter_ftl_template").val("");
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
						url: '/idoc/inter/delete.html',
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

function productSelectChange(){
	var selectedProduct = $('#productSelect').val();
	
	var projectOptionStr = '';
	var moduleOptionsStr = '';
	var pageOptionStr = '';
	
	var data = {};
	data.productId = selectedProduct;
	$.ajax({
		url: '/idoc/inter/getProjectList.html',
		type: 'GET',
		dataType: 'json',
		async: false,
		data: data,
		success: function(json){
			if(json.retCode == 200){
				_projectList = json.projectList;
				if(_projectList.length > 0){
					var firstProjectModules = null;
					var nowProjectOptionStr = '';
					for(var i = 0; i< _projectList.length; i++){
						var project = _projectList[i];
						if(i == 0){
							firstProjectModules = _projectList[i].moduleList;
							nowProjectOptionStr = '<option value="' + project.projectId + '">' + project.projectName + '</option>';
						}else{
							projectOptionStr += '<option value="' + project.projectId + '">' + project.projectName + '</option>';
						}
					}
					projectOptionStr = nowProjectOptionStr + projectOptionStr;
					
					for(var i = 0; firstProjectModules && i < firstProjectModules.length; i++){
						var module = firstProjectModules[i];
						moduleOptionsStr += '<option value="' + module.moduleId + '">' + module.moduleName + '</option>';
					}
					
					for(var i = 0; firstProjectModules && firstProjectModules.length > 0 && firstProjectModules[0].pageList && i < firstProjectModules[0].pageList.length; i++){
						var page = firstProjectModules[0].pageList[i];
						pageOptionStr += '<option value="' + page.pageId + '">' + page.pageName + '</option>';
					}
				}
			}
		}
	});
	
	if(projectOptionStr == '' || moduleOptionsStr == '' || pageOptionStr == ''){
		showAlertMessage('当前选取的的产品没有相应项目，模块或页面，请先创建模块或页面。');
	}
	
	$('#projectSelect').html(projectOptionStr);
	$('#moduleSelect').html(moduleOptionsStr);
	$('#pageSelect').html(pageOptionStr);
}

function projectSelectChange(){
	var selectedProject = $('#projectSelect').val();
	
	var moduleOptionsStr = '';
	var pageOptionStr = '';
	for(var i = 0; _projectList.length; i++){
		var project = _projectList[i];
		if(project.projectId == selectedProject){
			var moduleList = project.moduleList;
			for(var i = 0; moduleList && i < moduleList.length; i++){
				var module = moduleList[i];
				moduleOptionsStr += '<option value="' + module.moduleId + '">' + module.moduleName + '</option>';
			}
			
			for(var i = 0; moduleList && moduleList.length > 0 && moduleList[0].pageList && i < moduleList[0].pageList.length; i++){
				var page = moduleList[0].pageList[i];
				pageOptionStr += '<option value="' + page.pageId + '">' + page.pageName + '</option>';
			}
			
			break;
		}
	}
	
	if(moduleOptionsStr == '' || pageOptionStr == ''){
		showAlertMessage('当前选取的的项目没有相应模块或页面，请先创建模块或页面。');
	}
	
	$('#moduleSelect').html(moduleOptionsStr);
	$('#pageSelect').html(pageOptionStr);
}

function moduleSelectChange(){
	var selectedProject = $('#projectSelect').val();
	var selectedModule = $('#moduleSelect').val();
	
	var pageOptionStr = '';
	for(var i = 0; _projectList.length; i++){
		var project = _projectList[i];
		if(project.projectId == selectedProject){
			var moduleList = project.moduleList;
			for(var i = 0; i < moduleList.length; i++){
				var module = moduleList[i];
				if(module.moduleId == selectedModule){
					var pages = module.pageList;
					for(var i = 0; pages && i < pages.length; i++){
						var page = pages[i];
						pageOptionStr += '<option value="' + page.pageId + '">' + page.pageName + '</option>';
					}
					
					break;
				}
			}
			
			break;
		}
	}
	
	if(pageOptionStr == ''){
		showAlertMessage('当前选取的的模块没有相应的页面，请先创建页面。');
	}
	
	$('#pageSelect').html(pageOptionStr);
}

function setInterfaceInfoReadonly(enable){
	$('#interName').attr("readonly",enable);
	$('#interType').attr("disabled",enable);
	$('#requestType').attr("disabled",enable);
	$('#url').attr("readonly",enable);
	$(":radio").attr("disabled",enable);
	$("#table_redis tbody tr td input").attr("readonly",enable);
	$("#table_redis tbody tr td button").attr("disabled",enable);
	_editor.setReadOnly(enable);
	$('#retParamExample').attr("readonly", enable);
	$('#isNeedInterfaceTest').attr("disabled",enable);
	$('#isNeedPressureTest').attr("disabled",enable);
	$('#expectTestTime').attr("disabled",enable);
	$('#expectOnlineTime').attr("disabled",enable);
	$('.chosen-select').chosen('destroy');
	$('#reqPeople').attr("disabled",enable);
	$('#frontPeople').attr("disabled",enable);
	$('#behindPeople').attr("disabled",enable);
	$('#clientPeople').attr("disabled",enable);
	$('#testPeople').attr("disabled",enable);
	$('#inter_ftl_template').attr("disabled",enable);
	setTimeout('$(".chosen-select").chosen();', 200);
}

//设置下面的状态让多选下拉框改变时不会在后面显示保存取消按钮
function setMultiSelectWhenEdit(){
	if(reqPeopleEditable == true){
		$(".reqPeopleDiv").css("width","83%");
		$("#reqPeople_chosen").css("width","100%");
		$('.reqPeopleBtn').empty();
		reqPeopleEditable = false;
	}
	if(frontPeopleEditable == true){
		$(".frontPeopleDiv").css("width","83%");
		$("#frontPeople_chosen").css("width","100%");
		$('.frontPeopleBtn').empty();
		frontPeopleEditable = false;
	}
	if(behindPeopleEditable == true){
		$(".behindPeopleDiv").css("width","83%");
		$("#behindPeople_chosen").css("width","100%");
		$('.behindPeopleBtn').empty();
		behindPeopleEditable = false;
	}
	if(clientPeopleEditable == true){
		$(".clientPeopleDiv").css("width","83%");
		$("#clientPeople_chosen").css("width","100%");
		$('.clientPeopleBtn').empty();
		clientPeopleEditable = false;
	}
	if(testPeopleEditable == true){
		$(".testPeopleDiv").css("width","83%");
		$("#testPeople_chosen").css("width","100%");
		$('.testPeopleBtn').empty();
		testPeopleEditable = false;
	}
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
		statusStr = 'WRONG STATUS!';
		break;
	}
	
	return statusStr;
}

function getProductUsers(){
	var productId = $('#productId').val().trim();
//	var productName = $("#productName").val();
	var data = {};
	data.productId = productId;
	
	$.ajax({
		url: '/idoc/inter/users.html',
		type: 'POST',
		async: false,
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
	interfaceForm.url = interObj.url;
	interfaceForm.desc = interObj.desc;
	interfaceForm.retParamExample = interObj.retParamExample;
	interfaceForm.isNeedInterfaceTest = interObj.isNeedInterfaceTest;
	interfaceForm.isNeedPressureTest = interObj.isNeedPressureTest;
	interfaceForm.expectTestTime = interObj.expectTestTime;
	interfaceForm.expectOnlineTime = interObj.expectOnlineTime;
	interfaceForm.reqPeopleIds = interObj.reqPeopleIds;
	interfaceForm.frontPeopleIds = interObj.frontPeopleIds;
	interfaceForm.behindPeopleIds = interObj.behindPeopleIds;
	interfaceForm.clientPeopleIds = interObj.clientPeopleIds;
	interfaceForm.testPeopleIds = interObj.testPeopleIds;
	interfaceForm.reqParams = interObj.reqParams;
	interfaceForm.retParams = interObj.retParams;
	interfaceForm.ftlTemplate = interObj.ftlTemplate;
	
	return interfaceForm;
}

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}
function generateFtlFakeData(mockDataObj){
	var ftlStr = '<#-- 前端假数据 	------------------------------------------------>\n';
	for(var v in mockDataObj){
		ftlStr += '<#assign ' + v + ' = ' + JSON.stringify(mockDataObj[v], null, 4) + '/>\n';
	}
	
	return ftlStr;
}
function changeOnlineModule(){
	var content = moduleList;
	var moduleId = $("#importModule").val();
	if(moduleId==""){
		$("#importPage").empty();
		$("#importInterface").empty();
	}
	var len = content.length;
	for(var i=0; i<len; i++){
		var module = content[i];
		var id = module.onlineModuleId;
		if(id == moduleId){
			var pageList = module.pageList;
			$("#importPage").empty();
			var pages = "<option value=\"\"></option>";
			var pageLen = pageList.length;
			for(var j=0; j<pageLen; j++){
				var page = pageList[j];
				pages += "<option value=\""+page.onlinePageId+"\">" + page.pageName + "</>";
			}
			$("#importPage").append(pages);
		}
	}
}
function changeOnlinePage(){
	var content = moduleList;
	var moduleId = $("#importModule").val();
	var pageId = $("#importPage").val();
	var len = content.length;
	for(var i=0; i<len; i++){
		var module = content[i];
		var id = module.onlineModuleId;
		if(id == moduleId){
			var pageList = module.pageList;
			var pageLen = pageList.length;
			for(var j=0; j<pageLen; j++){
				var page = pageList[j];
				var page_id = page.onlinePageId;
				if(page_id==pageId){
					$("#importInterface").empty();
					var interList = page.interfaceList;
					var interLen = interList.length;
					var inters = "<option value=\"\"></option>";
					for(var m=0; m<interLen; m++){
						var interf = interList[m];
						inters += "<option value=\""+interf.interfaceId+"\">" + interf.interfaceName + "</>";
					}
					$("#importInterface").append(inters);
				}
			}
		}
	}
}
//导入在线接口
function importOnlineInterfaces(){
	var moduleId = $("#importModule").val();
	var pageId = $("#importPage").val();
	var interfaceId = $("#importInterface").val();
	if(moduleId==""){
		bootbox.alert("请选择需要导入的模块！");
		return;
	}
	if(pageId==""){
		bootbox.alert("请选择需要导入的页面！");
		return;
	}
	if(interfaceId==""){
		bootbox.alert("请选择需要导入的接口！");
		return;
	}
	var data = {};
	data.onlineModuleId = moduleId;
	data.onlinePageId = pageId;
	data.onlineInterfaceId = interfaceId;
	data.projectId = $("#projectId").val();
	$.ajax({
		url: '/idoc/inter/importOnlineInterface.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 1){
				bootbox.alert("接口导入成功！");
				window.location.reload();
			}else if(json.retCode == 2){
				bootbox.alert("该接口已存在，导入失败！");
			}else{
				bootbox.alert("导入失败！");
			}
		}
	});
}

function batchImportOnlineInterfaces(){
	var moduleId = $("#importModule").val();
	var pageId = $("#importPage").val();
	var interfaceId = $("#importInterface").val();
	if(moduleId==""){
		bootbox.alert("请选择需要导入的模块！");
		return;
	}
	var data = {};
	data.onlineModuleId = moduleId;
	data.onlinePageId = pageId;
	data.onlineInterfaceId = interfaceId;
	data.projectId = $("#projectId").val();
	$.ajax({
		url: '/idoc/inter/batchImportOnlineInterface.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 1){
				bootbox.alert("接口导入成功！");
				window.location.reload();
			}else if(json.retCode == 200){
				bootbox.alert("接口导入成功！成功导入" + json.interfaceNum.successNum + "个接口，失败" + json.interfaceNum.failedNum + "个接口，已经存在" + json.interfaceNum.existNum + "个接口。");
				window.location.reload();
			}else{
				bootbox.alert("导入失败！");
			}
		}
	});
}

function createPostmanObj(collectionId, requestId, mockDataObj){
	var postmanObj = {};
	postmanObj.id = collectionId;
	postmanObj.name = $('#interName').val().trim();
	postmanObj.description = '';
	postmanObj.order = [];
	postmanObj.order.push(requestId);
	postmanObj.folders = [];
	postmanObj.timestamp = new Date().getTime();
	postmanObj.owner = '0';
	postmanObj.remoteLink = '';
	postmanObj.public = false;
	postmanObj.requests = [];
	
	var request = {};
	
	var url;
	if($('#requestType').val() > 1){
		url = $('#url').val().trim();
		//request.dataMode = $('#dataMode').val();
		request.dataMode = $('input[name="dataMode"]:checked').val();
		if(request.dataMode == 'raw'){
			request.rawModeData = '' + JSON.stringify(mockDataObj, null, 4);
		}
	}else{
		url = $('#url').val().trim() + '?';
		for(var p in mockDataObj){
			url += p;
			if(mockDataObj[p]){
				url += '=' + mockDataObj[p];
			}
			
			url += '&';
		}
		url = url.substring(0, url.length - 1);
		
		request.dataMode = 'params';
	}
	
	request.id = requestId;
	request.url = url;
	request.method = $('#requestType option:selected').text();
	request.headers = '';
	request.data = [];
	if(request.dataMode == 'urlencoded'){
		for(var p in mockDataObj){
			var dataObj = {};
			dataObj.key = p;
			dataObj.value = mockDataObj[p];
			dataObj.type = 'text';
			dataObj.enabled = true;
			
			request.data.push(dataObj);
		}
	}
	request.tests = '';
	request.preRequestScript = '';
	request.currentHelper = 'normal';
	request.pathVariables = {};
	request.version = 1;
	request.name = url;
	request.description = '';
	request.descriptionFormat = 'html';
	request.collectionId = collectionId;
	
	postmanObj.requests.push(request);
	
	return postmanObj;
}
/**
 * 显示字典参数
 */
function showDictParam(paramId, dictId){
	if(paramId == "undefined" || dictId == "undefined"){
		bootbox.alert("请保存接口后，再进行此操作！");
		return;
	}
	if($("#isEditableInfor").text()=="当前接口已被您锁定，请尽快完成编辑。"){
		bootbox.alert("请保存接口后，再进行此操作！");
		return;
	}
	var interfaceId = $("#interId").val();
	var data = {};
	data.interfaceId = interfaceId;
	data.dictId = dictId;
	data.paramId = paramId;
	data.status = 1;
	
	$.ajax({
		url: '/idoc/inter/operateDictParam.html',
		type: 'POST',
		contentType: "application/json",
		dataType: 'json',
		data: JSON.stringify(data),
		success:function(json){
			if(json.retCode==200){
				bootbox.alert("显示参数操作成功！");
				initInterfaceInfo(interfaceId);
			}else{
				bootbox.alert("显示参数操作失败！");
			}
		}
	});
}
/**
 * 隐藏字典参数
 */
function hideDictParam(paramId, dictId){
	if(paramId == "undefined" || dictId == "undefined"){
		bootbox.alert("请保存接口后，再进行此操作！");
		return;
	}
	if($("#isEditableInfor").text()=="当前接口已被您锁定，请尽快完成编辑。"){
		bootbox.alert("请保存接口后，再进行此操作！");
		return;
	}
	var interfaceId = $("#interId").val();
	var data = new Object();
	data.interfaceId = interfaceId;
	data.dictId = dictId;
	data.paramId = paramId;
	data.status = 0;
	
	$.ajax({
		url: '/idoc/inter/operateDictParam.html',
		type: 'POST',
		contentType: "application/json",
		dataType: 'json',
		data: JSON.stringify(data),
		success:function(json){
			if(json.retCode==200){
				bootbox.alert("隐藏参数操作成功！");
				initInterfaceInfo(interfaceId);
			}else{
				bootbox.alert("隐藏参数操作失败！");
			}
		}
	});
}
function setEditPeople(interId,editPeople){
	var data = {};
	data.interfaceId = interId;
	data.editPeople = editPeople;
	$.ajax({
		url: '/idoc/inter/setEditPeople.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				var isEditableInforHtml = "当前接口已被您锁定，请尽快完成编辑。";
				$("#isEditableInfor").html(isEditableInforHtml);
				var status = getInterStatus(interId);
				var headBtnsHtml = '';
				if(status != 1)
					headBtnsHtml = '<button class="btn btn-success btn-sm" id="saveForNow">暂存</button>&nbsp;&nbsp;' +
						'<button class="btn btn-success btn-sm" id="save">保存</button>&nbsp;&nbsp;' +
						'<button class="btn btn-warning btn-sm" id="cancel">取消</button>';
				else{
					headBtnsHtml = '<button class="btn btn-success btn-sm" id="save">保存</button>&nbsp;&nbsp;' +
						'<button class="btn btn-warning btn-sm" id="cancel">取消</button>';
				}
				$('#headBtns').html(headBtnsHtml);
				$('#footBtns').html(headBtnsHtml);
				setInterfaceInfoReadonly(false);
				
				$('.chosen-select').chosen('destroy');
				setTimeout(function(){
					$(".chosen-select").chosen();
				}, 200);
				// 设置下面的状态让多选下拉框改变时不会在后面显示保存取消按钮
				setMultiSelectWhenEdit();
				param.setEditMode(true);
			}else{
				var isEditableInforHtml = "获取当前接口编辑权限失败，请重试。";
				$("#isEditableInfor").html(isEditableInforHtml);
			}
		}
	});
}
function getInterStatus(interId){
	var data = {};
	var status;
	data.interId = interId;
	$.ajax({
		url: '/idoc/inter/getInterStatus.html',
		data: data,
		async: false,
		type: 'POST',
		dataType: 'json',
		success: function(json){
			if(json.retCode == 200){
				status = json.status;
			}else{
				alert("接口状态查询失败。");
				status = 0;
			}
		}
	});
	return status;
}

function closeAndOpen(){
	var status = $("#closeAndOpen").attr("status");
	if(status == "close"){//收起
		$("#closeAndOpen").html(">>");
		$("#closeAndOpen").attr("status","open");
		$("#col9").css("width","97%");
		$("#col3").css("width","3%");
		var treeWidth = $("#col3").width();
		if(treeWidth < 16){
			$("#col9").css("width","96%");
			$("#col3").css("width","4%");
		}
		$("#reqPeople_chosen").css("width","100%");
		$("#frontPeople_chosen").css("width","100%");
		$("#behindPeople_chosen").css("width","100%");
		$("#clientPeople_chosen").css("width","100%");
		$("#testPeople_chosen").css("width","100%");

	}else{
		$("#closeAndOpen").html("<<");
		$("#closeAndOpen").attr("status","close");
		$("#col9").css("width","75%");
		$("#col3").css("width","25%");	
	}
}

function descCloseAndOpen(){
	var status = $("#desc_closeAndOpen").attr("value");
	if(status == "close"){
		$("#cke_1_contents").css("height","10px");
		$("#desc_closeAndOpen").attr("value","open");
		$("#desc_closeAndOpen").html("展开");
	}else if(status == "open"){
		$("#cke_1_contents").css("height","550px");
		$("#desc_closeAndOpen").attr("value","close");
		$("#desc_closeAndOpen").html("收起");
	}
}

function returnCloseAndOpen(element1,element2){
	var status = $("#" + element1).attr("value");
	if(status == "close"){
		$("#" + element2).css("height","10px");
		$("#" + element1).attr("value","open");
		$("#" + element1).html("展开");
	}else if(status == "open"){
		$("#" + element2).css("height","550px");
		$("#" + element1).attr("value","close");
		$("#" + element1).html("收起");
	}
}

function initInterfaceRedis(redisList){
	$("#newRedis").css("display","none");
	$("#showRedis").addClass("hidden");
	$("#addOneRedis").addClass("hidden");
	$("#table_redis tbody").empty();
	if(!redisList){
		$("input[name='redis'][value='0']").prop("checked",true);
	}else{
		$("input[name='redis'][value='1']").prop("checked",true);
		$("#showRedis").removeClass("hidden");
		for(var i=0;i<redisList.length;i++){
			var redis = redisList[i];
			var str = "<tr>" +
				"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存Key' value='" + redis.redisKey + "'></td>" +
				"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存数据' value='" + redis.redisInfo + "'></td>" +
				"<td><input class='form-control edit-field' type='text' placeholder='请填写缓存策略' value='" + redis.redisTactics + "'></td>" +
				"<td><button class='btn btn-warning btn-sm' onclick='deleteOneRedis(" + redis.redisId + ",this)'>删除</button>"+
				"<input type='hidden' value='" + redis.redisId +"'>" +
				"</td>" +
				"</tr>";
			$("#table_redis tbody").append(str);
		}
	}
}

function initParams(reqParams,retParams,lastReqParams,lastRetParams){
	if(reqParams!=null && reqParams.length>0){
		for(var i=0;i<reqParams.length;i++){
			var reqFlag = 1;
			var reqParam = reqParams[i];
			var lastReqParam;
			for(var j=0;lastReqParams!=null && j<lastReqParams.length;j++){
				if(lastReqParams[j].paramId != reqParam.paramId){
					continue;
				}else{
					lastReqParam = lastReqParams[j];
					break;
				}
			}
			if(lastReqParam && compareParam(reqParam,lastReqParam)){
				reqFlag = 0;
			}
			if(reqFlag == 1){
				$("#td-param-param-name-" + reqParam.uniqueId).css("color","red");
			}
			if(reqParam.dict){
				if(!lastReqParam || lastReqParam.dict==null || lastReqParam.dict.params==null || lastReqParam.dict.params.length<=0){
					initParams(reqParam.dict.params,null,null,null);
				}else{
					initParams(reqParam.dict.params,null,lastReqParam.dict.params,null);
				}
			}
		}
	}
	if(retParams!=null && retParams.length>0){
		for(var i=0;i<retParams.length;i++){
			var retFlag = 1;
			var retParam = retParams[i];
			var lastRetParam;
			for(var j=0;lastRetParams!=null && j<lastRetParams.length;j++){
				if(lastRetParams[j].paramId != retParam.paramId){
					continue;
				}else{
					lastRetParam = lastRetParams[j];
					break;
				}
			}
			if(lastRetParam && compareParam(retParam,lastRetParam)){
				retFlag = 0;
			}
			if(retFlag == 1){
				$("#td-param-param-name-" + retParam.uniqueId).css("color","red");
			}
			if(retParam.dict){
				if(!lastRetParam || lastRetParam.dict==null || lastRetParam.dict.params==null || lastRetParam.dict.params.length<=0){
					initParams(null,retParam.dict.params,null,null);
				}else{
					initParams(null,retParam.dict.params,null,lastRetParam.dict.params);
				}
			}
		}
	}
}

//比较两个参数是否相等
function compareParam(param, comParam){ // 此函数没有比较字典该字段是否显示的变化
	if(param.paramName == comParam.paramName && param.paramDesc == comParam.paramDesc && param.paramType == comParam.paramType 
			&& param.isNecessary == comParam.isNecessary && param.remark == comParam.remark && param.mock == comParam.mock){
		return true;
	}
	return false;
}
window.onbeforeunload = function(){
	if($('#footBtns').text().trim()){
		return bootbox.alert("请先保存或者取消您的接口");		
	}
}