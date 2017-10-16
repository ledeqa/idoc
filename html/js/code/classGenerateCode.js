var codeInfoObject;
var iniCode;
var downloadLink;
$(document).ready(function(){
	
	var dao = [];
	var entity = [], service = [], controller = [];
	var daoImplchild = [];
	var daoImpl = { name:"impl", open:true, icon:"/img/code/package_obj.gif",
			children: daoImplchild
	};
	var serviceImplchild = [];
	var serviceImpl = { name:"impl", open:true, icon:"/img/code/package_obj.gif",
			children: serviceImplchild
	};
	var mapper = [];
	
	var data = {};
	data.packageName = $.cookie('packageName');
	data.className = $.cookie('className');
	data.params = $.cookie('params');

	$.ajax({
		url:'/idoc/code/classGenerateCode.html',
		type:'POST',
		dataType:'json',
		async: false,
		data: data,
		success:function(json){
			if(json.retCode == 200){
				var sourceCodeInfo = json.sourceCodeInfo;
				codeInfoObject = sourceCodeInfo;
				if(sourceCodeInfo){
					var len = sourceCodeInfo.length;
					var flag = true;
					for(var i = 0; i < len; i++){
						var info = sourceCodeInfo[i];
						if(info.codeType == 'DAO'){
							var subDao = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							dao.push(subDao);
							if(flag){
								iniCode = i;
								flag = false;
							}
						}else if(info.codeType == 'DAOIMPL'){
							var subDaoImpl = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							daoImplchild.push(subDaoImpl);
						}else if(info.codeType == 'ENTITY'){
							var subEntity = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							entity.push(subEntity);
						}else if(info.codeType == 'SERVICE'){
							var subService = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							service.push(subService);
						}else if(info.codeType == 'SERVICEIMPL'){
							var subServiceImpl = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							serviceImplchild.push(subServiceImpl);
						}else if(info.codeType == 'CONTROLLER'){
							var subController = { name: info.className, icon:"/img/code/jcu_obj.gif", click: "nodeonclick('" + i + "');"};
							controller.push(subController);
						}else if(info.codeType == 'MAPPER'){
							var subMapper = { name: info.className, icon:"/img/code/xmldoc.gif", click: "nodeonclick('" + i + "');"};
							mapper.push(subMapper);
						}else if(info.codeType == 'DOWNLOAD'){
							downloadLink = info.filePath;
						}
					
					}
				}
			}
		}
	});
	dao.push(daoImpl);
	service.push(serviceImpl);
	var setting = {};

	var zNodechild = [
	 					{ name:"dao", open:true, icon:"/img/code/package_obj.gif",
	 						children: dao
	 					},
	 					{ name:"entity", open:true, icon:"/img/code/package_obj.gif",
	 						children: entity
	 					},
	 					{ name:"service", open:true, icon:"/img/code/package_obj.gif",
	 						children: service
		 				},
		 				{ name:"controller", open:true, icon:"/img/code/package_obj.gif",
	 						children: controller
		 				}
	 				];
	for(var i = 0; i < mapper.length; i++){
		zNodechild.push(mapper[i]);
	}
	var zNodes =[
	 			{ name:"src", open:true, icon:"/img/code/source-folder.gif",
	 				children: zNodechild
	 			},
	 			{ name:"db", open:true, icon:"/img/code/fldr_obj.gif",
	 				children: [
	 					{ name:"Todo.sql", open:true, icon:"/img/code/sourceEditor.gif"}
	 				]}
	 		];
	
	$.fn.zTree.init($("#codeTree"), setting, zNodes);
	if(typeof(iniCode) != "undefined"){
		nodeonclick(iniCode);
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
});
function nodeonclick(i){
	var code = codeInfoObject[i].code;
	$('#codeText').html('<b>' + code + '</b>');
	if($('#codeText').hasClass('prettyprinted')){
		$('#codeText').removeClass('prettyprinted');
	}$('#codeText').css('font-size', '15px');
	prettyPrint();
}