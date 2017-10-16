<!--首页 -->
<#include "../inc/core.ftl"> 
<@htmHead title="接口文档管理平台"
otherCss=["/css/common/chosen.css"]
otherJs=["/js/productAndproject/createProduct.js", 
"/js/common/bootbox.js",
"/js/common/chosen.jquery.js"]
/>

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">
					创建产品 <a class="btn btn-warning btn-sm"
						href="${cdnBaseUrl}/idoc/index.html">返回我的产品</a>
				</h2>
            </div>
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
									<input type="text" id="productName" class="form-control" maxlength="60" placeholder="必填，最大输入60个字符">
								</div>
							</div>
			 			</div>
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">产品描述：</label>
								<div class="col-sm-8" style="padding-left: 0px;">
									<input type="text" id="producDescription" class="form-control">
								</div>
							</div>
			 			</div>
			 			<div class="row">
				 			<div class="form-group" style="padding-top:5px;">
								<label class="col-sm-1 control-label"
									style="width: 100px; padding-top: 5px; padding-left: 20px; padding-right: 5px;">产品域名：</label>
								<div class="col-sm-8" style="padding-left: 0px;">
									<input type="text" id="productDomainUrl" class="form-control" maxlength="1000" placeholder="http://lede.qa.com">
								</div>
							</div>
			 			</div>
			 			<div class="col-lg-12" align="right" style="padding-bottom:5px;">
							<button class="btn btn-primary" id="addProductUser" onClick="addProductUser()">添加成员</button>
						    <button class="btn btn-primary" id="addProduct" onClick="createProduct()">确认创建</button>
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
												<th>操作</th>
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
        </div>
    </div>
    <!-- #page-wrapper -->
<@htmlNavFoot />
<@htmlFoot/>

