$(document).ready(function() {
	getProductAllProject(1);
});
function createNewProject() {
	bootbox
			.dialog({
				title : "创建项目",
				message : "<div class=\"row\">"

						+ '<div class="form-group">'
						+ '<label class="col-lg-2 control-label">项目名称:</label>'
						+ '<div class="col-lg-9">'
						+ "<input type=\"text\" id=\"createNewProjectName\" class=\"form-control\" maxLength=\"60\"/>"
						+ '</div>' 
						+ '</div>'

						+ "<tr><td>&nbsp;</td><td>&nbsp;</div>",
				locale : "zh_CN",

				buttons : {

					Ok : {
						label : '确定',
						className : "btn-primary",
						callback : function() {
							createProject();
						}
					},
					Cancel : {
						label : "取消",
						className : "btn-danger",
						callback : function() {
							// bootbox.alert("已取消");
							// alert("Cancel");
						}
					}
				}
			});
}

function createProject() {
	var data = {};
	data.projectName = $("#createNewProjectName").val().trim();
	data.productId = $("#productId").val();
	if(!data.projectName){
		bootbox.alert("项目名称不能为空！");
	}
	$.ajax({
		url : '/idoc/addProject.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				bootbox.alert("新建项目成功！");
				getProductAllProject(1);
			} else if (json.retCode == 201) {
				bootbox.alert("该项目已经存在，请修改名称后再创建！");
			} else {
				bootbox.alert("新建项目失败！");
			}
		}
	});
}

function changeIsOnline() {
	getProductAllProject(1);
}

function changePageProjectList(){
	getProductAllProject(1);
}

function getProductAllProject(pageNum) {
	var data = {};
	var len = 0;
	data.productId = $("#productId").val();
	data.status = $("#IsOnline").val();
	data.pageNum = pageNum;
	data.perPage = $("#perPage").val();
	data.projectName = $("#projectName").val();

	$.ajax({
				url : '/idoc/getProductAllProject.html',
				type : 'POST',
				data : data,
				dataType : 'json',
				success : function(json) {
					if (json.retCode == 200) {
						var list = json.retContent;
						len = list.length;

						$("#table_search_project tbody").empty();

						for (var i = 0; i < len; i++) {
							var res = list[i];
							var productId=res.productId;
							var createTime = getDate(res.createTime);
							var updateTime = getDate(res.updateTime);
							// alert(createTime+" "+updateTime);
							var pageInfor = json.page;
							// alert(res.uploadType);
							var str;
							if (res.status == 1) {
								insert = "否";

							} else if (res.status == 2) {
								insert = "是";
							}
							str = "<tr  onmouseover='over_color(this)' onmouseout='remove_color(this)' class=\"gradeA odd\" role=\"row\">"
									+ "<td width=\"8%\">"
									+ "<a href='/idoc/inter/index.html?projectId="+res.projectId+"'>"+res.projectName+"</a>"
									+ "</td>"
									+ "<td width=\"8%\">"
									+ res.interfaceNum
									+ "</td>"
									+ "<td width=\"8%\">"
									+ res.submitTestNum
									+ "</td>"
									+ "<td width=\"8%\">"
									+ res.onlineNum
									+ "</td>"
									+ "<td width=\"8%\">"
									+ insert
									+ "</td>"
									+ "<td width=\"12%\">"
									+ createTime
									+ "</td>"
									+ "<td width=\"12%\">"
									+ updateTime
									+ "</td>"
									+ "<td width=\"12%\">"
									+ "<div class='btn-group'>"
					                + "<button type='button' class='btn btn-primary dropdown-toggle' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>"
					                +"点击选择操作<span class='caret'></span></button>"
					                +"<ul class='dropdown-menu'>"
					                +"<li><a href=\"javascript:void(0);\" class=\"glyphicon glyphicon-pencil\" onclick=\"updateProductProject('"+ res.projectId+ "','" + res.projectName + "')\"> 修改</a></li>" 
									+" <li role=\"separator\" class=\"divider\"></li>"
					                +"<li><a href=\"javascript:void(0);\" class=\"glyphicon glyphicon-remove\" onclick=\"deleteProductProject('"+res.projectId +"')\"> 删除</a></li>" 
					                +" <li role=\"separator\" class=\"divider\"></li>"
					                +"<li><a class=\"glyphicon glyphicon-stats\" href=\"/idoc/statistics/projectStatistics.html?projectId="+res.projectId+"\"> 数据统计</a></li>"
					                //+" <li role=\"separator\" class=\"divider\"></li>"
					                //+"<li><a class=\"glyphicon glyphicon-stats\" target=\"_blank\" href=\"/idoc/getProjectJson.html?projectId="+res.projectId+"\"> 导出Json数据</a></li>"
									+ "</ul></div>"
									+ "</td></tr>";
							$("#table_search_project tbody").append(str);

						}
						getPage(pageInfor, len);
					} else {
//						bootbox.alert("没有找到符合条件的记录 ！");
						 $("#table_search_project tbody").empty();
					}
				}
			});
}

function updateProductProject(projectId, oldProjectName) {

	bootbox
			.dialog({
				title : "修改项目",
				message : "<div class=\"row\">"

						+ '<div class="form-group">'
						+ '<label class="col-lg-2 control-label">项目名称:</label>'
						+ '<div class="col-lg-9">'
						+ "<input type=\"text\" id=\"updateNewProjectName\" class=\"form-control\" value="
						+ oldProjectName + " />" + '</div>' + '</div>'

						+ "<tr><td>&nbsp;</td><td>&nbsp;</div>",
				locale : "zh_CN",

				buttons : {

					Ok : {
						label : '确定',
						className : "btn-primary",
						callback : function() {
							updateProject(projectId);
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
function updateProject(projectId) {
	var data = {};
	data.projectId = projectId;
	data.projectName = $("#updateNewProjectName").val().trim();
	$.ajax({
		url : '/idoc/updateProject.html',
		type : 'POST',
		dataType : 'json',
		data : data,
		async : false,
		success : function(json) {
			if (json.retCode == 200) {
				bootbox.alert("修改项目成功！");
				getProductAllProject(1);
			} else {
				bootbox.alert("修改项目失败！");
			}
		}
	});
}
function deleteProductProject(projectId) {
	var data={};
	data.projectId = projectId;
	bootbox.dialog({
		message : " ",
		title : "您确定删除？",
		buttons : {
			OK : {
				label : "确认",
				className : "btn-primary",
				callback : function() {
					
					$.ajax({
						url : '/idoc/deleteProject.html',
						type : 'POST',
						dataType : 'json',
						data : data,
						async : false,
						success : function(json) {
							if (json.retCode == 200) {
								bootbox.alert("删除成功！");
							} else {
								bootbox.alert("删除成员失败！");
							}
						}
					});

					getProductAllProject(1);
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


function getPage(pageInfor, totalNum) {
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

	$("#changePageProject").empty();
	$("#changePageProject")
			.append(
					"<li><a href=\"#\" onClick=\"getProductAllProject(1)\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
	for (var i = minPageNum; i <= maxPageNum; ++i) {
		var str;
		if (currentPage == i) {
			str = "<li class=\"active\"><a href=\"#\" onClick=\"getProductAllProject("
					+ i + ")\">" + i + "</a></li>";
		} else {
			str = "<li ><a href=\"#\" onClick=\"getProductAllProject(" + i
					+ ")\">" + i + "</a></li>";
		}

		$("#changePageProject").append(str);
	}
	$("#changePageProject")
			.append(
					"<li class=\"paginate_button\" aria-controls=\"dataTables-example\"><a href=\"#\" onClick=\"getProductAllProject("
							+ totalPage
							+ ")\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
	$("#changePageProject").append(
			"<li><span >" + "总计  " + totalRecord + " 条记录" + " 总计  " + totalPage
					+ " 页" + "</span></li>");

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
