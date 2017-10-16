/**
 * 自动生成代码页面js
 */

var addDialog;
var attrMap = {};
$(document).ready(function(){

	var table_index = -1;
	$('#add-attr-btn').click(function(){
		createAttrFrame('添加属性', 'add', '');
	});
	
	$("[name='all-checkbox']").change(function(){
		if($(this).prop("checked")){
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
	});
	
	$('body').delegate("input[name='column-checkbox']", 'change', function(){
		var node = $(this).parent().parent().next().html();
		if($(this).prop("checked")){
			$('#className-' + node).css('display', 'block');
			$('#codeName-' + node).css('display', 'block');
		}else{
			$('#className-' + node).css('display', 'none');
			$('#codeName-' + node).css('display', 'none');
		}
	});
	
	$('body').delegate('#add-btn', 'click', function(){
		$('#divInfo').css('display', 'none');
		var inputName = $('#inputName').val();
		if(isNullOrEmpty(inputName)){
			$('#divInfo').text('属性名称不能为空！');
			$('#divInfo').css('display', 'block');
			return;
		}else if(checkDigit(inputName.charAt(0))){
			$('#divInfo').text('属性名称首字母不能为数字！');
			$('#divInfo').css('display', 'block');
			return;
		}
		var inputType = $('input:radio[name="inputType"]:checked').val();
		var inputComment = $('#inputComment').val();
		var inputPrimary = $('#inputPrimary').prop("checked");
		
		if(inputName in attrMap && attrMap[inputName] != null){
			$('#divInfo').text('属性名称已经存在，重新输入！');
			$('#divInfo').css('display', 'block');
			return;
		}
		attrMap[inputName] = inputName;
		table_index++;
		var attr_table_html = '<tr id="attr-' + table_index + '">'     
			                + '<td>' + inputName + '<input class="attrName" name="attrName" value="' + inputName + '" type="hidden"></td>'
			                + '<td>' + inputType + '<input name="attrType" value="' + inputType + '" type="hidden"></td>';
		if(inputPrimary){
			attr_table_html += '<td><img class="primary-key-img" src="/img/code/key.png"><input name="attrPrimary" value="1" type="hidden"></td>';
		}else{
			attr_table_html += '<td><input name="attrPrimary" value="0" type="hidden"></td>';
		}
		attr_table_html    += '<td>' + inputComment + '<input name="attrComment" value="' + inputComment + '" type="hidden"></td>'
			                + '<td class="operate-td"><img onclick="showUpdateAttr(\'' + inputName + '\', \'' + inputType + '\', ' + inputPrimary + ', \'' + inputComment + '\', \'' + table_index + '\');" title="修改属性" style="cursor:pointer;" class="operate-img update-img" src="/img/code/editor_area.gif"></td>'
			                + '<td class="operate-td"><img title="删除属性" onclick="deleteAttr(\'' + table_index + '\');" class="operate-img delete-img" style="cursor:pointer;" src="/img/code/delete_edit.gif"></td>'
			                + '</tr>';
		$('#attr-body').append(attr_table_html);
		
	});
	
	$('body').delegate('#close-btn', 'click', function(){
		if(addDialog && !addDialog.closed)
			addDialog.close();
	});
	
	// 根据类属性生成代码
	$('#generate-btn').click(function(){
		var packageName = $('#inputPackage').val();
		var className = $('#inputName').val();
		var params = '';
		$('#attr-body tr').each(function(){
			var name = $(this).find("td:eq(0)").text();
			var type = $(this).find("td:eq(1)").text();
			var comment = $(this).find("td:eq(3)").text();
			var primary = $(this).find("td:eq(2)").find("input").val();
			params = params + ',' + name + ':' + type + ':' + comment + ':' + primary;
		});
		if(params != '') {
			params = params.substring(1);
		}
		
		if(isNullOrEmpty(className)) {
			alert('类名称不能为空');
			return;
		}
		
		$.cookie('packageName', packageName);
		$.cookie('className', className);
		$.cookie('params', params);
		window.open("/code/codeInfoForClass.html");
	});
	
	$.extend({
	    StandardPost:function(url, args){
	        var body = $(document.body),
	            form = $("<form method='post' target='_blank' style='display:none'></form>"),
	            input;
	        form.attr({"action":url});
	        $.each(args,function(key,value){
	            input = $("<input type='hidden'>");
	            input.attr({"name":key});
	            input.val(value);
	            form.append(input);
	        });

	        form.appendTo(document.body);
	        form.submit();
	        document.body.removeChild(form[0]);
	    }
	});
	// 根据数据库表字段生成代码
	$('#db-generate-btn').click(function(){
		$('#generateInfo').css('display', 'none');
		var flag = true;
		if($("[name='column-checkbox']:checked").length > 0){
			if(!checkDatabaseLinkInfo('generateInfo')){
				return;
			}
			var params = [];
			$("[name='column-checkbox']").each(function(){
				if(this.checked){
					var node = $(this).parent().parent().next().html();
					var className = $('#className-' + node).val();
					if(isNullOrEmpty(className)){
						$('#generateInfo').css('display', 'block');
						$('#generateInfo').text('请填写选中的数据库表对应将要生成代码的类名！');
						flag = false;
						return;
					}
					var codeName = $('#codeName-' + node).val(); // 类注释
					if(isNullOrEmpty(className)){
						className = node;
					}
					if(isNullOrEmpty(codeName)){
						codeName = 'Auto generate by idoc';
					}
					params.push(node + ':' + className + ':' + codeName);
				}
			});
			if(!flag){
				return;
			}
			
//			var data = {};
//			data.databaseDriver = $('#databaseDriver').val();
//			data.databaseUrl = $('#databaseUrl').val();
//			data.userName = $('#userName').val();
//			data.password = $('#password').val();
//			data.databaseName = $('#databaseName').val();
//			data.params = params.join(',');
//			$.StandardPost('/idoc/code/dbGenerateCode.html', data);
			
			$.cookie('databaseDriver', $('#databaseDriver').val());
			$.cookie('databaseUrl', $('#databaseUrl').val());
			$.cookie('userName', $('#userName').val());
			$.cookie('password', encodeURI($('#password').val()));
			$.cookie('databaseName', $('#databaseName').val());
			$.cookie('params', params.join(','));
			window.open("/code/codeInfo.html");
			
		}else{
			$().toastmessage('showToast', {
			    text     : '请选择数据库表!',
			    sticky   :  false,
			    position : 'top-right',
			    type     : 'error',
			    stayTime:   1500
			});
			return;
		}
	});
	
	$('#link-database-btn').click(function(){
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
					var tableList = json.tableList;
					for(var i = 0; i < tableList.length; i++){
						var tbody_html = '';
						tbody_html += '<tr><td><center><input type="checkbox" name="column-checkbox"></center></td>'
							   	   +  '<td>' + tableList[i] + '</td>'
							   	   +  '<td><input id="className-' + tableList[i] + '" class="form-control" type="text" style="display:none"></td>'
							   	   +  '<td><input id="codeName-' + tableList[i] + '" class="form-control" type="text" style="display:none"></td>'
							   	   +  '<td><center><button class="button button-glow button-rounded button-raised button-primary button-small" onclick="showTableDetail(\'' + tableList[i] + '\');">表详情</button><center></td>'
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
				
	});
});

function checkDatabaseLinkInfo(div){
	var databaseDriver = $('#databaseDriver').val();
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

function showUpdateAttr(inputName, inputType, inputPrimary, inputComment, table_index){
	createAttrFrame('修改属性', 'update', table_index);
	$('#inputName').val(inputName);
	$('input:radio[value="' + inputType + '"]').attr('checked', 'true');
	$('#inputComment').val(inputComment);
	if(inputPrimary){
		$('#inputPrimary').prop("checked", true); 
	}else{
		$('#inputPrimary').prop("checked", false);
	}
}

function updateAttrInfo(index){
	$('#divInfo').css('display', 'none');
	var inputName = $('#inputName').val();
	if(isNullOrEmpty(inputName)){
		if($('#divInfo').hasClass('alert alert-success')){
			$('#divInfo').removeClass('alert alert-success');
			$('#divInfo').addClass('alert alert-danger');
		}
		$('#divInfo').text('属性名称不能为空！');
		$('#divInfo').css('display', 'block');
		return;
	}else if(checkDigit(inputName.charAt(0))){
		if($('#divInfo').hasClass('alert alert-success')){
			$('#divInfo').removeClass('alert alert-success');
			$('#divInfo').addClass('alert alert-danger');
		}
		$('#divInfo').text('属性名称首字母不能为数字！');
		$('#divInfo').css('display', 'block');
		return;
	}
	var inputType = $('input:radio[name="inputType"]:checked').val();
	var inputComment = $('#inputComment').val();
	var inputPrimary = $('#inputPrimary').prop("checked");
	
	$('#attr-body').find('tr#attr-' + index).find('td').eq(0).html(inputName + '<input class="attrName" name="attrName" value="' + inputName + '" type="hidden">');
	$('#attr-body').find('tr#attr-' + index).find('td').eq(1).html(inputType + '<input name="attrType" value="' + inputType + '" type="hidden">');
	if(inputPrimary){
		$('#attr-body').find('tr#attr-' + index).find('td').eq(2).html('<img class="primary-key-img" src="/img/code/key.png"><input name="attrPrimary" value="1" type="hidden">');
	}else{
		$('#attr-body').find('tr#attr-' + index).find('td').eq(2).html('<input name="attrPrimary" value="0" type="hidden">');
	}
	$('#attr-body').find('tr#attr-' + index).find('td').eq(3).html(inputComment + '<input name="attrComment" value="' + inputComment + '" type="hidden">');
	$('#attr-body').find('tr#attr-' + index).find('td').eq(4).html('<img onclick="showUpdateAttr(\'' + inputName + '\', \'' + inputType + '\', ' + inputPrimary + ', \'' + inputComment + '\', \'' + index + '\');" title="修改属性" style="cursor:pointer;" class="operate-img update-img" src="/img/code/editor_area.gif">');
	if($('#divInfo').hasClass('alert-danger')){
		$('#divInfo').removeClass('alert-danger');
		$('#divInfo').addClass('alert-success');
		$('#divInfo').text('修改属性成功！');
	}
	$('#divInfo').css('display', 'block');
}

function deleteAttr(index){
	var deleteInputName = $('#attr-body').find('tr#attr-' + index).find('td').eq(0).find('[name="attrName"]').val();
	if(deleteInputName in attrMap){
		attrMap[deleteInputName] = null;
	}
	$('#attr-body').find('tr#attr-' + index).html('');
}

function createAttrFrame(title, type, index){
	var attr_html = '<div style="margin-top:21px;">'
         + '<form class="form-horizontal" style="margin-bottom: 0px; font-family: Helvetica Neue;">'
         + '<div class="form-group" style="margin-bottom: 15px">'
         + '<label class="control-label col-sm-3" for="inputName" style="padding-left: 0px">属性名：</label>'
         + '<div class="col-sm-9">'
         + '<input class="form-control" id="inputName" onkeyup="value=value.replace(/[^\\w\\.\\/]/ig, \'\')" type="text">'
         + '</div>'
         + '</div>'
         + '<div class="form-group" style="margin-bottom: 15px">'
         + '<label class="control-label col-sm-3" for="inputType">类型：</label>'
         + '<div class="col-sm-9">'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 10%">'
         + '<input name="inputType" value="String" checked="checked" type="radio"> String'
         + '</label>'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 6%">'
         + '<input name="inputType" value="Integer" type="radio"> Integer'
         + '</label>'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 8%">'
         + '<input name="inputType" value="Long" type="radio"> Long'
         + '</label>'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 10%">'
         + '<input name="inputType" value="Byte" type="radio"> Byte'
         + '</label>'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 6%">'
         + '<input name="inputType" value="Float" type="radio"> Float'
         + '</label>'
         + '<label class="radio" style="width:25%;float:left;padding-top:5px; margin-left: 8%">'
         + '<input name="inputType" value="Date" type="radio"> Date'
         + '</label>'
         + '</div>'
         + '</div>'
         + '<div class="form-group" style="margin-bottom: 0px">'
         + '<label class="control-label col-sm-3" for="inputComment">备注：</label>'
         + '<div class="col-sm-9">'
         + '<input class="form-control" id="inputComment" type="text">'
         + '</div>'
         + '</div>'
         + '<div class="form-group" style="margin-bottom: 15px">'
         + '<label class="control-label col-sm-3" for="inputPrimary"></label>'
         + '<div class="col-sm-9" style="margin-left: 30%">'
         + '<label class="checkbox">'
         + '<input id="inputPrimary" type="checkbox"> 设置为主键'
         + '</label>'
         + '</div>'
         + '</div>'
         + '<div class="form-group" style="margin-bottom: 20px">'
         + '<div class="col-sm-12" style="margin-left:15%;">';
	if(type == 'update')
		attr_html += '<a id="update-btn" href="javascript:void(0);" onclick="updateAttrInfo(\'' + index + '\');" class="button button-rounded button-flat button-action button-small" style="padding:0px 30px;">修改</a>';
	else
		attr_html +=  '<a id="add-btn" href="javascript:void(0);" class="button button-rounded button-flat button-action button-small" style="padding:0px 30px;">添加</a>';
	attr_html += '<a id="close-btn" href="javascript:void(0);" class="button button-rounded button-flat button-primary button-small" style="padding:0px 30px; margin-left: 15%">关闭</a>'
         + '</div>'
         + '</div>'
         + '<div id="divInfo" class="alert alert-danger" style="width:100%;margin:0px 0px;display:none;"></div>'
         + '</form>'
         + '</div>';
	addDialog = $.dialog({
		title: title,
		focus: false,
	    width: '420px',
	    height: 330,
	    lock: true,
	    content: attr_html
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

function checkLowercase(s){          
   if (/^[a-z]+$/.test(s)){
      return true;   
   }    
   return false;   
}
function checkDigit(s){          
	   if (/^[0-9]+$/.test(s)){   
	      return true;   
	   }    
	   return false;   
	}

function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}