/**
 * Created by lvcheng on 2017/4/18.
 */

$('#captchaImage').click(function(){
  $('#captchaImage').attr("src", "/ampc/user/yzmimg?timestamp=" + (new Date()).valueOf());
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
//    console.log('请填写用户名');
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
//    console.log('请填写密码');
    swal({
      title: '请填写密码!',
      type: 'error',
      timer: 1000,
      showConfirmButton: false
    });
    return;
  }

//   ajaxPost("/ampc/user/login",{
//     userAccount:name,
//     passWord:pas
//   }).success(function(res){
//     if(res.status == 0){
//
//       $('#name').val('');
//       $('#passwordIndex').val('');
//       $('#verify').val('');
//       window.location.href="home.html"
//     }else{
// //      console.log(res.msg);
//       swal({
//         title: res.msg+'!',
//         type: 'error',
//         timer: 1000,
//         showConfirmButton: false
//       });
//     }
//   });


  var verify = $('#verify').val();
  if(!verify){
   console.log('请填写验证码');
   swal({
     title: '请填写验证码!',
     type: 'error',
     timer: 1000,
     showConfirmButton: false
   });
   return;
  }
  //window.location.href="home.html";
  ajaxPost("/ampc/user/checkyzm",{yzm:verify}).success(function(res){
   if(res.data == 1){
     ajaxPost("/ampc/user/login",{
   	userAccount:name,
       passWord:pas
     }).success(function(res){
       if(res.status == 0){

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
   }else{
     console.log('验证码错误');
     $('#captchaImage').click();
     $('#verify').val('');
     swal({
       title: res.msg+'!',
       type: 'error',
       timer: 1000,
       showConfirmButton: false
     });
   }
  });
}




var parameterPar = {token: '', data: {}};
function ajaxPost(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  return $.ajax(url, {
    contentType: "application/json",
    type: "POST",
    async: true,
    dataType: 'JSON',
    data: p
  })
}
changeCity();
function changeCity(){
  var city = '四川';
  var title = '';
  var english = '';
  if(city == '四川'){
    console.log('1123');
    title = '成都市重污染应急管理评估系统';
    english = 'Heavy Air Pollution Emergency Response Evaluation System for Chengdu';
    Copyrights = 'Copyrights: 北京美科思远环境科技有限公司';
  }else{
    title = '环境空气质量管理评估系统';
    english = 'Research on Ambient Air Quality Evaluation System';
    Copyrights = 'Copyrights (c) 版权所有信息';
  }
  $('.hdtx1').html(title);
  $('.hdtx2').html(english);
  $('.Copyrights').text(Copyrights);
}