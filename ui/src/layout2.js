/**
 * center
 */
(function($) {
	//
	$.fn.layout = function(options) {
		debug(this);
		// 构造参数
		var opts = $.extend({}, $.fn.layout.defaults, options);

		// iterate and reformat each matched element
		return this.each(function() {
			$this = $(this);
			// build element specific options
			var o = $.meta ? $.extend({}, opts, $this.data()) : opts;
			// update element styles
			$this.css({
				backgroundColor : o.background,
				color : o.foreground
			});
			var markup = $this.html();
			// call our format function
			markup = $.fn.hilight.format(markup);
			$this.html(markup);
		});

	};

	// 私有函数：debugging
	function debug($obj) {
		if (window.console && window.console.log) {
			window.console.log('hilight selection count: ' + $obj.size());
		}
	}
	;
	// 定义暴露format函数
	$.fn.layout.close = function(txt) {
		return '<strong>' + txt + '</strong>';
	};

	// 插件的 属性defaults
	$.fn.layout.defaults = {
		foreground : 'red',
		background : 'yellow'
	};
	// 闭包结束
})(jQuery);