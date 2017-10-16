var paddingBase = 20;
$(document).ready(function(){

});
//生成id
function generateId(){
	return "dict_" + (new Date().getTime());
}
//初始化
function init(){
	
}
function changeEdit(){
	$("input").attr("disabled",true);
	$("select").attr("disabled",true);
}

function addParams(){
	var len = $("#table_add_dict tbody").find("tr").length;
	var selId = "dictSelect" + len;
	var dictOperId = "dictOperate" + len;
	var row = "<tr id=\"level_0\">" + 
		"<td><input class=\"form-control\" type=\"text\" maxlength=\"60\"></td>" +
		"<td><select id=\""+selId+"\" class=\"form-control\" onchange=changeSelect(this)>" +
				"<option value=\"int\">int</option>"+
				"<option value=\"long\">long</option>"+
				"<option value=\"float\">float</option>"+
				"<option value=\"double\">double</option>"+
				"<option value=\"short\">short</option>"+
				"<option value=\"byte\">byte</option>"+
				"<option value=\"char\">char</option>"+
				"<option value=\"string\">string</option>"+
				"<option value=\"boolean\">boolean</option>"+
				"<option value=\"object\">object</option>"+
				"<option value=\"array&lt;int&gt;\">array&lt;int&gt;</option>"+
				"<option value=\"array&lt;long&gt;\">array&lt;long&gt;</option>"+
				"<option value=\"array&lt;float&gt;\">array&lt;float&gt;</option>"+
				"<option value=\"array&lt;double&gt;\">array&lt;double&gt;</option>"+
				"<option value=\"array&lt;short&gt;\">array&lt;short&gt;</option>"+
				"<option value=\"array&lt;byte&gt;\">array&lt;byte&gt;</option>"+
				"<option value=\"array&lt;char&gt;\">array&lt;char&gt;</option>"+
				"<option value=\"array&lt;string&gt;\">array&lt;string&gt;</option>"+
				"<option value=\"array&lt;boolean&gt;\">array&lt;boolean&gt;</option>"+
				"<option value=\"array&lt;object&gt;\">array&lt;object&gt;</option>"+
				"<option value=\"自定义\">自定义</option>"+
		"</td>"+
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td id="+dictOperId+"><button class=\"btn btn-danger\" onclick=\"deleteParamRow(this)\">删除</td>" +
		"</tr>";
	$("#table_add_dict tbody").append(row);
}
//判断所选择的参数类型
function changeSelect(ele){
	var tr = $(ele).parent().parent();
	var id = $(ele).attr("id");
	var index = id.substring(id.indexOf("dictSelect")+10);
	var selVal = $("#"+id).val();
	if(selVal!="自定义" && selVal!="object" && selVal!="array<object>"){
		var tdContent = "<button class=\"btn btn-danger\" onclick=\"deleteParamRow('"+index+"')\">删除</button>";
		var tdId = "dictOperation" + index;
		$("#" + tdId).html(tdContent);
		//如果有子参数，需要删掉
		var level = getTrLevel(tr);
		var index = $("#table_add_dict tbody tr").index(tr[0]);
		var table_len = $("#table_add_dict tbody").find("tr").length;
		var deleteArr = new Array();
		for(var i=index+1; i<table_len; i++){
			 var nextTr = $("#table_add_dict tbody").find("tr").eq(i);
    		 if(nextTr){
    			 var nextTrId = nextTr.attr("id");
    			 if(nextTrId){
    				 var nextTrIdInt = getTrLevel(nextTr);
    				 if(nextTrIdInt>level){
    					 deleteArr.push(i);
    				 }else{
    					 break;
    				 }
    			 }
    		 }
		}
		deleteArr = deleteArr.reverse();
		if(deleteArr.length>0){
	   		 $.each(deleteArr, function(i, value){
				 $("#table_add_dict tbody tr:eq("+value+")").remove();
			 });
	   		 //删除完毕之后重新赋值下拉列表的id
        	 var table_len = $("#table_add_dict tbody").find("tr").length;
        	 for(var i=0; i<table_len; i++){
        		 var tr = $("#table_add_dict tbody tr").eq(i);
        		 tr.find("select").eq(0).attr("id", "dictSelect"+i);
        		 tr.find("td:last").attr("id", "dictOperate"+i)
        	 }
		}
		return;
	}else if(selVal=="object" || selVal=="array<object>"){
		var tdContent = "<button class=\"btn btn-primary\" onclick=\"addParamRow(this)\">新增 </button> " +
		" <button class=\"btn btn-danger\" onclick=\"deleteParamRow(this)\">删除</button>";
	}
	var tdId = "dictOperate" + index;
	$("#" + tdId).html(tdContent);
	if(selVal=="自定义"){
		bootbox.dialog({
			title : "选择字典",
			message :
				"<div class=\"row\">" +
				"<label class=\"col-sm-2 control-label\"" +
				"style=\"padding-top: 5px; padding-left: 5px; padding-right: 5px;\">请选择字典：</label>"+
				"<div class=\"col-sm-4\" style=\"padding-left: 5px;\">" +
				"<select id=\"selectDict\" class=\"form-control\"></select>" +
				"</div></div>",
				locale : "zh_CN",
				buttons : {
					Modify:{
						label : '确定',
						className : "btn-primary",
						callback : function() {
							var val = $("#selectDict").val();
							var text = $("#selectDict").find("input").val();
							var option = "<option value=\""+val+"\">" + text + "</option>";
							$("#"+id).attr("data", val);
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
	//$("#selectDict").empty();
	$.ajax({
		url:'/dict/queryAllDicts.html',
		type:'POST',
		success:function(json){
			if(json.retCode==-1){
				bootbox.alert("暂无可选择的字典！");
			}else{
				var dictList = json.retContent;
				var length = dictList.length;
				if(length==0){
					bootbox.alert("暂无可选择的字典！");
					return;
				}
				for(var i=0; i<length; i++){
					var dict = dictList[i];
					var dictRow = "<option value=\""+dict.dictId+"\">" + dict.dictName + "</option>";
					$("#selectDict").append(dictRow);
				}
			}
		}
	});
	
}
//新增加子参数
function addParamRow(ele){
	var tr = $(ele).parent().parent();
	var index = $("#table_add_dict tbody tr").index(tr[0]);
	var levelInt = getTrLevel(tr);
	var newLevel = levelInt + 1;
	var tr_id = "level_" + newLevel;
	var padding = paddingBase * newLevel;
	var tr_len = $("#table_add_dict tbody").find("tr").length;
	var selId = "dictSelect" + tr_len;
	var dictOperateId = "dictOperate" + tr_len;
	var row = "<tr id=\""+tr_id+"\">" + 
		"<td><input class=\"form-control\" type=\"text\" maxlength=\"60\" style=\"padding-left:"+padding+"px;\"></td>" +
		"<td><select id=\""+selId+"\" class=\"form-control\" onchange=changeSelect(this)>" +
			"<option value=\"int\">int</option>"+
			"<option value=\"long\">long</option>"+
			"<option value=\"float\">float</option>"+
			"<option value=\"double\">double</option>"+
			"<option value=\"short\">short</option>"+
			"<option value=\"byte\">byte</option>"+
			"<option value=\"char\">char</option>"+
			"<option value=\"string\">string</option>"+
			"<option value=\"boolean\">boolean</option>"+
			"<option value=\"object\">object</option>"+
			"<option value=\"array&lt;int&gt;\">array&lt;int&gt;</option>"+
			"<option value=\"array&lt;long&gt;\">array&lt;long&gt;</option>"+
			"<option value=\"array&lt;float&gt;\">array&lt;float&gt;</option>"+
			"<option value=\"array&lt;double&gt;\">array&lt;double&gt;</option>"+
			"<option value=\"array&lt;short&gt;\">array&lt;short&gt;</option>"+
			"<option value=\"array&lt;byte&gt;\">array&lt;byte&gt;</option>"+
			"<option value=\"array&lt;char&gt;\">array&lt;char&gt;</option>"+
			"<option value=\"array&lt;string&gt;\">array&lt;string&gt;</option>"+
			"<option value=\"array&lt;boolean&gt;\">array&lt;boolean&gt;</option>"+
			"<option value=\"array&lt;object&gt;\">array&lt;object&gt;</option>"+
			"<option value=\"自定义\">自定义</option>"+
		"</td>"+
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td><input class=\"form-control\" type=\"text\"></td>" +
		"<td id=\""+dictOperateId+"\"><button class=\"btn btn-danger\" onclick=\"deleteParamRow(this)\">删除</td>" +
		"</tr>";
	var next_tr = $("#table_add_dict tbody").find("tr").eq(index+1);
	if(next_tr){
		var next_tr_id = next_tr.attr("id");
		if(next_tr_id){
			var next_tr_level = getTrLevel(next_tr);
			if(newLevel>next_tr_level){
				tr.after(row);
			}else{
				$("#table_add_dict tbody").append(row);
			}
		}else{
			$("#table_add_dict tbody").append(row);
		}
	}else{
		$("#table_add_dict tbody").append(row);
	}
	
}
//删除参数,有子参数的需要连带删除子参数,绑定id的需要动态更新id
function deleteParamRow(ele){
	var tr = $(ele).parent().parent();
	var level = getTrLevel(tr);
	var index = $("#table_add_dict tbody tr").index(tr[0]);
	 bootbox.dialog({  
	     message: " ",  
	     title: "您确定要删除该参数吗？",  
	     buttons: {  
	    	 OK: {  
	             label: "确认",  
	             className: "btn-primary",  
	             callback: function () {  
	            	 //如有子参数，需要连带删除
	            	 var arrList = new Array();
	            	 arrList.push(index);
	            	 var table_len =  $("#table_add_dict tbody").find("tr").length;
	            	 for(var i=index+1; i<table_len; i++){
	            		 var nextTr = $("#table_add_dict tbody").find("tr").eq(i);
	            		 if(nextTr){
	            			 var nextTrId = nextTr.attr("id");
	            			 if(nextTrId){
	            				 var nextTrIdInt = getTrLevel(nextTr);
	            				 if(nextTrIdInt>level){
	            					 arrList.push(i);
	            				 }else{
	            					 break;
	            				 }
	            			 }
	            		 }
	            	 }
	            	 if(arrList.length>0){
	            		 arrList = arrList.reverse();
	            		 $.each(arrList, function(i, value){
	            			 $("#table_add_dict tbody tr:eq("+value+")").remove();
	            		 });
	            	 }
	            	 //删除完毕之后重新赋值下拉列表的id
	            	 var table_len = $("#table_add_dict tbody").find("tr").length;
	            	 for(var i=0; i<table_len; i++){
	            		 var tr = $("#table_add_dict tbody tr").eq(i);
	            		 tr.find("select").eq(0).attr("id", "dictSelect"+i);
	            		 tr.find("td:last").attr("id", "dictOperate"+i)
	            	 }
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
//保存参数
function saveParams(){
	var productId = 1;
	var dictName = $("#addDictName").val();
	if(!dictName){
		bootbox.alert("字典名称不能为空！");
		return;
	}
	var dictDesc = $("#addDictDesc").val();
	if(!dictDesc){
		bootbox.alert("字典描述不能为空！");
		return;
	}
	var param_table_len = $("#table_add_dict tbody").find("tr").length;
	if(param_table_len==0){
		bootbox.alert("没有可供保存的参数！");
		return;
	}
	//创建dictForm对象
	var dictForm = new Object();
	var params = new Array();
	dictForm.dictName = dictName;
	dictForm.dictDesc = dictDesc;
	dictForm.params = params;
	dictForm.productId = productId;
	//var param = new Object();
	for(var i=0; i<param_table_len; i++){
		var tr = $("#table_add_dict tbody tr").eq(i);
		var tr_level_int = getTrLevel(tr);
		if(tr_level_int > 0){
			continue;
		}
		var param_name = tr.find("td").eq(0).find("input").val();
		var param_type_sel = tr.find("select").eq(0).attr("id");
		var param_type = $("#"+param_type_sel).val();
		var param_desc = tr.find("td").eq(2).find("input").val();
		var param_mock = tr.find("td").eq(3).find("input").val();
		var param_remark = tr.find("td").eq(4).find("input").val();
		var para = new Object();
		para.paramName = param_name;
		para.paramType = param_type;
		para.paramDesc = param_desc;
		para.mock = param_mock;
		para.remark = param_remark;
		if(param_type=="object" || param_type=="array<object>"){
			var dictId = $("#"+param_type_sel).attr("data");
			para.dictId = dictId;
			params.push(para);
		}else if(param_type=="自定义"){
			var dict = new Object();
			var pa = new Array();
			dict.productId = productId;
			dict.dictName = param_name;
			dict.dictDesc = param_desc;
			dict.params = pa;
			para.dictForm = dict;
			params.push(para);
			recursiveSubParams(i+1, tr_level_int, dict, productId);
		}else{
			params.push(para);
		}
	}
	var data = JSON.stringify(dictForm);
	$.ajax({
		url:'/dict/addDicts.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			if(json.retCode==200){
				bootbox.alert("字典添加成功");
			}else{
				bootbox.alert("字典添加失败");
			}
		}
	});
}
//递归的获取子参数
function recursiveSubParams(index, parentLevel, parent, productId){
	var tr = $("#table_add_dict tbody tr").eq(index);
	if(!tr){
		return parent;
	}
	var level = getTrLevel(tr);
	var param_name = tr.find("td").eq(0).find("input").val();
	var param_type_sel = tr.find("select").eq(0).attr("id");
	var param_type = $("#"+param_type_sel).val();
	var param_desc = tr.find("td").eq(2).find("input").val();
	var param_mock = tr.find("td").eq(3).find("input").val();
	var param_remark = tr.find("td").eq(4).find("input").val();
	if((parentLevel+1)==level){
		var p = new Object();
		p.paramName = param_name;
		p.paramDesc = param_desc;
		p.paramType = param_type;
		p.mock = param_mock;
		p.remark = param_remark;
		if(param_type=="object" || param_type=="array<object>"){
			var dictFormId = $("#"+param_type_sel).attr("data");
			p.dictId = dictFormId;
			parent.params.push(p);
		}
		if(param_type=="自定义"){
			var dict_param = new Object();
			var param_arr = new Array();
			dict_param.dictName = param_name;
			dict_param.dictDesc = param_desc;
			dict_param.productId = productId;
			dict_param.params = param_arr;
			p.dictForm = dict_param;
			parent.params.push(p);
			return recursiveSubParams(index+1, level, dict_param, productId);
		}else{
			parent.params.push(p);
			return parent;
		}
	}
}
function getTrLevel(tr){
	var idAttr = tr.attr("id");
	var levelStr = idAttr.substring(idAttr.indexOf("_")+1);
	var levelInt = parseInt(levelStr);
	return levelInt;
}
//取消添加的参数
function cancelParams(){
		
	 bootbox.dialog({  
	     message: " ",  
	     title: "您确定要清除所添加的参数吗？",  
	     buttons: {  
	    	 OK: {  
	             label: "确认",  
	             className: "btn-primary",  
	             callback: function () {  
	            	 $("#table_add_dict tbody").empty();
	            	 
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