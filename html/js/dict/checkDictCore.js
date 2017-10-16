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
	var _dictParam;
	var _productId;
	
	var TEMPLATE = {
		'REQUEST_BEGIN_EDIT'			: '<table class="table table-bordered" id="core_check_table"><thead><tr><td class="head-expander"></td><th class="head-op">OP</th><th class="head-name">参数名称</th><th class="head-desc">含义</th><th class="head-type">类型</th><th class="head-required">是否必须</th><th class="head-remark">备注</th><th class="head-mock">mock</th></tr></thead><tbody>',
		'REQUEST_END'					: '</tbody></table>'
		//'REQUEST_PARAMETER_ADD_BUTTON'	: '<button id="core_add_param" class="btn btn-default btn-sm" onclick="param.addParam(\'request\');return false;"><i class="fa fa-plus"></i> 添加参数</button>'
	};
	
	var ELEMENT_ID = {
		'EDIT_INPUT'		: 'txtMTName'
	};
	
	var CONFIG = {
		'DEFAULT_MAX_LENGTH'         : 50,
        'REMARK_MAX_LENGTH'          : 9999,
        'DEFAULT_INPUT_WIDTH'        : 100,
        'PARAMETER_NAME_WIDTH'       : 160,
        'PARAMETER_DESC_WIDTH'		 : 250,
        'PARAMETER_TYPE_WIDTH'       : 110,
        'PARAMETER_REMARK_WIDTH'     : 300,
        'PARAMETER_MOCK_WIDTH'       : 250
	};
	
	param.getDictParam = function(){
		return _dictParam;
	}
	
	param.getTableId = function(){
		return "core_check_table";
	}
	
	param.init = function(dictParamObj){
		_productId = $('#productId').val().trim();

		_dictParam = [];
		if(dictParamObj){
			_dictParam = dictParamObj;
		}
		generateUniqueIdForParams(_dictParam);
		
		bind();
	}
	/**
     * add parameter
     */
    param.addParam = function(type, parentParamId) {
        var newParamId = "";
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
     * remove parameter
     */
    param.removeParam = function(paramId) {
        removeParameter(paramId);
        putObjectIntoDeletedPool("Parameter", paramId);
        bind();
    };
	
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
	
	param.edit = function(id, key){
		var el;
		var str = '';
		var oldValue;
		var width = 100;
		
		if(key == 'op'){
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
	            str += getEditInputHtml(oldValue, width, CONFIG.DEFAULT_MAX_LENGTH);
	            break;
	        case 'param-desc':
	            width = CONFIG.PARAMETER_DESC_WIDTH;
	            el = getTd(id, key);
	            oldValue = b.trim(param.paramDesc);
	            str += getEditTextAreaHtml(oldValue, width, CONFIG.DEFAULT_MAX_LENGTH);
	            break;
	        case 'remark':
	        	width = CONFIG.PARAMETER_REMARK_WIDTH;
	            el = getTd(id, key);
	            oldValue = b.trim(param.remark);
	            str += getEditTextAreaHtml(oldValue, width, CONFIG.DEFAULT_MAX_LENGTH);
	            break;
	        case 'mock':
	            width = CONFIG.PARAMETER_MOCK_WIDTH;
	            el = getTd(id, key);
	            // oldValue = b.trim(el.innerHTML)
	            oldValue = b.trim(param.mock);
	            str += getEditTextAreaHtml(oldValue, width, CONFIG.REMARK_MAX_LENGTH);
	            break;
	        default:
	            throw Error('not implemented, key:' + key);
		}
		
		// save edit state
		editing(id, oldValue, key);
		
		el.innerHTML = str;
		
		focusElement($('#' + ELEMENT_ID.EDIT_INPUT)[0]);
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
	
	param.dataTypeSelectChanged = function(parameterId, value) {
        setParameter(parameterId, value, 'paramType');
        if (value == 'object' || value == 'array<object>') {
            bind();
        }else if(value == '自定义'){
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
        					var checkDictId = $('#checkDictId').val().trim();
        					for(var j = 0; j < dicts.length; j++){
        						var dict = dicts[j];
        						if(checkDictId == dict.dictId){
        							//编辑字典时不显示它自己
        							continue;
        						}
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
        		    									generateUniqueIdForParams(dict.params);
        		    									setParameter(parameterId, dict.dictName, 'paramType');
        	        		    						setParameter(parameterId, dict, 'dict');
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
        		    						
        		    						//创建该字典的一个option
//        		    						var optionObj = new Option(selectedDict.dictName, selectedDict.dictId, false, true);
        		    						
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
        					bootbox.alert({message: '当前产品没有相关的字典，请先创建字典。',
            					buttons:{
            						ok:{
            							label: '确定'
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
        	bind();
        }
        
    };
	
	function bind(){
		_editContext = null;
		var reqStr = '';
		var reqDiv = $('#checkDictParamTb');
		var reqParamLength = _dictParam ? _dictParam.length : 0;
		
		//sortParams(_dictParam);
		
		reqStr += TEMPLATE.REQUEST_BEGIN_EDIT;
		
		for(var i = 0; i < reqParamLength; i++){
			var p = _dictParam[i];
			reqStr += getPTRHtml(p);
		}
		
		reqStr += TEMPLATE.REQUEST_END;
		//reqStr += TEMPLATE.REQUEST_PARAMETER_ADD_BUTTON;
		
		reqDiv.html(reqStr);
	}
	
	function generateUniqueIdForParams(paramList){
		if(!paramList) {
			return;
		}
		
		for(var i = 0; i < paramList.length; i++){
			var p = paramList[i];
			p.uniqueId = generateId();
			if(p.dict){
				generateUniqueIdForParams(p.dict.params)
			}
		}
	}
	
	/**
     * get parameter tr html
     */
    function getPTRHtml(param, level) {
    	var str = '';
    	var parameterList;
        if(param.dict){
        	parameterList = param.dict.params;
        }
        
        var parameterListNum = parameterList && parameterList.length ? parameterList.length : 0;
        var level = level || 0;
        
        str += '<tr class="tr-param ' + (level > 0 || parameterListNum > 0 ? " param-level-" + level : "") + '" ' +
        		'id="tr-param-' + param.uniqueId + '" >';
        
        //expander column
        str += '<td class="expander">';
        if(parameterListNum > 0){
        	str += '<div id="div-param-expander-' + param.uniqueId + '" onclick="param.paramShrink(' + param.uniqueId + ');return false;"><i class="fa fa-minus"></i></div>';
        }
        str += '</td>';
        
        //op column
        str += getPTDHtml(param.uniqueId,
        		'<div id="div-param-op-' + param.uniqueId +'" style="display:none;">' +
        		'<a href="#" onclick="param.removeParam(' + param.uniqueId + ');return false;"><i class="glyphicon glyphicon-remove"></i></a>' +
        		(parameterListNum > 0 || param.paramType == "object" || param.paramType == "array<object>" ?
        		'<a style="margin-left:5px;color:#47a947;" href="#" onclick="param.addParam(\'child\', ' + param.uniqueId + ');return false;"><i class="glyphicon glyphicon-plus"></i></a>' : '') + '</div>', 'op');
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
        
        for (var i = 0; i < parameterListNum; i++) {
            str += getPTRHtml(parameterList[i], level + 1);
        }
        
        return str;
    }
	
    function getPTDHtml(id, value, type, level) {
    	var str = '<td id="td-param-' + type + '-' + id + '" class="td-param ' + type + '" >' +
					(level ? new Array(level + 1).join('&nbsp;&nbsp;&nbsp;&nbsp;') : ' ') + value +
		    	'</td>';
    	
    	return str;
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
                    '自定义'
                ];
         var typeListNum = typeList.length;

        str += '<td id="td-param-dataType-' + id + '" class="td-param">' +
        		'<select class="form-control" id="select-dataType-' + id + '" onchange="param.dataTypeSelectChanged(' + id + ', this.value);">';
        for (var i = 0; i < typeListNum; i++) {
            var item = typeList[i];
            str += '<option value="'+ item +'"' + (item == type ? ' selected="true"' : '') + '>' + b.encodeHTML(item) + '</option>';
        }
        if(typeList.indexOf(type) == -1){
        	str += '<option value="'+ type +'" selected="true">' + type + '</option>';
        }
        
        str += '</select></td>';
        return str;
    }
    
    function getRequiredEditSelectHtml(id, isRequired) {
        var str = '',
            typeList = [
                    '否',
                    '是'
                ],
            typeListNum = typeList.length;

        str += '<td id="td-param-required-' + id + '" class="td-param">' +
        		'<select class="form-control" id="select-required-' + id + '" onchange="param.requiredSelectChanged(' + id + ', this.value)">';
        for (var i = 0; i < typeListNum; i++) {
            var item = typeList[i];
            str += '<option value="'+ i +'"' + (i == isRequired ? ' selected="true"' : '') + '>' + item + '</option>';
        }
        
        str += '</select></td>';
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
        
        var requestParameterList = _dictParam;
        var requestParameterListCount = requestParameterList ? requestParameterList.length : 0;
        
        for (l = 0; l < requestParameterListCount; l++) {
        	parameter = requestParameterList[l];
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
            	p.dictId = "";
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
        _dictParam.push(pNew);
        return pNew.uniqueId;
    };


    function newParameter() {
        var obj = {};
        obj.uniqueId = generateId();
        obj.paramId = '';
        obj.paramName = '';
        obj.paramDesc = '';
        obj.paramType = '';
        obj.isNecessary = '';
        obj.dictId = '';
        obj.remark = '';
        obj.mock = '';
        obj.dict = newDict();
        return obj;
    }
    
    function newDict(){
    	var obj = {};
    	obj.dictId = '';
    	obj.params = [];
    	
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
        
        var requestParameterList = _dictParam;
        var requestParameterListCount = requestParameterList ? requestParameterList.length : 0;
        
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
        return "<input id='" + ELEMENT_ID.EDIT_INPUT + "' class='edit-input form-control' type='text' value='" + value +
            "' style='width: " + width +"px' maxlength='" + maxLength + "' onblur='param.finishEdit();' />";
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
})();