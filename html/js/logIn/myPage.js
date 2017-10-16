$(document).ready(function() {
	getMyProducts(1);
});

function getMyProducts(pageNum){
	var data = {};
	var adminFlag = $("#adminFlag").val();
	var englishName = $("#englishName").val();
	data.perPage = $("#perPage").val();
	data.adminFlag = adminFlag;
	data.pageNum = pageNum;
	data.englishName = englishName;
	
	$.ajax({
		url: '/idoc/getMyProducts.html',
		type: "POST",
		data: data,
		dataType : 'json',
		success: function(json){
			if(json.retCode == 200){
				var list = json.retContent;
				len = list.length;
				var pageInfor = json.page;

				$("#table_search_dict tbody").empty();
				for (var i = 0; i < len; i++) {
					var product = list[i];
					var isAdmin = product.isAdmin;
					var str = "<tr>"
						+"<td style='text-align:center'>"+product.id+"</td>"
						+"<td style='text-align:center'><a href='/idoc/projectList.html?productId="+product.productId+"'>"+product.productName+"</a></td>"
						+"<td style='text-align:center'>"+product.projectNum+"</td>"
						+"<td style='text-align:center'><a href='"+product.dataDictUrl+"'>点击查看</a></td>"
						+"<td style='text-align:center'><a href='"+product.interfaceOnlineUrl+"'>点击查看</a></td>"
						+"<td style='text-align:center'><a class='btn btn-success' onclick=searchInterface('"+product.productId+"')>查询</a>&nbsp;&nbsp;"
						+"<a class='btn btn-warning' href='/idoc/updateProduct.html?productId="+product.productId+"'>编辑</a>&nbsp;&nbsp;";
					if(isAdmin == 1)
						str += "<button type='button' class='btn btn-danger' onClick=\"deleteProduct("+product.productId+",'"+product.productName+"')\">删除</button></td>";
					str += "</tr>";
					$("#table_search_dict tbody").append(str);
				}
				getPage(pageInfor, len);
			}else {
				 $("#table_search_dict tbody").empty();
			}
		}
	});
	
}
function changePageProductList(){
	getMyProducts(1);
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

	$("#changePageProduct").empty();
	$("#changePageProduct")
			.append(
					"<li><a href=\"#\" onClick=\"getMyProducts(1)\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
	for (var i = minPageNum; i <= maxPageNum; ++i) {
		var str;
		if (currentPage == i) {
			str = "<li class=\"active\"><a href=\"#\" onClick=\"getMyProducts("
					+ i + ")\">" + i + "</a></li>";
		} else {
			str = "<li ><a href=\"#\" onClick=\"getMyProducts(" + i
					+ ")\">" + i + "</a></li>";
		}

		$("#changePageProduct").append(str);
	}
	$("#changePageProduct")
			.append(
					"<li class=\"paginate_button\" aria-controls=\"dataTables-example\"><a href=\"#\" onClick=\"getMyProducts("
							+ totalPage
							+ ")\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
	$("#changePageProduct").append(
			"<li><span >" + "总计  " + totalRecord + " 条记录" + " 总计  " + totalPage
					+ " 页" + "</span></li>");

}
function deleteProduct(productId,productName){
	var data1 = {};	
	data1.productName=productName;
	data1.productId = productId;
	if(productName&&productName!=""){
		if(productName!="Demo"){
			bootbox.dialog({
				message : " ",
				title : "您确定删除该产品？",
				buttons : {
					OK : {
						label : "确认",
						className : "btn-primary",
						callback : function() {
							
							$.ajax({
								url:'/idoc/deleteProduct.html',
								type:'GET',
								data:data1,
								dataType:'json',
								success:function(json){
									if(json.retCode == 200){				
										
										bootbox.alert({  
											message: '删除成功',
								            buttons: {  
								               ok: {  
								                    label: '确定',  
								                    className: 'btn-myStyle'
								                }  
								            },
								            callback: function() {  
						                    	window.location.reload();
								            }
								        });
									}else{
										bootbox.alert("操作失败，请联系后台人员修改！");
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
		else{
			bootbox.alert("这是Demo产品,不允许删除!");
		}
	}
}

function searchInterface(productId){
	if(productId != null && productId.trim() != ''){
		bootbox.dialog({
			message : '<br><div class="row form-horizontal">' +
			'<div class="form-group">' +
			'<label class="col-lg-2 control-label">接口名称：</label>' +
			'<div class="col-lg-9">' +
			'<input id="interfaceName" class="form-control" placeholder="使用接口名称进行查询"/>'+
			'</div><br><br><br>' +
			'<label class="col-lg-2 control-label">接口 url：</label>' +
			'<div class="col-lg-9">' +
			'<input id="interfaceUrl" class="form-control" placeholder="使用接口url进行查询" />' +
			'</div>' +
			'</div> <br>' +
			'</div>',
			title : "接口查询",
			buttons : {
				OK : {
					label : "查询",
					className : "btn-primary",
					callback : function() {
						if($('#interfaceName').val() == '' && $('#interfaceUrl').val() == ''){
							$().toastmessage('showToast', {
	    					    text     : '接口名称和url不能同时为空',
	    					    sticky   :  false,
	    					    position : 'top-right',
	    					    type     : 'warning',
	    					    stayTime :  2000
	    					});
							return false;
						}
						var status = -1;
						var data = {};
						data.productId = productId;
						data.interfaceName = $('#interfaceName').val().trim();
						data.interfaceUrl = $('#interfaceUrl').val().trim();
						$.ajax({
							url:'/idoc/searchInterfaceId.html',
							type:'POST',
							data: data,
							async: false,
							dataType:'json',
							success:function(json){
								status = json.retCode;
								if(json.retCode == 200){
									var interfaceIdList = json.interfaceIdList;
									var len = interfaceIdList.length;
									bootbox.dialog({
										title: '查询结果',
										message: '<div class="row">' +
													'<div class="form col-sm-12">' +
														'<table class="table table-hover" id="searchResult">' +
															'<thead>' +
																'<tr>' +
																	'<th style="text-align:center">接口名称</th>' +
																	'<th style="text-align:center">接口Url</th>' +
																	'<th style="text-align:center">操作</th>' +
																'</tr>' +
															'</thead>' +
															'<tbody>' +
															'</tbody>' +
														'</table>' +
													'</div>' +
												'</div>',
										buttons:{
											cancel:{
												label: '关闭',
												className: 'btn-default'
											}
									
										},
										callback: function(){
										}
									});
									var domain = 'http://idoc.qa.lede.com/';
									var interUrl = 'idoc/inter/index.html';
									var str = "";
									$("#searchResult tbody").empty();
									for(var i = 0; i < len; i++){
										var inter = interfaceIdList[i];
										str += '<tr><td>'+inter.interfaceName+'</td><td>'+inter.interfaceUrl+'</td><td><a class="btn btn-success" href="'+domain+interUrl+'?projectId='+inter.projectId+'&interfaceId='+inter.interfaceId+'" target="_blank">查看</td></tr>';
										//window.open(domain + interUrl + "?projectId=" + inter.projectId + '&interfaceId=' + inter.interfaceId);
									}
									$("#searchResult tbody").append(str);
								}else{
									$().toastmessage('showToast', {
			    					    text     : '查询的接口不存在，请检查输入信息是否正确',
			    					    sticky   :  false,
			    					    position : 'top-right',
			    					    type     : 'warning',
			    					    stayTime :  2000
			    					});
								}
							}
						});
						if(status == -1){
							return false;
						}
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
}

//重定向
function goBackPage(){
	self.location = '/idoc/index.html';
}