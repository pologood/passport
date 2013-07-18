define("ui",[],function(){return{checkbox:function(c){c=$(c);var d=c.data("target");if(!d){return}d=$("#"+d);var b="checkbox-checked";function a(f){if(f){c.prop("checked",c.prop("checked")?null:true)}var e=c.prop("checked");e?d.addClass(b):d.removeClass(b)}c.click(function(){a()});c.on("focus",function(){if(this.blur&&window.attachEvent){this.blur()}});d.click(function(){a(1)})}}});define("utils",[],function(){return{uuid:function(){function s4(){return Math.floor((1+Math.random())*65536).toString(16).substring(1)}return s4()+s4()+s4()+s4()+s4()+s4()+s4()+s4()},addZero:function(num,len){num=num.toString();while(num.length<len){num="0"+num}return num},parseResponse:function(data){if(typeof data=="string"){try{data=eval("("+data+")")}catch(e){data={status:-1,statusText:"服务器故障"}}}return data},addIframe:function(url,callback){var iframe=document.createElement("iframe");iframe.src=url;iframe.style.position="absolute";iframe.style.top="1px";iframe.style.left="1px";iframe.style.width="1px";iframe.style.height="1px";if(iframe.attachEvent){iframe.attachEvent("onload",function(){callback&&callback()})}else{iframe.onload=function(){callback&&callback()}}document.body.appendChild(iframe)},getScript:function(url,callback){var script=document.createElement("script");var head=document.head;script.async=true;script.src=url;script.onload=script.onreadystatechange=function(_,isAbort){if(isAbort||!script.readyState||/loaded|complete/.test(script.readyState)){script.onload=script.onreadystatechange=null;if(script.parentNode){script.parentNode.removeChild(script)}script=null;if(!isAbort){callback()}}};head.insertBefore(script,head.firstChild)},getUrlByMail:function(mail){mail=mail.split("@")[1];if(!mail){return false}var hash={"139.com":"mail.10086.cn","gmail.com":"mail.google.com","sina.com":"mail.sina.com.cn","yeah.net":"www.yeah.net","hotmail.com":"www.hotmail.com","live.com":"www.outlook.com","live.cn":"www.outlook.com","live.com.cn":"www.outlook.com","outlook.com":"www.outlook.com","yahoo.com.cn":"mail.cn.yahoo.com","yahoo.cn":"mail.cn.yahoo.com","ymail.com":"www.ymail.com","eyou.com":"www.eyou.com","188.com":"www.188.com","foxmail.com":"www.foxmail.com"};var url;if(mail in hash){url=hash[mail]}else{url="mail."+mail}return"http://"+url}}});define("conf",[],function(){return{client_id:"1120",redirectUrl:"/static/api/jump.htm",thirdRedirectUrl:"/static/api/tj.htm"}});define("index",["./ui","./utils","./conf"],function(h,i,d){var g=false;var j=function(){var k=$("#Login");k.parent().parent().addClass("login-vcode");if(g){return}g=true;k.find(".vcode img,.vcode a").click(function(){b();return false});b(k);return true};var a=function(){var k=$("#Login");k.parent().parent().removeClass("login-vcode")};var c={renren:[880,620],sina:[780,640],qq:[500,300]};var b=function(){if(g){$("#Login").find(".vcode img").attr("src","/captcha?token="+PassportSC.getToken()+"&t="+ +new Date())}};var e=function(k){$("#Login .vcode-error .text").html(k).parent().show()};var f=function(k){$("#Login .uname-error .text").html(k).parent().show()};return{init:function(){h.checkbox("#RemChb");if($.cookie("fe_uname")){$("#Login .username input").val($.trim($.cookie("fe_uname")));$("#Login .username span").hide()}PassportSC.appid=d.client_id;PassportSC.redirectUrl=location.protocol+"//"+location.hostname+(location.port?(":"+location.port):"")+d.redirectUrl;$("#Login").on("submit",function(){var n=$("#Login");var m=n.find('input[name="password"]').val();if(!$.trim(n.find('input[name="username"]').val())||!m){f("请输入用户名密码");return false}if(m.length>16||m.length<6){f("用户名密码输入错误");return false}if($("#Login").parent().parent().hasClass("login-vcode")&&!$.trim(n.find('input[name="captcha"]').val())){e("请输入验证码");n.find('input[name="captcha"]').focus();return false}PassportSC.loginHandle(n.find('input[name="username"]').val(),n.find('input[name="password"]').val(),n.find('input[name="captcha"]').val(),n.find('input[name="autoLogin"]').prop("checked")?1:0,document.getElementById("logdiv"),function(p){var o=false;var q=n.find("input[name=captcha]");if(+p.needcaptcha){if(j()){o=true}$("#Login").parent().parent().addClass("login-vcode")}if(+p.status==20221){var r=q.val()?"验证码错误":"请输入验证码";e(r);!o&&b();q.focus()}else{if(+p.status==20230){f("登录异常，请1小时后再试");!o&&b()}else{f("用户名或密码错，请重新输入");!o&&b()}}},function(){$.cookie("fe_uname",$("#Login .username input").val(),{path:"/",expires:365});var o={};try{o=$.evalJSON(server_data).data}catch(p){window.console&&console.log(p)}if(o&&o.ru){location.href=o.ru}else{location.href="https://"+location.hostname}return});return false});$(document.body).click(function(){$("#Login .error").hide()});$("#Login .error a").click(function(){$("#Login .error").hide();return false});$(".login .third-login a").each(function(m,n){$(n).attr("href","https://account.sogou.com/connect/login?provider="+$(n).html()+"&client_id="+d.client_id+"&ru="+encodeURIComponent(location.href))});$("#Login .username input").change(function(){if(!$.trim($(this).val())){return}$.get("/web/login/checkNeedCaptcha",{username:$.trim($(this).val()),client_id:d.client_id,t:+new Date()},function(m){m=i.parseResponse(m);if(m.data.needCaptcha){j()}else{a()}})});var l=$("#Login .password input , #Login .username input , #Login .vcode input");var k;if(/se 2.x/i.test(navigator.userAgent)||/compatible;/i.test(navigator.userAgent)){k=setInterval(function(){l.each(function(m,n){if($(n).val().length){$(n).prev().hide()}})},100)}l.focus(function(){$(this).prev().hide();$(this).parent().find("b").show();k&&clearInterval(k)}).blur(function(){$(this).parent().find("b").hide();if(!$.trim($(this).val())){$(this).prev().show()}});l.parent().click(function(){$(this).find("input").focus()});window.onload=function(){l.each(function(m,n){if($(n).val()){$(n).prev().hide()}})}}}});