/**
 * Created by lvcheng on 2017/4/18.
 */

$('#captchaImage').click(function(){
  $('#captchaImage').attr("src", "img.do?timestamp=" + (new Date()).valueOf());
});

$("#button").click(function () {
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
  ajaxPost("/yzm.do",{zhi:verify}).success(function(res){
    if(res == 1){
      ajaxPost("/user/login",{
        userId:name,
        passWord:pas
      }).success(function(res){
        if(res.status == 0){
          var ls = window.localStorage;
          ls.setItem('userId', 1);
          ls.setItem('domain', 3);

          $('#name').val('');
          $('#passwordIndex').val('');
          $('#verify').val('');
          window.location.href="home.html"
        }else{
          console.log(res.msg)
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
      swal({
        title: '验证码错误!',
        type: 'error',
        timer: 1000,
        showConfirmButton: false
      });
    }
  });

});






var parameterPar = {token: '', data: {}};
function ajaxPost(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  return $.ajax('/ampc' + url, {
    contentType: "application/json",
    type: "POST",
    async: true,
    dataType: 'JSON',
    data: p
  })
}