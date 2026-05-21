
$(document).ready(function () {
    $("#confirm-orders").click(function () {
        var selectAddr = $('input:radio[name="addressid"]:checked').val();
        if (selectAddr == null) {
            swal("请先添加地址");
            return;
        }
        var isPay = $('#pay-select').val();
        var oldPrice = $('#total-old').text();
        var newPrice = $('#total-new').text();
        $.ajax({
            url: "/shop/orderFinish",
            type: "POST",
            data: {
                oldPrice: oldPrice,
                newPrice: newPrice,
                isPay: isPay,
                addressid: selectAddr
            },
            success: function (result) {
                if (result.code === 100) {
                    swal(result.msg, "", "success");
                    location.href = "/shop/info/list";
                    return;
                }
                swal(result.msg || "购买失败", "", "warning");
            },
            error: function () {
                swal("购买失败，无法连接到服务器！");
            }
        });
    });
});
