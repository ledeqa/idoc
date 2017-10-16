var downloadLink;
$(document).ready(function(){
	var dictId = $("#checkDictId").val();
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
				$("#check_DictName").val(dict.dictName);
				$("#check_DictDesc").val(dict.dictDesc);
				param.init(dict.params, false);
			}else{
				param.init(null, false);
			}
			disableEdit();
		}
	});
});

//不能编辑
function disableEdit(){
	$("#check_DictName").attr("readonly", "readonly");
	$("#check_DictDesc").attr("readonly", "readonly");
	var tableId = param.getTableId();
	$("#"+tableId).find("input,button,select").attr("disabled", "disabled");
}

//返回
function checkReturn(){
	location.href = "index.html?productId="+$("#productId").val();
}

//生成model对象
function generateDictModel(){
	var dictId = $("#checkDictId").val();
	var dictName = $("#check_DictName").val();
	if(dictId === ''){
		showAlertMessage('字典id不存在，重新登录系统试试!');
		return;
	}
	var data ={};
	data.dictId = dictId;
	data.dictName = dictName;
	$.ajax({
		url:'/dict/generateDictClass.html',
		type:'POST',
		data:data,
		dataType:'json',
		success:function(json){
			if(json.retCode == 200){
				var sourceCodeInfo = json.sourceCodeInfo;
				codeInfoObject = sourceCodeInfo;
				if(sourceCodeInfo){
					var len = sourceCodeInfo.length;
					var flag = true;
					for(var i = 0; i < len; i++){
						var info = sourceCodeInfo[i];
						downloadLink = info.filePath;
						if(typeof downloadLink != "undefined"){
							window.location.href = downloadLink;
						}else{
							$().toastmessage('showToast', {
							    text     : '没找到下载地址，下载失败!',
							    sticky   :  false,
							    position : 'top-right',
							    type     : 'error',
							    stayTime:   1500
							});
							return;
						}
					}
				}
			}
		}
	});
}
$('#download-btn').css('display', 'inline-table');
$('#download-btn').click(function(){
	if(typeof downloadLink != "undefined"){
		window.location.href = downloadLink;
	}else{
		$().toastmessage('showToast', {
		    text     : '没找到下载地址，下载失败!',
		    sticky   :  false,
		    position : 'top-right',
		    type     : 'error',
		    stayTime:   1500
		});
		return;
	}
});


function showAlertMessage(msg, callback){
	if(callback){
		bootbox.alert({message: msg,
			buttons:{
				ok:{
					label: '确定'
				}
			},
			callback: callback
		});
	}else{
		bootbox.alert({message: msg,
			buttons:{
				ok:{
					label: '确定'
				}
			}});
	}
}
//保存参数
/*function saveParams(){
	var params = param.getDictParam();
	var param_len = params.length;
	var dictName = $("#check_DictName").val();
	if(!dictName){
		bootbox.alert("字典名称不能为空！");
		return;
	}
	var dictDesc = $("#check_DictDesc").val();
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
	dictForm.dictId = $("#checkDictId").val();
	dictForm.dictName = dictName;
	dictForm.dictDesc = dictDesc;
	dictForm.params = params;
	dictForm.productId = $("#productId").val();
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
}*/
