<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台" 
otherCss=[
"/css/common/dataTables.bootstrap.css",
"/css/common/dataTables.responsive.css"
]
otherJs=[
"/js/common/dataTables.bootstrap.min.js",
"/js/common/jquery.dataTables.min.js",
"/js/common/bootbox.js",
"/js/dict/addDict.js"
]
/>

	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">编辑数据字典</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>
        <!-- /.row -->
        <!-- /.row -->
 	<div class="panel panel-default">
 	 	<div class="col-lg-4" style="padding-top:5px;padding-bottom:5px">
	 	 	<div class="input-group">
				  <span class="input-group-addon">字典名称:</span>
				  <input type="text" class="form-control" id="addDictName" placeholder="请输入字典名称" maxlength="60">
			</div>
	 	 	<div class="input-group">
				  <span class="input-group-addon">字典描述:</span>
				  <input type="text" class="form-control" id="addDictDesc" placeholder="请输入字典描述" maxlength="60">
			</div>
		</div>
		<div class="row">
			<div class="col-lg-12">
				<div>
					<table class="table table-striped table-bordered"
						id="table_add_dict">
						<thead>
							<tr>
								<th>名称</th>
								<th>类型</th>
								<th>含义</th>
								<th>mock</th>
								<th>备注</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div class="col-lg-12">
					<div class="col-lg-3" style="padding-bottom:5px; padding-left:5px">
						<button class="btn btn-primary" onClick="addParams()">添加参数</button>
					</div>
					<div class="col-lg-9" style="padding-bottom:5px" align="right">
						<button class="btn btn-primary" onClick="saveParams()">保存</button>
						<button class="btn btn-primary" onClick="cancelParams()">取消</button>
					</div>
				</div>
			</div>
		</div>
	</div>
    </div>
    <!-- #page-wrapper -->

<@htmlNavFoot />
<@htmlFoot/>

