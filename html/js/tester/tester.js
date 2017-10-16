/**
 * tester.js
 */

$(document).ready(function(){
	$('#run').click(function(){
		$('#resBoard').html('加载中，请稍后...');
		var projectId = $('#projectId').val();
		var url     = '';
        var qArr    = [];
        var i       = 0;
        var fields = $('.field');
        var baseUrl = 'http://idoc.qa.lede.com/mockjs/' + projectId;
        var path    = $('#path').text();
        
        if(path.indexOf('http://') >= 0){
        	path = path.substring(7);
            path = path.substring(path.indexOf("/"));
        }else if(path.indexOf('https://') >= 0){
        	path = path.substring(8);
            path = path.substring(path.indexOf("/"));
        }
        
        if (path[0] !== '/') {
            path = '/' + path;
        }
        
        baseUrl += path;
        fields.each(function(index) {
            var name = fields[index].name;
            var value = fields[index].value;
            qArr[i++] = name + '=' + encodeURIComponent(value);
        });
        
        url = baseUrl + (baseUrl.indexOf('?') === -1 ? '?' : '&') + qArr.join('&');
        
        $.ajax({
        	url: '/idoc/mock/requestOnServer.html?url=' + encodeURIComponent(url),
        	type: 'GET',
        	dataType: 'json',
        	success: function(json){
        		if(json.retCode == 200){
        			var responseText = eval('(' + json.responseText + ')');
        			testResHandler(responseText);
        		}else{
        			$('#resBoard').html(json.retDesc);
        		}
        	}
        
        })
        
	});
});

function testResHandler(response) {
    var obj = response;
    var jsonString;
  
    obj = Mock.mock(obj);

    jsonString = JSON.stringify(obj, function(key, val) {
        if (typeof val === 'function') {
            return "<mockjs custom function handler>";
        } else {
            return val;
        }
    });
    
    $('#resBoard').html(jsonString.formatJS());
    
    return obj;
}