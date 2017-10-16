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
	var _compareReqParams;
	var _compareRetParams;
	var _productId;
	
	var _copyParamList = [];
	var _copyParamHashSet = {}; // 用于复制参数时去重
	
	var TEMPLATE = {
		'REQUEST_BEGIN'					: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show"></th></tr></thead><tbody>',
		'REQUEST_BEGIN_EDIT'			: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-op"><center>状态</center></th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show"></th></tr></thead><tbody>',
		'REQUEST_END'					: '</tbody></table>',
		'REQUEST_PARAMETER_ADD_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.addParam(\'request\');return false;"><i class="fa fa-plus"></i> 添加参数</button>',
		
		'RESPONSE_BEGIN'				: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show">是否显示该字段</th></tr></thead><tbody>',
		'RESPONSE_BEGIN_EDIT'			: '<table class="table table-bordered"><thead><tr><th class="head-expander"></th><th class="head-op"><center>状态</center></th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th><th class="head-show">是否显示该字段</th></tr></thead><tbody>',
		'RESPONSE_END'					: '</tbody></table>',
		'RESPONSE_PARAMETER_ADD_BUTTON'	: '<button class="btn btn-default btn-sm" onclick="param.addParam(\'response\');return false;"><i class="fa fa-plus"></i> 添加参数</button>'
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
	}
	
	param.getRetParam = function(){
		return _retParam;
	}
	
	param.init = function(reqParamObj, retParamObj, compareReqParams, compareRetParams, isCurrentVersion){
		_productId = $('#productId').val().trim();
		_isEditMode = false;
		
		_reqParam = [];
		if(reqParamObj){
			_reqParam = reqParamObj;
		}
		generateUniqueIdForParams(_reqParam, true);
		_compareReqParams = [];
		if(compareReqParams){
			_compareReqParams = compareReqParams;
			generateUniqueIdForParams(_compareReqParams, true);
		}
		
		_retParam = [];
		if(retParamObj){
			_retParam = retParamObj;
		}
		generateUniqueIdForParams(_retParam, false);
		 _compareRetParams = [];
		if(compareRetParams){
			_compareRetParams = compareRetParams;
			generateUniqueIdForParams(_compareRetParams, false);
		}
		if(isCurrentVersion){
			bind();
		}else{
			bindCompare();
		}
	}
	/**
	 * set editMode
	 */
	param.setEditMode = function(editMode){
		_isEditMode = editMode;
		bind();
	}
	    
	/**
     * show operation column of parameter table (action table)
     */
	param.showOpColumn = function(pId){
		$('#div-param-op-' + pId).css({'display': ''});
	}
	
	/**
     * hide operation column of parameter table (action table)
     */
	param.hideOpColumn = function(pId){
		$('#div-param-op-' + pId).css({'display': 'none'});
	}
	
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
	}
	
	param.paramExpand = function(uniqueId){
		showAllChildParameters(getParameter(uniqueId));
        var divExpander = $('#div-param-expander-' + uniqueId)[0];
        if (divExpander) {
            divExpander.onclick = function() {param.paramShrink(uniqueId); return false;};
            $('#div-param-expander-' + uniqueId + ' i').first().removeClass('fa-plus');
            $('#div-param-expander-' + uniqueId + ' i').first().addClass('fa-minus');
        }
	}
	
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
        
        setParameter(editContext.id, newValue, property);
        
        var idReturn = editContext.id;
        $(el).text(newValue);
        
        if (isEditing()) {
            stopEditing();
        }
        return idReturn;
	}
	
	param.requiredSelectChanged = function(parameterId, value) {
        setParameter(parameterId, value, 'isNecessary');
	}
	
	function generateParamHtml(params, compareParams, level, isRquest){
		var paramHtml = '';
		if(!params && !compareParams){
			return paramHtml;
		}
		var comParamsMap = [];
		if(compareParams){
			for(var i = 0; i < compareParams.length; i++){
				comParamsMap[compareParams[i].paramName] = compareParams[i];
			}
		}
		if(params){
			for(var i = 0; i < params.length; i++){
				var param = params[i];
				if(params[i].paramName in comParamsMap){
					var comParam = comParamsMap[param.paramName];
					if(comParam != null){
						var isEquale = compareParam(param, comParam);
						if(isEquale){
							paramHtml += getParamTRHtml(isRquest, param, 'same', level);
						}else{
							paramHtml += getParamTRHtml(isRquest, param, 'diff-a', level);
							paramHtml += getParamTRHtml(isRquest, comParam, 'diff-b', level);
						}
						// 移除该项
						comParamsMap[param.paramName] = null;
						
						if(param.dict && comParam.dict){
							paramHtml += generateParamHtml(param.dict.params, comParam.dict.params, level + 1, isRquest);
						}else if(param.dict){
							paramHtml += generateParamHtml(param.dict.params, comParam.dict, level + 1, isRquest);
						}else if(comParam.dict){
							paramHtml += generateParamHtml(param.dict, comParam.dict.params, level + 1, isRquest);
						}else{
							paramHtml += generateParamHtml(param.dict, comParam.dict, level + 1, isRquest);
						}
					}
				}else{ // 该参数不在比较的版本中
					paramHtml += getParamTRHtml(isRquest, param, 'add', level);
					if(param.dict){
						paramHtml += generateParamHtml(param.dict.params, null, level + 1, isRquest);
					}
				}
			}
		}
		
		// 将比较版本中的存在但目标版本中不存在的参数显示出来
		for(key in comParamsMap){
			var value = comParamsMap[key];
			if(value != null){
				paramHtml += getParamTRHtml(isRquest, value, 'minus', level);
				if(value.dict){
					paramHtml += generateParamHtml(null, value.dict.params, level + 1, isRquest);
				}
				comParamsMap[key] = null;
			}
		}
		return paramHtml;
	}
	
	// 比较两个参数是否相等
	function compareParam(param, comParam){ // 此函数没有比较字典该字段是否显示的变化
		if(param.paramName == comParam.paramName && param.paramDesc == comParam.paramDesc && param.paramType == comParam.paramType 
				&& param.isNecessary == comParam.isNecessary && param.remark == comParam.remark && param.mock == comParam.mock){
			return true;
		}
		return false;
	}
	
	function getParamTRHtml(isRquest, param, info, level, dictId) {
        var typeList = [
                        'number',
						'int',
						'long',
						'float',
						'double',
						'short',
						'byte',
						'char',
						'string',
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
						'array<boolean>'
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
        
        if(info == 'diff-a'){
        	str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
    		'id="tr-param-' + param.uniqueId + '" style="background: #d0ebf2;">';
        }else if(info == 'diff-b'){
        	str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
    		'id="tr-param-' + param.uniqueId + '" style="background: #eddff3;">';
        }else if(info == 'add'){
        	str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
    		'id="tr-param-' + param.uniqueId + '" style="background: #e8f1df;">';
        }else if(info == 'minus'){
        	str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
    		'id="tr-param-' + param.uniqueId + '" style="background: #f1e0df;">';
        }else{
        	str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
    		'id="tr-param-' + param.uniqueId + '" style="background: #b8def5;">';
        }
        
        //expander column
        str += '<td class="expander">';
        if(parameterListNum > 0){
        	str += '<div class="pull-right" id="div-param-expander-' + param.uniqueId + '" onclick="param.paramShrink(' + param.uniqueId + ');return false;"><i class="fa fa-minus"></i></div>';
        }
        str += '</td>';
        
        if(info == 'diff-a' || info == 'diff-b'){
        	str += getPTDHtml(param.uniqueId,
        			'<div id="div-param-op-' + param.uniqueId +'" style="display:block;">' +
        			'<center><i class="glyphicon glyphicon-pencil"></i></center>' + '</div>', 'op');
        }else if(info == 'add'){
        	str += getPTDHtml(param.uniqueId,
        			'<div id="div-param-op-' + param.uniqueId +'" style="display:block;">' +
        			'<center><i class="glyphicon glyphicon-plus" style="color: #47a947;"></i></center>' + '</div>', 'op');
        }else if(info == 'minus'){
        	str += getPTDHtml(param.uniqueId,
        			'<div id="div-param-op-' + param.uniqueId +'" style="display:block;">' +
        			'<center><i class="glyphicon glyphicon-minus" style="color: red;"></i></center>' + '</div>', 'op');
        }else{
        	str += getPTDHtml(param.uniqueId,
        			'<div id="div-param-op-' + param.uniqueId +'" style="display:block;">' +
        			'<center><i class="glyphicon glyphicon-ok" style="color: #ffdc35;"></i></center>' + '</div>', 'op');
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
        str += '</tr>';
        return str;
    }
	
	function bind(){
		_editContext = null;
		var reqStr = '';
		var retStr = '';
		var reqDiv = $('#reqParamsTb');
		var retDiv = $('#retParamsTb');
		var reqParamLength = _reqParam ? _reqParam.length : 0;
		var retParamLength = _retParam ? _retParam.length : 0;
		
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
		}
		
		reqDiv.html(reqStr);
		retDiv.html(retStr);
	}
	
	function bindCompare(){
		_editContext = null;
		var reqStr = '';
		var retStr = '';
		var reqDiv = $('#reqParamsTb');
		var retDiv = $('#retParamsTb');
		var reqParamLength = _reqParam ? _reqParam.length : 0;
		var retParamLength = _retParam ? _retParam.length : 0;
		
		reqStr += TEMPLATE.REQUEST_BEGIN_EDIT;
		
		var reqParamHtml = '';
		reqParamHtml = generateParamHtml(_reqParam, _compareReqParams, 0, false);
		reqStr += reqParamHtml;
		
		reqStr += TEMPLATE.REQUEST_END;
		retStr += TEMPLATE.RESPONSE_BEGIN_EDIT;

		var retParamHtml = '';
		retParamHtml = generateParamHtml(_retParam, _compareRetParams, 0, true);
		retStr += retParamHtml;
		retStr += TEMPLATE.RESPONSE_END;
		
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
				generateUniqueIdForParams(p.dict.params)
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
                        'number',
						'int',
						'long',
						'float',
						'double',
						'short',
						'byte',
						'char',
						'string',
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
						'array<boolean>'
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
        if(isRquest && level > 0 && $.inArray(pType, typeList) != -1){
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
    		return '<td id="td-param-' + type + '-' + id + '" class="td-param ' + type + '">'
	    	+'</td>';
    	}
    	var str = '<td id="td-param-' + type + '-' + id + '" class="td-param ' + type + '">' +
					(level ? new Array(level + 1).join('&nbsp;&nbsp;') : ' ') + value +
		    	'</td>';
    	
    	return str;
    }
    //添加隐藏或显示操作按钮,0是不显示，1是显示
    function getOperationHtml(id, paramId, dictId, isShow){
		if(isShow==0){
			return '<td align="center" id="td-param-operate-' + id + '">'
				+'<button data-toggle="tooltip" title="点击显示/隐藏按钮设置返回结果中该字段的显示状态，显示为显示该字段，隐藏为不显示该字段" class="btn btn-success btn-default btn-xs" disabled="disabled">隐藏</button>'
				+'</td>';
		}else{
			return '<td align="center" id="td-param-operate-' + id + '">'
				+' <button data-toggle="tooltip" title="点击显示/隐藏按钮设置返回结果中该字段的显示状态，显示为显示该字段，隐藏为不显示该字段" class="btn btn-primary btn-default btn-xs" disabled="disabled">显示</button>'
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
                    'number',
                    'int',
					'long',
					'float',
					'double',
					'short',
					'byte',
					'char',
					'string',
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
                    '自定义',
                    'array<自定义>'
                ];
         var typeListNum = typeList.length;

        str += '<td id="td-param-dataType-' + id + '" class="td-param">';
        if (_isEditMode) {
        	str += '<select class="form-control" id="select-dataType-' + id + '">';
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
        
        var requestParameterList = _reqParam.concat(_compareReqParams);
        var requestParameterListCount = requestParameterList ? requestParameterList.length : 0;
        var responseParameterList = _retParam.concat(_compareRetParams);
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
     * add response parameter
     */
    function addResponseParameter() {
        var pNew = newParameter();
        _retParam.push(pNew);
        return pNew.uniqueId;
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
    function getEditInputHtml(value, width, maxLength) {
        if (!value) {
            value = '';
        }
        value = b.encodeHTML(value);
        return "<input id='" + ELEMENT_ID.EDIT_INPUT + "' class='form-control' type='text' value='" + value +
            "' style='margin:0;width: " + width +"px' maxlength='" + maxLength + "' onblur='param.finishEdit();' />";
    }
    
    /**
     * get edit input element
     */
    function getEditTextAreaHtml(value, width, maxLength) {
        if (!value) {
            value = '';
        }
        value = b.encodeHTML(value);
        return "<textarea id='" + ELEMENT_ID.EDIT_INPUT + "' class='edit-input form-control' type='text' style='width: " + 
        		width +"px' maxlength='" + maxLength + "' onblur='param.finishEdit();'>" + value + "</textarea>";
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
    
})();