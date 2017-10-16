$(document).ready(function(){
	dictQuery(1);
});
//搜索
function dictQuery(pageNum){

	var data = {};
	var productId = $("#productId").val();
	data.productId = productId;
	data.dictName = $("#dictName").val();
	data.pageNum = pageNum;
	data.perPage=$("#perPage").val();
	$.ajax({
		url:'/dict/queryDicts.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			if(json.retCode == 200){
				var list = json.retDesc;
				var len = list.length;
				$("#table_search_dict tbody").empty();
				for(var i=0; i<len; i++){
					var dictObj = list[i];
					var lineno = eval((i+1));
					var str = "";
					if(i%2==0){
						str += "<tr class=\"gradeA odd\" role=\"row\">";
					}else{
						str += "<tr class=\"gradeA even\" role=\"row\">";
					}
					str += "<td>"+lineno+"</td>" + 
						"<td>" + dictObj.dictName + "</td>";
						if(dictObj.dictDesc){
							str += "<td>" + dictObj.dictDesc + "</td>";
						}else{
							str += "<td></td>";
						}
						str += "<td>" + dictObj.version + "</td>" +
						"<td>" + 
							"<a href=checkDict.html?dictId="+dictObj.dictId+"&productId="+productId+" class=\"btn btn-sm btn-primary\">查看</a>"+
							"	<a href=editDict.html?dictId="+dictObj.dictId+"&productId="+productId+" class=\"btn btn-sm btn-info\">编辑</a>";
						if(dictObj.version>1){
							str += "	<a href=historyDict.html?productName="+ $("#pro_name").val() +"&dictName="+ dictObj.dictName +"&dictId="+dictObj.dictId+"&productId="+productId+" class=\"btn btn-sm btn-success\">历史版本信息</a>";
						}
						str += "	<button class=\"btn btn-sm btn-warning\" onclick=\"copyDict('"+dictObj.dictId+"', '"+dictObj.dictName+"')\">复制字典</button>";
						str += "	<button class=\"btn btn-sm btn-danger\" onclick=\"deleteDict('"+dictObj.dictId+"')\">删除</button>";
						str += "</td>";
					str += "</tr>";
					$("#table_search_dict tbody").append(str);					
				}
				var pageInfo = json.page;
				getPage(pageInfo,len);
			}else{
				/*$("#table_search_dict tbody").empty();
				$("#changePageDict").empty();*/
				bootbox.alert("当前没有可用数据字典，请点击“新增字典”按钮进行添加!");
			}
		}
	});
}
//复制字典
function copyDict(dictId, dictName){
	var data = {};
	data.dictId = dictId;
	data.productId = $("#productId").val();
	var copyName = "Copyof" + dictName;
	bootbox.dialog({
		title : "请填写字典名称和字典描述",
		message :
			"<div>" +
			"<div class=\"row\">"+
			"<div class=\"form-group\">" +
				"<label class=\"col-sm-1 control-label\"" +
					"style=\"width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;\">字典名称：</label>"+
					"<div class=\"col-sm-8\" style=\"padding-left: 0px;\">" +
						"<input type=\"text\" id=\"copy_DictName\" value=\"" + copyName +"\""+
						" class=\"form-control\">" +
					"</div></div></div>"+
			"<div class=\"row\">"+		
			"<div class=\"form-group\">" +
				"<label class=\"col-sm-1 control-label\"" +
					"style=\"width: 100px; padding-top: 5px; padding-left: 15px; padding-right: 5px;\">字典描述：</label>"+
					"<div class=\"col-sm-8\" style=\"padding-left: 0px; padding-top:2px\">" +
					"<input type=\"text\" id=\"copy_DictDesc\" class=\"form-control\">" +
					"</div>"+
			"</div></div></div>",
			locale : "zh_CN",
			buttons : {
				Modify:{
					label : '确定',
					className : "btn-primary",
					callback : function() {
						var newDictName = $("#copy_DictName").val();
						var newDictDesc = $("#copy_DictDesc").val();
						if(!newDictName || newDictName==''){
							bootbox.alert("字典名称不能为空！");
							return;
						}
						data.newDictName = newDictName;
						data.newDictDesc = newDictDesc;
						$.ajax({
							url: '/dict/copyDict.html',
							dataType: 'json',
							data: data,
							type: 'POST',
							success:function(json){
								bootbox.alert(json.retDesc);
								if(json.retCode == 200){
									dictQuery(1);
								}
							}
						});
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-primary",
					callback : function() {
						
					}
				}
			}
	});
}
//删除字典
function deleteDict(dictId){
	var data = {};
	data.dictId = dictId;
	 bootbox.dialog({  
         message: " ",  
         title: "您确定删除？",  
         buttons: {  
             OK: {  
                 label: "确认",  
                 className: "btn-primary",  
                 callback: function () {  
                	 $.ajax({
                			url:'/dict/delete.html',
                			type:'POST',
                			data:data,
                			dataType:'json',
                			success:function(json){
                				if(json.retCode = 200){
                					bootbox.alert("字典删除成功！");
                					dictQuery(1);
                				}else{
                					bootbox.alert("字典删除失败，请联系相关管理人员！");
                				}
                			}
                	});
                 }  
             },
             Cancel: {  
                 label: "取消",  
                 className: "btn-default",  
                 callback: function () {  
                 }  
             }
         }  
     });
}
//新增字典
function addDict(){
	location.href = "editDict.html?productId="+$("#productId").val();
}
//从word中导入字典
function importDocDicts(){
	bootbox.dialog({
		title : "选择字典文件",
		message :
			"<div>" +
			"<div class=\"col-sm-12\">" +
			"<div class=\"form-group\">" +
				"<input id=\"uploadDocDict\" type=\"file\" class=\"file\"" +
				"data-show-preview=\"true\" data-allowed-file-extensions='[\"doc\", \"docx\"]'" +
				"data-max-file-count=\"1\"" +
				"data-upload-url=\"/idoc/import/docs.html\"" +
			"/>" +
			"</div></div>",
			locale : "zh_CN",
			buttons : {
				Modify:{
					label : '确定',
					className : "btn-primary",
					callback : function() {
						
					}
				},
				Cancel:{
					label : '取消',
					className : "btn-primary",
					callback : function() {
						
					}
				}
			}
	});
}
//从数据库导入字典
function importDictFromDB(){
	bootbox.dialog({
		title: '从数据库导入字典',
		message: '<div class="row">' +
	      		 	'<div class="form col-sm-12" id="dbInfo">' +
	      		 		'<div class="panel-heading">' +
	      		 			'<h4><strong>数据库连接信息</strong></h4>' +
	      		 		'</div>' +
	      		 		'<div class="panel-body">' +
	      		 			'<form id="classForm" class="form-horizontal" role="form">' +
	      		 				'<div class="form-group" style="margin-left:0px;margin-right:-40px">' +
	      		 					'<label class="col-sm-2 control-label" for="databaseDriver">Driver: </label>' +
	      		 					'<div class="col-sm-8">' +
	      		 						'<select class="form-control" id="databaseDriver">' +
	      		 							'<option value="com.mysql.jdbc.Driver">com.mysql.jdbc.Driver</option>' +
	      		 							'<option value="oracle.jdbc.driver.OracleDriver">oracle.jdbc.driver.OracleDriver</option>' +
	      		 						'</select>' +
	      		 					'</div>' +
	      		 				'</div>' +
	      		 				'<div class="form-group" style="margin-left:0px;margin-right:-40px">' +
	      		 					'<label class="col-sm-2 control-label" for="databaseUrl">URL : </label>' +
	      		 					'<div class="col-sm-8">' +
	      		 						'<input id="databaseUrl" class="form-control" name="databaseUrl" type="text" placeholder="连接数据库的url">' +
	      		 					'</div>' +
	      		 				'</div>' +
	      		 				'<div class="form-group" style="margin-left:0px;margin-right:-40px">' +
	      		 					'<label class="col-sm-2 control-label" for="userName">用户名 : </label>' +
	      		 					'<div class="col-sm-8">' +
	      		 						'<input id="userName" class="form-control" name="userName" type="text" placeholder="数据库用户名">' +
	      		 					'</div>' +
	      		 				'</div>' +
	      		 				'<div class="form-group" style="margin-left:0px;margin-right:-40px">' +
	      		 					'<label class="col-sm-2 control-label" for="password">密码 : </label>' +
	      		 					'<div class="col-sm-8">' +
	      		 						'<input id="password" class="form-control" name="password" type="password" placeholder="数据库密码">' +
	      		 					'</div>' +
	      		 				'</div>' +
	      		 				'<div class="form-group" style="margin-left:0px;margin-right:-40px">' +
	      		 					'<label class="col-sm-2 control-label" for="databaseName">数据库名 : </label>' +
	      		 					'<div class="col-sm-8">' +
	      		 						'<input id="databaseName" class="form-control" name="databaseName" type="text" placeholder="数据库名">' +
	      		 					'</div>' +
	      		 					'<span id="databaseInfo" class="alert alert-danger col-sm-2" style="margin-left: 0px; padding: 6px 16px; display: none;"></span>' +
	      		 				'</div>' +
	      		 				'<div class="btn-class" style="margin-left: 17%; margin-top: 3%">' +
	      		 					'<a onclick="linkDataBase()" href="javascript:void(0);" class="button button-plain button-royal button-border button-pill button-small" style="padding:0px 20px;margin-left: 57%; margin-right:14px;"><i class="fa fa-star"></i>&nbsp;&nbsp;连接数据库</a>' +
	      		 				'</div>' +
	      		 			'</form>' +
	      		 		'</div>' +
	      		 	'</div>' +
	      		 	'<div id="tablesInfo" class="panel panel-warning" style="display:none">' +
	      		 		'<div class="panel-heading">' +
	      		 			'<h4><strong>数据库表信息</strong></h4>' +
	      		 		'</div>' +
	      		 		'<div class="panel-body">' +
	      		 			'<table class="table table-bordered">' +
	      		 				'<thead>' +
	      		 					'<tr>' +
	      		 						'<th align="center" style="width: 5%;"><center><input type="checkbox" name="all-checkbox" onchange="checkAllBox(this)"></center></th>' +
	      		 						'<th style="width: 25%;">表名</th>' +
						                '<th style="width: 25%;">类名</th>' +
						                '<th style="width: 25%;">注释</th>' +
						                '<th style="width: 20%;"><center>详情</center></th>' +
						            '</tr>' +
						        '</thead>' +
						        '<tbody id="db-table-body">' +
						        '</tbody>' +
						    '</table>' +
						    '<div class="btn-class" style="margin-left: 27%; margin-top: 3%">' +
						    	'<span id="generateDictInfo" class="alert alert-danger col-sm-6" style="margin-left: 30%; padding: 6px 16px; display: none;"></span>' +
						    	'<a onclick="generateDict()" href="javascript:void(0);" class="button button-plain button-caution button-border button-pill button-small pull-right"><i class="fa fa-keyboard-o"></i>&nbsp;&nbsp;生成字典</a>' +
						    '</div>' +
						'</div>' +
					'</div>' +
				'</div>',
		buttons:{
			cancel:{
				label: '关闭',
				className: 'btn-default'
			}
	
		},
		callback: function(){
			setTimeout(function(){
				$($('.modal-dialog')[0]).css({'width': '900px'});
			}, 200);
		}
	});
}
function linkDataBase(){
	$('#databaseInfo').css('display', 'none');
	if(!checkDatabaseLinkInfo('databaseInfo')){
		return;
	}
	var databaseDriver = $('#databaseDriver').val();
	var databaseUrl = $('#databaseUrl').val();
	var userName = $('#userName').val();
	var password = $('#password').val();
	var databaseName = $('#databaseName').val();
	
	var tbody = $('#db-table-body');
	tbody.empty();
	var data = {};
	data.databaseDriver = databaseDriver;
	data.databaseUrl = databaseUrl;
	data.userName = userName;
	data.password = password;
	data.databaseName = databaseName;
	$.ajax({
		url:'/idoc/code/getDatabaseTables.html',
		type:'POST',
		dataType:'json',
		data: data,
		success:function(json){
			if(json.retCode == 200){
				$("#dbInfo").css("display","none");
				$("#tablesInfo").css("display","block");
				var tableList = json.tableList;
				for(var i = 0; i < tableList.length; i++){
					var tbody_html = '';
					tbody_html += '<tr><td><center><input type="checkbox" name="column-checkbox" onchange="checkOneBox(this)"></center></td>'
						   	   +  '<td>' + tableList[i] + '</td>'
						   	   +  '<td><input id="className-' + tableList[i] + '" class="form-control" type="text" style="display:none"></td>'
						   	   +  '<td><input id="codeName-' + tableList[i] + '" class="form-control" type="text" style="display:none"></td>'
						   	   +  '<td><center><button style="padding:0px 15px;" class="button button-glow button-rounded button-raised button-primary button-small" onclick="showTableDetail(\'' + tableList[i] + '\');">表详情</button><center></td>'
						   	   +  '</tr>';
				    tbody.append(tbody_html);
				}
			}else{
				$('#databaseInfo').text('数据库连接失败!');
				$('#databaseInfo').css('display', 'block');
				return;
			}
		}
	});
}
function showTableDetail(table_name){
	$('#databaseInfo').css('display', 'none');
	if(!checkDatabaseLinkInfo('databaseInfo')){
		return;
	}
	var databaseDriver = $('#databaseDriver').val();
	var databaseUrl = $('#databaseUrl').val();
	var userName = $('#userName').val();
	var password = $('#password').val();
	var databaseName = $('#databaseName').val();
	
	var data = {};
	data.databaseDriver = databaseDriver;
	data.databaseUrl = databaseUrl;
	data.userName = userName;
	data.password = password;
	data.databaseName = databaseName;
	data.tableName = table_name;
	$.ajax({
		url:'/idoc/code/getTableColumns.html',
		type:'POST',
		dataType:'json',
		data: data,
		success:function(json){
			if(json.retCode == 200){
				var columnList = json.columnList;
				if(columnList.length > 0){
					showTableColumnInfo(table_name);
				}
				var tbody = $('#table-column');
				for(var i = 0; i < columnList.length; i++){
					var tbody_html = '';
					var column = columnList[i];
					if(i % 2 == 0)
						tbody_html += '<tr class="success">';
					else
						tbody_html += '<tr class="warning">';
					tbody_html +=  '<td>' + column.field + '</td>'
						   	   +  '<td>' + column.type + '</td>'
						   	   +  '<td>' + column.nullable + '</td>'
						   	   +  '<td>' + column.key + '</td>'
						   	   +  '<td>' + column.defaultContent + '</td>'
						   	   +  '</tr>';
				    tbody.append(tbody_html);
				}
				// 设置
				$('.ui_content').css('display', 'inline');
				
			}else{
				$('#databaseInfo').text('查询数据库失败!');
				$('#databaseInfo').css('display', 'block');
				return;
			}
		}
	});
}
function showTableColumnInfo(table_name){
	var column_html = '<table class="table table-bordered">'
				   + '<thead>'
				   + '<tr>'
				   + '<th style="width: 15%;">字段名</th>'
				   + '<th style="width: 25%;">类型</th>'
				   + '<th style="width: 15%;">是否为空</th>'
				   + '<th style="width: 15%;">主键</th>'
				   + '<th style="width: 30%;">默认值</th>'
				   + '</tr>'
				   + '</thead>'
				   + '<tbody id="table-column">'
				   + '</tbody>'
				   + '</table>';
	$.dialog({
		title: table_name + '表字段详情',
		focus: false,
	    width: '520px',
	    height: 430,
	    lock: true,
	    content: column_html,
	    cancelVal: '关闭',
	    cancel: true
	});
}
function generateDict(){
	var productId = $("#productId").val();
	$('#generateDictInfo').css('display', 'none');
	var flag = true;
	if($("[name='column-checkbox']:checked").length > 0){
		if(!checkDatabaseLinkInfo('generateDictInfo')){
			return;
		}
		var params = [];
		$("[name='column-checkbox']").each(function(){
			if(this.checked){
				var node = $(this).parent().parent().next().html();
				var className = $('#className-' + node).val();
				if(isNullOrEmpty(className)){
					$('#generateDictInfo').css('display', 'block');
					$('#generateDictInfo').text('请填写选中的数据库表对应将要生成代码的类名！');
					flag = false;
					return;
				}
				var codeName = $('#codeName-' + node).val(); // 类注释
				if(isNullOrEmpty(codeName)){
					codeName = 'Auto generate by idoc';
				}
				params.push(node + ':' + className + ':' + codeName);
			}
		});
		if(!flag){
			return;
		}
		
		var data = {};
		data.productId = productId;
		data.databaseDriver = $('#databaseDriver').val();
		data.databaseUrl = $('#databaseUrl').val();
		data.userName = $('#userName').val();
		data.password = $('#password').val();
		data.databaseName = $('#databaseName').val();
		data.params = params.join(',');
		//$.StandardPost('/dict/importDict.html', data);
		$.ajax({
			url: '/dict/importDict.html',
			type: 'POST',
			data: data,
			dataType: 'json',
			success: function(json){
				if(json.retCode==200){
					bootbox.alert("字典添加成功!");
					setTimeout(function (){
						var productId = $("#productId").val();
						location.href = "/dict/index.html?productId="+productId;
					}, 1000);
				}else if(json.retCode==201){
					bootbox.alert("该字典名称已经存在，请重新填写！");
				}else{
					bootbox.alert("字典添加失败！");
				}
			}
		});
	}else{
		bootbox.alert("请选择数据库表!");
	}
}
function checkDatabaseLinkInfo(div){
	var databaseUrl = $('#databaseUrl').val();
	var userName = $('#userName').val();
	var password = $('#password').val();
	var databaseName = $('#databaseName').val();
	if(isNullOrEmpty(databaseUrl)){
		$('#databaseInfo').text('url不能为空!');
		$('#databaseInfo').css('display', 'block');
		return false;
	}
	if(isNullOrEmpty(userName)){
		$('#' + div).text('用户名不能为空!');
		$('#' + div).css('display', 'block');
		return false;
	}
	if(isNullOrEmpty(password)){
		$('#' + div).text('密码不能为空!');
		$('#' + div).css('display', 'block');
		return false;
	}
	if(isNullOrEmpty(databaseName)){
		$('#' + div).text('数据库名不能为空');
		$('#' + div).css('display', 'block');
		return false;
	}
	return true;
}
function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}
function checkAllBox(item){
	if($(item).prop("checked")){
		$("[name='column-checkbox']").prop("checked",'true');
		$("[name='column-checkbox']").each(function(){
			var node = $(this).parent().parent().next().html();
			$('#className-' + node).css('display', 'block');
			$('#codeName-' + node).css('display', 'block');
		});
	}else{
		$("[name='column-checkbox']").removeProp("checked");
		$("[name='column-checkbox']").each(function(){
			var node = $(this).parent().parent().next().html();
			$('#className-' + node).css('display', 'none');
			$('#codeName-' + node).css('display', 'none');
		});
	}
}
function checkOneBox(item){
	var node = $(item).parent().parent().next().html();
	if($(item).prop("checked")){
		$('#className-' + node).css('display', 'block');
		$('#codeName-' + node).css('display', 'block');
	}else{
		$('#className-' + node).css('display', 'none');
		$('#codeName-' + node).css('display', 'none');
	}
}
function getPage(pageInfo,totalNum){
	var totalPage = pageInfo.totalPage;
	var currentPage = pageInfo.currentPage;
	var halfPageNum = 5;
	var minPageNum = 1;
	var maxPageNum = totalPage;
	var totalRecord=pageInfo.totalRecord;
	if(currentPage + halfPageNum < totalPage){
		maxPageNum = currentPage + halfPageNum;
	}
	if(currentPage - halfPageNum > 1){
		minPageNum = currentPage - halfPageNum;
	}
	
	$("#changePageDict").empty();
	$("#changePageDict").append("<li><a href=\"#\" onClick=\"dictQuery(1)\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
	for(var i=minPageNum; i <= maxPageNum; ++i){
		var str;
		if(currentPage == i){
			str = "<li class=\"active\"><a href=\"#\" onClick=\"dictQuery(" + i + ")\">" + i + "</a></li>";
		}else{
			str = "<li><a href=\"#\" onClick=\"dictQuery(" + i + ")\">" + i + "</a></li>";
		}

		$("#changePageDict").append(str);	
	}
	$("#changePageDict").append("<li><a href=\"#\" onClick=\"dictQuery(" + totalPage + ")\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
	$("#changePageDict").append("<li><span >"+"总计  "+totalRecord+" 条记录"+" 总计  "+totalPage+" 页"+"</span></li>");
}