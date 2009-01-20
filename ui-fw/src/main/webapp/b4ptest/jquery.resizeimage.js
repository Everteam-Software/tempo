/**
 * Resize Image plugin
 *
 * Provide simple drag capability like Google Map based on $.event.special.drag
 *
 * @example $("img").resizeImage(width, height);
 * @desc Resize an image within the rectangle constrained by width and height, keeping aspect ratio.
 *
 * @params
 *   width:  rectangle's width
 *   height: rectangle's height
 *
 * @options
 *   enlarge: Set to true to allow enlarge image, defaults to true.
 *   debug:  Set to true to display rectangle's border, defaults to false.
 *
 * @version 0.2
 * @author zykarl/zheng.yi@sh.adways.net
 */

(function($) {
  $.fn.resizeImage = function(width, height, options) {
    options = $.extend({}, $.fn.resizeImage.defaults, options);
    return this.each(function() {
      var $this = $(this);
      var opt = $.meta ? $.extend({}, options, $this.data()) : options;
      
      var org_width = $this.width(), org_height = $this.height();
      $this.css({
        width: width,
        height: height
      });     
      width = $this.width(); height = $this.height();
      var target_width = width, target_height = height;     
      if (!opt.enlarge && org_width <= target_width && org_height <= target_height) {
        target_width = org_width;
        target_height = org_height;
      }
      var width_ratio = target_width / org_width, height_ratio = target_height / org_height;
      
      if (width_ratio <= height_ratio) {
        target_height = org_height * width_ratio;
        $this.css({
          width: target_width,
          height: target_height
        });
      } else {
        target_width = org_width * height_ratio;
        $this.css({
          width: target_width,
          height: target_height
        });
      }
      $this.css({
          marginLeft: (width - target_width) / 2,
          marginTop: (height - target_height) / 2
      });
      
      if (opt.debug) {
        $this.wrap("<div />");
        $this.parent().css({
          width:  width,
          height: height,
          border: "1px solid black"
        });
      }
    });
  };
  
  $.fn.resizeImage.defaults = {
    enlarge: false,
    debug: false
  };
})(jQuery);
