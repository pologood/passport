(function(){ 
    var passhref = '';
    if( location.protocol.indexOf('https') != -1 ){
        passhref = 'https://account.sogou.com/static';
    }else{
        passhref = 'http://s.account.sogoucdn.com/u';
    }
    if (undefined !== window.__sogoujsStartLoading) {
        window.__sogoujsStartLoading = +new Date;
    }
    document.write('<script type="text/javascript" src="'+ passhref +'/api/sogou.js?t=2014080801"></script>');
})();
