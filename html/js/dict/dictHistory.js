$(document).ready(function(){
	dictQueryHistory(1);
});
//搜索
function dictQueryHistory(pageNum){

	var data = {};
	var dictId = $("#dictId").val();
	data.dictId = dictId;
	data.pageNum = pageNum;
	data.perPage=$("#perPage").val();
	$.ajax({
		url:'/dict/queryDictHistory.html',
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
					str += "<td>" +lineno+ "</td>";
					str += "<td>" + dictObj.versionNum + "</td>";
					str += "<td><a href=checkDictHistory.html?dictId="+dictObj.dictId+"&version="+dictObj.versionNum+"&productName="+$("#productName").val()+"&productId="+$("#productId").val()+" class=\"btn btn-sm btn-primary\">查看</a>" +
						"</td>";
					str += "</tr>";
					$("#table_search_dict tbody").append(str);					
				}
				var pageInfo = json.page;
				getPage(pageInfo,len);
			}else{
				bootbox.alert("当前没有可用数据字典，请点击“新增字典”按钮进行添加!");
			}
		}
	});
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
	
	$("#changePageDictHistory").empty();
	$("#changePageDictHistory").append("<li><a href=\"#\" onClick=\"dictQueryHistory(1)\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
	for(var i=minPageNum; i <= maxPageNum; ++i){
		var str;
		if(currentPage == i){
			str = "<li class=\"active\"><a href=\"#\" onClick=\"dictQueryHistory(" + i + ")\">" + i + "</a></li>";
		}else{
			str = "<li><a href=\"#\" onClick=\"dictQueryHistory(" + i + ")\">" + i + "</a></li>";
		}

		$("#changePageDictHistory").append(str);	
	}
	$("#changePageDictHistory").append("<li><a href=\"#\" onClick=\"dictQueryHistory(" + totalPage + ")\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
	$("#changePageDictHistory").append("<li><span >"+"总计  "+totalRecord+" 条记录"+" 总计  "+totalPage+" 页"+"</span></li>");
}