var interfaceIds = [];
$(document).ready(function() {
	$("#selectAll").change(function(){
		if($(this).prop("checked")){
			$("[name='moduleCheckbox']").prop("checked",'true');
		}else{
			$("[name='moduleCheckbox']").removeProp("checked");
		}
	});
	var projectId = $('#projectId').val();
	$("#tongji").click(function(){
		interfaceIds = [];
		getAllInterfaceInProject(projectId);
	});
	getAllInterfaceInProject(projectId);
	// 导出word文档
	$('#exportInterInfo').on("click", function(){
		if(interfaceIds!=null){
			// createFileOnServer标志用于服务器上生成word文档，true生成，false返回流
			var url = '/idoc/statistics/exportInterInfo2Excel.html?' + 
				'interfaceIds=' + interfaceIds.join(',') + 
				'&projectId=' + projectId + 
				'&createFileOnServer=false';
			window.open(url);
		}else{
			bootbox.alert('该项目下没有任何接口！');
			return;
		}
	});
	 
	$('#exportInterInfo').poshytip({
		className: 'tip-yellow',
		content: ' <font color="#00BB00">Tips：</font>在下方勾选统计模块，<br>点击“开始统计”按钮，然后<br>再点击“导出接口信息”按钮<br>导出选择的模块内的接口信<br>息，同时可以选择多个模块。',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		keepInViewport: true,
		offsetX: 0,
		offsetY: 5
	});
});

function getAllInterfaceInProject(projectId) {
	var modules = "";
	var checked;
	if($('#selectedModule input:checked').length > 0){
		checked = $('#selectedModule input:checked');
		for(var i = 0;i < checked.length; i++){
			modules += checked[i].value + ',';
		}
		modules = modules.substr(0, modules.length - 1);
	}
	var data = {};
	var len = 0;
	data.projectId = $("#projectId").val();
	data.modules = modules;
	var statusIncludeInterfaces = new Array(13); // 每种状态对应的接口信息
	for(var i = 1; i < 13; i++){
		statusIncludeInterfaces[i] = new Array();
	}
	var flowIncludeInterfaces = new Array(5); // 接口进展统计每种情况对应的接口信息
	for(var i = 0; i < 5; i++){
		flowIncludeInterfaces[i] = new Array();
	}
	var backPeopleMap = {}; // 后台开发人员信息统计，key为用户英文名，value为数组，value[0]中文名，之后为接口的地址
	var backPeopleContent = []; // 后台开发人员对应的接口信息
	var clientPeopleMap = {};
	var clientPeopleContent = [];
	var frontPeopleMap = {};
	var frontPeopleContent = [];
	var testPeopleMap = {};
	var testPeopleContent = [];
	var interfaceForceReasonMap = {}; // 记录所有接口的部分信息用于强制回收原因统计，key为interfaceId，value为数组，分别记录：接口名称、接口地址、是否提测延期、是否上线延期
	var globalForceReason; // 记录查询的强制回收原因用于按原因分类
	var reasonTypeSum = new Array(7); // 统计每种强制回收原因的接口数量
	var forceReasonContent = []; // 记录强制回收原因分布图每种原因对应的悬浮框接口内容
	var reasonTypeMap = {};
	$.ajax({
		url : '/idoc/statistics/getAllInterfaceInProject.html',
		type : 'POST',
		data : data,
		dataType : 'json',
		success : function(json) {
			if (json.retCode == 200) {
				var list = json.interfaces;
				len = list.length;

				$("#table_project_interface tbody").empty();

				var interfaceSum = len;
				var testDelaySum = 0; // 预计提测延期个数
				var testDelayDays = 0; // 预计提测延期天数
				var onlineDelaySum = 0;
				var onlineDelayDays = 0;
				var ventureSum = 0; // 存在上线风险接口个数
				var normalOnlineSum = 0; // 正常上线接口个数
				var testingSum = 0; // 正常测试中接口个数
				var interfaceStatus = new Array(10); // 统计接口状态
				for(var i = 1; i < 13; i++){//10代表暂存 11代表前端审核通过（310） 12代表客户端审核通过（301）
					interfaceStatus[i] = 0;
				}
				for (var i = 0; i < len; i++) {
					var interf = list[i];
					var forceReasonArray = [];
					var str = "";
					var interfaceInfo = '<a href="/idoc/inter/index.html?projectId=' + $("#projectId").val() + '&interfaceId=' + interf.interfaceId + '" target="_blank">'
						+ interf.interfaceName
						+ '</a>';
					interfaceIds.push(interf.interfaceId);
					forceReasonArray.push(interf.interfaceName);
					forceReasonArray.push(interfaceInfo);
					str = '<tr id="interface-' + i +'">'
						+ '<td>' + interfaceInfo + '</td>'
						+ '<td>'
						+ getStatus(interf.interfaceStatus)
						+ '</td>';
					if(!isNullOrEmpty(interf.interfaceStatus) && interf.interfaceStatus > 0 && (interf.interfaceStatus <= 10 || interf.interfaceStatus == 310 || interf.interfaceStatus == 301)){
						if(interf.interfaceStatus == 310){
							interfaceStatus[11]++;
							statusIncludeInterfaces[11].push(interfaceInfo);
						}else if(interf.interfaceStatus == 301){
							interfaceStatus[12]++;
							statusIncludeInterfaces[12].push(interfaceInfo);
						}else{
							interfaceStatus[interf.interfaceStatus]++;
							statusIncludeInterfaces[interf.interfaceStatus].push(interfaceInfo);
						}
					}
					if(isNullOrEmpty(interf.reqPeopleIds)){
						str += '<td>'
							+ '</td>';
					}else{
						var reqPeoples = interf.reqPeoples;
						var req = '';
						for(var j = 0; j < reqPeoples.length; j++){
							req += reqPeoples[j].userName + ', ';
						}
						req = req.substring(0, req.lastIndexOf(","));
						str += '<td>'
							+ req
							+ '</td>';
					}
					if(isNullOrEmpty(interf.behindPeopleIds)){
						str += '<td>'
							+ '</td>';
					}else{
						var behindPeoples = interf.behindPeoples;
						var behind = '';
						for(var j = 0; j < behindPeoples.length; j++){
							behind += behindPeoples[j].userName + ', ';
							var englishName = behindPeoples[j].englishName;
							if(!isNullOrEmpty(englishName)){
								if(englishName in backPeopleMap){
									backPeopleMap[englishName].push(interfaceInfo);
								}else{
									var interfaces = [];
									interfaces.push(behindPeoples[j].userName);
									interfaces.push(interfaceInfo);
									backPeopleMap[englishName] = interfaces;
								}
							}
						}
						behind = behind.substring(0, behind.lastIndexOf(","));
						str += '<td>'
							+ behind
							+ '</td>';
					}
					
					if(isNullOrEmpty(interf.clientPeopleIds)){
						str += '<td>'
							+ '</td>';
					}else{
						var clientPeoples = interf.clientPeoples;
						var client = '';
						for(var j = 0; j < clientPeoples.length; j++){
							client += clientPeoples[j].userName + ', ';
							var englishName = clientPeoples[j].englishName;
							if(!isNullOrEmpty(englishName)){
								if(englishName in clientPeopleMap){
									clientPeopleMap[englishName].push(interfaceInfo);
								}else{
									var interfaces = [];
									interfaces.push(clientPeoples[j].userName);
									interfaces.push(interfaceInfo);
									clientPeopleMap[englishName] = interfaces;
								}
							}
						}
						client = client.substring(0, client.lastIndexOf(","));
						str += '<td>'
							+ client
							+ '</td>';
					}
					
					if(isNullOrEmpty(interf.frontPeopleIds)){
						str += '<td>'
							+ '</td>';
					}else{
						var frontPeoples = interf.frontPeoples;
						var front = '';
						for(var j = 0; j < frontPeoples.length; j++){
							front += frontPeoples[j].userName + ', ';
							var englishName = frontPeoples[j].englishName;
							if(!isNullOrEmpty(englishName)){
								if(englishName in frontPeopleMap){
									frontPeopleMap[englishName].push(interfaceInfo);
								}else{
									var interfaces = [];
									interfaces.push(frontPeoples[j].userName);
									interfaces.push(interfaceInfo);
									frontPeopleMap[englishName] = interfaces;
								}
							}
						}
						front = front.substring(0, front.lastIndexOf(","));
						str += '<td>'
							+ front
							+ '</td>';
					}
					
					if(isNullOrEmpty(interf.testPeopleIds)){
						str += '<td>'
							+ '</td>';
					}else{
						var testPeoples = interf.testPeoples;
						var test = '';
						for(var j = 0; j < testPeoples.length; j++){
							test += testPeoples[j].userName + ', ';
							var englishName = testPeoples[j].englishName;
							if(!isNullOrEmpty(englishName)){
								if(englishName in testPeopleMap){
									testPeopleMap[englishName].push(interfaceInfo);
								}else{
									var interfaces = [];
									interfaces.push(testPeoples[j].userName);
									interfaces.push(interfaceInfo);
									testPeopleMap[englishName] = interfaces;
								}
							}
						}
						test = test.substring(0, test.lastIndexOf(","));
						str += '<td>'
							+ test
							+ '</td>';
					}
					
					var expectTestTime = '';
					if(isNullOrEmpty(interf.expectTestTime)){
						str += '<td>'
							+ '</td>';
					}else{
						expectTestTime = getSimpleDate(interf.expectTestTime);
						str += '<td>'
							+ expectTestTime
							+ '</td>';
					}
					var realTestTime = '';
					if(isNullOrEmpty(interf.realTestTime)){
						str += '<td>'
							+ '</td>';
					}else{
						realTestTime = getSimpleDate(interf.realTestTime);
						str += '<td>'
							+ realTestTime
							+ '</td>';
					}
					var isDelay = false; // 是否延期
					// 提测延期天数
					if(!isNullOrEmpty(expectTestTime)){
						if(!isNullOrEmpty(realTestTime)){
							if(realTestTime > expectTestTime){ // 延期
								var delay = DateDiff(realTestTime, expectTestTime);
								str += '<td>'
									+ delay + ' 天'
									+ '</td>';
								isDelay = true;
								testDelaySum++;
								testDelayDays += delay;
								flowIncludeInterfaces[0].push(interfaceInfo);
								forceReasonArray.push('是');
							}else{
								str += '<td>'
									+ '</td>';
								forceReasonArray.push('否');
							}
						}else{
							var now = new Date();
							now = getSimpleDate(now);
							if(now > expectTestTime){ // 延期
								var delay = DateDiff(now, expectTestTime);
								str += '<td>'
									+ delay + ' 天'
									+ '</td>';
								isDelay = true;
								testDelaySum++;
								testDelayDays += delay;
								flowIncludeInterfaces[0].push(interfaceInfo);
								forceReasonArray.push('是');
							}else{
								str += '<td>'
									+ '</td>';
								forceReasonArray.push('否');
							}
						}
					}else{
						str += '<td>'
							+ '</td>';
						forceReasonArray.push('否');
					}
					
					var expectOnlineTime = '';
					if(isNullOrEmpty(interf.expectOnlineTime)){
						str += '<td>'
							+ '</td>';
					}else{
						expectOnlineTime = getSimpleDate(interf.expectOnlineTime);
						str += '<td>'
							+ expectOnlineTime
							+ '</td>';
					}
					var realOnlineTime = '';
					if(isNullOrEmpty(interf.realOnlineTime)){
						str += '<td>'
							+ '</td>';
					}else{
						realOnlineTime = getSimpleDate(interf.realOnlineTime);
						str += '<td>'
							+ realOnlineTime
							+ '</td>';
					}
					// 上线延期天数
					if(!isNullOrEmpty(expectOnlineTime)){
						if(!isNullOrEmpty(realOnlineTime)){
							if(realOnlineTime > expectOnlineTime){ // 延期
								var delay = DateDiff(realOnlineTime, expectOnlineTime);
								str += '<td>'
									+ delay + ' 天'
									+ '</td>';
								isDelay = true;
								onlineDelaySum++;
								onlineDelayDays += delay;
								flowIncludeInterfaces[1].push(interfaceInfo);
								forceReasonArray.push('是');
							}else{
								str += '<td>'
									+ '</td>';
								forceReasonArray.push('否');
							}
						}else{
							var now = new Date();
							now = getSimpleDate(now);
							if(now > expectOnlineTime){ // 延期
								var delay = DateDiff(now, expectOnlineTime);
								str += '<td>'
									+ delay + ' 天'
									+ '</td>';
								isDelay = true;
								onlineDelaySum++;
								onlineDelayDays += delay;
								flowIncludeInterfaces[1].push(interfaceInfo);
								forceReasonArray.push('是');
							}else{
								str += '<td>'
									+ '</td>';
								forceReasonArray.push('否');
							}
						}
					}else{
						str += '<td>'
							+ '</td>';
						forceReasonArray.push('否');
					}
					interfaceForceReasonMap[interf.interfaceId] = forceReasonArray;
					
					if(isDelay){
						str += '<td align="center">'
							+ '是'
							+ '</td>';
					}else{
						str += '<td align="center">'
							+ '否'
							+ '</td>';
					}
					str += '</tr>';
					$("#table_project_interface tbody").append(str);
					if(isDelay){
						ventureSum++;
						flowIncludeInterfaces[4].push(interfaceInfo);
					}else{
						if(interf.interfaceStatus == '9'){
							normalOnlineSum++;
							flowIncludeInterfaces[2].push(interfaceInfo);
						}else{
							testingSum++;
							flowIncludeInterfaces[3].push(interfaceInfo);
						}
					}
					if(!isNullOrEmpty(interf.interfaceStatus) && interf.interfaceStatus > 0){
						if(interf.interfaceStatus == 1 || interf.interfaceStatus == 10){
							//编辑
							$('#interface-' + i).css("background-color","#FEFFD6");
						}else if(interf.interfaceStatus == 2 || interf.interfaceStatus == 3 || interf.interfaceStatus == 310 || interf.interfaceStatus == 301){
							//审核
							$('#interface-' + i).css("background-color","#C8C7FF");
						}else if(interf.interfaceStatus >= 4 && interf.interfaceStatus <= 6){
							//测试
							$('#interface-' + i).css("background-color","#D4CED8");
						}else if(interf.interfaceStatus == 7 || interf.interfaceStatus == 8){
							//压测
							$('#interface-' + i).css("background-color","#80FFFF");
						}else{
							//上线
							$('#interface-' + i).css("background-color","#80FF93");
						}
					}
					$("#table_project_interface tr:first").css("background-color", "#F0F0F0");
				}
				
				// 填充接口汇总表格
				$("#table_interface_summary tbody").empty();
				var summaryHtml = '<tr>'
					+ '<td>'
					+ interfaceSum
					+ '</td>'
					+ '<td>'
					+ testDelaySum
					+ '</td>'
					+ '<td>'
					+ testDelayDays + ' 天'
					+ '</td>'
					+ '<td>'
					+ onlineDelaySum
					+ '</td>'
					+ '<td>'
					+ onlineDelayDays + ' 天'
					+ '</td>'
					+ '</tr>';
				$("#table_interface_summary tbody").append(summaryHtml);
				$("#table_interface_summary tr:first").css("background-color", "#F0F0F0");
				
				// 画接口状态分布图
				var interfaceStatusLine = [];
				for(var i = 1; i < 10; i++){
					var point = [getStatus(i), interfaceStatus[i]];
					interfaceStatusLine.push(point);
				}
				drawInterfaceStatusBarChart(interfaceStatusLine);
				
				// 画接口进展统计图
				var interfaceFlowLine = [];
				var testDealayPoint = ["提测延期", testDelaySum];
				var onlineDelayPoint = ["上线延期", onlineDelaySum];
				var normalOnlinePoint = ["正常上线", normalOnlineSum];
				var testingPoint = ["正常测试中", testingSum];
				var venturePoint = ["上线有风险", ventureSum];
				interfaceFlowLine.push(testDealayPoint);
				interfaceFlowLine.push(onlineDelayPoint);
				interfaceFlowLine.push(normalOnlinePoint);
				interfaceFlowLine.push(testingPoint);
				interfaceFlowLine.push(venturePoint);
				drawInterfaceFlowBarChart(interfaceFlowLine);
				
				// 画人员任务分布图
				var backData = [];
				for(key in backPeopleMap){
					var info = backPeopleMap[key];
					backData.push([info[0], info.length -1]);
					var content = '';
					for(var i = 1; i < info.length; i++){
						if(i == 1){
							content = '开发者： <font color="#00BB00">' + info[0] + '</font>&nbsp;&nbsp;&nbsp;&nbsp;开发接口个数： <font color="red">' + (info.length -1) + '</font><br>';
						}
						content += '接口名称：' + info[i] + '<br>';
					}
					backPeopleContent.push(content);
				}
				if(backData.length > 0)
					drawBackPeopleTaskPieChart(backData);
				
				var clientData = [];
				for(key in clientPeopleMap){
					var info = clientPeopleMap[key];
					clientData.push([info[0], info.length -1]);
					var content = '';
					for(var i = 1; i < info.length; i++){
						if(i == 1){
							content = '开发者： <font color="#00BB00">' + info[0] + '</font>&nbsp;&nbsp;&nbsp;&nbsp;开发接口个数： <font color="red">' + (info.length -1) + '</font><br>';
						}
						content += '接口名称：' + info[i] + '<br>';
					}
					clientPeopleContent.push(content);
				}
				if(clientData.length > 0)
					drawClientPeopleTaskPieChart(clientData);
				
				var frontData = [];
				for(key in frontPeopleMap){
					var info = frontPeopleMap[key];
					frontData.push([info[0], info.length -1]);
					var content = '';
					for(var i = 1; i < info.length; i++){
						if(i == 1){
							content = '开发者： <font color="#00BB00">' + info[0] + '</font>&nbsp;&nbsp;&nbsp;&nbsp;开发接口个数： <font color="red">' + (info.length -1) + '</font><br>';
						}
						content += '接口名称：' + info[i] + '<br>';
					}
					frontPeopleContent.push(content);
				}
				if(frontData.length > 0)
					drawFrontPeopleTaskPieChart(frontData);
				
				var testData = [];
				for(key in testPeopleMap){
					var info = testPeopleMap[key];
					testData.push([info[0], info.length -1]);
					var content = '';
					for(var i = 1; i < info.length; i++){
						if(i == 1){
							content = '测试者： <font color="#00BB00">' + info[0] + '</font>&nbsp;&nbsp;&nbsp;&nbsp;测试接口个数： <font color="red">' + (info.length -1) + '</font><br>';
						}
						content += '接口名称：' + info[i] + '<br>';
					}
					testPeopleContent.push(content);
				}
				if(testData.length > 0)
					drawTestPeopleTaskPieChart(testData);
				
				// 获取强制回收原因统计
				var forceData = {};
				var interfaceIdArray = [];
				for(key in interfaceForceReasonMap){
					interfaceIdArray.push(key);
				}
				forceData.interfaceIdArray = interfaceIdArray;
				$.ajax({
					url : '/idoc/statistics/getInterfaceForceBackReason.html',
					type : 'POST',
					data : forceData,
					dataType : 'json',
					traditional: true,
					success : function(json) {
						if (json.retCode == 200) {
							var forceBackReason = json.forceBackReason;
							globalForceReason = forceBackReason;
							$("#table_force_reason tbody").empty();
							if(forceBackReason != null && forceBackReason.length > 0){
								var str = '';
								for(var j = 0; j < 7; j++){
									reasonTypeSum[j] = 0;
								}
								for(var j = 0; j < forceBackReason.length; j++){
									var key = forceBackReason[j].changementReason - 1;
									reasonTypeSum[key]++;
									if(key in reasonTypeMap){
										reasonTypeMap[key].push(interfaceForceReasonMap[forceBackReason[j].interfaceId][1]);
									}else{
										var interfaces = [];
										interfaces.push(interfaceForceReasonMap[forceBackReason[j].interfaceId][1]);
										reasonTypeMap[key] = interfaces;
									}
								}
								for(key in interfaceForceReasonMap){
									for(var j = 0; j < forceBackReason.length; j++){
										var force = forceBackReason[j];
										if(key == force.interfaceId){
											str += '<tr> <td>'
												+ interfaceForceReasonMap[key][1]
												+ '</td> <td>'
												+ getReason(force.changementReason)
												+ '</td>';
											if(!force.remark){
												str += '<td>'
													+ '</td>';
											}else{
												str += '<td>'
													+ force.remark
													+ '</td>';
											}
											str += '<td>'
												+ force.operator.userName
												+ '</td>'
												+ '<td>'
												+ getDate(force.createTime)
												+ '</td>'
												+ '<td align="center">'
												+ interfaceForceReasonMap[key][2]
												+ '</td>'
												+ '<td align="center">'
												+ interfaceForceReasonMap[key][3]
												+ '</td>  </tr>';
										}
									}
								}
								
								$("#table_force_reason tbody").append(str);
								setForceReasonTableOddEvenColor();
							}
							
							// 画强制回收原因分布图
							var forceData = [];
							for(key in reasonTypeMap){
								var info = reasonTypeMap[key];
								var reasonInterface = [];
								var forceReasonName = getReason(parseInt(key) + 1);
								forceData.push([forceReasonName, reasonTypeSum[key]]);
								var content = '';
								for(var j = 0; j < info.length; j++){
									if(j == 0){
										content = '回收原因： <font color="#00BB00">' + forceReasonName + '</font>&nbsp;&nbsp;&nbsp;&nbsp;回收接口个数： <font color="red">' + reasonTypeSum[key] + '</font><br>';
									}
									// 如果接口已经存在则只在悬浮框中显示一个
									if($.inArray(info[j], reasonInterface) == -1){
										reasonInterface.push(info[j]);
										content += '接口名称：' + info[j] + '<br>';
									}
								}
								forceReasonContent.push(content);
							}
							drawForceReasonPieChart(forceData);
						}else if(json.retCode == 301){
							$("#table_force_reason tbody").html('<tr class="warning"><td colspan="7">没有查询到相关数据。</td></tr>');
							$("#orderByReason").attr('disabled', true);
						}
					}
				});
				
			} else {
				 $("#table_project_interface tbody").empty();
				 $("#table_project_interface tbody").html('<tr class="warning"><td colspan="14">没有查询到相关数据。</td></tr>');
			}
		}
	});
	
	$("#orderByReason").on('change', function(){
		$("#table_force_reason tbody").empty();
		if($("#orderByReason").prop("checked")){ 
			if(globalForceReason != null && globalForceReason.length > 0){
				str = '';
				var reasonTypeFlag = new Array(7);
				for(var j = 0; j < 7; j++){
					reasonTypeFlag[j] = true;
				}
				for(var j = 0; j < globalForceReason.length; j++){
					var force = globalForceReason[j];
					if(reasonTypeFlag[force.changementReason - 1]){
						str += '<tr> <td rowspan=' + reasonTypeSum[force.changementReason - 1] + '><center>'
							+ getReason(force.changementReason)
							+ '</td></center>';
						reasonTypeFlag[force.changementReason - 1] = false;
					}
					str += '<td>'
						+ interfaceForceReasonMap[force.interfaceId][1]
						+ '</td>';
					if(!force.remark){
						str += '<td>'
							+ '</td>';
					}else{
						str += '<td>'
							+ force.remark
							+ '</td>';
					}
					str += '<td>'
						+ force.operator.userName
						+ '</td>'
						+ '<td>'
						+ getDate(force.createTime)
						+ '</td>'
						+ '<td align="center">'
						+ interfaceForceReasonMap[force.interfaceId][2]
						+ '</td>'
						+ '<td align="center">'
						+ interfaceForceReasonMap[force.interfaceId][3]
						+ '</td>  </tr>';
				}
			}
			$("#table_force_reason tbody").append(str);
			
			// 交换表头内容
			var text = $("#table_force_reason tr:eq(0) th:eq(0)").text();
			$("#table_force_reason tr:eq(0) th:eq(0)").text($("#table_force_reason tr:eq(0) th:eq(1)").text());
			$("#table_force_reason tr:eq(0) th:eq(1)").text(text);
			$("#table_force_reason tr:eq(0) th:eq(0)").css("text-align","center");
		}else{
			if(globalForceReason != null && globalForceReason.length > 0){
				var str = '';
				for(key in interfaceForceReasonMap){
					for(var j = 0; j < globalForceReason.length; j++){
						var force = globalForceReason[j];
						if(key == force.interfaceId){
							str += '<tr> <td>'
								+ interfaceForceReasonMap[key][1]
								+ '</td> <td>'
								+ getReason(force.changementReason)
								+ '</td>';
							if(!force.remark){
								str += '<td>'
									+ '</td>';
							}else{
								str += '<td>'
									+ force.remark
									+ '</td>';
							}
							str += '<td>'
								+ force.operator.userName
								+ '</td>'
								+ '<td>'
								+ getDate(force.createTime)
								+ '</td>'
								+ '<td align="center">'
								+ interfaceForceReasonMap[key][2]
								+ '</td>'
								+ '<td align="center">'
								+ interfaceForceReasonMap[key][3]
								+ '</td>  </tr>';
						}
					}
				}
				$("#table_force_reason tbody").append(str);
				var text = $("#table_force_reason tr:eq(0) th:eq(0)").text();
				$("#table_force_reason tr:eq(0) th:eq(0)").text($("#table_force_reason tr:eq(0) th:eq(1)").text());
				$("#table_force_reason tr:eq(0) th:eq(1)").text(text);
				$("#table_force_reason tr:eq(0) th:eq(0)").css("text-align","left");
			}
		}
		setForceReasonTableOddEvenColor();
	});
	
	$("#interfaceStatusBarChart").poshytip({
		className: 'tip-violet',
		content: '接口状态分布图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		keepInViewport: true,
		offsetX: 0,
		offsetY: 5
	});
	$("#interfaceStatusBarChart").poshytip('hide');
	$('#interfaceStatusBarChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = '';
			var interfaces = statusIncludeInterfaces[pointIndex + 1];
			for(var i = 0; i < interfaces.length; i++){
				content += '接口名称：' + interfaces[i] + '<br>';
			}
			$("#interfaceStatusBarChart").poshytip('update', content);
			$("#interfaceStatusBarChart").poshytip('showDelayed',100);
            //alert('series: '+seriesIndex+', point: '+pointIndex+', data: '+data+ ', pageX: '+ev.pageX+', pageY: '+ev.pageY);
        }
    );    
    $('#interfaceStatusBarChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#interfaceStatusBarChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#interfaceFlowBarChart").poshytip({
		className: 'tip-violet',
		content: '接口进展统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: 5
	});
	$("#interfaceFlowBarChart").poshytip('hide');
	$('#interfaceFlowBarChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = '';
			var interfaces = flowIncludeInterfaces[pointIndex];
			for(var i = 0; i < interfaces.length; i++){
				content += '接口名称：' + interfaces[i] + '<br>';
			}
			$("#interfaceFlowBarChart").poshytip('update', content);
			$("#interfaceFlowBarChart").poshytip('showDelayed',100);
        }
    );         
    $('#interfaceFlowBarChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#interfaceFlowBarChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#backPieChart").poshytip({
		className: 'tip-yellow',
		content: '后台开发人员任务统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: -15
	});
	$("#backPieChart").poshytip('hide');
	$('#backPieChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = backPeopleContent[pointIndex];
			$("#backPieChart").poshytip('update', content);
			$("#backPieChart").poshytip('showDelayed',100);
        }
    );         
    $('#backPieChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#backPieChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#clientPieChart").poshytip({
		className: 'tip-yellow',
		content: '客户端开发人员任务统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: -15
	});
	$("#clientPieChart").poshytip('hide');
	$('#clientPieChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = clientPeopleContent[pointIndex];
			$("#clientPieChart").poshytip('update', content);
			$("#clientPieChart").poshytip('showDelayed',100);
        }
    );         
    $('#clientPieChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#clientPieChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#frontPieChart").poshytip({
		className: 'tip-yellow',
		content: '客户端开发人员任务统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: -15
	});
	$("#frontPieChart").poshytip('hide');
	$('#frontPieChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = frontPeopleContent[pointIndex];
			$("#frontPieChart").poshytip('update', content);
			$("#frontPieChart").poshytip('showDelayed',100);
        }
    );         
    $('#frontPieChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#frontPieChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#testPieChart").poshytip({
		className: 'tip-yellow',
		content: '客户端开发人员任务统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: -15
	});
	$("#testPieChart").poshytip('hide');
	$('#testPieChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = testPeopleContent[pointIndex];
			$("#testPieChart").poshytip('update', content);
			$("#testPieChart").poshytip('showDelayed',100);
        }
    );         
    $('#testPieChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#testPieChart").poshytip('hideDelayed', 100);
        }
    );
    
    $("#forceReasonPieChart").poshytip({
		className: 'tip-yellow',
		content: '强制回收原因分布统计图',
		showOn: 'hover',
		bgImageFrameSize: 9,
		alignTo: 'cursor',
		alignX: 'inner-left',
		allowTipHover: true,
		keepInViewport: true,
		offsetX: 0,
		offsetY: -15
	});
	$("#forceReasonPieChart").poshytip('hide');
	$('#forceReasonPieChart').bind('jqplotDataHighlight', 
        function (ev, seriesIndex, pointIndex, data) {
			var content = forceReasonContent[pointIndex];
			$("#forceReasonPieChart").poshytip('update', content);
			$("#forceReasonPieChart").poshytip('showDelayed',100);
        }
    );         
    $('#forceReasonPieChart').bind('jqplotDataUnhighlight', 
        function (ev) {
    		$("#forceReasonPieChart").poshytip('hideDelayed', 100);
        }
    );
}

//接口状态分布图
function drawInterfaceStatusBarChart(interfaceStatusLine){
	$('#interfaceStatusBarChart').empty();
    $('#interfaceStatusBarChart').jqplot([interfaceStatusLine], {
        title:'接口状态分布图',
        animate: !$.jqplot.use_excanvas,
        seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            rendererOptions: {
                varyBarColor: true
            },
	    pointLabels: { 
	        	show: true, 
	        }
	    },
        axes:{
            xaxis:{
                renderer: $.jqplot.CategoryAxisRenderer
            },
		    yaxis:{
		    	label: '接口个数',
		    	min: 0,
	        }
        }
    });
}

//接口进展统计图
function drawInterfaceFlowBarChart(interfaceFlowLine){
	$('#interfaceFlowBarChart').empty();
    $('#interfaceFlowBarChart').jqplot([interfaceFlowLine], {
        title:'接口进展统计图',
        seriesColors:['#85802b', '#00749F', '#73C774', '#C7754C', '#17BDB8'],
        animate: !$.jqplot.use_excanvas,
        seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            rendererOptions: {
                varyBarColor: true
            },
		    pointLabels: { 
		    	show: true, 
		    }
        },
        axes:{
            xaxis:{
                renderer: $.jqplot.CategoryAxisRenderer
            },
	        yaxis:{
		    	label: '接口个数',
		    	min: 0,
	        }
        }
    });
}

// 人员任务分布图
function drawBackPeopleTaskPieChart(data){
	$("#backPieChart").empty();
	var plot = jQuery.jqplot ('backPieChart', [data], {
	  title:'后台开发人员任务统计图',
      seriesDefaults: {
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
}
function drawClientPeopleTaskPieChart(data){
	$("#clientPieChart").empty();
	var plot = jQuery.jqplot ('clientPieChart', [data], {
	  title:'客户端开发人员任务统计图',
      seriesDefaults: {
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
}
function drawFrontPeopleTaskPieChart(data){
	$("#frontPieChart").empty();
	var plot = jQuery.jqplot ('frontPieChart', [data], {
	  title:'前端开发人员任务统计图',
      seriesDefaults: {
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
}
function drawTestPeopleTaskPieChart(data){
	$("#testPieChart").empty();
	var plot = jQuery.jqplot ('testPieChart', [data], {
	  title:'测试人员任务统计图',
      seriesDefaults: {
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
}

// 强制回收原因分布图
function drawForceReasonPieChart(data){
	$("#forceReasonPieChart").empty();
	var plot = jQuery.jqplot ('forceReasonPieChart', [data], {
	  title:'强制回收原因分布图',
      seriesDefaults: {
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
}

function setForceReasonTableOddEvenColor(){
	$("#table_force_reason tr:odd").css("background-color", "#E6E6FA"); 
    $("#table_force_reason tr:even").css("background-color", "#FFF0FA");
    $("#table_force_reason tr:first").css("background-color", "#F0F0F0");
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

function getReason(reason){
	switch(reason){
	case 1:
		return "系统操作正常状态变更";
	case 2:
		return "产品原因";
	case 3:
		return "后台开发原因";
	case 4:
		return "客户端开发原因";
	case 5:
		return "前端开发原因";
	case 6:
		return "测试原因 ";
	case 7:
		return "其他 ";
	default:
		return "";
	}
}

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}

function getSimpleDate(time){
	var createDate = new Date(time);
	var month = createDate.getMonth() + 1;
	month = month < 10 ? "0"+month : month;
	var date = createDate.getDate();
	date = date < 10 ? "0" + date : date;
	createTime = createDate.getFullYear() + "-" + month + "-" + date;
	return createTime;
}

//计算两个日期的间隔天数  
function DateDiff(sDate1, sDate2){ //sDate1和sDate2是yyyy-MM-dd格式   
	var oDate1, oDate2, iDays;
	oDate1 = new Date(sDate1);
	oDate2 = new Date(sDate2);
	iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 /24); //把相差的毫秒数转换为天数   
	return iDays; 
}