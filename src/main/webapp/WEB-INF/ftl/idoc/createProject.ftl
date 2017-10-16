<#include "/inc/core.ftl">
<@htmHead title="接口文档管理平台" />

<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
           <h3 class="page-header">创建项目</h3>
            </div>
            <!-- /.col-lg-12 -->
        </div>
	<div class="panel panel-default">
		<div class="form-horizontal">
			<div class="form-group">
				
				<label class="col-sm-1 control-label"
					style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">项目名称：
				</label>
				<div class="col-sm-2" style="padding-left: 0px;">
					<input type="text" id="projectName" class="form-control" placeholder="必填">
						
				</div>
			<label class="col-sm-1 control-label"
					style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">是否上线：
				</label>
				<div class="col-sm-2" style="padding-left: 0px;">
				<select id="IsOnline" class="form-control" onchange="changeIsOnline()">
						<option value="0">否</option>
						<option value="1">是</option>
					</select>
				</div>
				<div class="col-sm-2" style="padding-left: 0px;">
				<button class="btn btn-primary" onClick="searchProject(1)">搜索</button>
				</div>
				<label class="col-sm-1 control-label"
					style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">
				</label>
				<div class="col-sm-2" style="padding-left: 0px;">
					<button class="btn btn-primary" onClick="createNewProject()">新建项目</button>
						
				</div>
				
			</div>
			
		</div>
		<div class="row">
			<div class="col-lg-12">
				<div>
					<table class="table table-striped table-bordered"
						id="table_search_project">
						<thead>
							<tr>
								<th>行号</th>
								<th>项目名称</th>
								<th>被测包名</th>
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
							<select id="perPage" class="form-control" onChange="changeList()">
								<option value="30">30</option>
								<option value="50">50</option>
								<option value="100">100</option>
								<option value="300">300</option>
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
 
 
<@htmlNavFoot />
<@htmlFoot/>

