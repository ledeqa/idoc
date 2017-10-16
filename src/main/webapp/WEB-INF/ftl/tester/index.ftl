<#include "../inc/core.ftl"> 
<@htmHead title="接口文档管理平台"
otherJs=["/js/common/mock.js", "/js/common/jsformat.js", "/js/tester/tester.js"]/>

	<style type="text/css">
	#resBoard{
		width: 100%;
		min-height: 350px;
		overflow-y: auto;
		word-wrap: break-word;
		word-break: break-all;
		margin: 12px 0;
	    padding: 8px;
	}
	</style>
	<div id="page-wrapper">
    	<div class="row">
            <div class="col-lg-12">
                <h2 class="page-header">接口名称：${inter.interfaceName! }</h2>
            </div>
			<input type="hidden" id="projectId" value="${projectId! }">
        </div>
        
        <div class="row">
        	<div class="col-lg-12">
			 	<div class="panel panel-default">
			 		<div class="panel-body">
			 			<div class="row">
			 				<div class="col-lg-12">
			 					<dl class="dl-horizontal">
			 						<dt>请求地址</dt>
			 						<dd id="path">${inter.url! }</dd>
			 					</dl>
			 				</div>
			 			</div>
			 			<div class="row">
							<div class="col-lg-6 form-horizontal">
								<#if inter.reqParams ??>
									<#list inter.reqParams as param>
										<div class="form-group">
											<label class="col-lg-2 col-lg-offset-1 control-label name">${param.paramName }</label>
										    <div class="col-lg-4">
										    	<input type="text" class="form-control field" name="${param.paramName }">
										    </div>
										</div>
									</#list>
								</#if>
								
								<button class="col-lg-offset-2 btn btn-primary" id="run">请求</button>								
							</div>			 			
			 			</div>
			 			<div class="row">
			 				<div class="col-lg-12">
			 					<div class="well" id="resBoard">ready</div>
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