$(document).ready(function(){
	var dictId = $("#dictId").val();
	var version = $("#version").val();
	var data = {};
	data.dictId = dictId;
	data.version = version;
	$.ajax({
		url:'/dict/queryDictVersionById.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			var dict = json.dict;
			if(dict){
				$("#check_history_DictName").val(dict.dictName);
				$("#check_history_DictDesc").val(dict.dictDesc);
				$("#return_dict_history_url").attr('href', 'historyDict.html?productName='+$("#productName").val()+'&dictName='+dict.dictName+
						'&dictId='+$("#dictId").val()+'&productId='+$("#productId").val());
				param.init(dict.params, false);
			}else{
				param.init(null, false);
			}
			disableEdit();
		}
	});
});
//回滚到此版本
function revertDictVersion(){
	var params = param.getDictParam();
	var param_len = params.length;
	var dictName = $("#check_history_DictName").val();
	if(!dictName){
		bootbox.alert("字典名称不能为空！");
		return;
	}
	var dictDesc = $("#check_history_DictDesc").val();
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
	dictForm.dictId = $("#dictId").val();
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
				bootbox.alert("字典回滚成功！");
				setTimeout(function(){
					location.href = "/dict/index.html?productId="+$("#productId").val()
				}, 500)
			}else{
				bootbox.alert("字典回滚失败，请联系相关人员！");
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