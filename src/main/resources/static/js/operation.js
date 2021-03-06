var isUpdating = false;

function commonError(XMLHttpRequest, textStatus, exception, message) {
    console.log(XMLHttpRequest);
    console.log(textStatus);
    console.log(exception);
    alert(message)
}

function uploadFile() {
    var files = document.getElementById("sn_file").files;
    var formData = new FormData();
    formData.append("file", files[0]);
    $.ajax({
        type: "POST",
        url: "upload",
        data: formData,
        processData:false,
        contentType: false,
        success: function (response) {
            console.log(response);
            alert("上传成功");
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "上传失败");
        }
    })
}

function updateList() {
    if (isUpdating) {
        return
    }
    isUpdating = true;
    var list = document.getElementById("sn_list");
    list.innerText = "";
    $.ajax({
        type: "GET",
        url: "findAll",
        dataType: "json",
        success: function (response) {
            console.log(response);
            var data = response.data;
            data.forEach(function(value) {
                var item = document.createElement("li");
                item.innerText = value;
                list.appendChild(item);
            });
            isUpdating = false;
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "查询失败");
        }
    })
}

function addSn() {
    var input = document.getElementById("sn_added");
    var snList = [input.value];
    var snData = {snList: snList};
    $.ajax({
        type: "POST",
        url: "add",
        data: JSON.stringify(snData),
        contentType: "application/json",
        dataType: "json",
        success: function () {
            input.value = "";
            updateList();
            alert("添加成功");
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "添加失败");
        }
    })
}

function deleteSn() {
    var input = document.getElementById("sn_deleted");
    var sn = input.value;
    $.ajax({
        type: "DELETE",
        url: "remove/" + sn,
        dataType: "json",
        success: function () {
            input.value = "";
            updateList();
            alert("删除成功");
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "删除失败");
        }
    })
}

function checkSyncStatus() {
    var text = document.getElementById("syncStatus");
    $.ajax({
        type: "GET",
        url: "syncService/syncStatus",
        dataType: "json",
        success: function (response) {
            if (response.code === 200) {
                text.innerHTML = "<div style=\"color: blue\">No task is running, you can start a new job now</div>";
            } else {
                text.innerHTML = "<div style=\"color: red\">" + response.message + "</div>";
            }
        },
        error:function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "查询失败");
        }
    })
}

function startSyncTask() {
    var text = document.getElementById("startSyncResult");
    $.ajax({
        type: "GET",
        url: "syncService/sync",
        dataType: "json",
        success: function (response) {
            if (response.code === 200) {
                text.innerHTML = "<div style=\"color: blue\">A new job has been started</div>";
            } else {
                text.innerHTML = "<div style=\"color: red\">" + response.message + "</div>";
            }
        },
        error:function (XMLHttpRequest, textStatus, exception) {
            commonError(XMLHttpRequest, textStatus, exception, "启动失败");
        }
    })
}
