<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台" 
otherCss=["/css/common/dataTables.responsive.css",
"/css/common/jquery.toastmessage.css",
"/css/common/dataTables.bootstrap.css"]
otherJs=[
"/js/common/jquery.toastmessage.js",
"/js/logIn/myPage.js",
"/js/common/bootbox.js"
]
/>

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">我的产品</h2>
                <input type="hidden" id="adminFlag" value="${adminFlag! }">
                <input type="hidden" id="englishName" value="${englishName! }">
            </div>
            <!-- /.col-lg-12 -->
        </div>
        <!-- /.row -->
        <!-- /.row -->
		<div class="row">
			<div class="col-lg-12">
			 	<div class="panel panel-default">
					<div class="panel-body" style="min-height: 76%;">
					    <div class="row">
					        <div class="pull-right">
					        	<#if adminFlag=1>
					                <a href="/idoc/createProduct.html" style="float:right; padding-right:10px;padding-bottom:5px">
					                	<button type="button" class="btn btn-primary">新建产品</button>
					                </a>
					            </#if>
					        </div>
				        </div>
						<table class="table table-striped table-bordered"
							id="table_search_dict">
							<thead>
	                       		<tr>
	                           	   <th style="text-align:center">&nbsp;</th>
	                               <th style="text-align:center">产品名称</th>
	                               <th style="text-align:center">项目总数</th>
	                               <th style="text-align:center">数据字典</th>
	                               <th style="text-align:center">在线接口</th>
	                               <th style="text-align:center">操作</th>
	                            </tr>
	                        </thead>
	                             <tbody>                              
	                                                             
	                              </tbody>
						</table>
						
						<div class="row">
								<div class="form-group">
									<div class="col-sm-6">
												<label class="col-sm-1 control-label"
											style="width: 130px; padding-top: 5px; padding-left: 15px; padding-right: 5px; font-size: 14px; font-family: Helvetica Neue, ​Helvetica, ​Arial, ​sans-serif; font-weight: normal; color: #337ab7;">
											每页显示条数： </label>
										<div class="col-sm-2" style="padding-left: 0px;">
											<select id="perPage" class="form-control" onChange="changePageProductList()">
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
											<ul class="pagination" id="changePageProduct"></ul>
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

