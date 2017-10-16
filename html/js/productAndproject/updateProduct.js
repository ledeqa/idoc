$(document).ready(function() {
	getUserRole();
	getProductUser(1);
});

function getProductUser(pageNum) {
	var data = {};
	data.productId = $("#productId").val();
	data.perPage = $("#perPage").val();
	data.pageNum = pageNum;
	data.userName = $("#userName1").val().trim();
	data.userEmail = $("#userEmail").val().trim();
	data.userRole = $("#userRoleSelect").val();

	$.ajax({
		url : '/idoc/getProductUserById.html',
		type : 'POST',
		data : data,
		dataType : 'json',
		success : function(json) {
			var list = json.productUserModels;
			var pageInfor = json.page;
			$("#table_product_user tbody").empty();
			if (json.retCode == 200) {
				var len = list.length;
				for (var i = 0; i < len; i++) {
					var res = list[i];
					str = "";
					str = "<tr  id=\""+res.user.userId+"\"onmouseover='over_color(this)' onmouseout='remove_color(this)' class=\"gradeA odd\" role=\"row\">"
							+ "<td >"
							+ res.user.userName
							+ "</td>"
							+ "<td >"
							+ res.user.corpMail
							+ "</td>"
							+ "<td >"
							+ res.role.roleName
							+ "</td>"
							+ "<td >"
							+"<button  class=\"btn btn-success\" onclick=\"updateProductUser('"+ res.user.userName+"','" + res.user.userId + "','" +res.productUserId+"')\">修改</button>&nbsp;&nbsp;<button  class=\"btn btn-warning\" onclick=\"delectProductUser('"
							+ res.productUserId
							+ "')\">删除</button>"
							+ "</td>"
							+"</tr>";
							$("#table_product_user tbody").append(str);
				}
				getPage(pageInfor, len);
			} else {
				bootbox.alert("没有找到产品对应的成员！");
				getPage(pageInfor, 0);
			}
		}
	});
}

function getPage(pageInfor, totalNum){
	var totalPage = pageInfor.totalPage;
	var currentPage = pageInfor.currentPage;
	var totalRecord = pageInfor.totalRecord;
	var halfPageNum = 5;
	var minPageNum = 1;
	var maxPageNum = totalPage;

	if (currentPage + halfPageNum < totalPage) {
		maxPageNum = currentPage + halfPageNum;
	}
	if (currentPage - halfPageNum > 1) {
		minPageNum = currentPage - halfPageNum;
	}

	$("#changePageUser").empty();
	$("#changePageUser")
			.append(
					"<li><a href=\"#\" onClick=\"getProductUser(1)\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
	for (var i = minPageNum; i <= maxPageNum; ++i) {
		var str;
		if (currentPage == i) {
			str = "<li class=\"active\"><a href=\"#\" onClick=\"getProductUser("
					+ i + ")\">" + i + "</a></li>";
		} else {
			str = "<li ><a href=\"#\" onClick=\"getProductUser(" + i
					+ ")\">" + i + "</a></li>";
		}

		$("#changePageUser").append(str);
	}
	$("#changePageUser")
			.append(
					"<li class=\"paginate_button\" aria-controls=\"dataTables-example\"><a href=\"#\" onClick=\"getProductUser("
							+ totalPage
							+ ")\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
	$("#changePageUser").append(
			"<li><span >" + "总计  " + totalRecord + " 条记录" + " 总计  " + totalPage
					+ " 页" + "</span></li>");
}

function addProductUser() {

	bootbox
			.dialog({
				title : "添加成员",
				message : '<div class="row form-horizontal">'
						+ '<div class="form-group">'
						+ '<label class="col-lg-2 control-label">成员名:</label>'
						+ '<div class="col-lg-9">'
						+ '<select id="allUserSelect" class="form-control chosen-select" multiple data-placeholder="请输入成员名称">'
						+ '</select>'
						+ '</div>'
						+ '</div>'
						+

						'<div class="form-group">'
						+ '<label class="col-lg-2 control-label">角色名:</label>'
						+ '<div class="col-lg-9">'
						+ '<select id="roleSelected" class="form-control" data-placeholder="请选择角色"></select> '
						+ '</div>' + '</div>' + '</div>',
				locale : "zh_CN",

				buttons : {

					Ok : {
						label : '确定',
						className : 'btn-primary',
						callback : function() {
							var userId = $('#allUserSelect').val();
							if (!userId) {
								$('#allUserSelect')
										.after('<span style="color:red;">请选择成员</span>');

								return false;
							}
							addProductUserReal();
//							serchUesrById();
						}
					},
					Cancel : {
						label : "取消",
						className : "btn-danger",
						callback : function() {
						}
					}

				}
			});
	getRoleName();
	setTimeout(getAllUser(), 100);
	setTimeout('$(".chosen-select").chosen();', 200);
}

function updateProductUser(productUserName, userId, productUserId) {

	bootbox
			.dialog({
				title : "修改成员",
				message : '<div class="row form-horizontal">'
						+ '<div class="form-group">'
						+ '<label class="col-lg-2 control-label">成员名:</label>'
						+ '<div class="col-lg-9">'
						+ '<input class="form-control" id="userName" value="' + productUserName + '">'
						+ '</div>'
						+ '</div>'
						+'<div class="form-group">'
						+ '<label class="col-lg-2 control-label">角色名:</label>'
						+ '<div class="col-lg-9">'
						+ '<select id="roleSelected" class="form-control" ></select> '
						+ '</div>' + '</div>' + '</div>',
				locale : "zh_CN",

				buttons : {

					Ok : {
						label : '确定',
						className : 'btn-primary',
						callback : function() {
							var userName = $('#userName').val().trim();
							if(productUserName == userName)
								updateProductUserRole(userId, userName, productUserId, $("#roleSelected").val().trim(), false);
							else
								updateProductUserRole(userId, userName, productUserId, $("#roleSelected").val().trim(), true);
							var currPage = $("#changePageUser li.active a").html();
							if($("#userName1").val() != "")
								$("#userName1").val(userName);
							getProductUser(currPage);
						}
					},
					Cancel : {
						label : "取消",
						className : "btn-danger",
						callback : function() {
							// bootbox.alert("已取消");
							//alert("Cancel");  
						}
					}

				}
			});
	getRoleName();
}
function delectProductUser(productUserId){
	var data = {};
	data.productUserId=productUserId;
	
	
		
		bootbox.dialog({
			message : " ",
			title : "您确定删除？",
			buttons : {
				OK : {
					label : "确认",
					className : "btn-primary",
					callback : function() {
						// alert("OK");  
						$.ajax({
							url : '/idoc/deleteProductUser.html',
							type : 'POST',
							dataType : 'json',
							data : data,
							async : false,
							success : function(json) {
								if (json.retCode == 200) {
									bootbox.alert("删除成功！");
									$("#userName1").val("");
									$("#userEmail").val("");
									getProductUser(1);
								} else {
									bootbox.alert("删除成员失败！");
								}
							}
						});

					}
				},
				Cancel : {
					label : "取消",
					className : "btn-danger",
					callback : function() {

					}
				}

			}
		});
	
}

function updateProductUserRole(userId, userName, productUserId,roleId, isUpdateUserName){
	var data = {};
	data.userId = userId;
	data.userName = userName;
	data.productUserId=productUserId;
	data.roleId=roleId;
	data.isUpdateUserName = isUpdateUserName;
	if(!data.userName){
		bootbox.alert("成员名称不能为空！");
		return;
	}
	$.ajax({
		url : '/idoc/updateProductUser.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				bootbox.alert("修改成功！");
				getProductUser(1);
			} 
			else if(json.retCode == 401){
				bootbox.alert("参数错误！");	
			}
			else {
				bootbox.alert("修改成员失败！");
			}
		}
	});
}

 function updateProductName(){
	var data={};
	data.productId = $("#productId").val().trim();
	data.productName = $("#productName").val();
	data.productDesc = $("#productDesc").val();
	data.productDomainUrl = $("#productDomainUrl").val();
	data.productFlow=$("#productFlow").val();
	if(!data.productName){
		bootbox.alert("产品名称不能为空！");
		return;
	}
	
	bootbox.dialog({
		message : " ",
		title : "您确定修改产品？",
		buttons : {
			OK : {
				label : "确认",
				className : "btn-primary",
				callback : function() {
					$.ajax({
						url : '/idoc/updateProductName.html',
						type : 'POST',
						dataType : 'json',
						data : data,
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
						            	self.location = "/idoc/index.html";
						            }
						        });
							
							} else {
								bootbox.alert("修改产品名称失败！");
							}
						}
					});

				}
			},
			Cancel : {
				label : "取消",
				className : "btn-danger",
				callback : function() {

				}
			}

		}
	});
 }
function addProductUserReal() {
	var data = {};
	var roleId = $("#roleSelected").val().trim();
	data.productId = $("#productId").val().trim();
	data.roleId = roleId;
	data.userIds = $("#allUserSelect").val();
	data.productName = $("#productName").val().trim();
	$.ajax({
		url : '/idoc/addProductUser.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				bootbox.alert("新增成功！");
			} else {
				bootbox.alert("新增成员失败！");
			}
			
			getProductUser(1);
		}
	
	});

						
}

function serchUesrById() {
	var data = {};
	var userIds = $("#allUserSelect").val();
	data.userIds = userIds;
	$
			.ajax({
				url : '/idoc/selectUserByIds.html',
				type : 'POST',
				dataType : 'json',
				data : data,
				async : false,
				success : function(json) {
					if (json.retCode == 200) {
						var userModels = json.userModels;
						var tbl1 = $("#table_product_user tbody");
						var roleName = document.all.roleSelected.options[document.all.roleSelected.selectedIndex].text;
						var role = $('#roleSelected').val();
						for (var i = 0; i < userModels.length; ++i) {
							trHtml1 = "";
							trHtml1 += "<tr id = '" + userModels[i].userId
									+ "," + role + "'>";
							trHtml1 += "<td width='8%'>"
									+ userModels[i].userName + "</td>";
							trHtml1 += "<td width='8%'>"
									+ userModels[i].corpMail + "</td>";
							trHtml1 += "<td width='8%'>" + roleName + "</td>";
							trHtml1 += "<td width='8%'>"
									+ "<button  class=\"btn btn-success\" onclick=\"deleteFromTB_PRODUCT_RELEASE_MANAGE('"
									+ $("#allUserSelect").val()
									+ "')\">修改</button>&nbsp;&nbsp;<button  class=\"btn btn-warning\" onclick=\"deleteFromTB_PRODUCT_RELEASE_MANAGE('"
									+ $("#allUserSelect").val()
									+ "')\">删除</button>" + "</td>";
							trHtml1 += "</tr>";
							/////////////////////
							tbl1.append(trHtml1);
						}

					} else {
						bootbox.alert("获得成员失败！");
					}
				}
			});

}

function getUserRole(){
	var data = {};
	$.ajax({
		url : '/idoc/selectAllRole.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				var roleNameList = json.roleConfigList;
				$("#userRoleSelect").empty();
				$("#userRoleSelect").append("<option value=\"\">全部</option>");
				for (var i = 0; i < roleNameList.length; ++i) {
					var str = "<option value=" + roleNameList[i].roleId + ">"
							+ roleNameList[i].roleName + "</option>";
					$("#userRoleSelect").append(str);
				}
			}else {
				bootbox.alert("角色表为空");
			}
		}
	});
}

function changePageUserList(){
	//改变每页显示个数
	getProductUser(1);
}

function changeUserRole(){
	getProductUser(1);
}

function getRoleName() {
	var data = {};
	$.ajax({
		url : '/idoc/selectAllRole.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				var roleNameList = json.roleConfigList;

				$("#roleSelected").empty();

				for (var i = 0; i < roleNameList.length; ++i) {
					var str = "<option value=" + roleNameList[i].roleId + ">"
							+ roleNameList[i].roleName + "</option>";
					$("#roleSelected").append(str);
				}
				$("#taskProjectName option[value='" + roleNameList[0] + "']")
						.attr("selected", true);
			} else {
				bootbox.alert("角色表为空");
			}
		}
	});

}

function getAllUser() {
	var data = {};
	var productId = $("#productId").val().trim();
    data.productId = productId;
    
	$.ajax({
		url : '/idoc/selectOtherUser.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				var userList = json.userModels;
				$("#allUserSelect").empty();
				/*
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
				*/
				for (var i = 0; i < userList.length; ++i) {
					var userId = userList[i].userId;
					//var index = $.inArray(userId, exitUsers);
					//if(index == -1){
						var str = "<option value=" + userList[i].userId + ">"
						+ userList[i].nickName + " "
						+ userList[i].userName + "</option>";
						$("#allUserSelect").append(str);
					//}
				}
				//$("#taskProjectName option[value='1']").attr("selected", projectNameList[0]);
				$("#taskProjectName option[value='" + userList[0] + "']").attr(
						"selected", true);
			} else {
				bootbox.alert("用户表为空");
			}
		}
	});

}

function over_color(obj) {
	var overColor = "yellow";
	var clickColor = "pink";
	var defaultColor = "#ffffff";
	if (obj.style.backgroundColor != clickColor)
		obj.style.backgroundColor = overColor;
}
function remove_color(obj) {
	var overColor = "yellow";
	var clickColor = "pink";
	var defaultColor = "#ffffff";
	if (obj.style.backgroundColor != clickColor)
		obj.style.backgroundColor = defaultColor;
}
//重定向
function goBackPage(){
	self.location = "/idoc/updateProduct.html?productId='"+$("#productId").val().trim()+"'";
}