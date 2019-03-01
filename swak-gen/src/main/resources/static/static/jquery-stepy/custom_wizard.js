/* [ ---- Gebo Admin Panel - wizard ---- ] */

	$(document).ready(function() {
		//* simple wizard
		public_wizard.simple();
		//* wizard with validation
		public_wizard.validation();
		//* add step numbers to titles
		public_wizard.steps_nb();
	});

	public_wizard = {
		simple: function(){
			$('#simple_wizard').stepy({
				titleClick	: true,
				nextLabel:      '下一步  <i class="icon-chevron-right icon-white"></i>',
				backLabel:      '<i class="icon-chevron-left"></i> 上一步'
			});
		},
		validation: function(){
			$('#validate_wizard').stepy({
				nextLabel:      '下一步 <i class="icon-chevron-right icon-white"></i>',
				backLabel:      '<i class="icon-chevron-left"></i> 上一步',
				block		: true,
				errorImage	: true,
				titleClick	: true,
				validate	: true
			});
		},
		//* add numbers to step titles
		steps_nb: function(){
			$('.stepy-titles').each(function(){
				$(this).children('li').each(function(index){
					var myIndex = index + 1
					$(this).append('<span class="stepNb">'+myIndex+'</span>');
				})
			})
		}
	};