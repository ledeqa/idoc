function updateUserInfo() {
	var data = {};
	data.corpMail = $("#corpMail").val();
	data.nickName = $("#userName").val();
	data.jobNumber = $("#jobNumber").val();
	data.telePhone = $("#telephone").val();
	if (!isNullOrEmpty(data.corpMail) && !isNullOrEmpty(data.userName)) {
		$.ajax({
			url : '/idoc/updateUserInfo.html',
			type : 'POST',
			dataType : 'json',
			data : data,
			success : function(json) {
				if (json.retCode == 200) {
					bootbox.alert("修改用户信息成功！");
				} else {
					bootbox.alert("修改用户信息失败，请稍后再试！");
				}
			}
		});
	} else {
		bootbox.alert("请填写完整的用户信息！");
	}
}

// 判断字符串是否为空
function isNullOrEmpty(strVal) {
	if (strVal == '' || strVal == null || strVal == undefined) {
		return true;
	} else {
		return false;
	}
}