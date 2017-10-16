<!--首页 -->
<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台" />

	<style type="text/css">
		.page_center{
   			margin-top: 40px;
   			margin-bottom: 20px;
    		text-align:center; 
		}
	</style>
	
	<div id="page-wrapper" style="min-height: 500px !important;">
		<div class="row">
            <div class="col-lg-12">
            	<div class="page_center">
	                <h1>IMP(Interface Management Platform)</h1>
			        <p>
			        	IMP是一个方便接口管理的可视化工具。 该平台的宗旨是通过自动化工具减少不必要的沟通，提升大家的工作效率。 
			        </p> 
            	</div>
            </div>
        </div>
        
        <div class="row">
            <div class="col-lg-12">
            	<div class="page_center">
	                <a class="btn btn-outline btn-success" href="${cdnBaseUrl}/demo/productDemo.html?productName=Demo">查看DEMO</a>
            	</div>
            </div>
        </div>
        
        <div class="row">
            <div class="col-lg-12">
            	<div class="page_center">
	                <div class="col-lg-4">
						<img class="img-circle" src="../img/edit.jpg" alt="Generic placeholder image" width="140" height="140">
						<h2>接口文档编辑</h2>
						<p>通过对接口的管理，让开发测试人员更好地了解接口所处的状态，及时通知接口的变更，使接口在整个流程中变得透明。让前后端约定接口的工作变得十分简单 。</p>         
			        </div>
			        <div class="col-lg-4">
						<img class="img-circle" src="../img/mock.jpg" alt="Generic placeholder image" width="140" height="140">
						<h2>返回数据MOCK</h2>
						<p>mock数据提供HTTP模拟请求,前端数据模拟，接口代码生成等功能。可以方便地单独测试接口的功能是否正常。</p>
			        </div>
			        <div class="col-lg-4">
						<img class="img-circle" src="../img/data.jpg" alt="Generic placeholder image" width="140" height="140">
						<h2>重要数据统计</h2>
						<p>对项目开发、测试等过程中的接口文档进行统计，包括项目接口信息，接口状态信息和接口修改信息进行统计，方便了解接口在整个过程中的变更。</p>
			        </div>
            	</div>
            </div>
        </div>
	</div>
<@htmlNavFoot />
<@htmlFoot/>

