/**
 * 接口历史信息页面js
 */
$(document).ready(function(){
	if($('#interfaceId').val() == ''){
		showAlertMessage('当前接口id不存在，请联系管理员。');
		return;
	}
	if($('#productId').val() == ''){
		showAlertMessage('当前产品id不存在，请联系管理员。');
		return;
	}
	
	$("[data-toggle='popover']").popover(); // 提示信息
	
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
	
	$("#interfaceHistoryTree").jstree({
		"core" : {
			"animation" : "fast"
		},
		"themes" : {
			"theme" : "default",
			"url" : "/js/common/jsTree/themes/default/style.css",
			"dots" : true,
			"icons" : false,
		},
		"plugins" : [ "themes", "html_data", "ui", "crrm"]
	});
	
	// 展开所有和折叠所有
	var expandAllHtml = '<button type="button" id="expandAll" title="展开所有" class="btn btn-xs btn-primary"><i class="glyphicon glyphicon-plus"></i></button>';
	$('#collapseTree').html(expandAllHtml);
	
	$('#collapseTree').delegate('#expandAll', 'click', function(){
		$("#interfaceHistoryTree").jstree("open_all");
		var collapeAllHtml = '<button type="button" id="collapseAll" title="收起所有" class="btn btn-xs btn-primary"><i class="glyphicon glyphicon-minus"></i></button>';
		$('#collapseTree').html(collapeAllHtml);
	});
	$('#collapseTree').delegate('#collapseAll', 'click',function(){
		$("#interfaceHistoryTree").jstree("close_all");
		$('#collapseTree').html(expandAllHtml);
	});
	
	getProductUsers();
	
	getCurrentInter();
	
 });

function getCurrentInter(){
	var data = {};
	data.interfaceId = $('#interfaceId').val();
	$.ajax({
		url: '/idoc/onlineInter/searchInterface.html',
		type: 'GET',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				_currentInterObj = json.inter; // 当前接口信息，用于比较历史版本
			}else{
				showAlertMessage('查询到的当前接口信息为空！');
			}
		}
	});
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

function initVersionInfo(versionId, formOnclick){	
//	if(formOnclick && $('#versionId').val() === versionId){
//		return;
//	}
	setInterfacePageColor(); // 隐藏比较的差异
	var data = {};
	data.versionId = versionId;
	$.ajax({
		url: '/idoc/onlineInter/searchInterfaceVersion.html',
		type: 'GET',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				var snapshot = json.version.snapshot;
				var versionNum = json.version.versionNum;
				if(isNullOrEmpty(snapshot)){
					showAlertMessage('查询到的接口历史版本信息快照为空！');
					return;
				}
				var inter = JSON.parse(snapshot);
				if(inter != null){
					displayInterVersion(inter, versionNum);
				}
			}else{
				showAlertMessage('查询接口历史版本信息失败！');
			}
		}
	});

}

function compareInterVersion(){
	var selectedNode = $('#interfaceHistoryTree').jstree('get_selected');
	
	if(selectedNode.length == 0 || !$(selectedNode).attr('id').startsWith('version-')){
		bootbox.alert({
			message: '请在左侧选择需要需要比较的接口',
			buttons:{
				ok:{
					label: '确定',
					className: 'btn-primary'
				}
			}
		});
		
		return;
	}
	var sourceInterVersionId = $(selectedNode).attr('version');
	var selectedInterVersionId = $('#compareSelect').val();
	if(sourceInterVersionId == selectedInterVersionId){
		return;
	}else if(selectedInterVersionId == "v0"){
		if(_interObj != null && _currentInterObj != null){
			compareHistoryVersion(_interObj, _currentInterObj, true);
		}else{
			showAlertMessage('当前版本或对比接口的历史版本为空！');
		}
	}else{
		var data = {};
		data.versionId = selectedInterVersionId;
		$.ajax({
			url: '/idoc/onlineInter/searchInterfaceVersion.html',
			type: 'GET',
			dataType: 'json',
			data: data,
			success: function(json){
				if(json.retCode == 200){
					var snapshot = json.version.snapshot;
					if(isNullOrEmpty(snapshot)){
						showAlertMessage('查询到接口[' + selectedInterVersionId + ']历史版本信息快照为空！');
						return;
					}
					var interDest = JSON.parse(snapshot);
					if(_interObj != null && interDest != null){
						compareHistoryVersion(_interObj, interDest, false);
					}else{
						showAlertMessage('当前选中版本或对比的历史版本为空！');
					}
				}else{
					showAlertMessage('查询对比接口的历史版本信息失败！');
				}
			}
		});
	}
}

// 显示选中的历史版本信息
function displayInterVersion(inter,versionNum){
	$('#hint').css({'display': 'none'});
	$('#footBtns').html('');
	$('#interMain').css({'display': ''});
	$('#interId').val(inter.interfaceId);
	$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
	
	$('#interName').val(inter.interfaceName);
	
	var statusStr = getStatus(inter.interfaceStatus);
	$('#lb_interStatus').text(statusStr);
	$('#lb_interStatus').css('font-size',16);
	$('#lb_creator').text(inter.creator.userName);
	$('#lb_createTime').text(getDate(inter.createTime.time));
	
	$('#lb_editVersion').text(inter.onlineVersion);
	$('#lb_iterVersion').text(inter.iterVersion);
	
	$('#interType').val(inter.interfaceType);
	$('#requestType').val(inter.requestType);
	$('#url').val(inter.url);
	
	_interObj = inter;
	
	//控制ftl模板显示还是隐藏
	if(inter.interfaceType == 2){
		$("#ftl_template_div").show();
		$("#inter_ftl_template").val(inter.ftlTemplate);
	}else{
		$("#ftl_template_div").hide();
		$("#inter_ftl_template").val("");
	}
	
	param.init(inter.reqParams, inter.retParams,null, null, true);
	
	if(inter.retParamExample == null)
		$('#retParamExample').text('');
	else
		$('#retParamExample').text(inter.retParamExample);
	
	$('#isNeedInterfaceTest').val(inter.isNeedInterfaceTest);
	$('#isNeedInterfaceTest').change();
	
	$('#isNeedPressureTest').val(inter.isNeedPressureTest);
	$('#isNeedPressureTest').change();
	
	if(!isNullOrEmpty(inter.expectTestTime)){
		var expectTestTime = getDate(inter.expectTestTime.time);
		expectTestTime = expectTestTime.substring(0, 10);
		$('#expectTestTime').val(expectTestTime);
		_interObj.expectTestTime = inter.expectTestTime.time;
	}
	
	if(!isNullOrEmpty(inter.expectOnlineTime)){
		var expectOnline = getDate(inter.expectOnlineTime.time);
		expectOnline = expectOnline.substring(0, 10);
		$('#expectOnlineTime').val(expectOnline);
		_interObj.expectOnlineTime = inter.expectOnlineTime.time;
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
	setTimeout(function(){
		_editor.setData(inter.desc);
		$(".chosen-select").chosen();
		setInterfaceInfoReadonly(true);
	}, 500);
	
	$('#creatorId').val(inter.creatorId);
}

function setInterfacePageColor(){
	$('#interName').css('border', 'none');
	$('#lb_interStatus').css('border', 'none');
	$('#lb_creator').css('border', 'none');
	$('#lb_createTime').css('border', 'none');
	$('#lb_editVersion').css('border', 'none');
	$('#lb_iterVersion').css('border', 'none');
	$('#interType').css('border', 'none');
	$('#requestType').css('border', 'none');
	$('#url').css('border', 'none');
	$("#inter_ftl_template").css('border', 'none');
	$('#retParamExample').css('border', 'none');
	$('#isNeedInterfaceTest').css('border', 'none');
	$('#isNeedPressureTest').css('border', 'none');
	$('#expectTestTime').css('border', 'none');
	$('#expectOnlineTime').css('border', 'none');
	$('#descDiv').css('border', 'none');
	$('#reqPeoples').css('border', 'none');
	$('#clientPeoples').css('border', 'none');
	$('#frontPeoples').css('border', 'none');
	$('#behindPeoples').css('border', 'none');
	$('#testPeoples').css('border', 'none');
}

// interSrc源接口，显示其和目标接口interDest的差异
function compareHistoryVersion(interSrc, interDest, isCurrent){ // isCurrent 是否是当前版本
	setInterfacePageColor();
	var inter = interSrc;
	$('#hint').css({'display': 'none'});
	$('#footBtns').html('');
	$('#interMain').css({'display': ''});
	$('#interId').val(inter.interfaceId);
	$('#moduleTree').jstree('select_node', $('#inter-' + inter.interfaceId));
	
	if(inter.interfaceName != interDest.interfaceName){
		$('#interName').css('border', '2px solid red');
	}
	$('#interName').val(inter.interfaceName);
	
	var statusStr = getStatus(inter.interfaceStatus);
	if(inter.interfaceStatus != interDest.interfaceStatus){
		$('#lb_interStatus').css('border', '2px solid red');
	}
	$('#lb_interStatus').text(statusStr);
	
	$('#lb_interStatus').css('font-size',16);
	if(inter.creator.userName != interDest.creator.userName){
		$('#lb_creator').css('border', '2px solid red');
	}
	$('#lb_creator').text(inter.creator.userName);
	
	if((isCurrent && inter.createTime.time != interDest.createTime) || (!isCurrent && inter.createTime.time != interDest.createTime.time)){
		$('#lb_createTime').css('border', '2px solid red');
	}
	$('#lb_createTime').text(getDate(inter.createTime.time));
	
	if(inter.editVersion != interDest.editVersion){
		$('#lb_editVersion').css('border', '2px solid red');
	}
	$('#lb_editVersion').text(inter.editVersion);
	
	if(inter.iterVersion != interDest.iterVersion){
		$('#lb_iterVersion').css('border', '2px solid red');
	}
	$('#lb_iterVersion').text(inter.iterVersion);
	
	if(inter.interfaceType != interDest.interfaceType){
		$('#interType').css('border', '2px solid red');
	}
	$('#interType').val(inter.interfaceType);
	if(inter.requestType != interDest.requestType){
		$('#requestType').css('border', '2px solid red');
	}
	$('#requestType').val(inter.requestType);
	
	if(inter.url != interDest.url){
		$('#url').css('border', '2px solid red');
	}
	$('#url').val(inter.url);
	
	//控制ftl模板显示还是隐藏
	if(inter.interfaceType == 2){
		$("#ftl_template_div").show();
		if(inter.ftlTemplate != interDest.ftlTemplate){
			$("#inter_ftl_template").css('border', '2px solid red');
		}
		$("#inter_ftl_template").val(inter.ftlTemplate);
	}else{
		$("#ftl_template_div").hide();
		$("#inter_ftl_template").val("");
	}
	
	param.init(inter.reqParams, inter.retParams, interDest.reqParams, interDest.retParams, false);
	
	if(inter.retParamExample == null){
		$('#retParamExample').text('');
		if(!isNullOrEmpty(interDest.retParamExample)){
			$('#retParamExample').css('border', '2px solid red');
		}
	}
	else{
		if(inter.retParamExample != interDest.retParamExample){
			$('#retParamExample').css('border', '2px solid red');
		}
		$('#retParamExample').text(inter.retParamExample);
	}
	
	if(inter.isNeedInterfaceTest != interDest.isNeedInterfaceTest){
		$('#isNeedInterfaceTest').css('border', '2px solid red');
	}
	$('#isNeedInterfaceTest').val(inter.isNeedInterfaceTest);
	$('#isNeedInterfaceTest').change();
	
	if(inter.isNeedPressureTest != interDest.isNeedPressureTest){
		$('#isNeedPressureTest').css('border', '2px solid red');
	}
	$('#isNeedPressureTest').val(inter.isNeedPressureTest);
	$('#isNeedPressureTest').change();
	
	if(!isNullOrEmpty(inter.expectTestTime)){
		var expectTest = getDate(inter.expectTestTime);
		expectTest = expectTest.substring(0, 10);
		if((isCurrent && inter.expectTestTime != interDest.expectTestTime) || (!isCurrent && inter.expectTestTime != interDest.expectTestTime.time)){
			$('#expectTestTime').css('border', '2px solid red');
		}
		$('#expectTestTime').val(expectTest);
	}else{
		if((isCurrent && interDest.expectTestTime != null) || (!isCurrent && interDest.expectTestTime.time != null)){
			$('#expectTestTime').css('border', '2px solid red');
		}
	}
	
	if(!isNullOrEmpty(inter.expectOnlineTime)){
		var expectOnline = getDate(inter.expectOnlineTime);
		expectOnline = expectOnline.substring(0, 10);
		if((isCurrent && inter.expectOnlineTime != interDest.expectOnlineTime) || (!isCurrent && inter.expectOnlineTime != interDest.expectOnlineTime.time)){
			$('#expectOnlineTime').css('border', '2px solid red');
		}
		$('#expectOnlineTime').val(expectOnline);
	}else{
		if((isCurrent && interDest.expectOnlineTime != null) || (!isCurrent && interDest.expectOnlineTime.time != null)){
			$('#expectOnlineTime').css('border', '2px solid red');
		}
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
	
	var reqPeoples = [];
	var frontPeoples = [];
	var behindPeoples = [];
	var clientPeoples = [];
	var testPeoples = [];
	for(var i = 0; inter.reqPeoples && i < inter.reqPeoples.length; i++){
		reqPeoples.push(inter.reqPeoples[i].userId);
		for(j = 0; reqPeople[0].options && j < reqPeople[0].options.length; j++){
			var user = inter.reqPeoples[i];
			if(user.userId == reqPeople[0].options[j].value){
				reqPeople[0].options[j].selected = 'selected';
				break;
			}
		}
	}
	
	for(var i = 0; inter.frontPeoples && i < inter.frontPeoples.length; i++){
		frontPeoples.push(inter.frontPeoples[i].userId);
		for(j = 0; frontPeople[0].options && j < frontPeople[0].options.length; j++){
			var user = inter.frontPeoples[i];
			if(user.userId == frontPeople[0].options[j].value){
				frontPeople[0].options[j].selected = 'selected';
				break;
			}
		}
	}
	
	for(var i = 0; inter.behindPeoples && i < inter.behindPeoples.length; i++){
		behindPeoples.push(inter.behindPeoples[i].userId);
		for(j = 0; behindPeople[0].options && j < behindPeople[0].options.length; j++){
			var user = inter.behindPeoples[i];
			if(user.userId == behindPeople[0].options[j].value){
				behindPeople[0].options[j].selected = 'selected';
				break;
			}
		}
	}
	
	for(var i = 0; inter.clientPeoples && i < inter.clientPeoples.length; i++){
		clientPeoples.push(inter.clientPeoples[i].userId);
		for(j = 0; clientPeople[0].options && j < clientPeople[0].options.length; j++){
			var user = inter.clientPeoples[i];
			if(user.userId == clientPeople[0].options[j].value){
				clientPeople[0].options[j].selected = 'selected';
				break;
			}
		}
	}
	
	for(var i = 0; inter.testPeoples && i < inter.testPeoples.length; i++){
		testPeoples.push(inter.testPeoples[i].userId);
		for(j = 0; testPeople[0].options && j < testPeople[0].options.length; j++){
			var user = inter.testPeoples[i];
			if(user.userId == testPeople[0].options[j].value){
				testPeople[0].options[j].selected = 'selected';
				break;
			}
		}
	}
	
	var _reqPeoples = [];
	var _frontPeoples = [];
	var _behindPeoples = [];
	var _clientPeoples = [];
	var _testPeoples = []; 
	for(var i = 0; interDest.reqPeoples && i < interDest.reqPeoples.length; i++){
		_reqPeoples.push(interDest.reqPeoples[i].userId);
	}
	for(var i = 0; interDest.frontPeoples && i < interDest.frontPeoples.length; i++){
		_frontPeoples.push(interDest.frontPeoples[i].userId);
	}
	for(var i = 0; interDest.behindPeoples && i < interDest.behindPeoples.length; i++){
		_behindPeoples.push(interDest.behindPeoples[i].userId);
	}
	for(var i = 0; interDest.clientPeoples && i < interDest.clientPeoples.length; i++){
		_clientPeoples.push(interDest.clientPeoples[i].userId);
	}
	for(var i = 0; interDest.testPeoples && i < interDest.testPeoples.length; i++){
		_testPeoples.push(interDest.testPeoples[i].userId);
	}
	
	setTimeout(function(){
		if(inter.desc != interDest.desc){
			$('#descDiv').css('border', '2px solid red');
		}
		_editor.setData(inter.desc);
		$(".chosen-select").chosen();
		setInterfaceInfoReadonly(true);
		
		if(_reqPeoples.toString() != reqPeoples.toString()){
			$('#reqPeoples').css('border', '2px solid red');
		}
		if(_frontPeoples.toString() != frontPeoples.toString()){
			$('#frontPeoples').css('border', '2px solid red');
		}
		if(_behindPeoples.toString() != behindPeoples.toString()){
			$('#behindPeoples').css('border', '2px solid red');
		}
		if(_clientPeoples.toString() != clientPeoples.toString()){
			$('#clientPeoples').css('border', '2px solid red');
		}
		if(_testPeoples.toString() != testPeoples.toString()){
			$('#testPeoples').css('border', '2px solid red');
		}
	}, 500);
	
	$('#creatorId').val(inter.creatorId);
}


function setInterfaceInfoReadonly(enable){
	$('#interName').attr("readonly",enable);
	$('#interType').attr("disabled",enable);
	$('#requestType').attr("disabled",enable);
	$('#url').attr("readonly",enable);
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

function getStatus(status){
	var statusStr = '';
	switch(status){
	case 10:
		statusStr = '暂存中';
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
							if(roleName.indexOf("产品")>=0 || roleName.indexOf("管理员")>=0)
								reqPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("前端")>=0 || roleName.indexOf("管理员")>=0)
								frontPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("后台")>=0 || roleName.indexOf("管理员")>=0)
								behindPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("客户端")>=0 || roleName.indexOf("管理员")>=0)
								clientPeople[0].options.add(new Option(user.userName, user.userId));
							if(roleName.indexOf("测试")>=0 || roleName.indexOf("管理员")>=0)
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

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}