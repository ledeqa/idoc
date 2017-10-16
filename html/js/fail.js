function contactToAdmin(){
	var data = {};
	data.exceptionMsg = $('#reportMsg').val();
	$.ajax({
		url: '/fail/contactAdmin.html',
		type: 'POST',
		dataType: 'json',
		data: data,
		success: function(json){
			if(json.retCode == 200){
				bootbox.alert({message: '您好！很抱歉给您带来的不便，已经成功发送popo消息给管理员，我们会尽快处理，谢谢您的使用！',
					buttons:{
						ok:{
							label: '确定'
						}
					}});
			}else{
				bootbox.alert({message: '管理员没有接收到系统异常的消息，你可以主动联系管理员！',
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