/**
 * jQuery suggest plugin.
 * @author yinyong#sogou-inc.com
 * @date 2013/11/05 19:10:00
 * @version 0.0.1
 */
(function(window, document, $, undefined) {
    'use strict';
    //check if support oninput event.
    var supportInputEvt = ('oninput' in document.createElement('input'));
   
   //key that cannot be input with shift 
    var disabledShiftKeyMap={
        '`':192,
        '!':49,
        '#':51,
        '$':52,
        '%':53,
        '^':54,
        '&':55,
        '*':56,
        '(':57,
        ')':48
    };
    //key cannot be input
    var disabledKeyMap={
        '-':189,
        '+':187,
        '[':219,
        ']':221,
        '|':220,
        ';':186,
        "'":222,
        ',':188,
        '/':191,
        '-/':111,
        '-*':106,
        '--':109,
        '-+':107
    };
    //key that should not affect suggestion
    var noActionKeyMap={
        '←':37,
        '→':39,
        CTRL:17,
        SHIFT:16,
        ALT:18,
        COMMAND:91,
        'CAPS-LOCK':20,
        IME:229,
        INSERT:45,
        HOME:36,
        END:35,
        'PAGE-UP':33,
        'PAGE-DOWN':34,
        'NUM-LOCK':144
    };

    //all key map
    var keyMap={
        '.':190,
        '-.':110,
         '@':50,
         '↑':38,
         '↓':40,
         ENTER:13,
         SPACE:32,
         ESC:27,
         TAB:9
    };

    $.extend(keyMap,disabledShiftKeyMap);
    $.extend(keyMap,disabledKeyMap);
    $.extend(keyMap,noActionKeyMap);
    /**
     * Create a regexp pattern that match the keyMap.
     * @param  {Object} map keyMap
     * @return {RegExp}    match
     * @since 0.0.1
     * @version 0.0.1
     */
    function createKeymapReg(map) {
        var ret = [];
        for (var e in map) {
            if ('function' !== typeof map[e] && map.hasOwnProperty(e)) {
                ret.push(map[e]);
            }
        }
        return new RegExp('^(' + ret.join('|') + ')$', "");
    }

    var disabledShiftRegexp=createKeymapReg(disabledShiftKeyMap);
    var disabledRegexp=createKeymapReg(disabledKeyMap);
    var noactionRegexp=createKeymapReg(noActionKeyMap);
    /**
     * Check if ans is the ancestor of sub.
     * @param  {HTMLElement}  ans
     * @param  {HTMLElement}  sub
     * @return {Boolean}
     * @version 0.0.1
     * @since 0.0.1
     */
    function isAncestor(ans, sub) {
        if (!sub || !ans) return false;
        while (sub && sub !== ans) {
            sub = sub.parentNode;
        }
        return sub === ans;
    }
    /**
     * Suggestion Class
     * @param {InputElement} ele    
     * @param {Object} options
     * @version 0.0.1
     * @since 0.0.1
     */
    function Suggestion(ele, options) {
        var $ele = $(ele),
            self = this;
        self.settings = {
            container: ".sugg",
            focusItemClass: "focus",
            hoverItemClass: "hover",
            constructItem: function(item) {
                return $("<li/>").text(item);
            },
            getItems: function(ele) {
                return [];
            },
            getItemString: function(index, item, ele) {
                return $(ele).text();
            },
            onItemSelected: function(index, item) {
                return true;
            }
        };

        self.isVisible = function() {
            return $(self.settings.container).is(":visible")
        };
        self.eleItems = [];

        $.extend(self.settings, options || {});

        function resetCurrentIndex() {
            self.current = -1;
        }

        resetCurrentIndex();
        /**
         * Put the content of an item to the input element.
         * @param  {Integer} index 
         * @version 0.0.1
         * @since 0.0.1
         */
        function inputItem(index) {
            var c = self.eleItems[index];
            if (!c) return;
            $ele.val(self.settings.getItemString.call($ele, index, self.dataItems[index], c));
        }
        /**
         * Show suggection.
         * @version 0.0.1
         * @since 0.0.1
         */
        function showSuggest() {
            $(self.settings.container).empty();
            //clear
            self.eleItems.splice(0, self.eleItems.length);

            self.dataItems = self.settings.getItems.call($ele);
            if(!self.dataItems.length){return hideSuggest();}

            for (var i = 0; i < self.dataItems.length; ++i) {
                var itemEle = self.settings.constructItem.call($ele, self.dataItems[i]);
                if ('string' === typeof itemEle || (itemEle && itemEle.constructor === String))
                    itemEle = $(itemEle);

                self.eleItems.push(itemEle);
                var clickFunc=(function(index){
                    return function(e){
                        if (self.settings.onItemSelected.call($ele, index, self.dataItems[index])) {
                            inputItem(index);
                        }
                        hideSuggest();
                    }
                })(i);
                itemEle.click(clickFunc).hover(function() {
                    $(this).addClass(self.settings.hoverItemClass);
                }, function() {
                    $(this).removeClass(self.settings.hoverItemClass);
                });
                $(self.settings.container).append(itemEle);
            }
            resetCurrentIndex();
            $(self.settings.container).show();
        };
        /**
         * Hide suggestion.
         * @version 0.0.1
         * @since 0.0.1
         */
        function hideSuggest() {
            $(self.settings.container).empty().hide();
            self.eleItems.splice(0, self.eleItems.length);
            resetCurrentIndex();
        }
        /**
         * Focus(verb) an item.
         * @param  {Integer} index
         * @version 0.0.1
         * @since 0.0.1
         */
        function focusItem(index) {
            for (var i = 0; i < self.eleItems.length; ++i) {
                var c = self.eleItems[i];
                c.removeClass(self.settings.focusItemClass);
            }
            self.eleItems[index].addClass(self.settings.focusItemClass);
        }

        //bind event
        (function() {
            if (supportInputEvt) {
                $ele.on('input', function(e) {
                    showSuggest();
                });
            }

            $ele.blur(function(e){
                //blur could prevent from clicking,so ...
                setTimeout(hideSuggest,200);
            });

            $ele.on("click focus",function(e) {
                if (!self.isVisible()) {
                    showSuggest();
                }
            });

            //keydown:prevent from inputing some illegal key&enable ENTER and SPACE to select.
            $ele.keydown(function(e){
                var code=e.keyCode;
               //disabled !@#$%^&*()-=[];',./  etc.
                if(disabledRegexp.test(code)||(disabledShiftRegexp.test(code)&&e.shiftKey)){
                    e.preventDefault();
                    return false;
                }else if(keyMap['TAB']===code){
                    if (self.isVisible() && !e.shiftKey && self.current < self.eleItems.length - 1) {
                        focusItem(++self.current);
                        e.preventDefault();
                        return false;
                    } else if (self.isVisible() &&e.shiftKey&& self.current > 0 && self.eleItems.length) {
                        focusItem(--self.current);
                        e.preventDefault();
                        return false;
                    }
                   
                }
                else if(keyMap['SPACE']===code||keyMap['ENTER']===code){
                    //Space&Enter=>input
                    if (self.isVisible() && self.current >= 0 && self.current <= self.eleItems.length - 1) {
                        inputItem(self.current);
                        hideSuggest();
                         e.preventDefault();
                    }
                    return ;
                } 
            });
            //Trigger a suggestion show.
            $ele.keyup(function(e) {
                var code = e.keyCode;
                switch (true) {
                    case keyMap['ESC'] === code:
                        hideSuggest();
                        break;
                    case keyMap['↑'] === code:
                        if (self.isVisible() && self.current > 0 && self.eleItems.length) {
                            focusItem(--self.current);
                        }
                        return;
                    case keyMap['↓'] === code:
                        if (self.isVisible() && self.current < self.eleItems.length - 1) {
                            focusItem(++self.current);
                        }
                        return;
                    case noactionRegexp.test(code):
                        return;
                    case keyMap['TAB']===code:
                        return;
                    default:;
                }
                //ENTER and SPACE trigger input but not show.
                if (!supportInputEvt&&keyMap['ENTER']!==code&&keyMap['SPACE']!==code) {
                    showSuggest();
                }
            });
            $(document).click(function(e) {
                if (!$(e.target).is($ele)&&!isAncestor($(self.settings.container),$(e.target)))
                    hideSuggest();
            });
        })();
    }
    /**
     * Suggest API.
     * @param  {Object} options
     * @return {jQuery Object}         this
     */
    $.fn.suggest = function(options) {
        $.each($(this), function(index, item) {
            new Suggestion($(item), options);
        });
        return this;
    };
})(window, document, jQuery);