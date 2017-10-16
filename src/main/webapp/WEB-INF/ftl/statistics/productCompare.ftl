<!--首页 -->
<#include "../inc/core.ftl"> 
<@htmHead title="接口文档管理平台"
otherCss=["/css/common/dataTables.responsive.css",
"/css/common/dataTables.bootstrap.css",
"/css/common/jqplot/jquery.jqplot.css",
"/css/common/poshytip/tip-violet/tip-violet.css",
"/css/common/poshytip/tip-green/tip-green.css",
"/css/common/poshytip/tip-yellow/tip-yellow.css",
"/css/common/bootstrap-datetimepicker/bootstrap-datetimepicker.css"] 
otherJs=["/js/common/bootbox.js",
"/js/common/dataTables.bootstrap.min.js",
"/js/common/jquery.dataTables.min.js",
"/js/common/jqplot/jquery.jqplot.js",
"/js/common/jqplot/plugins/jqplot.barRenderer.min.js",
"/js/common/jqplot/plugins/jqplot.categoryAxisRenderer.min.js",
"/js/common/jqplot/plugins/jqplot.highlighter.min.js",
"/js/common/jqplot/plugins/jqplot.pointLabels.min.js",
"/js/common/jqplot/plugins/jqplot.cursor.min.js",
"/js/common/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min.js",
"/js/common/jqplot/plugins/jqplot.canvasAxisTickRenderer.js",
"/js/common/jqplot/plugins/jqplot.canvasTextRenderer.js",
"/js/common/jqplot/plugins/jqplot.dateAxisRenderer.min.js",
"/js/common/jqplot/plugins/jqplot.pieRenderer.min.js",
"/js/common/jquery.poshytip.min.js",
"/js/common/bootstrap-datetimepicker/bootstrap-datetimepicker.js",
"/js/common/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js",
"/js/statistics/productCompare.js"] />

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <center><h2 class="page-header">各产品对比信息统计</h2></center>
            </div>
            <!-- /.col-lg-12 -->
        </div>
        
        <div class="row">
			<div class="col-lg-12">
				<div class="panel panel-info">
					<div class="panel-body">
						<div class="form-inline">
							<div class="form-group">
								<label>请选择要统计的产品：</label>&nbsp;
								<input type="checkbox" id="selectAll">&nbsp;全选 <br>
								<div class="form-group" id="selectedProduct">
									<#list productList as product>
										<label class="chexkbox-inline" style="margin-left:20px;">
											<input type="checkbox" id="${product.productName!''}" name="productCheckbox" value="${product.productId!''}">&nbsp; <font style="color:#FFAF60"> ${product.productName!''}</font>
										</label>
									</#list>
								</div>
							</div>
							
							<br><br>
								<div class="form-group">
									<label>开始时间：</label>&nbsp;
									<input type="text" id="startTime" class="form-control time" placeholder="请选择开始时间" readonly>
								</div>
							
								<div class="form-group col-lg-offset-1">
									<label>结束时间：</label>&nbsp;
									<input type="text" id="endTime" class="form-control time" placeholder="请选择结束时间" readonly>
								</div>
							
								<button class="btn btn-primary col-lg-offset-1" id="compare">开始对比</button>
						</div>
					</div>
				</div>
			</div>
		</div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-info">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">走势图</h3>
				   	</div>
			 		<div class="panel-body">
			 			<div class="row">
							<div class="col-lg-12">
								<span id="tips"></span>
							</div>
						</div>
						<div class="row">
							<div class="col-lg-4">
								<div id="forceBackTrendChart" style="height: 500px;"></div>
							</div>
							<div class="col-lg-4">
								<div id="interfaceDelayTrendChart" style="height: 500px;"></div>
							</div>
							<div class="col-lg-4">
								<div id="testTrendChart" style="height: 500px;"></div>
							</div>
						</div>
			 		</div>
				</div>
        	</div>
        </div>
        
    </div>
    <!-- #page-wrapper -->

<@htmlNavFoot />
<@htmlFoot/>