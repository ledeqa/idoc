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

function addRoleConfigModel(){
	
	bootbox
		.dialog({
			title : "添加角色：",
			message : 
		"<div>"
		+"<div class='row'>"
			+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
				+"<label class='col-sm-1 control-label'"
					+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>角色名称："
				+"</label>"
				+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
					+"<textarea type='text' id='roleValue' rows='3' class='form-control' ></textarea>"
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
						data.roleValue = $("#roleValue").val();
						
						if(!isNullOrEmpty(data.roleValue)){
							$.ajax({
								url : '/idoc/addRoleConfigModel.html',
								type : 'POST',
								data : data,
								dataType : 'json',
								success : function(json) {
									if (json.retCode == 200) {
										bootbox.alert("添加成功！");
										refreshCurrentPage();
									}else if(json.retCode == 300){
										bootbox.alert("添加的角色已经存在！");
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
	window.location.reload();
}

function deleteRole(roleId){

	var data1 = {};	
	data1.roleId = roleId;
	
	$.ajax({
		url:'/idoc/deleteRole.html',
		type:'GET',
		data:data1,
		dataType:'json',
		success:function(json){
			if(json.retCode == 200){				
				
				bootbox.alert({  
					message: '删除成功',
		            buttons: {  
		               ok: {  
		                    label: '确定',  
		                    className: 'btn-myStyle',
		                }  
		            },
		            callback: function() {  
                    	window.location.reload();
		            }
		        });
			}else{
				bootbox.alert("操作失败，请联系后台人员修改！");
			}
		}
	});	
}


function updateRole(roleName,roleId){

	bootbox
	.dialog({
		title : "修改角色：",
		message : 
	"<div>"
	+"<div class='row'>"
		+"<div class='col-sm-30' style='width: 800px; padding-top: 5px; padding-left: 0px; padding-right: 5px;'>"
			+"<label class='col-sm-1 control-label'"
				+"style='width: 110px; padding-top: 5px; padding-left: 15px; padding-right: 5px;'>角色名称："
			+"</label>"
			+"<div class='col-sm-2' style='width: 450px; padding-left: 0px;'>"
				+"<textarea type='text' id='roleValue' rows='3' class='form-control' >"+roleName+"</textarea>"
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
					data.roleName = $("#roleValue").val();
					data.roleId = roleId;
					
					if(!isNullOrEmpty(data.roleName)){
						$.ajax({
							url : '/idoc/updateRoleConfigModel.html',
							type : 'POST',
							data : data,
							dataType : 'json',
							success : function(json) {
								if (json.retCode == 200) {
									
									bootbox.alert({  
										message: '修改成功',
							            buttons: {  
							               ok: {  
							                    label: '确定',  
							                    className: 'btn-myStyle',
							                }  
							            },
							            callback: function() {  
							            	refreshCurrentPage();
							            }
							        });
								}else{
									bootbox.alert("修改失败，请稍后再试！");
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