/**
 * 面板
 */
(function($) {
	// public构造函数
	$.fn.panel = function(options, param) {
		// 构造参数
		var opts = $.extend({}, $.fn.layout.defaults, options);

		// 处理dom对象
		this.css({
			backgroundColor : o.background,
			color : o.foreground
		});

		var markup = $this.html();
		// call our format function
		markup = $.fn.hilight.format(markup);
		$this.html(markup);

	};

	// jQuery类私有成员函数：private void debugging(a)
	function debug($obj) {
		if (window.console && window.console.log) {
			window.console.log('hilight selection count: ' + $obj.size());
		}
	}

	// public成员函数属性
	$.fn.panel.defaults = {
		backgroundColor : null,
		color : red
	};

	// jQuery类的public static类函数 public static add(a,b)
	$.extend({
		add : function(a, b) {
			return a + b;
		}
	});

})(jQquery);
