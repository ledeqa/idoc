$(document).ready(function(){
	var dictId = $("#editDictId").val();
	var data = {};
	data.dictId = dictId;
	$.ajax({
		url:'/dict/edit.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			var dict = json.dict;
			if(dict){
				$("#edit_DictName").val(dict.dictName);
				$("#edit_DictDesc").val(dict.dictDesc);
				param.init(dict.params, true);
			}else{
				param.init(null, true);
			}
		}
	});
});
//返回
function returnDictIndex(){
	location.href = "index.html?productId="+$("#productId").val();
}
//校验字典名称是否存在
function validateDictName(){
	var productId = $("#productId").val();
	var dictName = $("#edit_DictName").val();
	if(!dictName || dictName==""){
		return;
	}
	var data = {};
	data.productId = productId;
	data.dictName = dictName;
	$.ajax({
		url:'/dict/validateName.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			if(json.retCode==200){
				bootbox.alert("该字典名称已经存在，请重新填写！");
				$("#btn_save_param").attr("disabled", true);
			}else{
				$("#btn_save_param").removeAttr("disabled");
			}
		}
	});
}
//保存参数
function saveParams(){
	var params = param.getDictParam();
	var param_len = params.length;
	var dictName = $("#edit_DictName").val();
	if(!dictName){
		bootbox.alert("字典名称不能为空！");
		return;
	}
	var dictDesc = $("#edit_DictDesc").val();
	if(!dictDesc){
		bootbox.alert("字典描述不能为空！");
		return;
	}
	if(param_len==0){
		bootbox.alert("没有可供保存的参数！");
		return;
	}
	//创建dictForm对象
	var dictForm = new Object();
	dictForm.dictId = $("#editDictId").val();
	dictForm.dictName = dictName;
	dictForm.dictDesc = dictDesc;
	dictForm.params = params;
	dictForm.productId = $("#productId").val();
	recurselySetProductId(dictForm);
	var data = JSON.stringify(dictForm);
	$.ajax({
		url:'/dict/addDicts.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			if(json.retCode==200){
				if($("#editDictId").val()){
					bootbox.alert("字典修改成功！");
				}else{
					bootbox.alert("字典添加成功！");
				}
				setTimeout(function (){
					var productId = $("#productId").val();
					location.href = "/dict/index.html?productId="+productId;
				}, 1000);
			}else if(json.retCode==201){
				bootbox.alert("该字典名称已经存在，请重新填写！");
			}else{
				if($("#editDictId").val()){
					bootbox.alert("字典修改失败！");
				}else{
					bootbox.alert("字典添加失败！");
				}
			}
		}
	});
}
//递归的设置productId
function recurselySetProductId(dictForm){
	var params = dictForm.params;
	if(!params || params==null){
		return;
	}
	var param_len = params.length;
	for(var i=0; i<param_len; i++){
		var param = params[i];
		var para_dict = param.dict;
		if(!para_dict || para_dict==null){
			continue;
		}
		para_dict.productId = $("#productId").val();
		if(!para_dict.dictName || para_dict.dictName==""){
			para_dict.dictName = "object";
			para_dict.dictDesc = param.paramDesc;
		}
		recurselySetProductId(para_dict);
	}
}
