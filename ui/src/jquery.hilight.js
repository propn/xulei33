/**
 * JQuery-ui plugin
 */
// ����һ���հ�
(function($) {
	// ����Ķ���
	$.fn.hilight = function(options) {
		//debug(this);
		
		// build main options before element iteration
		var opts = $.extend({}, $.fn.hilight.defaults, options);
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
	// ˽�к�����debugging
	function debug($obj) {
		if (window.console && window.console.log)
			window.console.log('hilight selection count: ' + $obj.size());
	}
	;
	// ���屩¶format����
	$.fn.hilight.format = function(txt) {
		return '<strong>' + txt + '</strong>';
	};
	
	// �����defaults
	$.fn.hilight.defaults = {
		foreground : 'red',
		background : 'yellow'
	};
	// �հ�����
})(jQuery);
