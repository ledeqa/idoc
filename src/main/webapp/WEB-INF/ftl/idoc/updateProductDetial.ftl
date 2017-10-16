<!--首页 -->
<#include "../inc/core.ftl"> 
<@htmHead title="接口文档管理平台"
otherCss=["/css/common/chosen.css",
"/css/common/dataTables.responsive.css",
"/css/common/dataTables.bootstrap.css"]
otherJs=["/js/productAndproject/updateProductDetial.js", 
"/js/common/bootbox.js",
"/js/common/chosen.jquery.js"]/>

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
				<h2 class="page-header">
					修改产品 <a class="btn btn-warning btn-sm"
						href="${cdnBaseUrl}/idoc/index.html">返回我的产品</a>
				</h2>
            </div>
            <input type="hidden" id="productId" value="${productModel.productId?if_exists}">
			<input type="hidden" id="productDetial" value="${productModel.productId?if_exists}">
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
		 		<div class="panel panel-default">
		 			<div class="panel-body">
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">产品名称：</label>
								<div class="col-sm-8" style="padding-left: 0px;">
									<input type="text" id="productName" class="form-control" maxlength="60" readonly="readonly" value='${productModel.productName?if_exists}'/>
								</div>
							</div>
			 			</div>
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">产品描述：</label>
								<div class="col-sm-8" style="padding-left: 0px;">
									<input type="text" id="productDesc" class="form-control" readonly="readonly" value='${productModel.productDesc?if_exists}'/>
								</div>
							</div>
			 			</div>
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">产品域名：</label>
								<div class="col-sm-8" style="padding-left: 0px;">
									<input type="text" id="productDomainUrl" class="form-control" readonly="readonly" value='${productModel.productDomainUrl?if_exists}'/>
								</div>
							</div>
			 			</div>
			 			
			 			<div class="col-lg-12" style="padding-top:10px; padding-bottom:5px">
							<label class="col-lg-1 control-label" style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">成员名称：
							</label>
							<div class="col-lg-2" style="padding-left: 0px;">
								<input type="text" id="userName1" class="form-control">
							</div>
							<label class="col-lg-1 control-label" style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">成员邮箱：
							</label>
							<div class="col-lg-2" style="padding-left: 0px;">
								<input type="text" id="userEmail" class="form-control">
							</div>
							<label class="col-lg-1 control-label" style="width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;">成员角色：
							</label>
							<div class="col-lg-2" style="padding-left: 0px;">
								<select id="userRoleSelect" class="form-control" onchange="changeUserRole()">
									
								</select>
							</div>
							<div class="col-lg-2">
								<button class="btn btn-primary" onClick="getProductDetialUser(1)">搜索</button>
							</div>
						</div>
			 			
						<div class="row">
							<div class="col-lg-12">
								<div>
									<table class="table table-striped table-bordered"
										id="table_product_user">
										<thead>
											<tr class="success">
												<th>成员名称</th>
												<th>邮箱</th>
												<th>角色</th>
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
										<select id="perPage" class="form-control" onChange="changePageUserList()">
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
										<ul class="pagination" id="changePageUser"></ul>
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

