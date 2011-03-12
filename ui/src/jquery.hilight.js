(function($) {
	$.fn.setTableUI = function(options) {
		var opts = $.extend({}, $.fn.setTableUI.defaults, options);
		
		this.each(function() {
			
			var thisTable = $(this);
			
			$(thisTable).find("tr").bind("mouseover", function() {
				$(this).css({
					color : "#ff0011",
					background : "blue"
				});
			});
			
			$(thisTable).find("tr").bind("mouseout", function() {
				$(this).css({
					color : "#000000",
					background : "white"
				});
			});
			
		});
		
	};
	// 插件的 属性defaults
	$.fn.setTableUI.defaults = {
		evenRowClass : "evenRow",
		oddRowClass : "oddRow",
		activeRowClass : "activeRow"
	};
	// 闭包结束
})(jQuery);