<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="自动生成代码"

otherCss=["/css/common/jquery.toastmessage.css", "/css/common/font-awesome/css/font-awesome.css"] 
otherJs=["/js/common/bootbox.js", "/js/common/FileSaver.js", "/js/common/jquery.toastmessage.js",
"/js/common/lhgdialog/lhgdialog.js?skin=jtop", "/js/code/code.js", "/js/common/jquery.cookie.js"]
/>
	<div id="page-wrapper" style="width: 80%; margin-top: 2%; margin-left: 10%; border-left: 3px solid rgb(217,216,216); border-right: 3px solid rgb(217,216,216);">
    	<div class="row">
            <div class="col-sm-12">
                <h1 class="page-header" style="border-bottom: 2px solid #1F7D9C;">自动生成模板代码
                </h1>
            </div>
            <!-- /.col-sm-12 -->
        </div>
        <!-- /.row -->
		<div class="row">
			<div class="container" style="max-width: 85%; min-height: 700px; margin-top: 20px; border: 1px solid rgb(217,216,216);">
			 <div class="row">
			  <ul class="nav nav-pills pull-right" style="margin-top: 15px; margin-right: 5px;">
			    <li class="active"><a data-toggle="pill" href="#byClass">通过类自动生成代码</a></li>
			    <li><a data-toggle="pill" href="#bySql">通过SQL自动生成代码</a></li>
			    <li><a data-toggle="pill" href="#byDatabase">通过数据库表自动生成代码</a></li>
			  </ul>
			 </div>
			  
			  <div class="tab-content">
			    <div id="byClass" class="tab-pane fade in active">
			      <h3>根据类属性自动生成代码</h3>
			      <form id="classForm" class="form-horizontal" role="form" style="width:80%;margin:20px 10%; padding-top:60px;">
				      <div class="form-group" style="margin-left: 10%">
					    <label class="col-sm-2 control-label" for="inputPackage">Java类包路径 : </label>
					    <div class="col-sm-5">
					      <input id="inputPackage" class="form-control" name="packageName" type="text">
					     </div>
					      <span class="prompt-span" style="margin-left: 15px;">例如：com.corp.sys</span>
					  </div>
					  <div class="form-group" style="margin-left: 10%">
					      <label class="col-sm-2 control-label" for="inputName">类名 : </label>
					      <div class="col-sm-5">
					      	<input id="inputName" class="form-control" name="className" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" type="text">
					      </div>
					      <span id="divInfo" class="alert alert-danger col-sm-2" style="margin-left: 15px; padding: 6px 16px; display: none;">属性名不能为空</span>
					  </div>
					  <table class="table table-hover">
		              <thead>
		                <tr>
		                  <th style="width: 25%;">属性名</th>
		                  <th style="width: 15%;">类型</th>
		                  <th style="width: 15%;">主键</th>
		                  <th style="width: 15%;">备注</th>
		                  <th style="width: 15%;">编辑</th>
		                  <th style="width: 15%;">删除</th>
		                </tr>
		              </thead>
		              <tbody id="attr-body">
		              </tbody>
		            </table>
					  <div class="btn-class" style="margin-left: 27%; margin-top: 3%">
					      <a id="add-attr-btn" href="javascript:void(0);" class="button button-rounded button-flat button-primary button-small" style="padding:0px 40px;margin-right:14px;">添加属性</a>
					      <a id="generate-btn" href="javascript:void(0);" class="button button-rounded button-flat button-action button-small" style="padding:0px 40px;margin-left: 10%; margin-right:14px;">生成代码</a>
				      </div>
				  </form>
			      
			    </div>
			    <div id="bySql" class="tab-pane fade">
			      <h3>根据SQL语句自动生成代码</h3>
			    </div>
			    <div id="byDatabase" class="tab-pane fade">
			      <h3>根据数据库中表结构自动生成代码</h3>
			      	<div class="panel panel-info">
					   <div class="panel-heading">
					      <h3 class="panel-title">数据库连接信息</h3>
					   </div>
					   <div class="panel-body">
					      	<form id="classForm" class="form-horizontal" role="form" style="width:80%;margin:20px 10%; padding-top:20px;">
						      <div class="form-group" style="margin-left: 10%">
							    <label class="col-sm-2 control-label" for="databaseDriver">数据库Driver: </label>
							    <div class="col-sm-8">
							      <select class="form-control" id="databaseDriver">
							      	<option value="com.mysql.jdbc.Driver">com.mysql.jdbc.Driver</option>
							      	<option value="oracle.jdbc.driver.OracleDriver">oracle.jdbc.driver.OracleDriver</option>
							      </select>
							     </div>
							  </div>
							  <div class="form-group" style="margin-left: 10%">
							      <label class="col-sm-2 control-label" for="databaseUrl">数据库URL : </label>
							      <div class="col-sm-8">
							      	<input id="databaseUrl" class="form-control" name="databaseUrl" type="text" placeholder="连接数据库的url">
							      </div>
							  </div>
							  <div class="form-group" style="margin-left: 10%">
							      <label class="col-sm-2 control-label" for="userName">用户名 : </label>
							      <div class="col-sm-8">
							      	<input id="userName" class="form-control" name="userName" type="text" placeholder="数据库用户名">
							      </div>
							  </div>
							  <div class="form-group" style="margin-left: 10%">
							      <label class="col-sm-2 control-label" for="password">密码 : </label>
							      <div class="col-sm-8">
							      	<input id="password" class="form-control" name="password" type="password" placeholder="数据库密码">
							      </div>
							  </div>
							  <div class="form-group" style="margin-left: 10%">
							      <label class="col-sm-2 control-label" for="databaseName">数据库名 : </label>
							      <div class="col-sm-8">
							      	<input id="databaseName" class="form-control" name="databaseName" type="text" placeholder="数据库名">
							      </div>
							      <span id="databaseInfo" class="alert alert-danger col-sm-2" style="margin-left: 0px; padding: 6px 16px; display: none;"></span>
							  </div>
							  <div class="btn-class" style="margin-left: 27%; margin-top: 3%">
							      <a id="link-database-btn" href="javascript:void(0);" class="button button-plain button-royal button-border button-pill button-small" style="padding:0px 40px;margin-left: 57%; margin-right:14px;"><i class="fa fa-star"></i>&nbsp;&nbsp;连接数据库</a>
						      </div>
						  </form>
					   </div>
					</div>
					<div class="panel panel-warning">
					   <div class="panel-heading">
					      <h3 class="panel-title">数据库表信息</h3>
					   </div>
					   <div class="panel-body">
					      <table class="table table-bordered">
				              <thead>
				                <tr>
				                  <th align="center" style="width: 5%;"><center><input type="checkbox" name="all-checkbox"></center></th>
				                  <th style="width: 30%;">表名</th>
				                  <th style="width: 25%;">类名</th>
				                  <th style="width: 25%;">注释</th>
				                  <th style="width: 15%;"><center>详情</center></th>
				                </tr>
				              </thead>
				              <tbody id="db-table-body">
				              </tbody>
		            	  </table>
		            	  <div class="btn-class" style="margin-left: 27%; margin-top: 3%">
		            	  	   <span id="generateInfo" class="alert alert-danger col-sm-6" style="margin-left: 30%; padding: 6px 16px; display: none;"></span>
							   <a id="db-generate-btn" href="javascript:void(0);" class="button button-plain button-caution button-border button-pill button-small pull-right"><i class="fa fa-keyboard-o"></i>&nbsp;&nbsp;生成代码</a>&nbsp;&nbsp;
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