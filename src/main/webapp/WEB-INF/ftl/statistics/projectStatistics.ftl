<!--首页 -->
<#include "../inc/core.ftl"> 
<@htmHead title="接口文档管理平台"
otherCss=["/css/common/dataTables.responsive.css",
"/css/common/dataTables.bootstrap.css",
"/css/common/jqplot/jquery.jqplot.css",
"/css/common/poshytip/tip-violet/tip-violet.css",
"/css/common/poshytip/tip-green/tip-green.css",
"/css/common/poshytip/tip-yellow/tip-yellow.css"]
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
"/js/common/jqplot/plugins/jqplot.pieRenderer.min.js",
"/js/common/jquery.poshytip.min.js",
"/js/statistics/projectStatistics.js"] />

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <center><h2 class="page-header"><font color="#00BB00">${projectName?if_exists}</font>-项目相关信息统计</h2></center>
            </div>
            <input type="hidden" id="projectName" value="${projectName?if_exists}">
			<input type="hidden" id="projectId" value="${projectId?if_exists}">
            <!-- /.col-lg-12 -->
        </div>
        
        <div class="row">
			<div class="col-lg-12">
				<div class="panel panel-info">
					<div class="panel-body">
						<div class="form-inline">
							<div class="form-group">
								<label>请选择要统计的模块：</label>&nbsp;
								<input type="checkbox" id="selectAll">&nbsp;全选&nbsp;&nbsp;
								<button class="btn btn-primary col-lg-offset-1" id="tongji">开始统计</button>&nbsp;&nbsp;&nbsp;
								<button class="btn btn-success" type="button" id="exportInterInfo"><i class="fa fa-download"></i>&nbsp;导出接口信息</button>
								<div class="form-group" id="selectedModule">
									<#if moduleList?exists>
										<#list moduleList as module>
											<label class="chexkbox-inline" style="margin-left:20px;">
												<input type="checkbox" id="${module.moduleName!''}" name="moduleCheckbox" value="${module.moduleId!''}">&nbsp; <font style="color:#FFAF60"> ${module.moduleName!''}</font>
											</label>
										</#list>
									<#else>
										没有查询到相关模块数据。
									</#if>
								</div>
							</div>
							
							<br>

						</div>
					</div>
				</div>
			</div>
		</div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-success">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">接口信息列表</h3>
				   	</div>
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-12">
								<table class="table table-striped table-bordered"
									id="table_project_interface">
									<thead>
										<tr>
											<th>接口名称</th>
											<th>接口状态</th>
											<th>需求人员</th>
											<th>后台开发</th>
											<th>客户端开发</th>
											<th>前端开发</th>
											<th>测试人员</th>
											<th>预计提测时间</th>
											<th>实际提测时间</th>
											<th>提测延期天数</th>
											<th>预计上线时间</th>
											<th>实际上线时间</th>
											<th>上线延期天数</th>
											<th>上线是否有风险</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
			 		</div>
				</div>
        	</div>
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-info">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">项目接口数据汇总</h3>
				   	</div>
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-12">
								<table class="table table-striped table-bordered"
									id="table_interface_summary">
									<thead>
										<tr>
											<th>接口总数</th>
											<th>提测延期个数</th>
											<th>提测延期天数</th>
											<th>上线延期个数</th>
											<th>上线延期天数</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
			 		</div>
				</div>
        	</div>
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-info">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">接口状态分布</h3>
				   	</div>
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-6">
								<div id="interfaceStatusBarChart" style="height: 500px;"></div>
							</div>
							<div class="col-lg-4 col-lg-offset-1">
								<div id="interfaceFlowBarChart" style="height: 500px;"></div>
							</div>
						</div>
			 		</div>
				</div>
        	</div>
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-info">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">人员任务分布图</h3>
				   	</div>
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-4 col-lg-offset-1">
								<div id="backPieChart" style="height: 500px;"></div>
							</div>
							<div class="col-lg-4 col-lg-offset-2">
								<div id="clientPieChart" style="height: 500px;"></div>
							</div>
						</div>
						<br><br><br>
						<div class="row">
							<div class="col-lg-4 col-lg-offset-1">
								<div id="frontPieChart" style="height: 500px;"></div>
							</div>
							<div class="col-lg-4 col-lg-offset-2">
								<div id="testPieChart" style="height: 500px;"></div>
							</div>
						</div>
			 		</div>
				</div>
        	</div>
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-info">
			 		<div class="panel-heading">
				      	<h3 class="panel-title">强制回收原因分布
					      	<div class="panel-title checkbox pull-right">
						      <label>
						      <input type="checkbox" id="orderByReason"> <font color="#00BB00">按回收原因分类</font>
						      </label>
						    </div>
					    </h3>
				   	</div>
				   	
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-12">
								<table class="table table-striped table-bordered"
									id="table_force_reason">
									<thead>
										<tr>
											<th>接口名称</th>
											<th>强制回收原因</th>
											<th>回收原因备注</th>
											<th>回收操作者</th>
											<th>强制回收时间</th>
											<th><center>是否提测延期</center></th>
											<th><center>是否上线延期</center></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
						<br><br>
						<div class="row">
							<div class="col-lg-4 col-lg-offset-4">
								<div id="forceReasonPieChart" style="height: 500px;"></div>
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