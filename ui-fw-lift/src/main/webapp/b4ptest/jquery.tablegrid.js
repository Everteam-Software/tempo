/**
 * 表格 zebra crossing & hover & click 效果插件
 *
 * @author     Akon(番茄红了) <aultoale@gmail.com>
 * @copyright  Copyright (c) 2008 (http://www.tblog.com.cn)
 * @license    http://www.gnu.org/licenses/gpl.html     GPL 3
 *
 * @example $('table').tablegrid();
 *
 * @params {oddColor, evenColor, overColor, selColor, useClick}
 * oddColor  : 奇数行背景色
 * evenColor : 偶数行背景色
 * overColor : 鼠标悬停时背景色
 * selColor  : 行选中时背景色
 * useClick  : 是否启用点击选中
 */

$.fn.tablegrid = function(params){
    var options = {
        oddColor   : '#E0EFFA',
        evenColor  : '#F2F9FD',
        overColor  : '#C0D0E0',
        selColor   : '#FFCC99',
        useClick   : false
    };
    $.extend(options, params);
    $(this).each(function(){
        $(this).find('tr:odd').css('backgroundColor', options.oddColor);
        $(this).find('tr:even').css('backgroundColor', options.evenColor);
        $(this).find('tr').each(function(){
            this.origColor = $(this).css('backgroundColor');
            this.clicked = false;
            if (options.useClick) {
                $(this).click(function(){
                    if (this.clicked) {
                        $(this).css('backgroundColor', this.origColor);
                        this.clicked = false;
                    } else {
                        $(this).css('backgroundColor', options.selColor);
                        this.clicked = true;
                    }
                    $(this).find('td > input[@type=checkbox]').attr('checked', this.clicked);
                });
            }
            $(this).mouseover(function(){
                $(this).css('backgroundColor', options.overColor);
            });
            $(this).mouseout(function(){
                if (this.clicked) {
                    $(this).css('backgroundColor', options.selColor);
                } else {
                    $(this).css('backgroundColor', this.origColor);
                }
            });
        });
    });
};