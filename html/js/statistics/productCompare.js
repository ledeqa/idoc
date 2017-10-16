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
	
	$("#selectAll").change(function(){
		if($(this).prop("checked")){
			$("[name='productCheckbox']").prop("checked",'true');
		}else{
			$("[name='productCheckbox']").removeProp("checked");
		}
	});
	
	$("#compare").on('click', function(){
		var products = '';
		var checked;
		if($('#selectedProduct input:checked').length > 0){
			checked = $('#selectedProduct input:checked');
			for(var i = 0;i < checked.length; i++){
				products += checked[i].value + ',';
			}
			products = products.substr(0, products.length - 1);
		}else{
			bootbox.alert('请选择需要对比的产品！');
			return;
		}
		var startTime = $('#startTime').val().trim();
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
		
		var data = {};
		data.products = products;
		data.startTime = startTime;
		data.endTime =endTime;
		$.ajax({
			url : '/idoc/statistics/getSelectedStatisticsProduct.html',
			type : 'POST',
			data : data,
			dataType : 'json',
			success : function(json) {
				if (json.retCode == 200) {
					var statisticsProductList = json.statisticsProductList;
					var forceBackTrendLines = [];
					var interfaceDelayTrendLines = [];
					var testTrendLines = [];
					var forceBackTrendLine = new Array(checked.length);
					var interfaceDelayTrendLine = new Array(checked.length);
					var testTrendLine = new Array(checked.length);
					for(var i = 0; i < checked.length; i++){
						forceBackTrendLine[i] = new Array();
						interfaceDelayTrendLine[i] = new Array();
						testTrendLine[i] = new Array();
					}
					for(var i = 0; i < statisticsProductList.length; i++){
						var statisticsProduct = statisticsProductList[i];
						for(var j = 0; j < checked.length; j++){
							if(checked[j].value == statisticsProduct.productId){
								var forcePercentage = 0;
								var delayPercentage = 0;
								if(statisticsProduct.interfaceNum != 0){
									forcePercentage = statisticsProduct.forceBackNum/statisticsProduct.interfaceNum;
									delayPercentage = statisticsProduct.delayInterfaceNum/statisticsProduct.interfaceNum;
								}
								forceBackTrendLine[j].push([statisticsProduct.projectCreateTime, forcePercentage * 100, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.forceBackNum]);
								interfaceDelayTrendLine[j].push([statisticsProduct.projectCreateTime, delayPercentage * 100, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.delayInterfaceNum]);
								testTrendLine[j].push([statisticsProduct.projectCreateTime, statisticsProduct.averageTestTime, statisticsProduct.projectName, getDate(statisticsProduct.projectCreateTime), statisticsProduct.interfaceNum, statisticsProduct.averageTestTime + ' 天']);
								break;
							}
						}
					}
					
					var seriesArrary = [];
					for(var i = 0; i < checked.length; i++){
						if(forceBackTrendLine[i].length > 0)
							forceBackTrendLines.push(forceBackTrendLine[i]);
						if(interfaceDelayTrendLine[i].length > 0)
							interfaceDelayTrendLines.push(interfaceDelayTrendLine[i]);
						if(testTrendLine[i].length > 0)
							testTrendLines.push(testTrendLine[i]);
						if(forceBackTrendLine[i].length > 0 || interfaceDelayTrendLine[i].length > 0 || testTrendLine[i].length > 0)
							seriesArrary.push({label: checked[i].id});
					}
					// 画强制回收走势图
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
					drawTrendChart(forceChart, forceTitle, force_xLabel, force_yLabel, forceBackTrendLines, seriesArrary, forceFormatString);
					
					// 画延期百分比走势图
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
					drawTrendChart(delayChart, delayTitle, delay_xLabel, delay_yLabel, interfaceDelayTrendLines, seriesArrary, delayFormatString);
					
					// 画测试周期走势图
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
					drawTrendChart(testChart, testTitle, test_xLabel, test_yLabel, testTrendLines, seriesArrary, testFormatString);
				}else{
					$("#forceBackTrendChart").empty();
					$("#interfaceDelayTrendChart").empty();
					$("#testTrendChart").empty();
					$("#forceBackTrendChart").html('没有该产品的相关数据。');
					$('#tips').empty();
				}
			}
		});
	});
});

//走势图
function drawTrendChart(chart, title, xLabel, yLabel, lines, seriesArrary, formatString){
	$('#' + chart).empty();
	if(lines.length <= 0){
		$('#tips').empty();
		$("#forceBackTrendChart").html('没有查询到该产品的相关数据。');
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
	var plot = $.jqplot(chart, lines, {
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