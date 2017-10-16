<#include "../inc/core.ftl">
<@htmHead title="接口文档管理平台"
otherJs=["/js/common/bootbox.js",
"/js/usermanager/addUser.js",
"/js/core/core.js"]
/>
<@htmlNavHead activeName="userinfo"/>
<div id="page-wrapper">
    <div id="tableDiv" class="row" style="width:60%">
        <br/> <br/> <br/>
        <div class="col-md-12">

            <h3><span class="label label-info">完善个人信息的功能</span></h3>
            <br/>
            <div class="row form-group">
                <label for="corpMailLabel" class="col-sm-2 control-label">邮箱: </label>
                <div class="col-sm-12">
                <#if userCorpMail?exists>
                    <input type="text" id="corpMail" value="${userCorpMail}" class="form-control" readonly/>
                <#else>
                    <input type="text" id="corpMail" class="form-control"/>
                </#if>
                </div>
            </div>

            <div class="row form-group">
                <label for="userNameLabel" class="col-sm-2 control-label">用户姓名：</label>
                <div class="col-sm-12">
                    <input type="text" id="userName" value="${userName!''}" class="form-control"/>
                </div>
            </div>

            <div class="row form-group">
                <label for="userNameLabel" class="col-sm-2 control-label">手机号码：</label>
                <div class="col-sm-12">
                    <input type="text" id="telephone" value="${telephone!''}" class="form-control"/>
                </div>
            </div>

            <div class="row form-group">
                <label for="jobNumberLabel" class="col-sm-2 control-label">工&nbsp&nbsp&nbsp号：</label>
                <div class="col-sm-12">
                    <input type="text" id="jobNumber" value="${jobNumber!''}" class="form-control"/>
                </div>
            </div>
            <#--<div class="row form-group">-->
                <#--<div class="col-sm-12">-->
                    <#--<img src="-->

                    <#--/idoc/qrcode.html?email=${userCorpMail}" alt="用户信息二维码"/>-->
                <#--</div>-->
            <#--</div>-->

            <hr>
            <br/>
            <div class="row" style="padding-top: 5px; padding-left: 680px; padding-right: 5px;">
                <label for="addUserInfoLabel" class="col-sm-2 control-label">
                    <button id="addUserInfo" class="btn btn-primary" onClick="updateUserInfo()">修改用户信息</button>
                </label>
                <div class="col-sm-12"></div>
            </div>
        </div>
    </div>
</div>
<@htmlNavFoot />
<@htmlFoot/>