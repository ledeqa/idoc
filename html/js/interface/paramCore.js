/**
 * core.js
 */
var core = core || {};

(function(){
	core.parameter = core.parameter || {};
	

	var param = core.parameter;
	var b = baidu;
	var _isEditMode = true;
	var _editContext;
	var _deletedObjectList;
	var _reqParam;
	var _retParam;
	var _productId;
	var isReqParamChange = false;
	var isRetParamChange = false;
	
	var _copyParamList = [];
	var _copyParamHashSet = {}; // 用于复制参数时去重
	
	var TEMPLATE = {
		'REQUEST_BEGIN'					: '<table class="table table-bordered"><thead><tr><td class="head-expander"></td><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show"></th></tr></thead><tbody>',
		'REQUEST_BEGIN_EDIT'			: '<table class="table table-bordered"><thead><tr><td class="head-expander"></td><th class="head-op">OP</th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show"></th></tr></thead><tbody>',
		'REQUEST_END'					: '</tbody></table>',
		'REQUEST_PARAMETER_ADD_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.addParam(\'request\');return false;"><i class="fa fa-plus"></i> 添加参数</button>&nbsp;&nbsp',
		'REQUEST_PARAMETER_JSON_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.importParam(\'request\',\'json\');return false;"><i class="fa fa-plus"></i> 导入json数据</button>&nbsp;&nbsp',
		'REQUEST_PARAMETER_XML_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.importParam(\'request\',\'xml\');return false;"><i class="fa fa-plus"></i> 导入xml数据</button>',
		
		'RESPONSE_BEGIN'				: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show">是否显示该字段</th></tr></thead><tbody>',
		'RESPONSE_BEGIN_EDIT'			: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-op">OP</th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show">是否显示该字段</th></tr></thead><tbody>',
		'RESPONSE_END'					: '</tbody></table>',
		'RESPONSE_PARAMETER_ADD_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.addParam(\'response\');return false;"><i class="fa fa-plus"></i> 添加参数</button>&nbsp;&nbsp',
		'RESPONSE_PARAMETER_JSON_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.importParam(\'response\',\'json\');return false;"><i class="fa fa-plus"></i> 导入json数据</button>&nbsp;&nbsp',
		'RESPONSE_PARAMETER_XML_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.importParam(\'response\',\'xml\');return false;"><i class="fa fa-plus"></i> 导入xml数据</button>',
	};
	
	var ELEMENT_ID = {
		'EDIT_INPUT'		: 'txtMTName'
	};
	
	var CONFIG = {
		'DEFAULT_MAX_LENGTH'         : 50,
        'REMARK_MAX_LENGTH'          : 9999,
        'DEFAULT_INPUT_WIDTH'        : 100,
        'PARAMETER_NAME_WIDTH'       : 200,
        'PARAMETER_DESC_WIDTH'		 : 250,
        'PARAMETER_TYPE_WIDTH'       : 200,
        'PARAMETER_REMARK_WIDTH'     : 300,
        'PARAMETER_MOCK_WIDTH'       : 250,
		'PARAMETER_OPERATE_WIDTH'    : 300,
        "KEYPRESS_EVENT_NAME"        : (baidu.browser.firefox ? "keypress" : "keydown")
	};
	
	param.getReqParam = function(){
		return _reqParam;
	};
	
	param.getRetParam = function(){
		return _retParam;
	};
	
	param.getIsReqParamChange = function(){
		return isReqParamChange;
	}
	
	param.getIsRetParamChange = function(){
		return isRetParamChange;
	}
	
	param.setIsParamChange = function(){
		isReqParamChange = false;
		isRetParamChange = false;
	}
	
	param.init = function(reqParamObj, retParamObj, editMode){
		_productId = $('#productId').val().trim();
		_isEditMode = editMode;
		
		_reqParam = [];
		if(reqParamObj){
			_reqParam = reqParamObj;
		}
		generateUniqueIdForParams(_reqParam, true);
		
		_retParam = [];
		if(retParamObj){
			_retParam = retParamObj;
		}
		generateUniqueIdForParams(_retParam, false);
		
		bind();
	};
	/**
	 * set editMode
	 */
	param.setEditMode = function(editMode){
		_isEditMode = editMode;
		bind();
	};
	
	/**
     * add parameter
     */
    param.addParam = function(type, parentParamId) {
        var newParamId = -1;
        if (type == 'child') {
            newParamId = addChildParameter(parentParamId);
        } else if (type == 'request') {
            newParamId = addRequestParameter();
        } else if (type =='response') {
            newParamId = addResponseParameter();
        } else {
            throw new Error('unkown type: ' + type + ' in param.addParam(type, parentParamId);');
        }
        // finish edit before refresh
        this.finishEdit();
        bind();
        if (newParamId > 0) {
            this.edit(newParamId, 'param-name');
        }
    };
    
    /**
     * import parameter
     */
    param.importParam = function(type, dataType, parentParamId){
    	var jsonExample = '{\n' +
    		'	\"userId\": \"1\",\n' +
    		'	\"userName\": \"张三\",\n' +
    		'	\"userEmail\": \"zhangsan@163.com\",\n' +
    		'	\"userAddress\": {\n' +
    		'		\"province\": \"北京\",\n' +
    		'		\"city\": \"北京\",\n' +
    		'		\"area\": \"海淀区\",\n' +
    		'		\"road\": {\n' +
    		'			\"roadName\": \"西土城路\",\n' +
    		'			\"roadNo\": \"10\"\n' +
    		'		}\n' +
    		'	},\n' +
    		'	\"cars\":[\n' +
    		'		{\"name\":\"宝马\",\"type\":\"X5\"},\n' +
    		'		{\"name\":\"奥迪\",\"type\":\"Q5\"}\n' +
    		'	]\n' +
    		'}\n';
    	var xmlExample = '<xml>\n' +
    		'	<userId>1</userId>\n' +
    		'	<userName>张三</userName>\n' +
    		'	<userEmail>zhangsan@163.com</userEmail>\n' +
    		'	<userAddress>\n' +
    		'		<province>北京</province>\n' +
    		'		<city>北京</city>\n' +
    		'		<area>北京</area>\n' +
    		'		<road>\n' +
    		'			<roadName>西土城路</roadName>\n' +
    		'			<roadNo>10</roadNo>\n' +
    		'		</road>\n' +
    		'	</userAddress>\n' +
    		'	<cars>\n' +
    		'		<car>\n' +
    		'			<name>宝马</name>\n' +
    		'			<type>X5</type>\n' +
    		'		</car>\n' +
    		'		<car>\n' +
    		'			<name>奥迪</name>\n' +
    		'			<type>Q5</type>\n' +
    		'		</car>\n' +
    		'	</cars>\n' +
    		'</xml>';
    	var example = jsonExample;
    	if(dataType == "xml")
    		example = xmlExample;
    	bootbox.dialog({
			title: '导入'+dataType+'数据',
			message: '<div class="row" id="jsonDataForm">' +
						'<div class="form col-sm-12">' +
							'<div class="form-group">' +
								'<h4><strong>'+dataType+'数据样例</strong>' +
								'</h4>' +
								'<textarea id="dataTxtExample" class="form-control" style="height:250px;">' +
									example +
								'</textarea>' +
							'</div>' +
							'<div class="form-group">' +
								'<h4><strong>'+dataType+'数据</strong>' +
								'</h4>' +
								'<textarea id="dataTxt" class="form-control" style="height:250px;">' +
							    '</textarea>' +
							'</div>' +
							'<span id="jsonError" style="display:none;color:red"></span>' +
						'</div>' +
					'</div>',
			buttons:{
				Modify:{
					label: '确定',
					className: 'btn-primary',
					callback: function(){
						var dataStr = $("#dataTxt").val();
						if(dataType == "json"){
							try{
								var jsonData = $.parseJSON(dataStr);
							}catch(e){
								//alert(e.message);
								$("#jsonError").text("json格式书写错误：" + e.message);
								$("#jsonError").show();
								return false;
							}
							if(type == "request"){
								importRequestParameter(jsonData);
							}else if(type == "response"){
								importResponseParameter(jsonData);
							}else{
					            throw new Error('unkown type: ' + type + ' in param.importParam(type, dataType, parentParamId);');
					        }
							param.finishEdit();
					        bind();
						}else{
							throw new Error('unkown dataType: ' + dataType + ' in param.importParam(type, dataType, parentParamId);');
						}
					}
				},
				cancel:{
					label: '关闭',
					className: 'btn-default'
				}
			},
			callback: function(){
				setTimeout(function(){
					$($('.modal-dialog')[0]).css({'width': '800px'});
				}, 200);
			}
		});
    };
    
    /**
     * remove parameter
     */
    param.removeParam = function(paramId) {
    	isParamChange(paramId);
        removeParameter(paramId);
        putObjectIntoDeletedPool("Parameter", paramId);
        bind();
    };
	
	/**
     * show operation column of parameter table (action table)
     */
	param.showOpColumn = function(pId){
		$('#div-param-op-' + pId).css({'display': ''});
	};
	
	/**
     * hide operation column of parameter table (action table)
     */
	param.hideOpColumn = function(pId){
		$('#div-param-op-' + pId).css({'display': 'none'});
	};
	
	/**
     * complex parameter shrink
     */
	param.paramShrink = function(uniqueId) {
		hideAllChildParameters(getParameter(uniqueId));
		var divExpander = $('#div-param-expander-' + uniqueId)[0];
        if (divExpander) {
            divExpander.onclick = function() {param.paramExpand(uniqueId);};
            $('#div-param-expander-' + uniqueId + ' i').first().removeClass('fa-minus');
            $('#div-param-expander-' + uniqueId + ' i').first().addClass('fa-plus');
        }
	};
	
	param.paramExpand = function(uniqueId){
		showAllChildParameters(getParameter(uniqueId));
        var divExpander = $('#div-param-expander-' + uniqueId)[0];
        if (divExpander) {
            divExpander.onclick = function() {param.paramShrink(uniqueId); return false;};
            $('#div-param-expander-' + uniqueId + ' i').first().removeClass('fa-plus');
            $('#div-param-expander-' + uniqueId + ' i').first().addClass('fa-minus');
        }
	};
	
	param.edit = function(id, key){
		var el;
		var str = '';
		var oldValue;
		var width = 100;
		
		if((!_isEditMode && key != 'mock') || key == 'op'){
			return;
		}
		
		if (isEditing()) {
            if (_editContext.id == id && _editContext.key == key) {
                return ;
            } else {
                this.finishEdit();
            }
        }
		
		var param = getParameter(id);
		
		switch (key) {
	        case 'param-name':
	            width = CONFIG.PARAMETER_NAME_WIDTH;
	            el = getTd(id, key);
	            oldValue = b.trim(param.paramName);
	            str += getEditInputHtml(id, oldValue, width, CONFIG.DEFAULT_MAX_LENGTH);
	            break;
	        case 'param-desc':
	            width = CONFIG.PARAMETER_DESC_WIDTH;
	            el = getTd(id, key);
	            oldValue = b.trim(param.paramDesc);
	            str += getEditTextAreaHtml(id, oldValue, width, CONFIG.REMARK_MAX_LENGTH);
	            break;
	        case 'remark':
	        	width = CONFIG.PARAMETER_REMARK_WIDTH;
	            el = getTd(id, key);
	            oldValue = b.trim(param.remark);
	            str += getEditTextAreaHtml(id, oldValue, width, CONFIG.REMARK_MAX_LENGTH);
	            break;
	        case 'mock':
	            width = CONFIG.PARAMETER_MOCK_WIDTH;
	            el = getTd(id, key);
	            // oldValue = b.trim(el.innerHTML)
	            oldValue = b.trim(param.mock);
	            str += getEditTextAreaHtml(id, oldValue, width, CONFIG.REMARK_MAX_LENGTH);
	            break;
	        /*case 'operate':
	        	width = CONFIG.PARAMETER_OPERATE_WIDTH;
	        	el = getTd(id, key);
	        	str += getOperationHtml(id, '');*/
	        default:
	            throw Error('not implemented, key:' + key);
		}
				
		// save edit state
		editing(id, oldValue, key);
		
		el.innerHTML = str;
		
		focusElement($('#' + ELEMENT_ID.EDIT_INPUT)[0]);
	};
	
	/**
     * invoked when edit is ready to be finished
     */
	param.finishEdit = function(){
		if (!isEditing()){
			return;
		}
        var inputDiv = $('#' + ELEMENT_ID.EDIT_INPUT);
        if (!inputDiv){
        	return;
        }
        
        var val = inputDiv.val().trim();
        var oldval = inputDiv.attr("oldValue").trim();
        if(!(val === oldval)){
        	var paramId = inputDiv.attr("paramId");
        	isParamChange(paramId);
        }
        
        var editContext = stopEditing();
        var newValue = $.trim(inputDiv.val());
        var el;
        var property = null;
        
        switch (editContext.key) {
	        case "param-name":
	        	property = 'paramName';
	        	break;
	        case 'param-desc':
	        	property = 'paramDesc';
	        	break;
	        case 'remark':
	        	property = 'remark';
	        	break;
	        case 'mock':
	        	property = 'mock';
	            break;
	        default:
	            throw Error("not implemented finish edit for key:" + editContext.key);
	    }
        
        el = getTd(editContext.id, editContext.key);
        
        if(!_isEditMode && editContext.key == 'mock'){ // 对于处于非编辑状态的mock字段，如果内容发生改变，保存该字段
        	var oldMockValue = getParameter(editContext.id).mock;
        	if(oldMockValue != newValue){
        		var data  = {};
        		data.paramId = getParameter(editContext.id).paramId;
        		data.mock = newValue;
        		$.ajax({
        			url: '/idoc/inter/modifyMockContent.html',
        			type: 'POST',
        			dataType: 'json',
        			data: data,
        			success: function(json){
        				if(json.retCode == 200){
        					$().toastmessage('showToast', {
        					    text     : '保存成功!',
        					    sticky   :  false,
        					    position : 'top-right', //middle-center
        					    type     : 'success',
        					    stayTime:   1000
        					});
        				}else{
        					$().toastmessage('showToast', {
        					    text     : '保存失败!',
        					    sticky   :  true,
        					    position : 'top-right',
        					    type     : 'error',
        					    stayTime:   1500
        					});
        				}
        			}
        		});
        	}
        }
        
        setParameter(editContext.id, newValue, property);
        
        var idReturn = editContext.id;
        $(el).text(newValue);
        
        if (isEditing()) {
            stopEditing();
        }
        return idReturn;
	};
	
	$(document).keydown(function(e){ 
		// 复制一个参数
		if(e.ctrlKey && e.shiftKey && e.keyCode == 90){ // 67 ctrl + shift + c  90 ctrl + shift + c
	    	if(window.getSelection() && window.getSelection().focusNode){
	    		var id = window.getSelection().focusNode.parentElement.id;
	    		if(window.getSelection().focusNode.nodeType == 1){ // 选中的本身是个元素，对应编辑状态时，鼠标选中的是元素本身
	    			id = window.getSelection().focusNode.id;
	    		}
	    		if(id.indexOf("td-param-param-name-") != -1){
	    			_copyParamHashSet = {};
	    			var copyParam = getParameter(id.substring(20));
	    			if(copyParam != null){
	    				var _copy = b.object.clone(copyParam);
	    				var copy = JSON.stringify(_copy);
	    				localStorage.clear();
	    				localStorage.setItem(Math.uuidFast(),copy);
	    				_copyParamHashSet[copyParam.paramId] = copyParam;
//	    				alert(_copyParamList.length + '  ' + copyParam.paramId + '  ' + copyParam.paramName);
	    				$().toastmessage('showToast', {
    					    text     : '复制成功!',
    					    sticky   :  false,
    					    position : 'top-right',
    					    type     : 'success',
    					    stayTime :  1000
    					});
	    			}
	    		}
//	    		alert(window.getSelection().focusNode.parentElement.outerHTML);
	    	}
	    }
		
		// 复制多个参数
		if(e.altKey && e.keyCode == 67){ // alt + c
	    	if(window.getSelection() && window.getSelection().focusNode){
	    		var id = window.getSelection().focusNode.parentElement.id;
	    		if(window.getSelection().focusNode.nodeType == 1){ // 选中的本身是个元素
	    			id = window.getSelection().focusNode.id;
	    		}
	    		if(id && id.indexOf("td-param-param-name-") != -1){
	    			var copyParam = getParameter(id.substring(20));
	    			if(copyParam != null && !(copyParam.paramId in _copyParamHashSet)){
	    				var _copy = b.object.clone(copyParam);
	    				var copy = JSON.stringify(_copy);
	    				localStorage.setItem(Math.uuidFast(),copy);
	    				_copyParamHashSet[copyParam.paramId] = copyParam;
//	    				alert(_copyParamList.length + '  ' + copyParam.paramId + '  ' + copyParam.paramName);
	    				$().toastmessage('showToast', {
    					    text     : '复制成功，共复制' + localStorage.length +'个参数!',
    					    sticky   :  false,
    					    position : 'top-right',
    					    type     : 'success',
    					    stayTime:   1000
    					});
	    			}
	    		}
	    	}
	    }
		
		// 拷贝复制的参数
		if(e.ctrlKey && e.shiftKey && e.keyCode == 86 && _isEditMode){ // ctrl + shift + v
	    	if(window.getSelection() && window.getSelection().focusNode){
	    		var id = window.getSelection().focusNode.id;
	    		if(id && id.indexOf("td-param-param-name-") != -1){
	    			var paramType = window.getSelection().focusNode.parentElement.parentElement.parentElement.parentElement.id;
	    			var len = localStorage.length;
	    			_copyParamList = [];
	    			for(var i = 0; i < len; i++){
	    				var _copy = JSON.parse(localStorage.getItem(localStorage.key(i)));
	    				_copyParamList.push(_copy);
	    			}
	    			if(_copyParamList.length <= 0){
	    				$().toastmessage('showToast', {
    					    text     : '没有选择需要复制的参数!',
    					    sticky   :  false,
    					    position : 'top-right',
    					    type     : 'warning',
    					    stayTime:   1500
    					});
    					return;
	    			}
	    			var parentElement = window.getSelection().focusNode.parentElement;
	    			var paramOrDict = parentElement.className;
	    			if(paramOrDict.indexOf("tr-param  param-level-") != -1 && Number(paramOrDict.substring(22)) > 0){
	    				var level = Number(paramOrDict.substring(22)) - 1;
	    				var sibling = parentElement.previousSibling;
	    				var root;
	    				while(sibling && sibling.className.indexOf("tr-param  param-level-") != -1){
	    					if(Number(sibling.className.substring(22)) == level){
	    						root = sibling;
	    						break;
	    					}
	    					sibling = sibling.previousSibling;
	    				}
	    				if(root){
		    				var parentId = root.id;
		    				if(parentId != null){
		    					var param = getParameter(parentId.substring(9));
		    					if(param && param.dictId != null){
		    			            if(!param.dict){
		    			            	param.dict = newDict();
		    			            }
		    			            for(var i = 0; i < _copyParamList.length; i++){
		    			            	var copy_param = _copyParamList[i];
		    		    				if(copy_param.dictId != null && copy_param.dict != null && (copy_param.paramType == "object" || copy_param.paramType == "array<object>")){ // 重新保存一份匿名字典参数
		    		    	        		$.ajax({
		    		    	        			url: '/idoc/inter/saveObjectDict.html',
		    		    	        			type: 'POST',
		    		    	        			dataType: 'json',
		    		    	        			contentType: 'application/json',
		    		    	        			data: JSON.stringify(copy_param.dict),
		    		    	        			async: false,
		    		    	        			success: function(json){
		    		    	        				if(json.retCode == 200){
		    		    	        					var dict = json.dict;
		    		    	        					if(dict){
		    		    	        						copy_param.dict = dict;
		    		    	        						copy_param.dictId = dict.dictId;
		    		    	        					}
		    		    	        				}else{
		    		    	        					$().toastmessage('showToast', {
		    		    	        					    text     : '保存Object字典参数时失败!',
		    		    	        					    sticky   :  true,
		    		    	        					    position : 'top-right',
		    		    	        					    type     : 'error',
		    		    	        					    stayTime:   1500
		    		    	        					});
		    		    	        					return;
		    		    	        				}
		    		    	        			}
		    		    	        		});
		    		    				}else if(copy_param.dictId != null && copy_param.dict == null){ // 复制过来的字典没有实体，查询并绑定
		    		    					var data  = {};
		    		    	        		data.dictId = copy_param.dictId;
		    		    	        		$.ajax({
		    		    	        			url: '/idoc/inter/searchParamDict.html',
		    		    	        			type: 'POST',
		    		    	        			dataType: 'json',
		    		    	        			data: data,
		    		    	        			async: false,
		    		    	        			success: function(json){
		    		    	        				if(json.retCode == 200){
		    		    	        					copy_param.dict = json.dict;
		    		    	        				}else{
		    		    	        					$().toastmessage('showToast', {
		    		    	        					    text     : '复制参数时，查询字典失败!',
		    		    	        					    sticky   :  true,
		    		    	        					    position : 'top-right',
		    		    	        					    type     : 'error',
		    		    	        					    stayTime:   1500
		    		    	        					});
		    		    	        					return;
		    		    	        				}
		    		    	        			}
		    		    	        		});
		    		    				}
		    			            	
		    		    				copy_param.paramId = null;
		    		    				generateUniqId(copy_param);
		    		    				param.dict.params.push(copy_param);
		    			            	//param.dict.params.push(_copyParamList[i]);
		    			            }
		    					}
		    				}else{
		    					$().toastmessage('showToast', {
		    					    text     : '没找到该节点的父节点，不能粘贴到该节点下面!',
		    					    sticky   :  false,
		    					    position : 'top-right',
		    					    type     : 'warning',
		    					    stayTime:   1500
		    					});
		    					return;
		    				}
	    				}else{
	    					$().toastmessage('showToast', {
	    					    text     : '不能在此处粘贴参数!',
	    					    sticky   :  false,
	    					    position : 'top-right',
	    					    type     : 'warning',
	    					    stayTime:   1500
	    					});
	    					return;
	    				}
	    			}else{
		    			for(var i = 0; i < _copyParamList.length; i++){
		    				var copy_param = _copyParamList[i];
		    				if(copy_param.dictId != null && copy_param.dict != null && (copy_param.paramType == "object" || copy_param.paramType == "array<object>")){ // 重新保存一份匿名字典参数
		    	        		$.ajax({
		    	        			url: '/idoc/inter/saveObjectDict.html',
		    	        			type: 'POST',
		    	        			dataType: 'json',
		    	        			contentType: 'application/json',
		    	        			data: JSON.stringify(copy_param.dict),
		    	        			async: false,
		    	        			success: function(json){
		    	        				if(json.retCode == 200){
		    	        					var dict = json.dict;
		    	        					if(dict){
		    	        						copy_param.dict = dict;
		    	        						copy_param.dictId = dict.dictId;
		    	        					}
		    	        				}else{
		    	        					$().toastmessage('showToast', {
		    	        					    text     : '保存Object字典参数时失败!',
		    	        					    sticky   :  true,
		    	        					    position : 'top-right',
		    	        					    type     : 'error',
		    	        					    stayTime:   1500
		    	        					});
		    	        					return;
		    	        				}
		    	        			}
		    	        		});
		    				}else if(copy_param.dictId != null && copy_param.dict == null){ // 复制过来的字典没有实体，查询并绑定
		    					var data  = {};
		    	        		data.dictId = copy_param.dictId;
		    	        		$.ajax({
		    	        			url: '/idoc/inter/searchParamDict.html',
		    	        			type: 'POST',
		    	        			dataType: 'json',
		    	        			data: data,
		    	        			async: false,
		    	        			success: function(json){
		    	        				if(json.retCode == 200){
		    	        					copy_param.dict = json.dict;
		    	        				}else{
		    	        					$().toastmessage('showToast', {
		    	        					    text     : '复制参数时，查询字典失败!',
		    	        					    sticky   :  true,
		    	        					    position : 'top-right',
		    	        					    type     : 'error',
		    	        					    stayTime:   1500
		    	        					});
		    	        					return;
		    	        				}
		    	        			}
		    	        		});
		    				}
		    				copy_param.paramId = null;
		    				generateUniqId(copy_param);
		    				if(paramType && paramType == 'reqParamsTb'){
		    					_reqParam.push(copy_param);
		    				}else if(paramType && paramType == 'retParamsTb'){
		    					_retParam.push(copy_param);
		    				}else{
		    					$().toastmessage('showToast', {
	        					    text     : '复制参数失败，请联系管理员!',
	        					    sticky   :  false,
	        					    position : 'top-right',
	        					    type     : 'error',
	        					    stayTime:   1500
	        					});
		    					return;
		    				}
		    			}
	    			}
	    			bind();
	    		}
	    	}
	    }
	});
	
	param.requiredSelectChanged = function(parameterId, value) {
        isParamChange(parameterId);
        setParameter(parameterId, value, 'isNecessary');
	}
	
	param.dataTypeSelectChanged = function(parameterId, value) {
        isParamChange(parameterId);
        setParameter(parameterId, value, 'paramType');
        if (value == 'object' || value == 'array<object>') {
            bind();
        }else if(value == '自定义' || value == 'array<自定义>'){
        	var data = {};
        	data.productId = _productId;
        	$.ajax({
        		url: '/dict/queryDictsByProduct.html',
        		type: 'POST',
        		dataType: 'json',
        		data: data,
        		success: function(json){
        			if(json.retCode == 200){
        				if(json.dictList){
        					var dicts = json.dictList;
        					var options = '';
        					for(var j = 0; j < dicts.length; j++){
        						var dict = dicts[j];
        						options += '<option value="' + dict.dictId + '">' + dict.dictName + '</option>';
        					}
        					var msg = '<div class="row form-horizontal">' +
											'<div class="form-group">' +
												'<label class="col-lg-2 control-label">字典名称</label>' +
												'<div class="col-lg-9">' +
											    	'<select id="dictList" class="form-control">' +
											    		options +
											    	'</select>' +
											    '</div>' +
											'</div>' +
										'</div>';
        					
        					bootbox.dialog({
        		        		title: '选择字典',
        		        		message: msg,
        		        		buttons:{
        		        			createDict:{
            							label: '创建字典',
            							className: 'btn-success',
            							callback: function(){
            								window.open('/dict/editDict.html?productId=' + $('#productId').val());
            								return false;
            							}
            						},
        		        			ok:{
        		        				label: '确定',
        		        				className: 'btn-primary',
        		    					callback: function() {
        		    						var dictId = $('#dictList').val();
        		    						var data = {};
        		    						data.dictId = dictId;
        		    						$.ajax({
        		    							url: '/dict/edit.html',
        		    							type: 'POST',
        		    							dataType: 'json',
        		    							data: data,
        		    							success: function(json){
        		    								if(json.retCode == 200){
        		    									var dict = json.dict;
        		    									generateUniqueIdForParams(dict.params, true);
        		    									if(value == '自定义'){
        		    										setParameter(parameterId, dict.dictName, 'paramType');
        		    									}else{
        		    										setParameter(parameterId, 'array<' + dict.dictName + '>', 'paramType');
        		    									}
        	        		    						setParameter(parameterId, dict, 'dict');
        	        		    						setParameter(parameterId, dict.dictId, 'dictId');
        	        		    						bind();
        		    								}else{
        		    									bootbox.alert({message: '获取字典详细信息出错。',
        		    		            					buttons:{
        		    		            						ok:{
        		    		            							label: '确定'
        		    		            						}
        		    		            					}
        		    		            				});
        		    								}
        		    								
        		    							}
        		    						});
        		    						
        		    					}
        		        			},
        		        			cancel:{
        		        				label: '取消',
        		        				className: 'btn-warning',
        		        				callback: function(){
        		        					$('#select-dataType-' + parameterId).val('');
        		        				}
        		        			}
        		        		}
        		        	});
        				}else{
        					bootbox.dialog({message: '当前产品没有相关的字典，请先创建字典。',
            					buttons:{
            						ok:{
            							label: '确定',
            							className: 'btn-primary',
            							callback: function(){
            								$('#select-dataType-' + parameterId).val('');
            							}
            						},
            						createDict:{
            							label: '创建字典',
            							className: 'btn-success',
            							callback: function(){
            								window.open('/dict/editDict.html?productId=' + $('#productId').val());
            							}
            						}
            					}
            				});
        				}
        			}else{
        				bootbox.alert({message: '查询字典列表失败。',
        					buttons:{
        						ok:{
        							label: '确定'
        						}
        					}
        				});
        			}
        		}
        	});
        }else{
        	setParameter(parameterId, null, 'dict');
        	setParameter(parameterId, null, 'dictId');
        	bind();
        }
        
    };
	
	function bind(){
		_editContext = null;
		var reqStr = '';
		var retStr = '';
		var reqDiv = $('#reqParamsTb');
		var retDiv = $('#retParamsTb');
		var reqParamLength = _reqParam ? _reqParam.length : 0;
		var retParamLength = _retParam ? _retParam.length : 0;
		
//		sortParams(_reqParam);
//		sortParams(_retParam);
		if(_isEditMode){
			reqStr += TEMPLATE.REQUEST_BEGIN_EDIT;
		}else{
			reqStr += TEMPLATE.REQUEST_BEGIN;
		}
		
		for(var i = 0; i < reqParamLength; i++){
			var p = _reqParam[i];
			reqStr += getPTRHtml(false, p);
		}
		
		reqStr += TEMPLATE.REQUEST_END;
		if(_isEditMode){
			reqStr += TEMPLATE.REQUEST_PARAMETER_ADD_BUTTON;
			reqStr += TEMPLATE.REQUEST_PARAMETER_JSON_BUTTON;
			//reqStr += TEMPLATE.REQUEST_PARAMETER_XML_BUTTON;
		}
		
		if(_isEditMode){
			retStr += TEMPLATE.RESPONSE_BEGIN_EDIT;
		}else{
			retStr += TEMPLATE.RESPONSE_BEGIN;
		}
		

		for(var i = 0; i < retParamLength; i++){
			var p = _retParam[i];
			retStr += getPTRHtml(true, p);
		}
		retStr += TEMPLATE.RESPONSE_END;
		if(_isEditMode){
			retStr += TEMPLATE.RESPONSE_PARAMETER_ADD_BUTTON;
			retStr += TEMPLATE.RESPONSE_PARAMETER_JSON_BUTTON;
			//retStr += TEMPLATE.RESPONSE_PARAMETER_XML_BUTTON;
		}
		
		reqDiv.html(reqStr);
		retDiv.html(retStr);
	}
	
	function generateUniqueIdForParams(paramList, isRequest){
		if(!paramList) {
			return;
		}
		
		for(var i = 0; i < paramList.length; i++){
			var p = paramList[i];
			p.uniqueId = generateId();
			if(p.dict){
				generateUniqueIdForParams(p.dict.params);
				if(!isRequest){
					setShowParameter(p.dict);
				}
			}
		}
	}
	/**
	 * 设置字典参数是否显示属性
	 */
	function setShowParameter(dict){
		var interfaceId = $("#interId").val();
		var dictId = dict.dictId;
		var data = {};
		data.interfaceId = interfaceId;
		data.dictId = dictId;
		$.ajax({
			url: '/idoc/inter/queryDictParams.html',
			type: 'POST',
			contentType: "application/json",
			dataType: 'json',
			data: JSON.stringify(data),
			async: false,
			success:function(json){
				if(json.retCode = 200){
					var list = json.retDesc;
					var len = list.length;
					for(var i=0; i<len; i++){
						var dictParamObj = list[i];
						var status = dictParamObj.status;
						var paramId = dictParamObj.paramId;
						for(var j=0; j<dict.params.length; j++){
							var param = dict.params[j];
							if(param.paramId == paramId){
								param.isShow = status;
							}
						}
					}
				}
			}
		});
	}
	/**
     * get parameter tr html
     */
    function getPTRHtml(isRquest, param, level, dictId) {
        var typeList = [
                        'int',
                        'long',
                        'float',
                        'double',
                        'bigdecimal',
                        'short',
                        'byte',
                        'char',
                        'string',
                        'object',
                        'boolean',
                        'date',
                        'array<int>',
                        'array<long>',
                        'array<float>',
                        'array<double>',
                        'array<short>',
                        'array<byte>',
                        'array<char>',
                        'array<string>',
                        'array<object>',
                        'array<boolean>',
                        'array<date>'
                    ];
    	var str = '';
    	var parameterList;
    	var pDictId;
        if(param.dict){
        	parameterList = param.dict.params;
        	pDictId = param.dict.dictId;
        }

        var parameterListNum = parameterList && parameterList.length ? parameterList.length : 0;
        var level = level || 0;
        
        str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
        		'id="tr-param-' + param.uniqueId + '" onmouseover="param.showOpColumn(' + param.uniqueId + ');" onmouseout="param.hideOpColumn(' + param.uniqueId + ');" >';
        
        //expander column
        str += '<td class="expander">';
        if(parameterListNum > 0){
        	str += '<div class="pull-right" id="div-param-expander-' + param.uniqueId + '" onclick="param.paramShrink(' + param.uniqueId + ');return false;"><i class="fa fa-minus"></i></div>';
        }
        str += '</td>';
        
        if(_isEditMode){
        	//op column
        	str += getPTDHtml(param.uniqueId,
        			'<div id="div-param-op-' + param.uniqueId +'" style="display:none;">' +
        			'<a href="#" onclick="param.removeParam(' + param.uniqueId + ');return false;"><i class="glyphicon glyphicon-remove"></i></a>' +
        			(parameterListNum > 0 || param.paramType == "object" || param.paramType == "array<object>" ?
        					'<a style="margin-left:5px;color:#47a947;" href="#" onclick="param.addParam(\'child\', ' + param.uniqueId + ');return false;"><i class="glyphicon glyphicon-plus"></i></a>' : '') + '</div>', 'op');
        }
        //paramName column
        str += getPTDHtml(param.uniqueId, param.paramName, 'param-name', level);
        
        //paramDesc column
        str += getPTDHtml(param.uniqueId, param.paramDesc, "param-desc");
        
        //paramType column
        str += getDataTypeEditSelectHtml(param.uniqueId, param.paramType);
        
        //required column
        str += getRequiredEditSelectHtml(param.uniqueId, param.isNecessary);
        
        //remark column
        str += getPTDHtml(param.uniqueId, param.remark, 'remark');
        
        //mock column
        str += getPTDHtml(param.uniqueId, param.mock, 'mock');
        
        //operation column
        var pType = param.paramType;
        if(isRquest && level>0 && $.inArray(pType, typeList) != -1){
        	str += getOperationHtml(param.uniqueId, param.paramId, dictId, param.isShow);
        }else{
        	str += "<td></td>";
        }
        for (var i = 0; i < parameterListNum; i++) {
            str += getPTRHtml(isRquest, parameterList[i], level + 1, pDictId);
        }
        
        return str;
    }
	
    function getPTDHtml(id, value, type, level) {
    	if(!value){
    		return '<td id="td-param-' + type + '-' + id + '" class="td-param ' + type + '" onclick="param.edit(' + id + ', \'' + type + '\'' + ');" >'
	    	+'</td>';
    	}
    	var str = '<td id="td-param-' + type + '-' + id + '" class="td-param ' + type + '" onclick="param.edit(' + id + ', \'' + type + '\'' + ');" >' +
					(level ? new Array(level + 1).join('&nbsp;&nbsp;') : ' ') + value +
		    	'</td>';
    	
    	return str;
    }
    //添加隐藏或显示操作按钮,0是不显示，1是显示
    function getOperationHtml(id, paramId, dictId, isShow){
		if(isShow==0){
			return '<td align="center" id="td-param-operate-' + id + '">'
				+'<button data-toggle="tooltip" title="点击显示/隐藏按钮设置返回结果中该字段的显示状态，显示为显示该字段，隐藏为不显示该字段" class="btn btn-success btn-default btn-xs" onClick="showDictParam(\''+paramId+'\', \''+dictId+'\');">隐藏</button>'
				+'</td>';
		}else{
			return '<td align="center" id="td-param-operate-' + id + '">'
				+' <button data-toggle="tooltip" title="点击显示/隐藏按钮设置返回结果中该字段的显示状态，显示为显示该字段，隐藏为不显示该字段" class="btn btn-primary btn-default btn-xs" onClick="hideDictParam(\''+paramId+'\', \''+dictId+'\');">显示</button>'
				+'</td>';
		}
    }
    /**
     * get parameter data type edit select html
     */
    function getDataTypeEditSelectHtml(id, type) {
        var str = '';
        var typeList = [
                    '',
                    'int',
                    'long',
                    'float',
                    'double',
                    'bigdecimal',
                    'short',
                    'byte',
                    'char',
                    'string',
                    'date',
                    'object',
                    'boolean',
                    'array<int>',
                    'array<long>',
                    'array<float>',
                    'array<double>',
                    'array<short>',
                    'array<byte>',
                    'array<char>',
                    'array<string>',
                    'array<object>',
                    'array<boolean>',
                    'array<date>',
                    '自定义',
                    'array<自定义>'
                ];
         var typeListNum = typeList.length;

        str += '<td id="td-param-dataType-' + id + '" class="td-param">';
        if (_isEditMode) {
        	str += '<select class="form-control" id="select-dataType-' + id + '" onchange="param.dataTypeSelectChanged(' + id + ', this.value);">';
        	for (var i = 0; i < typeListNum; i++) {
        		var item = typeList[i];
        		str += '<option value="'+ item +'"' + (item == type ? ' selected="true"' : '') + '>' + b.encodeHTML(item) + '</option>';
        	}
        	if(typeList.indexOf(type) == -1){
        		str += '<option value="'+ type +'" selected="true">' + b.encodeHTML(type) + '</option>';
        	}
        	str += '</select>';
        }else{
        	str += b.encodeHTML(type)
        }
        str += '</td>';
        
        return str;
    }
    
    function getRequiredEditSelectHtml(id, isRequired) {
        var str = '',
            typeList = [
                    '否',
                    '是'
                ],
            typeListNum = typeList.length;

        str += '<td id="td-param-required-' + id + '" class="td-param">';
        if (_isEditMode) {
        	str += '<select class="form-control" id="select-required-' + id + '" onchange="param.requiredSelectChanged(' + id + ', this.value)">';
        	for (var i = 0; i < typeListNum; i++) {
        		var item = typeList[i];
        		str += '<option value="'+ i +'"' + (i == isRequired ? ' selected="true"' : '') + '>' + item + '</option>';
        	}
        	
        	str += '</select>';
        }else{
        	str += typeList[isRequired];
        }
        str += '</td>';
        
        return str;
    }
    
    var _generateCounter = 1;
    /**
     * id generator
     * [todo] generate unique id, this algorithm may have problem.
     */
    function generateId() {
        return (new Date()).getTime() - 1283136075795 + (_generateCounter++);
    }
    
    /**
     * get parameter
     */
    function getParameter(id) {
        var parameter;
        var recursivelyFoundParameter;
        
        var requestParameterList = _reqParam;
        var requestParameterListCount = requestParameterList ? requestParameterList.length : 0;
        var responseParameterList = _retParam;
        var responseParameterListCount = responseParameterList ? responseParameterList.length : 0;
        
        for (l = 0; l < requestParameterListCount; l++) {
        	parameter = requestParameterList[l];
        	if (parameter.uniqueId == id) return parameter;
        	recursivelyFoundParameter = getParameterRecursively(parameter, id);
        	if (recursivelyFoundParameter) {
        		return recursivelyFoundParameter;
        	}
        	
        }
        
        for (l = 0; l < responseParameterListCount; l++) {
        	parameter = responseParameterList[l];
        	if (parameter.uniqueId == id) return parameter;
        	recursivelyFoundParameter = getParameterRecursively(parameter, id);
        	if (recursivelyFoundParameter) {
        		return recursivelyFoundParameter;
        	}
        }
        return null;
    };
    
    /**
     * set parameter
     */
    function setParameter(id, value, property) {
        var p = getParameter(id);
        if (p === null) return;
        p[property] = value;
    };
    
    /**
     * add child parameter of complex parameter
     */
    function addChildParameter(parentParamUniqueId) {
        var p = getParameter(parentParamUniqueId);
        if (p !== null) {
            var pNew = newParameter();
            if(!p.dict){
            	p.dict = newDict();
            }
            p.dict.params.push(pNew);
            return pNew.uniqueId;
        }
        return -1;
    };
    
    /**
     * add request parameter
     */
    function addRequestParameter() {
        var pNew = newParameter();
        _reqParam.push(pNew);
        return pNew.uniqueId;
    };
    
    /**
     * import request parameter
     */
    function importRequestParameter(data){
    	for(var key in data){
    		var pNew = newParameter();
    		pNew.paramName = key;
    		var value = data[key];
    		//alert(value);
    		var paramtype = typeof value;
    		if(paramtype == "number")
    			paramtype = "long";
    		pNew.paramType = paramtype;
    		//alert(paramtype);
    		if(paramtype == "object"){
    			if(value.length){
    				pNew.paramType = "array<object>";
    				value = value[0];
    			}
    			pNew.dict = getDict(value);
    		}
    		_reqParam.push(pNew);
    	}
    }
    
    function getDict(data){
    	var pDict = newDict();
    	for(var key in data){
    		var param = newParameter();
    		param.paramName = key;
    		var value = data[key];
    		var paramtype = typeof value;
    		if(paramtype == "number")
    			paramtype = "long";
    		param.paramType = paramtype;
    		if(paramtype == "object"){
    			param.dict = getDict(value);
    		}
    		pDict.params.push(param);
    	}
    	return pDict;
    };

    /**
     * add response parameter
     */
    function addResponseParameter() {
        var pNew = newParameter();
        _retParam.push(pNew);
        return pNew.uniqueId;
    };
    
    /**
     * import response parameter
     */
    function importResponseParameter(data){
    	for(var key in data){
    		var pNew = newParameter();
    		pNew.paramName = key;
    		var value = data[key];
    		//alert(value);
    		var paramtype = typeof value;
    		pNew.paramType = paramtype;
    		//alert(paramtype);
    		if(paramtype == "object"){
    			if(value.length){
    				pNew.paramType = "array<object>";
    				value = value[0];
    			}
    			pNew.dict = getDict(value);
    		}
    		_retParam.push(pNew);
    	}
    };

    function newParameter() {
        var obj = {};
        obj.uniqueId = generateId();
        obj.paramId;
        obj.paramName = '';
        obj.paramDesc = '';
        obj.paramType = '';
        obj.isNecessary = 1;
        obj.dictId;
        obj.remark = '';
        obj.mock = '';
        obj.dict = newDict();
        return obj;
    }
    
    function newDict(){
    	var obj = {};
    	obj.params = [];
    	obj.dictName = 'object';
    	obj.productId = _productId;
    	
    	return obj;
    }
    
    /**
     * get parameter recursively
     * return obj if exists,
     * otherwise return null
     */
    function getParameterRecursively(p, id) {
    	if(!p.dict){
    		return null;
    	}
        var parameterList = p.dict.params;
        var parameterListNum = parameterList && parameterList.length ? parameterList.length : 0;

        for (var i = 0; i < parameterListNum; i++) {
            var parameter = parameterList[i];
            if (parameter.uniqueId == id) {
                return parameter;
            }

            var recursivelyFoundParameter = getParameterRecursively(parameter, id);
            if (recursivelyFoundParameter) {
                return recursivelyFoundParameter;
            }
        }

        return null;
    }
    
    /**
     * remove parameter
     */
    function removeParameter(id) {
        var parameter;
        
        var requestParameterList = _reqParam;
        var requestParameterListCount = requestParameterList ? requestParameterList.length : 0;
        var responseParameterList = _retParam;
        var responseParameterListCount = responseParameterList ? responseParameterList.length : 0;
        
        for (var l = 0; l < requestParameterListCount; l++) {
        	parameter = requestParameterList[l];
        	if (parameter.uniqueId == id) {
        		requestParameterList.splice(l, 1);
        		return 0;
        	}
        	if (removeParameterRecursively(parameter, id) === 0) {
        		return 0;
        	}
        }
        
        for (var l = 0; l < responseParameterListCount; l++) {
        	parameter = responseParameterList[l];
        	if (parameter.uniqueId == id) {
        		responseParameterList.splice(l, 1);
        		return 0;
        	}
        	if (removeParameterRecursively(parameter, id) === 0) {
        		return 0;
        	}
        }
        
        return -1;
    };

    /**
     * remove parameter recursively
     * return 0 if remove successfully,
     * otherwise return -1
     */
    function removeParameterRecursively(p, id) {
    	if(!p.dict){
    		return -1;
    	}
        var parameterList = p.dict.params;
        var parameterListNum = parameterList && parameterList.length ? parameterList.length : 0;
    	

        for (var i = 0; i < parameterListNum; i++) {
            var parameter = parameterList[i];
            if (parameter.uniqueId == id) {
                parameterList.splice(i, 1);
                return 0;
            }
            if (removeParameterRecursively(parameter, id) === 0) {
                return 0;
            }
        }
        return -1;
    }
    
    /**
     * put object into deleted pool
     * these pool will be transfered to server
     * when the user perform "save" operation
     */
    function putObjectIntoDeletedPool(className, id) {
        if (!_deletedObjectList) {
            _deletedObjectList = [];
        }
        if (className && id) {
            _deletedObjectList.push({"className" : className, "id" : id});
        }
    }
    
    /**
     * sort parameters recursively
     */
    function sortParams(params) {
        if (!params) return;
        var i = 0;
        var n = params.length;
        var o;
        params.sort(paramsSorter);
        for (; i < n; i++) {
           if(params[i].dict){
        	   sortParams(params[i].dict.params);
           }
        }
    }
    
    /**
     * params sorter
     */
    function paramsSorter(p1, p2) {
        if (p1 !== p2) {
            return p1.paramName > p2.paramName ? 1 : -1;
        }
        return p1.paramId > p2.paramId ? 1 : -1;
    }
	
    
    /**
     * hide all child parameters of a complex parameter
     */
    function hideAllChildParameters(param) {
        if (!param || !param.dict ||!param.dict.params || param.dict.params <=0) return;
        var paramList = param.dict.params,
            paramListNum = paramList.length;
        for (var i = 0; i < paramListNum; i++) {
            var paramChild = paramList[i];
            hideParam(paramChild);
            hideAllChildParameters(paramChild);
        }
    }
    
    /**
     * hide parameter
     */
    function hideParam(param) {
        $("#tr-param-" + param.uniqueId).css({'display': 'none'});
    }
    
    /**
     * show all child parameters of a complex parameter
     */
    function showAllChildParameters(param) {
        if (!param || !param.dict || !param.dict.params || param.dict.params <=0) return;
        var paramList = param.dict.params,
            paramListNum = paramList.length;
        for (var i = 0; i < paramListNum; i++) {
            var paramChild = paramList[i];
            showParam(paramChild);
            showAllChildParameters(paramChild);
        }
    }

    /**
     * show parameter
     */
    function showParam(param) {
    	$("#tr-param-" + param.uniqueId).css({'display': ''});
    }
    
    /**
     * is editing
     */
    function isEditing() {
        return _editContext !== null;
    }

    /**
     * editing
     */
    function editing(id, content, key) {
        _editContext = {"id" : id, "content" : content, "key" : key};
    }

    /**
     * stop editing
     */
    function stopEditing() {
        if (_editContext === null) {
            throw Error("no edit view, can not stop");
        }
        var context = _editContext;
        _editContext = null;
        return context;
    }
    
    /**
     * get edit input element
     */
    function getEditInputHtml(paramId, value, width, maxLength) {
        if (!value) {
            value = '';
        }
        value = b.encodeHTML(value);
        return "<input id='" + ELEMENT_ID.EDIT_INPUT + "' class='form-control' type='text' value='" + value +
            "' style='margin:0;width: " + width +"px' maxlength='" + maxLength + "' onblur='param.finishEdit();'" + " oldValue='" + value + "' paramId='" + paramId +"' />";
    }
    
    /**
     * get edit input element
     */
    function getEditTextAreaHtml(paramId, value, width, maxLength) {
        if (!value) {
            value = '';
        }
        value = b.encodeHTML(value);
        return "<textarea id='" + ELEMENT_ID.EDIT_INPUT + "' class='edit-input form-control' type='text' style='width: " + 
        		width +"px' maxlength='" + maxLength + "' onblur='param.finishEdit();'" + "oldValue='" + value + "' paramId='" + paramId + "'>" + value + "</textarea>";
    }
    
    /**
     * get td id
     */
    function getTdId(id, type) {
        return "#td-param-" + type + "-" + id;
    }

    /**
     * get td
     */
    function getTd(id, type) {
        return $(getTdId(id, type))[0];
    }
    
    /**
     * focus element
     * fix IE problem
     */
    function focusElement(element) {
        if (!element) return;
        if (b.browser.ie) {
            setTimeout(function(){element.focus();}, 200);
        } else {
            element.focus();
        }
    }
    
    // 为指定参数生成uniqueId，包括其字典
    function generateUniqId(param){
    	 if (!param) return;
    	 param.uniqueId = generateId();
    	 
    	 if(param.dict){
    		 var paramList = param.dict.params;
    		 for (var i = 0; i < paramList.length; i++) {
                 var paramChild = paramList[i];
                 generateUniqId(paramChild);
             }
    	 }
    }
    
    function isParamChange(parameterId){
    	if(isReqParamChange && isRetParamChange)
    		return;
	    var paramTb = $("#" + "tr-param-" + parameterId).parent().parent().parent();
	    if(!paramTb)
	    	return;
	    if(paramTb.attr("id") == "reqParamsTb")
	    	isReqParamChange = true;
	    if(paramTb.attr("id") == "retParamsTb")
	    	isRetParamChange = true;
    }
    
})();