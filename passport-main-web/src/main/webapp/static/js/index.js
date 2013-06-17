
/*
 * ui module script
 * @author zhengxin
*/
 



define('ui',[],function(){



    return{
        checkbox: function(el){
            el = $(el);
            var target = el.data('target');
            if( !target ) return;
            target = $('#' +target);
            var checkedClass = 'checkbox-checked';
            
            el.click( function(){
                var checked = el.prop('checked');
                checked ? target.addClass(checkedClass) :
                    target.removeClass(checkedClass);
            });
        }

    };
});

/*
 * form module script
 * @author zhengxin
*/
 



define('utils',[], function(){

    
    return {
        uuid: function(){
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            };            
            return s4() + s4()  + s4()  + s4()  +
                s4() +  s4() + s4() + s4();

        },
        parseResponse: function(data){
            if( typeof data == 'string' ){
                try{
                    data = eval('('+data+')');
                }catch(e){
                    data = {status:-1,statusText:'服务器故障'};
                }
            }
            return data;
        }
    };

});

define('conf',[],function(){


    return{
        client_id:"1120",
        redirectUrl: "/static/api/jump.htm"
    };
});

define('index' , ['./ui' , './utils' , './conf'] , function(ui , utils , conf){
    
    var vcodeInited = false;
    var initVcode = function(){
        if( vcodeInited ) return;
        vcodeInited = true;
        var $el = $('#Login');
        $el.parent().parent().addClass('login-vcode');

        $el.find('.vcode img,.vcode a').click(function(){
            refreshVcode();
            return false;
        });

        refreshVcode($el);
    };

    var refreshVcode = function(){
        $('#Login').find('.vcode img').attr('src' , "/captcha?token="+ $('#Login').find('.token').val() + '&t=' + +new Date() );
    };

    var showVcodeError = function(text){
        $('#Login .vcode-error .text').html(text).parent().show();
    };

    var showUnameError = function(text){
        $('#Login .uname-error .text').html(text).parent().show();
    };

    return {
        init: function(){
            ui.checkbox('#RemChb');

            PassportSC.appid = conf.client_id;
            PassportSC.redirectUrl = location.protocol +  '//' + location.hostname + ( location.port ? (':' + location.port) :'' ) + conf.redirectUrl;
            $('#Login').on('submit' , function(){
                var $el = $('#Login');
                PassportSC.loginHandle( $el.find('input[name="username"]').val() , 
                                        $el.find('input[name="password"]').val() ,
                                        $el.find('input[name="captcha"]').val() , 
                                        $el.find('input[name="autoLogin"]').val(),
                                        document.getElementById('logdiv'),
                                        function(data){
                                            if( data.data.needCaptcha ){
                                                initVcode();
                                                showVcodeError('请输入验证码');
                                            }
                                            if( +data.status == 20221 ){//vcode
                                                showVcodeError('请输入验证码');
                                            }else{
                                                showUnameError('用户名或密码错，请重新输入');
                                            }
                                        } ,
                                        function(){
                                            alert('登录成功');
                                            return;
                                        }
                                      );
                
                return false;
            });

            $(document.body).click(function(){
                $('#Login .error').hide();
            });
            $('#Login .error a').click(function(){
                $('#Login .error').hide();
                return false;
            });
        }
    };
});
