var onlinePeopleCount;
$(document).ready(function(){
	var mydate = new Date();
	var year = mydate.getFullYear();
	$('#year').html(year);
	$.ajax({
		url:'/idoc/getMyProduct.html',
		type:'POST',
		dataType:'json',
		success:function(json){
			if(json.retCode == 200){
				var productList = json.retContent;
				if(!productList){
					return;
				}
				//给需要在页面中使用productList的页面中赋值给一个全局变量
				_productList = productList;
				var len = productList.length;
				var str = "";
				$("#my_product_list").empty();
				for(var i=0; i<len; i++){
					var product = productList[i];
					str += "<li><a href=\"/idoc/projectList.html?productId=" + product.productId + "\">" + product.productName + "</a></li>";
				}
				$("#my_product_list").append(str);
			}
		}
	});
	$.ajax({
		url:'/getOnlinePeopleCount.html',
		type:'POST',
		dataType:'json',
		success:function(json){
			onlinePeopleCount = json.SessionCount;
			historyPeople = json.historyPeople;
			//$('#onlinePeopleCount').html("当前共"+onlinePeopleCount+"人在线");
           // $('#historyPeople').html("已服务次数："+historyPeople);
		}
	});

	$.goup({
        trigger: 100,
        bottomOffset: 100,
        locationOffset: 10
    });
});

function getDate(time){

	var createDate = new Date(time);
	var month = createDate.getMonth() + 1;
	month = month < 10 ? "0"+month : month;
	var date = createDate.getDate();
	date = date < 10 ? "0" + date : date;
	var hour = createDate.getHours();
	hour = hour < 10 ? "0" + hour : hour;
	var minute = createDate.getMinutes();
	minute = minute < 10 ? "0" + minute : minute;
	var second = createDate.getSeconds();
	second = second < 10 ? "0" + second : second;

	createTime = createDate.getFullYear() + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
	return createTime;
}

function formatXml(text) {
    //去掉多余的空格
    text = '\n' + text.replace(/(<\w+)(\s.*?>)/g, function($0, name, props) {
        return name + ' ' + props.replace(/\s+(\w+=)/g, " $1");
    }).replace(/>\s*?</g, ">\n<");

    //把注释编码
    text = text.replace(/\n/g, '\r').replace(/<!--(.+?)-->/g,
            function($0, text) {
                var ret = '<!--' + escape(text) + '-->';
                //alert(ret);
                return ret;
            }).replace(/\r/g, '\n');

    //调整格式
    var rgx = /\n(<(([^\?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/mg;
    var nodeStack = [];
    var output = text.replace(rgx, function($0, all, name, isBegin,
            isCloseFull1, isCloseFull2, isFull1, isFull2) {
        var isClosed = (isCloseFull1 == '/') || (isCloseFull2 == '/')
                || (isFull1 == '/') || (isFull2 == '/');
        //alert([all,isClosed].join('='));
        var prefix = '';
        if (isBegin == '!') {
            prefix = getPrefix(nodeStack.length);
        } else {
            if (isBegin != '/') {
                prefix = getPrefix(nodeStack.length);
                if (!isClosed) {
                    nodeStack.push(name);
                }
            } else {
                nodeStack.pop();
                prefix = getPrefix(nodeStack.length);
            }

        }
        var ret = '\n' + prefix + all;
        return ret;
    });

    var prefixSpace = -1;
    var outputText = output.substring(1);
    //alert(outputText);

    //把注释还原并解码，调格式
    outputText = outputText.replace(/\n/g, '\r').replace(
            /(\s*)<!--(.+?)-->/g,
            function($0, prefix, text) {
                //alert(['[',prefix,']=',prefix.length].join(''));
                if (prefix.charAt(0) == '\r')
                    prefix = prefix.substring(1);
                text = unescape(text).replace(/\r/g, '\n');
                var ret = '\n' + prefix + '<!--'
                        + text.replace(/^\s*/mg, prefix) + '-->';
                //alert(ret);
                return ret;
            });

    return outputText.replace(/\s+$/g, '').replace(/\r/g, '\r\n');

}

function getPrefix(prefixIndex) {
    var span = '    ';
    var output = [];
    for ( var i = 0; i < prefixIndex; ++i) {
        output.push(span);
    }

    return output.join('');
}


