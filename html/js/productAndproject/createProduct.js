$(document).ready(function(){
	$('.chosen-select').chosen();
	
});
function addProductUser(){
	
	bootbox.dialog({title:"添加成员",
		  message:'<div class="row form-horizontal">' 
			+'<div class="form-group">' 
			+'<label class="col-lg-2 control-label">成员名:</label>' 
			+'<div class="col-lg-9">' 
			+'<select id="allUserSelect" class="form-control chosen-select" multiple data-placeholder="请输入成员名称">'
    	    +'</select>' +
		    '</div>' +
		'</div>' +
		
		'<div class="form-group">' +
		'<label class="col-lg-2 control-label">角色名:</label>' +
		'<div class="col-lg-9">' +
		'<select id="roleSelected" class="form-control" ></select> '
		+
	    '</div>' +
	'</div>' +
	  '</div>'
		,
		 locale:"zh_CN",

		 buttons:{
			
	           Ok:{
		      label: '确定',
		      className : 'btn-primary',
		      callback: function() {
		    	  var userId = $('#allUserSelect').val();
					if(!userId){
						$('#allUserSelect').after('<span style="color:red;">请选择成员</span>');
						
						return false;
					}
				 serchUesrById();

		      }	
	      
		     
	           },
	           Cancel: {  
	               label: "取消",  
	               className : "btn-danger",  
	               callback: function () {  
	              	// bootbox.alert("已取消");
	                   //alert("Cancel");  
	               }  
	           }  
	           
	}});
	getRoleName();
	setTimeout(getAllUser(), 100);
	setTimeout('$(".chosen-select").chosen();', 200);
}

function serchUesrById(){
	var data={};
	var userIds=$("#allUserSelect").val();
	 data.userIds=userIds;
		$.ajax({
			url:'/idoc/selectUserByIds.html',
			type:'POST',
			dataType:'json',
			data: data,
			success:function(json){
				if(json.retCode == 200){
					var userModels = json.userModels;
					var tbl1 = $("#table_product_user tbody");
					var roleName=document.all.roleSelected.options[document.all.roleSelected.selectedIndex].text;
					var role=$('#roleSelected').val();
					for(var i = 0; i < userModels.length; ++i){	
						 trHtml1 = "";
		                    trHtml1 += "<tr id = '" + userModels[i].userId +","+role+"'>";
		                    trHtml1 += "<td width='8%'>" + userModels[i].userName + "</td>";
		                    trHtml1 += "<td width='8%'>" + userModels[i].corpMail+ "</td>";
		                    trHtml1 += "<td width='8%'>" + roleName + "</td>";
		                    trHtml1 += "<td width='8%'><a href='javascript:void(0)'class='btn btn-warning btn-sm' onclick='deleteRow(this)'>删除</a></td>";
		                    trHtml1 += "</tr>";
		                 tbl1.append(trHtml1);	
					}
					
				}else{
					bootbox.alert("获得成员失败！");
				}
			}
		});
		
}

function deleteRow(t) {
	$(t).parent().parent().remove();

}

function createProduct(){

	var data = {};	
	data.productName = $("#productName").val();
	data.producDescription =$("#producDescription").val();
	data.productDomainUrl = $("#productDomainUrl").val();
	data.allIds="";
	if(!data.productName){
		bootbox.alert("产品名称不能为空！");
		return;
	}
    var trsUser=$('#table_product_user tbody tr');
    for(var k = 0; k < trsUser.length; k++){
		data.allIds += trsUser[k].id + "##";
	}
	if($("#productName").val().trim()||$("#productName").val().trim()==""){
		
		bootbox.dialog({  
		       message: " ",  
		       title: "您确定创建？",  
		       buttons: {  
		           OK: {  
		               label: "确认",  
		               className: "btn-primary",  
		               callback: function () {  
		                  // alert("OK");  
		            	   $.ajax({
		           			url:'/idoc/addProduct.html',
		           			type:'POST',
		           			data:data,
		           			dataType:'json',
		           			
		           			success:function(json){
		           				if(json.retCode == 200){				
		           					bootbox.alert({  
										message: '新建产品成功',
							            buttons: {  
							               ok: {  
							                    label: '确定',  
							                    className: 'btn-myStyle',
							                }  
							            },
							            callback: function() {  
							            	self.location = "/idoc/index.html";
							            }
							        });
		           				}
		           				else if(json.retCode == 400){
		           					bootbox.alert("该产品已经存在");	
		           				}
		           				else{
		           					bootbox.alert("操作失败，请联系后台人员修改！");
		           				}
		           			}
		           		});	
		            	   
		               }  
		           }, 
		 Cancel: {  
             label: "取消",  
             className: "btn-danger",  
             callback: function () {  
            	
             }  
         }  
         
		 }  
		}); 
		
		
	}else{
		bootbox.alert("产品名称为空，请输入产品名称！");
	}
	
	
}

function getRoleName(){
	var data = {};
	$.ajax({
		url:'/idoc/selectAllRole.html',
		type:'POST',
		dataType:'json',
		data: data,
		async:false,
		success:function(json){
			if(json.retCode == 200){
				var roleNameList = json.roleConfigList;
				$("#roleSelected").empty();
				var tbl1 = $("#table_product_user tbody");
				for(var i = 0; i < roleNameList.length; ++i){	
					var str = "<option value="+roleNameList[i].roleId+">" + roleNameList[i].roleName + "</option>";
					$("#roleSelected").append(str);
				}
				$("#taskProjectName option[value='"+roleNameList[0]+"']").attr("selected",true);
			}else{
				bootbox.alert("角色表为空");
			}
		}
	});
	
}

function getAllUser(){
	var data = {};
	
	$.ajax({
		url:'/idoc/selectAllUser.html',
		type:'POST',
		dataType:'json',
		data: data,
		async:false,
		success:function(json){
			if(json.retCode == 200){
				var userList = json.userModels;
				$("#allUserSelect").empty();
				var exitUsers = new Array();
				var table_len = $("#table_product_user tbody").find("tr").length;
				for(var i=0; i<table_len; i++){
					var tr = $("#table_product_user tbody").find("tr").eq(i);
					var userId = tr.attr("id");
					if(userId){
						var id = userId.split(",");
						exitUsers.push(eval(id[0]));
					}
				}
				for(var i = 0; i < userList.length; ++i){
					var userId = userList[i].userId;
					var index = $.inArray(userId, exitUsers);
					if(index == -1){
						var str = "<option value="+userList[i].userId+">" + userList[i].nickName+" "+userList[i].userName + "</option>";
						$("#allUserSelect").append(str);
					}
				}
				//$("#taskProjectName option[value='1']").attr("selected", projectNameList[0]);
				$("#taskProjectName option[value='"+userList[0]+"']").attr("selected",true);
			}else{
				bootbox.alert("用户表为空");
			}
		}
	});
	
}