<!--首页 -->
<#include "../inc/core.ftl"> <@htmHead title="接口文档管理平台"
otherCss=["/css/common/dataTables.responsive.css",
"/css/common/dataTables.bootstrap.css"]
otherJs=["/js/common/bootbox.js",
"/js/common/jquery.dataTables.min.js",
"/js/common/dataTables.bootstrap.min.js",
"/js/productAndproject/projectList.js"]/>

	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li class="active">${productName?if_exists}</li>
				</ol>
			</div>
		</div>
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">项目相关(所属产品:${productName?if_exists})</h2>
            </div>
            <input type="hidden" id="productName" value="${productName?if_exists}">
			<input type="hidden" id="productId" value="${productId?if_exists}">
            <!-- /.col-lg-12 -->
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-default">
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-12" style="padding-top:10px; padding-bottom:5px">
								<label class="col-lg-1 control-label" style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">项目名称：
								</label>
								<div class="col-lg-2" style="padding-left: 0px;">
									<input type="text" id="projectName" class="form-control">
								</div>
								<label class="col-lg-1 control-label" style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">是否上线：
								</label>
								<div class="col-lg-2" style="padding-left: 0px;">
									<select id="IsOnline" class="form-control"
										onchange="changeIsOnline()">
										<option value="0">全部</option>
										<option value="1">否</option>
										<option value="2">是</option>
									</select>
								</div>
								<div class="col-lg-2">
									<button class="btn btn-primary" onClick="getProductAllProject(1)">搜索</button>
								</div>
								<#if role?exists && (role == "管理员" || role == "后台开发负责人" || role == "测试负责人")>
									<div class="pull-right">
										<button class="btn btn-primary" onClick="createNewProject()">新建项目</button>
									</div>
								</#if>
							</div>
						</div>
						<div class="row">
							<div class="col-lg-12">
								<div>
									<table class="table table-striped table-bordered"
											id="table_search_project">
											<thead>
												<tr>
													<th>项目名称</th>
													<th>接口个数</th>
													<th>已提测个数</th>
													<th>已上线个数</th>
													<th>是否上线</th>
													<th>创建时间</th>
													<th>修改时间</th>
													<th>操作</th>
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="form-group">
									<div class="col-sm-6">
												<label class="col-sm-1 control-label"
											style="width: 130px; padding-top: 5px; padding-left: 15px; padding-right: 5px; font-size: 14px; font-family: Helvetica Neue, ​Helvetica, ​Arial, ​sans-serif; font-weight: normal; color: #337ab7;">
											每页显示条数： </label>
										<div class="col-sm-2" style="padding-left: 0px;">
											<select id="perPage" class="form-control" onChange="changePageProjectList()">
												<option value="10">10</option>
												<option value="30">30</option>
												<option value="50">50</option>
												<option value="100">100</option>
											</select>
										</div>
									</div>
									<div class="col-sm-6">
										<div id="dataTables-example_paginate"
											class="dataTables_paginate paging_simple_numbers">
											<ul class="pagination" id="changePageProject"></ul>
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

<@htmlNavFoot />
<@htmlFoot/>