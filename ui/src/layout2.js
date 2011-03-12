/**
 * center
 */
(function($) {
	//
	$.fn.layout = function(options) {
		debug(this);
		// �������
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

	// ˽�к�����debugging
	function debug($obj) {
		if (window.console && window.console.log) {
			window.console.log('hilight selection count: ' + $obj.size());
		}
	}
	;
	// ���屩¶format����
	$.fn.layout.close = function(txt) {
		return '<strong>' + txt + '</strong>';
	};

	// ����� ����defaults
	$.fn.layout.defaults = {
		foreground : 'red',
		background : 'yellow'
	};
	// �հ�����
})(jQuery);