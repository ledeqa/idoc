function addIniConfigModel(){
	
	bootbox
		.dialog({
			title : "添加ini配置信息：",
			message : 
		"<div>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>ini_key："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<input type='text' id='iniKey' class='form-control'>"
				+"</div>"
		    +"</div>"
		+"</div>"
			
		+"<br>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>ini_value："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<textarea type='text' id='iniValue' rows='3' class='form-control' ></textarea>"
				+"</div>"
			+"</div>"
		+"</div>"
		
		+"<br>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>配置说明："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<textarea type='text' id='iniDesc' rows='4' class='form-control' ></textarea>"
				+"</div>"
			+"</div>"
		+"</div>"
	
		+ "</div>",
			locale : "zh_CN",
			buttons : {
			Modify:{
					label : '添加',
					className : "btn-primary",
					callback : function() {
						var data = {};
						data.iniKey = $("#iniKey").val();
						data.iniValue = $("#iniValue").val();
						data.iniDesc = $("#iniDesc").val();
						
						if(!isNullOrEmpty(data.iniKey) && !isNullOrEmpty(data.iniValue) 
						&& !isNullOrEmpty(data.iniDesc)){
							$.ajax({
								url : '/ini/addIniConfigModel.html',
								type : 'POST',
								data : data,
								dataType : 'json',
								success : function(json) {
									if (json.retCode == 200) {
										bootbox.alert("添加成功！");
										refreshCurrentPage();
									}else if(json.retCode == 300){
										bootbox.alert("添加的key已经存在！");
									}else{
										bootbox.alert("添加信息失败，请稍后再试！");
									}
								}
							});
						}else{
							bootbox.alert("请填写完整信息！");
						}
					}
				},
			Cancel:{
					label : '取消',
					className : "btn-danger",
					callback : function() {
					}
				}
			}
		});
}

function refreshCurrentPage(){
	$.ajax({
		url : '/ini/refreshCurrentPage.html',
		type : 'POST',
		dataType : 'json',
		success : function(json) {
			if (json.retCode == 200) {
				var iniConfigList = json.iniConfigList;
				var tbodyIniConfig = $("#tbIniConfig tbody");
				tbodyIniConfig.empty();
				
				for(var i = 0; i < iniConfigList.length; i++){
					var tbbodyHtml;
					var iniConfig = iniConfigList[i];
					tbbodyHtml = "<tr onmouseover='over_color(this)' onmouseout='remove_color(this)'>"
								+ "<td style='width: 250px;'>" + iniConfig.iniKey + "</td>"
								+ "<td style='width: 600px;'>" + iniConfig.iniValue + "</td>"
								+ "<td style='width: 600px;'>" + iniConfig.iniDesc + "</td>"
								+ "<td style='width: 150px;'>"
								+ "<input iniKey='" + iniConfig.iniKey + "' type='button' class='btn btn-sm btn-success updateIniConfig' value='修改' /> &nbsp;"
						        + "<input iniKey='" + iniConfig.iniKey + "' type='button' class='btn btn-sm btn-warning deleteIniConfig' value='删除' />"
						        + "</td>"
						        + "</tr>"
				    tbodyIniConfig.append(tbbodyHtml);
				}
				
				$('.updateIniConfig').click(function(){
					var iniKey = $(this).attr("iniKey");
					updateIniConfigModel(iniKey);
				});
				$('.deleteIniConfig').click(function(){
					var iniKey = $(this).attr("iniKey");
					deleteIniConfigModel(iniKey);
				});
			}else{
				bootbox.alert("刷新当前页面失败！");
			}
		}
	});
}

function updateIniConfigModel(iniKey){
	selectIniConfigByIniKey(iniKey);
	bootbox
	.dialog({
		title : "修改ini配置信息：",
		message : 
		"<div>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>ini_key："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<input type='text' id='iniKey' value='" + iniKey + "' class='form-control' readonly>"
				+"</div>"
		    +"</div>"
		+"</div>"
			
		+"<br>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>ini_value："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<textarea type='text' id='iniValue' rows='3' class='form-control' > </textarea>"
				+"</div>"
			+"</div>"
		+"</div>"
		
		+"<br>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>配置说明："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<textarea type='text' id='iniDesc' rows='4' class='form-control' > </textarea>"
				+"</div>"
			+"</div>"
		+"</div>"
	
		+ "</div>",
			locale : "zh_CN",
			buttons : {
			Modify:{
					label : '修改',
					className : "btn-primary",
					callback : function() {
						var data = {};
						data.iniKey = $("#iniKey").val();
						data.iniValue = $("#iniValue").val();
						data.iniDesc = $("#iniDesc").val();
						
						if(!isNullOrEmpty(data.iniValue) && !isNullOrEmpty(data.iniDesc)){
							$.ajax({
								url : '/ini/updateIniConfigModel.html',
								type : 'POST',
								data : data,
								dataType : 'json',
								success : function(json) {
									if (json.retCode == 200) {
										bootbox.alert("修改成功！");
										refreshCurrentPage();
									}else{
										bootbox.alert("修改信息失败，请稍后再试！");
									}
								}
							});
						}else{
							bootbox.alert("请填写完整信息！");
						}
					}
				},
			Cancel:{
					label : '取消',
					className : "btn-danger",
					callback : function() {
					}
				}
			}
		});
}

function selectIniConfigByIniKey(iniKey){
	var data = {};
	data.iniKey = iniKey;
	$.ajax({
		url : '/ini/selectIniConfigByIniKey.html',
		type : 'POST',
		data : data,
		dataType : 'json',
		success : function(json) {
			if (json.retCode == 200) {
				var iniConfig = json.iniConfig;
				$("#iniValue").empty();
				$("#iniDesc").empty();
				
				$("#iniValue").val(iniConfig.iniValue);
				$("#iniDesc").val(iniConfig.iniDesc);
			}else{
				bootbox.alert("获取iniKey对应信息失败！");
			}
		}
	});
}
function deleteIniConfigModel(iniKey){
	
	var data = {};
	data.iniKey = iniKey;
	
	 bootbox.dialog({  
       message: "您确定要删除该条配置信息吗？",  
       title: "删除提示框",  
       buttons: {  
           Cancel: {  
               label: "取消",  
               className: "btn-info",  
               callback: function () {  
               }  
           }  
           , OK: {  
               label: "确认",  
               className: "btn-primary",  
               callback: function () {  
            	   $.ajax({
						url:'/ini/deleteIniConfigModel.html',
						type:'POST',
						data:data,
						dataType:'json',
						success:function(json){
							if(json.retCode == 200){
								bootbox.alert("删除成功！");
								refreshCurrentPage();
							}else{
								bootbox.alert("删除失败！");
							}
						}		
					}); 
               }  
           }  
       }  
   });  	
}	

function refreshIniConfig(){
	$.ajax({
		url : '/ini/refreshIniConfig.html',
		type : 'POST',
		dataType : 'json',
		success : function(json) {
			if (json.retCode == 200) {
				bootbox.alert("刷新配置成功！");
			}else{
				bootbox.alert("刷新配置失败，请稍后再试！");
			}
		}
	});
}

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
	return true;
	} else {
	return false;
	}
}

function over_color(obj){
	   var overColor="Lavender";
	    var clickColor="pink";
	    var defaultColor="#ffffff";
if(obj.style.backgroundColor!=clickColor)
obj.style.backgroundColor=overColor;
}
function remove_color(obj){
	   var overColor="Lavender";
	    var clickColor="pink";
	    var defaultColor="#ffffff";
if(obj.style.backgroundColor!=clickColor)
obj.style.backgroundColor=defaultColor;
}