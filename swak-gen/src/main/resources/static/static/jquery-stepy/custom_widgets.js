/* [ ---- Gebo Admin Panel - widgets ---- ] */

    $(document).ready(function() {
        gebo_sortable.init();
    });
    gebo_sortable = {
        init: function() {
            
            var thisCookie = $.cookie('sortOrder');
            if(thisCookie != null) {
                $.each(thisCookie.split(';'),function(i,id) {
                    thisSortable = $('#sortable_panels div[class*="span"]').not('.not_sortable').get(i);
                    if(id != 'null'){
                        $.each(id.split(','),function(i,id) {
                            $("#"+id).appendTo(thisSortable);
                        });
                    }
                })
            }
            
            $('#sortable_panels div[class*="span"]').not('.not_sortable').sortable({
                connectWith: '#sortable_panels div[class*="span"]',
                helper: 'original',
                handle: '.w-box-header',
                cancel: ".sort-disabled",
                forceHelperSize: true,
                forcePlaceholderSize: true,
                tolerance: 'pointer',
                activate: function(event, ui) {
                    $(".ui-sortable").addClass('sort_ph');
                },
                stop: function(event, ui) {
                    $(".ui-sortable").removeClass('sort_ph');
                },
                update: function (e, ui) {
                    var elem = [];
                    $('#sortable_panels div[class*="span"]').not('.not_sortable').each(function(){
                        elem.push($(this).sortable("toArray"));
                    });
                    var str = '';
                    var m_len = elem.length;
                    jQuery.each(elem, function(index,value) {
                        var s_len = value.length;
                        if(value == '') {
                            str += 'null';
                        } else {
                            jQuery.each(value, function(index,value) {
                                str += value;
                                if (index != s_len - 1) {
                                    str += ","
                                }
                            });
                        }
                        if (index != m_len - 1) {
                            str += ";"
                        }
                    });
                    $.cookie('sortOrder', str, { expires: 7});
                }
            });
			
			$('.reset_layout').click(function(){
				$.cookie('sortOrder', null);
				location.reload();
			});
        }
    };
