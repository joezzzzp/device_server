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