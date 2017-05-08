/**
 * Created by lvcheng on 2017/4/18.
 */

$('#captchaImage').click(function(){
  $('#captchaImage').attr("src", "http://localhost:8082/ampc/user/yzmimg?timestamp=" + (new Date()).valueOf());
});

$("#button").click(function () {
  loadIngFun();
});

//回车绑定
$("#name").keydown(function (event) {
  if (event.which == "13"){
    loadIngFun();
  }
});
$("#passwordIndex").keydown(function (event) {
  if (event.which == "13"){
    loadIngFun();
  }
});
$("#verify").keydown(function (event) {
  if (event.which == "13"){
    loadIngFun();
  }
});
$("#button").keydown(function (event) {
  if (event.which == "13"){
    loadIngFun();
  }
});

function loadIngFun(){
  var name = $('#name').val();
  if(!name){
    console.log('请填写用户名');
    swal({
      title: '请填写用户名!',
      type: 'error',
      timer: 1000,
      showConfirmButton: false
    });
    return;
  }
  var pas = $('#passwordIndex').val();
  if(!pas){
    console.log('请填写密码');
    swal({
      title: '请填写密码!',
      type: 'error',
      timer: 1000,
      showConfirmButton: false
    });
    return;
  }

  ajaxPost("/user/login",{
    userAccount:name,
    passWord:pas
  }).success(function(res){
    if(res.data == 1){

      $('#name').val('');
      $('#passwordIndex').val('');
      $('#verify').val('');
      window.location.href="home.html"
    }else{
      console.log(res.msg);
      swal({
        title: res.msg+'!',
        type: 'error',
        timer: 1000,
        showConfirmButton: false
      });
    }
  });


  //var verify = $('#verify').val();
  //if(!verify){
  //  console.log('请填写验证码');
  //  swal({
  //    title: '请填写验证码!',
  //    type: 'error',
  //    timer: 1000,
  //    showConfirmButton: false
  //  });
  //  return;
  //}
  ////window.location.href="home.html";
  //ajaxPost("/user/checkyzm",{yzm:verify}).success(function(res){
  //  if(res.data == 1){
  //    ajaxPost("/user/login",{
  //  	userAccount:name,
  //      passWord:pas
  //    }).success(function(res){
  //      if(res.data == 1){
  //
  //        $('#name').val('');
  //        $('#passwordIndex').val('');
  //        $('#verify').val('');
  //        window.location.href="home.html"
  //      }else{
  //        console.log(res.msg);
  //        swal({
  //          title: res.msg+'!',
  //          type: 'error',
  //          timer: 1000,
  //          showConfirmButton: false
  //        });
  //      }
  //    });
  //  }else{
  //    console.log('验证码错误');
  //    swal({
  //      title: res.msg+'!',
  //      type: 'error',
  //      timer: 1000,
  //      showConfirmButton: false
  //    });
  //  }
  //});
}




var parameterPar = {token: '', data: {}};
function ajaxPost(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  // return $.ajax('http://192.168.4.214:8082/ampc' + url, {
  return $.ajax('http://localhost:8082/ampc' + url, {
    contentType: "application/json",
    type: "POST",
    async: true,
    dataType: 'JSON',
    data: p
  })
}