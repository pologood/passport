define("utils",[],function(){return{uuid:function(){function s4(){return Math.floor((1+Math.random())*65536).toString(16).substring(1)}return s4()+s4()+s4()+s4()+s4()+s4()+s4()+s4()},addZero:function(num,len){num=num.toString();while(num.length<len){num="0"+num}return num},parseResponse:function(data){if(typeof data=="string"){try{data=eval("("+data+")")}catch(e){data={status:-1,statusText:"服务器故障"}}}return data},addIframe:function(url,callback){var iframe=document.createElement("iframe");iframe.src=url;iframe.style.position="absolute";iframe.style.top="1px";iframe.style.left="1px";iframe.style.width="1px";iframe.style.height="1px";if(iframe.attachEvent){iframe.attachEvent("onload",function(){callback&&callback()})}else{iframe.onload=function(){callback&&callback()}}document.body.appendChild(iframe)},getScript:function(url,callback){var script=document.createElement("script");var head=document.head;script.async=true;script.src=url;script.onload=script.onreadystatechange=function(_,isAbort){if(isAbort||!script.readyState||/loaded|complete/.test(script.readyState)){script.onload=script.onreadystatechange=null;if(script.parentNode){script.parentNode.removeChild(script)}script=null;if(!isAbort){callback()}}};head.insertBefore(script,head.firstChild)},getUrlByMail:function(mail){mail=mail.split("@")[1];if(!mail){return false}var hash={"139.com":"mail.10086.cn","gmail.com":"mail.google.com","sina.com":"mail.sina.com.cn","yeah.net":"www.yeah.net","hotmail.com":"www.hotmail.com","live.com":"www.outlook.com","live.cn":"www.outlook.com","live.com.cn":"www.outlook.com","outlook.com":"www.outlook.com","yahoo.com.cn":"mail.cn.yahoo.com","yahoo.cn":"mail.cn.yahoo.com","ymail.com":"www.ymail.com","eyou.com":"www.eyou.com","188.com":"www.188.com","foxmail.com":"www.foxmail.com"};var url;if(mail in hash){url=hash[mail]}else{url="mail."+mail}return"http://"+url}}});define("common",["./utils"],function(a){return{addUrlCommon:function(b){if(b.ru){$(".main-content .nav li a,.banner li a").each(function(c,d){$(d).attr("href",$(d).attr("href")+"?ru="+encodeURIComponent(b.ru))})}if(b.client_id){$(".main-content .nav li a,.banner li a").each(function(c,d){$(d).attr("href",$(d).attr("href")+($(d).attr("href").indexOf("?")==-1?"?":"&")+"client_id="+b.client_id)})}},showBannerUnderLine:function(){$(".banner ul").show();var b=$(".banner ul li.current");if(b.length){$(".banner .underline").css("left",b.position().left).css("width",b.css("width"))}},parseHeader:function(b){$("#Header .username").html(decodeURIComponent(b.uniqname||b.username));if(b.username||b.uniqname){$("#Header .info").show()}},bindJumpEmail:function(){$("#JumpToUrl").click(function(){if($("#JumpTarget")){window.open(a.getUrlByMail($("#JumpTarget").html()))}return false})},bindResendEmail:function(f){var c=this;var e=60;var b=null;var d=$("#ResendEmail");d.click(function(i){if(!b){var g=this;var h=e;$.ajax({url:"/web/resendActiveMail",data:{client_id:1120,username:f.email||d.attr("data-email")},type:"post",dataType:"json",beforeSend:function(){$(g).addClass("disabled")},error:function(k,j){d.removeClass("disabled");$(g).text("重发验证邮件");alert("通信错误")},success:function(j){j=$.evalJSON(j);if(0!=j.status){$(g).text("重发验证邮件");d.removeClass("disabled");return alert(j.statusText||"重发验证邮件失败")}b=setInterval(function(){$(g).text(h--+"秒后重发");if(!h){clearInterval(b);b=null;$(g).text("重发验证邮件");$(g).removeClass("disabled")}},1000)}})}i.preventDefault()})}}});define("conf",[],function(){return{client_id:"1120",redirectUrl:"/static/api/jump.htm",thirdRedirectUrl:"/static/api/tj.htm"}});(function(e){window.uuiJQuery=e;function i(m,l){for(var k in l){if(m[k]){continue}m[k]=l[k]}}var c={};function f(k){var p=k.match(/WebKit\/([\d.]+)/),m=k.match(/(Android)\s+([\d.]+)/),o=k.match(/(iPad).*OS\s([\d_]+)/),q=!o&&k.match(/(iPhone\sOS)\s([\d_]+)/),t=k.match(/(webOS|hpwOS)[\s\/]([\d.]+)/),r=t&&k.match(/TouchPad/),s=k.match(/Kindle\/([\d.]+)/),n=k.match(/Silk\/([\d._]+)/),l=k.match(/(BlackBerry).*Version\/([\d.]+)/);if(m){c.android=true,c.version=m[2]}if(q){c.ios=c.iphone=true,c.version=q[2].replace(/_/g,".")}if(o){c.ios=c.ipad=true,c.version=o[2].replace(/_/g,".")}if(t){c.webos=true,c.version=t[2]}if(r){c.touchpad=true}if(l){c.blackberry=true,c.version=l[2]}if(s){c.kindle=true,c.version=s[1]}}if(!e.os){f(navigator.userAgent)}else{c=e.os}var j;uiDict={};function b(){return{top:(document.body.scrollTop||document.documentElement.scrollTop),left:(document.body.scrollLeft||document.documentElement.scrollLeft)}}function a(l){var k=b();return{clientX:l.clientX,clientY:l.clientY,left:l.clientX+k.left,top:l.clientY+k.top}}function h(){return Math.floor(Math.random()*65536).toString(16)}var g=c&&c.version;if(g){var d=a;a=function(k){var k=k.originalEvent;if(k.touches){return d(k.touches[0])}return d(k)}}e.fn.getUUI=function(l){var k=[];var l=l||this.uiName;this.each(function(n,o){var m=e(o).data(l);if(m&&uiDict[m]){k.push(uiDict[m])}});return k};e.fn.excUUICMD=function(m,l){var k=this.getUUI(this.uiName);e.each(k,function(n,o){o.excUUICMD&&o.excUUICMD(m,l)});return this};e.UUIBase={ismobile:g,stopPropagation:function(k){k.stopPropagation&&k.stopPropagation();k.cancelBubble=true},preventDefault:function(k){if(k&&k.preventDefault){k.preventDefault()}else{window.event.returnValue=false}return false},getScroll:b,getEPos:a,guid:function(){return(h()+h()+h()+h()+h()+h()+h()+h())},getMousePos:function(){return j},empty:function(){if(document.selection&&document.selection.empty){document.selection.empty()}else{if(window.getSelection){window.getSelection().removeAllRanges()}}},offset:function(k){return e.extend({width:k.width(),height:k.height()},k.offset())},eventHash:{mousedown:g?"touchstart":"mousedown",mousemove:g?"touchmove":"mousemove",mouseover:g?"touchstart":"mouseover",mouseup:g?"touchend":"mouseup",click:"click"},baseClass:{excUUICMD:function(l,k){if(this[l]){this[l](k)}if(l=="destroy"){this._destroy(k)}},on:function(n,l,m,k){arguments[1]=this.eventName(l);this.eventList?this.eventList.push(arguments):this.eventList=[arguments];n.on.apply(n,Array.prototype.slice.call(arguments,1));return arguments},eventName:function(k){return e.UUIBase.eventHash[k]||k},off:function(l){var m=l[0],k=this;m.off.apply(m,Array.prototype.slice.call(l,1));e.each(this.eventList,function(o,n){if(l==n){k.eventList.splice(o,1)}})},_destroy:function(){if(!this.eventList){return}e.each(this.eventList,function(k,l){l[0].off.apply(l[0],Array.prototype.slice.call(l,1))})}},data:{},css:[],init:function(){if(e.UUIBase.css.length){var l=e.UUIBase.css.join("");if(l==""){return}var k=document.createElement("style");k.setAttribute("type","text/css");k.innerHTML=l;e("head").append(k);e.UUIBase.css=[];e.UUIBase.data=[]}},create:function(k,l){e[k]=l;i(e[k].prototype,e.UUIBase.baseClass);e.fn[k]=function(n){var m=n||{};this.uiName=k;this.each(function(p,q){var o=e(q).data(k);if(o){if(m.destroy){uiDict[o].excUUICMD("destroy",m);delete uiDict[o];e(q).removeData(k)}else{n&&uiDict[o].excUUICMD("update",m)}}else{if(!m.destroy){if(m.enable===undefined&&m.disable===undefined){m.enable=true}o=k+(+(new Date()));e(q).data(k,o);uiDict[o]=new e[k](e(q),m)}}});if(m.instance){return this.getUUI()}return this}}};e(function(){e(document).on(e.UUIBase.eventHash.mousemove,function(k){j=a(k)});g&&e(document).on(e.UUIBase.eventHash.mousedown,function(k){j=a(k)})})})(jQuery);define("uuibase",function(){});(function(c){var a={type:"submit",onsinglefail:function(){},onsinglesuccess:function(){},onfocus:function(){},onblur:function(){},onformfail:function(){return false},onformsuccess:function(){return false}};function b(g,e){var f=this;f._dom=g;var d=f.options=c.extend({},a);f.guid=c.UUIBase.guid();f.update(e||{});f._bindEvent()}b.prototype={update:function(d){this.options=c.extend(this.options,d)},_types:{num:function(d){return !d.length||/^\d+$/.test(d)},cellphone:function(d){return !d.length||/^1\d{10}$/.test(d)},require:function(d){return c.trim(d).length},email:function(d){return !d.length||/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(d)},min:function(e,d){return !e.length||e.length>=d},max:function(e,d){return !e.length||e.length<=d},range:function(f,e,d){return !f.length||f.length<=d&&f.length>=e}},_bindEvent:function(){var e=this;var d=e._dom;this.on(d,"submit",function(){var g=c(this);var h=true;var f=g.find(":input");f.each(function(i,j){h=e._validate(c(j))&&h});return(h?e.options.onformsuccess(g):e.options.onformfail(g))});d.on("focus",":input",function(){e.options.onfocus(c(this))});d.on("blur",":input",function(){e.options.onblur(c(this))});if(e.options.type=="blur"){d.on("blur",":input",function(){return e._validate(c(this))})}},_validate:function(s){var r=s.attr("uui-type"),f=s.attr("uui-reg"),p=this;if(!r&&!f){return true}var h=[];if(f){h.push([function(i){return new RegExp(f,"g").test(i)},[]])}if(r){r=r.split(" ");for(var m=0,g=r.length;m<g;m++){var o=r[m];var k,j=[];var e=/^(\w+)\((.*)\)$/g,q;q=e.exec(o);if(q){k=p._types[q[1]];j=q[2].split(",")}else{k=p._types[o]}h.push([k,j,o])}}for(var m=0,g=h.length;m<g;m++){var k=h[m][0],j=h[m][1],d=h[m][2];j.unshift(s.val());if(!k){continue}var n=k.apply(null,j);n?p.options.onsinglesuccess(s,d):p.options.onsinglefail(s,d);if(!n){break}}return n}};b.addType=function(d,e){b.prototype._types[d]=e};c.UUIBase.create("uuiForm",b);c(c.UUIBase.init)})(jQuery);define("uuiForm",function(){});define("form",["./utils","./conf","./uuibase","./uuiForm"],function(l,j){var k=/^([a-zA-Z0-9]|[\u4e00-\u9fa5]){2,12}$/;var g=["搜狐","搜狗","搜狐微博","sohu","souhu","sogou","sougou"];if(!Array.indexOf){Array.prototype.indexOf=function(o){for(var n=0;n<this.length;++n){if(this[n]==o){return n}}return -1}}$.uuiForm.addType("password",function(n){return n.length<=16&&n.length>=6});$.uuiForm.addType("vpasswd",function(p,q){var n=$("#"+q.slice(0,1).toUpperCase()+q.slice(1)+"Ipt");if(n&&n.length){var o=n.val();return o==p}return true});$.uuiForm.addType("nick",function(n){return/^[a-zA-Z]([a-zA-Z0-9_.]{3,15})$/.test(n)});$.uuiForm.addType("new_answer",function(n){return n&&n.replace(/[^\x00-\xff]/g,"xx").length<=50});$.uuiForm.addType("nickname",function(n){return k.test(n)&&(g.indexOf(n)<0)});$.uuiForm.addType("uniqname",function(n){return k.test(n)&&(g.indexOf(n)<0)});$.uuiForm.addType("fullname",function(n){return n.length<=50&&(n==""||/^[a-z\u4e00-\u9fa5]+$/i.test(n))});$.uuiForm.addType("personalid",function(n){return(n=="")||e.valid(n)});$.uuiForm.addType("nonempty",function(n){return !!$.trim(n)});var e={aCity:{11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"},valid:function(o){var q=0;var r="";if(!/^\d{17}(\d|x)$/i.test(o)){return false}o=o.replace(/x$/i,"a");if(this.aCity[parseInt(o.substr(0,2))]==null){return false}var n=o.substr(6,4)+"-"+Number(o.substr(10,2))+"-"+Number(o.substr(12,2));var s=new Date(n.replace(/-/g,"/"));if(n!=(s.getFullYear()+"-"+(s.getMonth()+1)+"-"+s.getDate())){return false}for(var p=17;p>=0;p--){q+=(Math.pow(2,p)%11)*parseInt(o.charAt(17-p),11)}if(q%11!=1){return false}return true}};var f={require:function(o){var n=o.parent().prev().html();return"请填写"+n.replace("：","")},nonempty:function(n){return"不能为空"},email:function(){return"邮箱格式不正确"},password:function(){return"密码长度为6-16位"},cellphone:function(){return"请输入正确的手机号码"},vpasswd:function(){return"两次密码输入不一致"},range:function(n){return""},max:function(o,n){return"输入字符请少于"+n+"个字"},nickname:function(n){if(n.val().length<2||n.val().length>12){return"昵称长度为2-12位"}else{if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test(n.val())){return"只能使用中文、字母、数字"}else{if(g.indexOf(n.val())>-1){return"含有非法关键字"}}}return"昵称不合法"},uniqname:function(n){if(n.val().length<2||n.val().length>12){return"昵称长度为2-12位"}else{if(/[^\u4e00-\u9fa5a-zA-Z0-9]/.test(n.val())){return"只能使用中文、字母、数字"}else{if(g.indexOf(n.val())>-1){return"含有非法关键字"}}}return"昵称不合法"},fullname:function(n){if(n.val().length>50){return"不能超过50个字符"}else{return"真实姓名仅允许输入英文字母和汉字"}},new_answer:function(n){return"不能超过50个英文字母或25个汉字"},nick:function(n){if(n.val().length<4||n.val().length>16){return"个性帐号长度为4-16位"}return"字母开头的数字、字母、下划线或组合"},personalid:function(){return"请输入18位有效的身份证号码"}};var b={email:"请输入您作为帐号的邮箱名",password:"6-16位，字母(区分大小写)、数字、符号",nick:"字母开头的数字、字母、下划线或组合"};var h=function(o,n){if(!o.parent().parent().find("."+n).length){o.parent().parent().append('<span class="'+n+'"></span>')}};var m=function(o,n){return o.parent().parent().find("."+n)};var c=function(o){if(o.attr("data-desc")){return o.attr("data-desc")}var n=o.attr("uui-type");n=(n||"").split(" ");var p;_.forEach(n,function(q){if(q!="require"&&!p&&b[q]){p=q}});return p?(b[p]||""):""};var d=function(p,o,n){return f[o]&&f[o](p,n)||""};var i=function(o){var n=l.uuid();o.find(".token").val(n);o.find(".vpic img").attr("src","/captcha?token="+n+"&t="+ +new Date())};var a=function(n){n.find(".vpic img,.change-vpic").click(function(){n.find(".vpic img").attr("src","/captcha?token="+n.find(".token").val()+"&t="+ +new Date());return false});n.click(function(){n.find(".form-error,.form-success").hide()})};return{render:function(o,n){n=n||{};o.uuiForm({type:"blur",onfocus:function(r){r.parent().addClass("form-el-focus");m(r,"error").hide();var s=c(r);if(s&&s.length){h(r,"desc");m(r,"desc").show().html(s)}},onblur:function(r){r.parent().removeClass("form-el-focus");m(r,"desc").hide()},onsinglefail:function(t,s){var r=s.split("(")[1];s=s.split("(")[0];r=r?r.slice(0,-1).split(","):[];var u=d(t,s,r);if(u&&u.length){h(t,"error");m(t,"desc").hide();m(t,"error").show().html(u)}},onsinglesuccess:function(s,r){m(s,"error").hide()},onformsuccess:function(r){if(!n.onbeforesubmit||n.onbeforesubmit(r)){$.post(r.attr("action"),r.serialize(),function(t){t=l.parseResponse(t);if(!+t.status){r.find(".form-success").show().find("span").html(t.statusText?t.statusText:"提交成功");n.onsuccess&&n.onsuccess(r,t)}else{var s=t.statusText?t.statusText.split("|")[0]:"未知错误";r.find(".form-error").show().find("span").html(s);n.onfailure&&n.onfailure(r)}})}return false},onformfail:function(r){r.find(".desc").hide();n.onformfail&&n.onformfail();return false}});o.append('<input type="hidden" name="token" value="" class="token"/>');var p={};try{p=$.evalJSON(server_data).data||{}}catch(q){window.console&&console.log(q)}o.append('<input name="client_id" value="'+(p.client_id?p.client_id:j.client_id)+'" type="hidden"/>');o.find(".form-btn").before('<div class="form-error"><span></span></div>');o.find(".form-btn").before('<div class="form-success"><span></span></div>');i(o);a(o)},initTel:function(p){var o,s="秒后重新获取验证码",r,t=60,n=t,q;$(".tel-valid-btn").click(function(){if(q){return}$(".main-content .form form").find(".tel-valid-error").hide();var w=$('.main-content .form form input[name="'+(p?p:"username")+'"]');if(w&&w.length){var z=w.parent().find(".error");if(!$.trim(w.val()).length){w.blur();return}if(z.length&&z.css("display")!="none"){return}}var A=$('.main-content .form form input[name="code-captcha"]');q=true;var y=$(this),u=y.parents("form");r=y.html();var v=y.attr("action")||"/web/sendsms";$.post(v,{mobile:w.val(),new_mobile:w.val(),client_id:j.client_id,captcha:A.val(),t:+new Date(),token:$("input[name=token]").val()},function(B){B=l.parseResponse(B);if(+B.status){if(20257==B.status){$(".main-content .form form").find(".form-item-vcode").removeClass("hide")}else{if(+B.status!=20201){if(20221==B.status){i(u)}$(".main-content .form form").find(".tel-valid-error").show().html(B.statusText?B.statusText:"系统错误")}}x()}else{y.addClass("tel-valid-btn-disable");o=setInterval(function(){if(!--n){x()}else{y.html(n+s)}},1000)}});function x(){y.html(r);clearInterval(o);q=false;n=t;y.removeClass("tel-valid-btn-disable")}})},showFormError:function(n){$(".main-content .form form").find(".form-error").show().find("span").html(n)},freshToken:function(n){i(n)},initToken:function(n){i(n);a(n)}}});var __ssjs__=typeof exports=="undefined"?false:true;if(__ssjs__){var Ursa={varType:{},escapeType:{}}}(function(){if(!__ssjs__){if(typeof Ursa!="undefined"&&typeof Ursa.render!="undefined"){return}window.Ursa=window.Ursa||{varType:{},escapeType:{}}}var config={starter:"{",ender:"}",commentStarter:"#",commentEnder:"#",opStarter:"{",opEnder:"}",statementStarter:"%",statementEnder:"%"},starter=config.starter,ender=config.ender,commentStarter=config.commentStarter,commentEnder=config.commentEnder,opStarter=config.opStarter,opEnder=config.opEnder,statementStarter=config.statementStarter,statementEnder=config.statementEnder,endStartReg=new RegExp("["+opEnder+commentEnder+statementEnder+"]","g");function setConfig(conf){for(var i in conf){if(config[i]){config[i]=conf[i]}}starter=config.starter,ender=config.ender,commentStarter=config.commentStarter,commentEnder=config.commentEnder,opStarter=config.opStarter,opEnder=config.opEnder,statementStarter=config.statementStarter,statementEnder=config.statementEnder}function range(start,end,size){var res=[],size=size||1;if(start<=end){while(start<end){res.push(start);start+=size*1}}else{while(start>end){res.push(start);start=start-size}}return res}function each(rge,callback){if(rge instanceof Array){for(var i=0,len=rge.length;i<len;i++){callback&&callback(rge[i],i,i)}}else{if(rge instanceof Object){var index=0;for(var key in rge){if(typeof rge[key]!="function"){callback&&callback(rge[key],key,index);index++}}}}}function dumpError(code,tplString,pointer,matches){var msg;switch(code){case 1:msg="错误的使用了\\，行数:"+getLineNumber(tplString,pointer);break;case 2:msg='缺少结束符}"，行数:'+getLineNumber(tplString,pointer);break;case 3:msg='缺少"{","#"或者"%"，行数:'+getLineNumber(tplString,pointer);break;case 4:msg="未闭合的{，,行数:"+getLineNumber(tplString,pointer);break;case 5:msg="以下标签未闭合"+matches.join(",");break;case 6:msg="创建模板失败"+tplString;break;case 7:msg='缺少"'+matches.replace("end","")+",行数:"+getLineNumber(tplString,pointer);break;case 8:msg="缺少结束符}"+tplString;break;default:msg="出错了";break}throw new Error(msg)}var __undefinded;function cleanWhiteSpace(result){result=result.replace(/\t/g,"    ");result=result.replace(/\r\n/g,"\n");result=result.replace(/\r/g,"\n");result=result.replace(/^(\s*\S*(\s+\S+)*)\s*$/,"$1");return result}function _length(rge){if(!rge){return 0}if(rge instanceof Array){return rge.length}var length=0;each(rge,function(item,i,index){length=index+1});return length}function _jsIn(key,rge){if(!key||!rge){return false}if(rge instanceof Array){for(var i=0,len=rge.length;i<len;i++){if(key==rge[i]){return true}}}try{return rge.match(key)?true:false}catch(e){return false}}function _jsIs(vars,type,args3,args4){switch(type){case"odd":return vars%2==1;break;case"even":return vars%2==0;break;case"divisibleby":return vars%args3==0;break;case"defined":return typeof vars!="undefined";break;default:if(Ursa.varType&&Ursa.varType[type]){return Ursa.varType[type].apply(null,arguments)}else{return false}}}function _trim(str){return str?(str+"").replace(/(^\s*)|(\s*$)/g,""):""}function _default(vars){return vars}function _abs(vars){return Math.abs(vars)}function _format(vars){if(!vars){return""}var placeHolder=vars.split(/%s/g);var str="",arg=arguments;each(placeHolder,function(item,key,i){str+=item+(arg[i+1]?arg[i+1]:"")});return str}function _join(vars,div){if(!vars){return""}if(vars instanceof Array){return vars.join(typeof div!="undefined"?div:",")}return vars}function _replace(str,replacer){if(!str){return""}var str=str;each(replacer,function(value,key){str=str.replace(new RegExp(key,"g"),value)});return str}function _slice(arr,start,length){if(arr&&arr.slice){return arr.slice(start,start+length)}else{return arr}}function _sort(arr){if(arr&&arr.sort){arr.sort(function(a,b){return a-b})}return arr}function _escape(str,type){if(typeof str=="undefined"||str==null){return""}if(str&&(str.safe==1)){return str.str}var str=str.toString();if(type=="js"){return str.replace(/\'/g,"\\'").replace(/\"/g,'\\"')}if(type=="none"){return{str:str,safe:1}}if(Ursa.escapeType&&Ursa.escapeType[type]){return Ursa.escapeType[type](str)}return str.replace(/<|>/g,function(m){if(m=="<"){return"&lt;"}return"&gt;"})}function _raw(str){return{safe:1,str:str}}function _truncate(str,len,killwords,end){if(typeof str=="undefined"){return""}var str=new String(str);var killwords=killwords||false;var end=typeof end=="undefined"?"...":"";if(killwords){return(typeof len=="undefined"?str.substr(0,str.length):str.substr(0,len)+(str.length<=len?"":end))}return end}function _substring(str,start,end){if(typeof str=="undefined"){return""}var str=new String(str);var end=typeof end!="undefined"?end:str.length;return str.substring(start,end)}function _upper(str){if(typeof str=="undefined"){return""}return new String(str).toUpperCase()}function _lower(str){if(typeof str=="undefined"){return""}return new String(str).toLowerCase()}Ursa._tpl={};Ursa.render=function(tplName,data,tplString){if(!Ursa._tpl[tplName]){Ursa.compile(tplString,tplName)}return Ursa._tpl[tplName](data)};Ursa.compile=function(tplString,tplName){var str=SyntaxGetter(tplString);try{eval('Ursa._tpl["'+tplName+'"] = '+str)}catch(e){dumpError(6,e)}return Ursa._tpl[tplName]};var tags="^(for|endfor|if|elif|else|endif|set)";var tagsReplacer={"for":{validate:/for[\s]+[^\s]+\sin[\s]+[\S]+/g,pfixFunc:function(obj){var statement=obj.statement,args=statement.split(/[\s]+in[\s]+/g)[0],_args,_value=_args,_key=args,context=statement.replace(new RegExp("^"+args+"[\\s]+in[\\s]+","g"),"");if(args.indexOf(",")!=-1){args=args.split(",");if(args.length>2){dumpError('多余的","在'+args.join(","),"tpl")}_key=args[0];_value=args[1];_args=args.reverse().join(",")}else{_key="_key";_value=args;_args=args+",_key"}return"(function() {var loop = {index:0,index0:-1,length: _length("+context+")}; if(loop.length > 0) {each("+context+", function("+_args+") {loop.index ++;loop.index0 ++;loop.key = "+_key+";loop.value = "+_value+";loop.first = loop.index0 == 0;loop.last = loop.index == loop.length;"}},endfor:{pfixFunc:function(obj,hasElse){return(hasElse?"":"})")+"}})();"}},"if":{validate:/if[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"if("+tests},sfix:") {"},elif:{validate:/elif[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"} else if("+tests},sfix:") {"},"else":{pfixFunc:function(obj,start){if(start=="for"){return"})} else {"}return"} else {"}},endif:{pfix:"}"},set:{validate:/set[\s]+[^\s]+/g,pfixFunc:function(obj){var statement=obj.statement;var tests=compileOperator(statement);return"var "+tests},sfix:";"}};var operator="\\/\\/|\\*\\*|\\||in|is";var operatorReplacer={"//":{pfix:"parseInt(",sfix:")"},"**":{pfixFunc:function(){return"Math.pow("},sfix:")"},"|":{sfix:")"},"in":{pfixFunc:function(vars){return"_jsIn(((typeof "+vars+' != "undefined") ? '+vars+": __undefinded)"},sfix:")"},is:{pfixFunc:function(vars){return"_jsIs(typeof "+vars+' != "undefined" ? '+vars+" : __undefinded"},sfix:")"},and:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*and[\s]*/g," && ")}},or:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*or[\s]*/g," || ")}},not:{pfixFunc:function(obj){var statement=obj.statement;return statement.replace(/[\s]*not[\s]*/g,"!")}}};function merge(obj,opstatement,start){return(obj.pfixFunc&&obj.pfixFunc(opstatement,start)||obj.pfix||"")+(opstatement.sfix||obj.sfix||"")}function funcVars(str){var str=str.replace(/\([\s]*\)/g,"").replace(/[\s\(]+/g,",").replace(/\)$/g,"");var dot=str.indexOf(",");if(dot==-1){str+='"'}else{str=str.substring(0,dot)+'"'+str.substring(dot)}return str}function redoGetStrings(str,bark){each(bark,function(value,key){str=str.replace(new RegExp(key,"g"),value)});return str}function compileOperator(opstatement){var reg=new RegExp("(^(not)|[\\s]+(and|or|not))[\\s]+","g"),matches;opstatement=opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+not[\s]+[^\s\(\)]+(\([^\)]*\))?/g,function(m){var vars=m.split(/[\s]+is[\s]+not/);var str="!"+operatorReplacer.is["pfixFunc"](vars[0]);vars.splice(0,1);vars=funcVars(_trim(vars.join("")));return str+(vars?', "'+vars+"":"")+operatorReplacer.is["sfix"]});opstatement=opstatement.replace(/[^\s\(\)]+[\s]+is[\s]+[^\s\(\)]+(\([^\)]*\))?/g,function(m){var vars=m.split(/[\s]+is[\s]+/);var str=operatorReplacer.is["pfixFunc"](vars[0]);vars.splice(0,1);vars=funcVars(_trim(vars.join("is")));return str+(vars?', "'+vars+"":"")+operatorReplacer.is["sfix"]});var vars=opstatement.match(/[^\s]+[\s]+in[\s]+[^\s]+/g);if(vars){for(var i=0,len=vars.length;i<len;i++){var varName=vars[i].split(/[\s]+/g);var rge=varName[varName.length-1];varName=varName[0];opstatement=opstatement.replace(vars[i],operatorReplacer["in"].pfixFunc(varName)+","+rge+operatorReplacer["in"].sfix)}}opstatement=opstatement.replace(reg,function(m){var m=_trim(m);if(m=="not"){return"!"}if(m=="and"){return"&&"}if(m=="or"){return"||"}});return opstatement}function output(source){source=source.split("|");var str=compileOperator(source[0]);for(var i=1,len=source.length;i<len;i++){var func="_"+_trim(source[i]);var fs=func.split("(");var fname=_trim(fs[0]);fs.splice(0,1);fs=_trim(fs.join("("));if(fname=="_default"){str=fname+"( typeof "+str+' == "undefined" ? '+fs.replace(/\)$/g,"")+" : "+str+")"}else{str=fname+"("+str+((!fs||fs==")")?")":","+fs)}}return"__output.push(_escape("+str+"));"}function getLineNumber(tplString,pointer){return tplString?(tplString.substr(0,pointer+1).match(/\n/g)||[]).length+1:0}function setKeyV(obj,value){var k=Math.random()*100000>>0;while(!obj["__`begin`__"+k+"__`end`__"]){k++;obj["__`begin`__"+k+"__`end`__"]=value}return"__`begin`__"+k+"__`end`__"}Ursa.ioStart=function(){return"function (__context) {var __output = [];with(__context) {"};Ursa.ioEnd=function(){return'};return __output.join("");}'};Ursa.ioHTML=function(ins){return'__output.push("'+_escape(ins,"js")+'");'};Ursa.ioOutput=function(ins){return output(ins)};Ursa.ioOP=function(ins){return compileOperator(ins)+";"};Ursa.ioMerge=function(matches,sourceObj,flag){return merge(tagsReplacer[matches],sourceObj,flag)};Ursa.set=function(key,value){Ursa[key]=value};function SyntaxGetter(tplString){var pointer=-1,tplString=cleanWhiteSpace(tplString),character,stack="",statement="",endType="",tree=[],oldType,result=Ursa.ioStart(),tagStack=[],tagStackPointer=[],strDic={},type=false;while((character=tplString.charAt(++pointer))!=""){id=tagStackPointer.length;if(type==3){if(character==commentEnder){character=tplString.charAt(++pointer);if(character==ender){type=false}}continue}if(type%3==1&&(character=="'"||character=='"')){var start=tplString.charAt(pointer),tmpStr=start;while((character=tplString.charAt(++pointer))&&(character!=start)){if(character=="\\"){tmpStr+="\\";character=tplString.charAt(++pointer)}tmpStr+=character}tmpStr+=start;stack+=setKeyV(strDic,tmpStr)}else{if(character=="\\"){type=2;stack+=character+character}else{if(character==starter){character=tplString.charAt(++pointer);oldType=type;switch(character){case commentStarter:type=3;break;case opStarter:type=4;break;case statementStarter:type=1;break;default:stack+=starter;if(character.match(/[\'\"]/g)){pointer--}else{stack+=character}continue;break}if(oldType==2){result+=Ursa.ioHTML(stack);stack=""}else{if(character==ender){}}}else{if(endType=character.match(endStartReg)){endType=endType[0];if(type!=2){character=tplString.charAt(++pointer);if(character==ender){if(endType==opEnder){result+=Ursa.ioOutput(_trim(stack))}else{var start=tagStackPointer[tagStackPointer.length-1],matches,flag=start&&start.type,source=_trim(stack),id=1;if((matches=source.match(tags))){matches=matches[0];if(matches.indexOf("end")==0){id=tagStackPointer.length;flag=tagStack.splice(start.p,tagStack.length-start.p).length>1;tagStackPointer.splice(tagStackPointer.length-1,1)}else{if(matches!="set"){tagStack.push(matches);if(matches=="if"||matches=="for"){tagStackPointer.push({p:tagStack.length-1,type:matches})}id=tagStackPointer.length}}result+=Ursa.ioMerge(matches,{statement:source.replace(new RegExp("^"+matches+"[\\s]*","g"),"")},flag)}else{result+=Ursa.ioOP(source)}}type=false;stack="";continue}else{if(character.match(endStartReg)){pointer--;stack+=endType;continue}else{stack+=endType+character}}}else{stack+=endType}}else{if(!type){type=2}stack+=character}}}}}if(stack){if(type==2){result+=Ursa.ioHTML(stack);stack=null}else{dumpError(8,stack)}}result+=Ursa.ioEnd();if(tagStack.length){dumpError(5,tplString,pointer,tagStack)}return redoGetStrings(result.replace(/\n/g,""),strDic)}Ursa.parse=SyntaxGetter;Ursa.setConfig=setConfig})();if(__ssjs__){exports.Ursa=Ursa}else{if(window.define){define("Ursa",[],function(){return Ursa})}}define("tpl",["./Ursa"],function(a){a.setConfig({starter:"<",ender:">"});return{render:function(b,c){return a.render(+new Date(),c,b)}}});define("ui",[],function(){return{checkbox:function(c){c=$(c);var d=c.data("target");if(!d){return}d=$("#"+d);var b="checkbox-checked";function a(f){if(f){c.prop("checked",c.prop("checked")?null:true)}var e=c.prop("checked");e?d.addClass(b):d.removeClass(b)}c.click(function(){a()});c.on("focus",function(){if(this.blur&&window.attachEvent){this.blur()}});d.click(function(){a(1)})}}});
/*! http://mths.be/placeholder v2.0.7 by @mathias */
(function(b,j,d){var a="placeholder" in j.createElement("input");var g="placeholder" in j.createElement("textarea");var m=d.fn;var e=d.valHooks;var h=d.propHooks;var l;var k;if(a&&g){k=m.placeholder=function(){return this};k.input=k.textarea=true}else{k=m.placeholder=function(){var n=this;n.filter((a?"textarea":":input")+"[placeholder]").not(".placeholder").bind({"focus.placeholder":f,"blur.placeholder":c}).data("placeholder-enabled",true).trigger("blur.placeholder");return n};k.input=a;k.textarea=g;l={get:function(p){var n=d(p);var o=n.data("placeholder-password");if(o){return o[0].value}return n.data("placeholder-enabled")&&n.hasClass("placeholder")?"":p.value},set:function(p,q){var n=d(p);var o=n.data("placeholder-password");if(o){return o[0].value=q}if(!n.data("placeholder-enabled")){return p.value=q}if(q==""){p.value=q;if(p!=j.activeElement){c.call(p)}}else{if(n.hasClass("placeholder")){f.call(p,true,q)||(p.value=q)}else{p.value=q}}return n}};if(!a){e.input=l;h.value=l}if(!g){e.textarea=l;h.value=l}d(function(){d(j).delegate("form","submit.placeholder",function(){var n=d(".placeholder",this).each(f);setTimeout(function(){n.each(c)},10)})});d(b).bind("beforeunload.placeholder",function(){d(".placeholder").each(function(){this.value=""})})}function i(o){var n={};var p=/^jQuery\d+$/;d.each(o.attributes,function(r,q){if(q.specified&&!p.test(q.name)){n[q.name]=q.value}});return n}function f(o,p){var n=this;var q=d(n);if(n.value==q.attr("placeholder")&&q.hasClass("placeholder")){if(q.data("placeholder-password")){q=q.hide().next().show().attr("id",q.removeAttr("id").data("placeholder-id"));if(o===true){return q[0].value=p}q.focus()}else{n.value="";q.removeClass("placeholder");n==j.activeElement&&n.select()}}}function c(){var r;var n=this;var q=d(n);var p=this.id;if(n.value==""){if(n.type=="password"){if(!q.data("placeholder-textinput")){try{r=q.clone().attr({type:"text"})}catch(o){r=d("<input>").attr(d.extend(i(this),{type:"text"}))}r.removeAttr("name").data({"placeholder-password":q,"placeholder-id":p}).bind("focus.placeholder",f);q.data({"placeholder-textinput":r,"placeholder-id":p}).before(r)}q=q.removeAttr("id").hide().prev().attr("id",p).show()}q.addClass("placeholder");q[0].value=q.attr("placeholder")}else{q.removeClass("placeholder")}}}(this,document,jQuery));define("lib/jquery.placeholder",function(){});define("reg",["./common","./form","./conf","./utils","./tpl","./ui","./lib/jquery.placeholder"],function(c,d,i,l,b,k){var j={};var f=function(p,o){if(!p.parent().parent().find("."+o).length){p.parent().parent().append('<span class="'+o+'"></span>')}};var n=function(p,o){return p.parent().parent().find("."+o)};var e={};var m=function(p,o){var s=p.find('input[name="username"]');if(!s||!s.length){o&&o(0);return}if(!s.val().length){o&&o(-1);return}var q=n(s,"error");if(q&&q.length&&q.css("display")!="none"){o&&o(-1);return}function r(t){f(s,"error");n(s,"error").show().html(t);n(s,"desc").hide()}if(e[s.val()]){r(e[s.val()]);o&&o(1);return}$.get("/web/account/checkusername",{username:s.val(),t:+new Date()},function(t){t=l.parseResponse(t);if(!+t.status){o&&o(0)}else{r(t.statusText);e[s.val()]=t.statusText;o&&o(1)}})};var h=function(p){var o=$(".main-content .form form");d.render(o,{onformfail:function(){m(o);return false},onbeforesubmit:function(){m(o,function(q){if(!q){var r=o.find(".form-desc input").prop("checked");if(!r){d.showFormError("请阅读协议");return}$.post(o.attr("action"),o.serialize(),function(t){t=l.parseResponse(t);if(!+t.status){g[p]&&g[p](o,t)}else{if(+t.status==20221){var s=o.find(".token").val();o.find(".vpic img").attr("src","/captcha?token="+s+"&t="+(+new Date()))}d.showFormError(t.statusText)}})}return false})}});o.append('<input type="hidden" name="ru" value="'+(j.ru||"")+'" class="ru"/>');o.find("input[name=username]").blur(function(){var q=$(this).parent().parent().find(".error");if(!q||!q.length||q.css("display")=="none"){setTimeout(function(){m(o)},100)}})};var g={common:function(p,q){var o=q.data.ru;location.href=o},nick:function(o,p){g.common(o,p)},tel:function(o,p){g.common(o,p)},email:function(o,p){$(".main-content .step").addClass("step2");o.parent().html(b.render($("#Target3").html(),{sec_email:o.find('input[name="username"]').val()}));c.bindJumpEmail();c.bindResendEmail(p)}};function a(o){$("#JumpTarget").html(o.email||"");c.bindJumpEmail();c.bindResendEmail(o)}if((new RegExp("[?&]client_id=([^&]+)","g").exec(location.href))[1]==1014){$("#closeMailTip").show()}return{init:function(q){var o={};try{o=$.evalJSON(_server_data);j=o.data||{}}catch(s){o={};window.console&&console.log(s)}c.addUrlCommon(j);c.showBannerUnderLine();h(q);$(".nav").show();d.initTel();k.checkbox("#ConfirmChb");if(q==="remind"){a(j)}else{if("emailsuccess"===q){var p=Ursa.render("x",{data:o},$("#result-tpl").html());$("#result-tpl").parent().html(p);if(o.status==0&&o.data&&o.data.ru){var r=5;setInterval(function(){$("#time").text(r--);if(!r){location.href=o.data.ru}},1000)}}}}}});