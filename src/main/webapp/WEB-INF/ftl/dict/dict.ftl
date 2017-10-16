<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台" 
otherCss=[
"/css/common/dataTables.bootstrap.css",
"/css/common/dataTables.responsive.css",
"/css/common/fileinput.css"
]
otherJs=[
"/js/common/dataTables.bootstrap.min.js",
"/js/common/jquery.dataTables.min.js",
"/js/common/jquery.toastmessage.js",
"/js/common/lhgdialog/lhgdialog.js?skin=jtop",
"/js/common/bootbox.js",
"/js/common/fileinput.js",
"/js/dict/dict.js"
]
/>

	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li class="active">${productName?if_exists}</li>
				</ol>
				  <input type="text" hidden="true" id="pro_name" value='${productName?if_exists}'/>
			</div>
		</div>
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">数据字典管理
                	<small><button class="btn btn-primary" onClick="addDict()">新增字典</button></small>
                	<!-- <small><button class="btn btn-primary" data-target="#uploadDocDict" data-toggle="modal">导入字典</button></small> -->
                	<small><button class="btn btn-primary" onClick="importDictFromDB()">从数据库导入字典</button></small>
                </h2>
            </div>
        </div>
        <!-- 模态窗口 -->
        <div class="modal fade" id="uploadDocDict" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">请选择word文档</h4>
					</div>
					<div class="modal-body">
					<form action="/idoc/import/docs.html?productId=${productId}" method="post" enctype="multipart/form-data">
						<input id="uploadDict" type="file" class="file" name="file"
							data-show-preview="false" data-allowed-file-extensions='["doc", "docx"]'
							data-max-file-count="1"
							/>
						<!-- <input type="submit" value="上传"/> -->
					</form>
					</div>
					<div class="modal-footer">
						<!-- <button type="button" class="btn btn-primary" data-dismiss="modal">关闭
						</button> -->
					</div>
				</div>
			</div>
		</div>
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-default">
			 		<div class="panel-body">
						<div class="form-group" style="padding-top:10px">
							<label class="col-sm-1 control-label"
								style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">字典名称：</label>
							<div class="col-sm-2" style="padding-left: 0px;">
								<input type="text" id="dictName" class="form-control">
							</div>
							<button class="btn btn-primary" onClick="dictQuery(1)">搜索</button>
							<input type="text" hidden="true" id="productId" value="${productId!''}"/>
						</div>
						<div class="row">
							<div class="col-lg-12">
								<div>
									<table class="table table-striped table-bordered"
										id="table_search_dict">
										<thead>
											<tr>
												<th>行号</th>
												<th>字典名称</th>
												<th>字典描述</th>
												<th>版本信息</th>
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
										<select id="perPage" class="form-control" onChange="changeDictList()">
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
										<ul class="pagination" id="changePageDict"></ul>
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

