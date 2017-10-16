$(document).ready(function() {
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
	
	$("#table_product_summary tr:first").css("background-color", "#F0F0F0");
	$("#statistics").on('click', function(){
		var productId = $('#product').val();
		var startTime = $('#startTime').val().trim();
		if(productId == -1){
			bootbox.alert('请选择需要统计的产品！');
			return;
		}
		if(startTime == ''){
			bootbox.alert('请选择开始时间！');
			return;
		}
		var endTime = $('#endTime').val().trim();
		if(endTime == ''){
			bootbox.alert('请选择结束时间！');
			return;
		}
		if(startTime > endTime){
			bootbox.alert('结束时间不能早于开始时间，请重新选择！');
			return;
		}
		
		$('#tips').html('<div class="alert alert-block alert-success">' +
				'<button type="button" class="close" data-dismiss="alert">' +
					'<i class="ace-icon fa fa-times"></i>' +
				'</button>' +
				'<i class="ace-icon fa fa-check green"></i>' +
					'Tips：鼠标' +
					'<strong style="color:#FF0000">' +
					'	移到' +
					'</strong>' +
					'图像顶点上显示具体信息，' +
					'<strong style="color:#FF0000">' +
					'	拖动' +
					'</strong>' +
					'鼠标能放大图像，' +
					'<strong style="color:#FF0000">' +
						'双击' +
					'</strong>' +
					'还原图像。下面的走势图对应产品下所有项目的统计情况，横轴是项目的创建时间，纵轴数据对应该项目下接口的平均数值。' +
					'</div>');
		var data = {};
		data.productId = productId;
		data.startTime = startTime;
		data.endTime =endTime;
		$.ajax({
			url : '/idoc/statistics/getStatisticsProduct.html',
			type : 'POST',
			data : data,
			dataType : 'json',
			success : function(json) {
				if (json.retCode == 200) {
					var statisticsProductList = json.statisticsProductList;
					var dictNum = json.dictNum;
					var onlineInterfaceNum = json.onlineInterfaceNum;
					$('#table_product_summary tbody').empty();
					var projectNum = statisticsProductList.length;
					var interfaceNum = 0;
					var onlineNum = 0; // 上线接口按个数
					var userNum = 0;
					var forceBackNum = 0;
					var delayInterfaceNum = 0;
					var userIds = {};
					var forceBackMap = {}; // 记录各种原因对应个数，key为原因id，value为个数
					var forceBackTrendLine = [];
					var interfaceDelayTrendLine = [];
					var testTrendLine = [];
					for(var i = 0; i < projectNum; i++){
						var statisticsProduct = statisticsProductList[i];
						interfaceNum += statisticsProduct.interfaceNum;
						onlineNum += statisticsProduct.onlineNum;
						delayInterfaceNum += statisticsProduct.delayInterfaceNum;
						forceBackNum += statisticsProduct.forceBackNum;
						var users = statisticsProduct.userIds.split(",");
						for(var j = 0; j < users.length; j++){
							if(users[j] in userIds){
								userIds[users[j]]++;
							}else{
								userIds[users[j]] = 1;
							}
						}
						if(statisticsProduct.forceBackNum != 0){
							var details = statisticsProduct.forceBackDetail.split(",");
							for(var j = 0; j < details.length; j++){
								var reason = details[j].split(":");
								if(reason[0] in forceBackMap){
									forceBackMap[reason[0]] += parseInt(reason[1]);
								}else{
									forceBackMap[reason[0]] = parseInt(reason[1]);
								}
							}
						}
						var forcePercentage = 0;
						var delayPercentage = 0;
						if(statisticsProduct.interfaceNum != 0){
							forcePercentage = statisticsProduct.forceBackNum/statisticsProduct.interfaceNum;
							delayPercentage = statisticsProduct.delayInterfaceNum/statisticsProduct.interfaceNum;
						}
						forceBackTrendLine.push([statisticsProduct.projectCreateTime, forcePercentage * 100, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.forceBackNum]);
						interfaceDelayTrendLine.push([statisticsProduct.projectCreateTime, delayPercentage * 100, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.delayInterfaceNum]);
						testTrendLine.push([statisticsProduct.projectCreateTime, statisticsProduct.averageTestTime, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.averageTestTime + ' 天']);
					}
					userNum = Object.keys(userIds).length;
					var str = '<tr>'
							+ '<td>'
							+ projectNum
							+ '</td>'
							+ '<td>'
							+ interfaceNum
							+ '</td>'
							+ '<td>'
							+ userNum
							+ '</td>'
							+ '<td>'
							+ dictNum
							+ '</td>'
							+ '<td>'
							+ onlineInterfaceNum
							+ '</td>'
							+ '<td>'
							+ delayInterfaceNum
							+ '</td>'
							+ '<td>'
							+ forceBackNum
							+ '</td>'
						    + '</tr>';
					$('#table_product_summary tbody').append(str);
					
					if(interfaceNum > 0){
						var interfaceStatusData = [];
						interfaceStatusData.push(["进行中", interfaceNum - onlineNum]);
						interfaceStatusData.push(["已完成", onlineNum]);
						drawInterfaceStatusPieChart(interfaceStatusData);
						
						var forceBackReasonData = [];
						for(key in forceBackMap){
							forceBackReasonData.push([getReason(parseInt(key)), forceBackMap[key]]);
						}
						drawForceBackReasonPieChart(forceBackReasonData);
						
						var interfaceDelayData = [];
						interfaceDelayData.push(["延期", delayInterfaceNum]);
						interfaceDelayData.push(["非延期", interfaceNum - delayInterfaceNum]);
						drawInterfaceDelayPieChart(interfaceDelayData);
						
						// 画强制回收走势图
						var forceSeriesArrary = [];
						forceSeriesArrary.push({label: "强制回收次数"});
						var forceChart = 'forceBackTrendChart';
						var forceTitle = '产品相关项目强制回收次数走势图';
						var force_xLabel = '项目创建时间';
						var force_yLabel = '回收比例(%)';
						var forceFormatString = '<table class="jqplot-highlighter"> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr><td>项目名称：%s</td></tr> \
					          <tr><td>创建时间：%s</td></tr> \
							  <tr><td>接口总数：%s</td></tr> \
					          <tr><td>回收个数：%s</td></tr> \
					          </table>';
						drawTrendChart(forceChart, forceTitle, force_xLabel, force_yLabel, forceBackTrendLine, forceSeriesArrary, forceFormatString);
						
						// 画延期百分比走势图
						var delaySeriesArrary = [];
						delaySeriesArrary.push({label: "延期百分比"});
						var delayChart = 'interfaceDelayTrendChart';
						var delayTitle = '产品相关项目延期百分比走势图';
						var delay_xLabel = '项目创建时间';
						var delay_yLabel = '延期百分比(%)';
						var delayFormatString = '<table class="jqplot-highlighter"> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr><td>项目名称：%s</td></tr> \
					          <tr><td>创建时间：%s</td></tr> \
							  <tr><td>接口总数：%s</td></tr> \
					          <tr><td>延期个数：%s</td></tr> \
					          </table>';
						drawTrendChart(delayChart, delayTitle, delay_xLabel, delay_yLabel, interfaceDelayTrendLine, delaySeriesArrary, delayFormatString);
						
						// 画测试周期走势图
						var testSeriesArrary = [];
						testSeriesArrary.push({label: "测试周期"});
						var testChart = 'testTrendChart';
						var testTitle = '产品相关项目测试周期走势图';
						var test_xLabel = '项目创建时间';
						var test_yLabel = '测试周期(天)';
						var testFormatString = '<table class="jqplot-highlighter"> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr class="hidden"><td>%s</td></tr> \
					          <tr><td>项目名称：%s</td></tr> \
					          <tr><td>创建时间：%s</td></tr> \
							  <tr><td>接口总数：%s</td></tr> \
					          <tr><td>测试天数：%s</td></tr> \
					          </table>';
						drawTrendChart(testChart, testTitle, test_xLabel, test_yLabel, testTrendLine, testSeriesArrary, testFormatString);
					}else{
						$("#interfaceStatusPieChart").empty();
						$("#forceBackReasonPieChart").empty();
						$("#interfaceDelayPieChart").empty();
						$("#interfaceStatusPieChart").html('没有该产品的相关数据。');
						$("#forceBackTrendChart").empty();
						$("#interfaceDelayTrendChart").empty();
						$("#testTrendChart").empty();
						$("#forceBackTrendChart").html('没有该产品的相关数据。');
						$('#tips').empty();
					}
				}else{
					$("#table_product_summary tbody").empty();
					$("#table_product_summary tbody").html('<tr class="warning"><td colspan="5">没有查询到相关数据。</td></tr>');
				}
			}
		});
	});
});

// 产品状态分布图
function drawInterfaceStatusPieChart(data){
	$("#interfaceStatusPieChart").empty();
	if(data.length <= 0){
		return;
	}
	var plot = jQuery.jqplot ('interfaceStatusPieChart', [data], {
	  title:'产品状态分布图',
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

//强制回收原因分布图
function drawForceBackReasonPieChart(data){
	$("#forceBackReasonPieChart").empty();
	if(data.length <= 0){
		return;
	}
	var plot = jQuery.jqplot ('forceBackReasonPieChart', [data], {
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

// 延期情况分布图
function drawInterfaceDelayPieChart(data){
	$("#interfaceDelayPieChart").empty();
	if(data.length <= 0){
		return;
	}
	var plot = jQuery.jqplot ('interfaceDelayPieChart', [data], {
	  title:'延期情况分布图',
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

// 走势图
function drawTrendChart(chart, title, xLabel, yLabel, line, seriesArrary, formatString){
	$('#' + chart).empty();
	var plot = $.jqplot(chart, [line], {
		  title: {
			  text: title,
			  fontFamily: '"微软雅黑", Serif'
		  },
		  animate: true,
		  seriesDefaults: {
	        showMarker:true,
	        pointLabels: {
	          show: false,
	          edgeTolerance: 5
	        },
	        rendererOptions: { smooth: true },
	        cursor:{
	        	show: true,
	        	showTooltip: true
	        }},
	  series: seriesArrary, 
      legend: {  
          show: true,  
          location: 'ne'  
      },  
	      axes: {
	  xaxis: {
	          renderer: $.jqplot.DateAxisRenderer,
	          label:  xLabel,
	          labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
	          labelOptions: {
              fontFamily: '"微软雅黑", Serif',
              fontSize: '12pt'
          },
          tickRenderer: $.jqplot.CanvasAxisTickRenderer,
	          tickOptions: {
              formatString: "%Y-%#m-%#d",
              angle: -30
          }
	        },
	        yaxis: {
	          label: yLabel,
	          labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
	          tickOptions: {
              formatString: "%d",
          },
          labelOptions: {
              fontFamily: '"微软雅黑", Serif',
              fontSize: '12pt'
          },
          min: 0.0
	        }
	      },
	      highlighter: {
	    	  show:true,
	          showMarker:false,
	          yvalues: 5,
	          formatString: formatString
	      },
	      cursor: {
	            show: true,
	            zoom: true,
	            looseZoom: true,
	            showTooltip: false
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