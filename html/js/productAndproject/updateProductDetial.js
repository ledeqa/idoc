$(document).ready(function() {
	getUserRole();
	getProductDetialUser(1);
});

function getProductDetialUser(pageNum) {
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
					$("#table_product_user tbody").empty();
					var list = json.productUserModels;
					var pageInfor = json.page;
					if (json.retCode == 200) {
						len = list.length;
						for (var i = 0; i < len; i++) {
							var res = list[i];
							str = "";
							str = "<tr id=\""+res.user.userId+"\" onmouseover='over_color(this)' onmouseout='remove_color(this)' class=\"gradeA odd\" role=\"row\">"
									+ "<td >"
									+ res.user.userName
									+ "</td>"
									+ "<td >"
									+ res.user.corpMail
									+ "</td>"
									+ "<td >"
									+ res.role.roleName
									+ "</td>"
									+ "</tr>";
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
function changePageUserList(){
	//改变每页显示个数
	getProductDetialUser(1);
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
function changeUserRole(){
	getProductDetialUser(1);
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