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
"/js/common/bootbox.js",
"/js/common/fileinput.js",
"/js/dict/dictHistory.js"
]
/>

	<div id="page-wrapper">
		<div class="row">
			<div class="breadhead">
				<ol class="breadcrumb">
				  <li><i class="fa fa-home"></i>&nbsp;<a href="/">首页</a></li>
				  <li><a href="/dict/index.html?productId=${productId! }">${productName! }-数据字典</a></li>
				  <li class="active">查看${dictName!''}字典历史版本信息</li>
				</ol>
			</div>
		</div>
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">${dictName!''}字典历史版本信息
                	<small><a class="btn btn-sm btn-success" href="/dict/index.html?productId=${productId! }">返回</a></small>
                </h2>
            </div>
            <input type="text" hidden="true" id="dictId" value="${dictId!''}"/>
            <input type="text" hidden="true" id="productId" value="${productId!''}"/>
            <input type="text" hidden="true" id="productName" value="${productName!''}"/>
        </div>
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-default">
			 		<div class="panel-body">
						<div class="row">
							<div class="col-lg-12">
								<div>
									<table class="table table-striped table-bordered"
										id="table_search_dict">
										<thead>
											<tr>
												<th>行号</th>
												<th>版本号</th>
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
										<select id="perPage" class="form-control" onChange="changeDictHistoryList()">
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
										<ul class="pagination" id="changePageDictHistory"></ul>
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

