<#include "/inc/admin_core.ftl"> <@htmHead title="接口文档管理平台"
otherJs=["/js/common/bootbox.js",
"/js/role/roleConfigManagement.js"] />
<style>
</style>

<@htmlNavHead activeName="role"/>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h3 class="page-header">
				<button class="btn btn-primary" onClick="addRoleConfigModel()">添加角色</button> &nbsp;
				<button class="btn btn-success" onClick="refreshCurrentPage()">刷新角色信息</button>
			</h3>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="row">
			<div class="col-lg-12">
				<table class="table table-striped table-bordered"
					id="tbIniConfig">
					<thead>
						<tr class="info">
							<th>#</th>
							<th style="width: 250px;">角色ID</th>
							<th style="width: 600px;">角色名称</th>
							<th style="width: 600px;">创建时间</th>							
							<th style="width: 150px;">操作</th>
						</tr>
					</thead>
					<tbody>
						<#if roleConfigList?exists>
							<#list roleConfigList as roleConfig>
								<tr onmouseover='over_color(this)' onmouseout='remove_color(this)'>
								<td style="width: 200px;">${roleConfig_index + 1 }</td>
								<td style="width: 300px;">
								${roleConfig.roleId?if_exists?c}
								</td>
								<td style="width: 400px;">
								${roleConfig.roleName?if_exists}
								</td>
								<td style="width: 150px;">
								${roleConfig.createTime?if_exists}
								</td>							
								<td style='width: 400px;'>
								<button id="updateIniConfig" class="btn btn-sm btn-success" onClick="updateRole('${roleConfig.roleName?if_exists}','${roleConfig.roleId?if_exists?c}')">修改</button> &nbsp;
								<button id="deleteIniConfig" class="btn btn-sm btn-warning" onClick="deleteRole('${roleConfig.roleId?if_exists?c}')">删除</button>
								</td>
								</tr>
							</#list>
						</#if>
					</tbody>
				</table>
			</div>
		</div>

	</div>
</div>
<@htmlFoot/>
