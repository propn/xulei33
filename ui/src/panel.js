/**
 * ���
 */
(function($) {
	// public���캯��
	$.fn.panel = function(options, param) {
		// �������
		var opts = $.extend({}, $.fn.layout.defaults, options);

		// ����dom����
		this.css({
			backgroundColor : o.background,
			color : o.foreground
		});

		var markup = $this.html();
		// call our format function
		markup = $.fn.hilight.format(markup);
		$this.html(markup);

	};

	// jQuery��˽�г�Ա������private void debugging(a)
	function debug($obj) {
		if (window.console && window.console.log) {
			window.console.log('hilight selection count: ' + $obj.size());
		}
	}

	// public��Ա��������
	$.fn.panel.defaults = {
		backgroundColor : null,
		color : red
	};

	// jQuery���public static�ຯ�� public static add(a,b)
	$.extend({
		add : function(a, b) {
			return a + b;
		}
	});

})(jQquery);
