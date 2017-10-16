<#include "/inc/admin_core.ftl"> <@htmHead title="接口文档管理平台"
otherCss=["/css/common/dataTables.responsive.css",
"/css/common/dataTables.bootstrap.css"]
otherJs=["/js/common/jquery.dataTables.min.js",
"/js/ini/iniConfigManagement.js",
"/js/common/dataTables.bootstrap.min.js",
"/js/common/bootbox.js"] />
<style>
</style>

<@htmlNavHead activeName="ini"/>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h3 class="page-header">
				<button class="btn btn-primary" onClick="addIniConfigModel()">添加配置信息</button> &nbsp;
				<button class="btn btn-success" onClick="refreshIniConfig()">刷新配置信息</button>
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
							<th style="width: 250px;">ini_key</th>
							<th style="width: 600px;">ini_value</th>
							<th style="width: 600px;">配置说明</th>
							<th style="width: 150px;">操作</th>
						</tr>
					</thead>
					<tbody>
						<#if iniConfigList?exists>
							<#list iniConfigList as iniConfig>
								<tr onmouseover='over_color(this)' onmouseout='remove_color(this)'>
								<td style="width: 250px;">
								${iniConfig.iniKey?if_exists}
								</td>
								<td style="width: 600px;">
								${iniConfig.iniValue?if_exists}
								</td>
								<td style="width: 600px;">
								${iniConfig.iniDesc?if_exists}
								</td>
								
								<td style='width: 150px;'>
								<button id="updateIniConfig" class="btn btn-sm btn-success" onClick="updateIniConfigModel('${iniConfig.iniKey?if_exists}')">修改</button> &nbsp;
								<button id="deleteIniConfig" class="btn btn-sm btn-warning" onClick="deleteIniConfigModel('${iniConfig.iniKey?if_exists}')">删除</button>
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
<@htmlNavFoot /> 
<@htmlFoot/>
