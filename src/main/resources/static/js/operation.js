function updateList() {
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
        },
        error: function (XMLHttpRequest, textStatus, exception) {
            console.log(XMLHttpRequest);
            console.log(textStatus);
            console.log(exception);
            alert("查询失败")
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
            console.log(XMLHttpRequest);
            console.log(textStatus);
            console.log(exception);
            alert("添加失败")
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
            console.log(XMLHttpRequest);
            console.log(textStatus);
            console.log(exception);
            alert("删除失败")
        }
    })
}

