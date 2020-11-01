function commonError(XMLHttpRequest, textStatus, exception, message) {
    console.log(XMLHttpRequest);
    console.log(textStatus);
    console.log(exception);
    alert(message)
}

function exportFile() {
    var files = document.getElementById("sn_file").files;
    var formData = new FormData();
    formData.append("file", files[0]);
    var startDate = document.getElementById("start_date").value
    var endDate = document.getElementById("end_date").value
    var params = {start: startDate, end: endDate};
    var queryString = $.param(params)
    console.log(queryString)
    $.ajax({
        type: "POST",
        url: "thunderCount/get?" + queryString,
        parameters: {},
        data: formData,
        processData: false,
        contentType: false,
        success: function (response, textStatus, xhr) {
            let fileName = xhr.getResponseHeader("Content-Disposition")
                .split(";")[1].split("filename=")[1];
            console.log(fileName)
            console.log(response)
            openDownloadDialog(new Blob([response]), fileName)
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "导出失败");
        }
    })
}

function openDownloadDialog(url, saveName) {
    if(typeof url == 'object' && url instanceof Blob) {
        url = URL.createObjectURL(url); // 创建blob地址
    }
    var aLink = document.createElement('a');
    aLink.href = url;
    aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
    var event;
    if(window.MouseEvent)
        event = new MouseEvent('click');
    else {
        event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0,
            0, 0, 0, false, false, false,
            false, 0, null);
    }
    aLink.dispatchEvent(event);
}


$(function() {
    //得到当前时间
    var date_now = new Date();
    //得到当前年份
    var year = date_now.getFullYear();
    //得到当前月份
    //注：
    //  1：js中获取Date中的month时，会比当前月份少一个月，所以这里需要先加一
    //  2: 判断当前月份是否小于10，如果小于，那么就在月份的前面加一个 '0' ， 如果大于，就显示当前月份
    var month = date_now.getMonth() + 1 < 10 ? "0" + (date_now.getMonth() + 1) : (date_now.getMonth() + 1);
    //得到当前日子（多少号）
    var date = date_now.getDate() < 10 ? "0" + date_now.getDate() : date_now.getDate();
    //设置input标签的max属性
    console.log("set max time")
    $("#start_date").attr("max", year + "-" + month + "-" + date);
    $("#end_date").attr("max", year + "-" + month + "-" + date);
})