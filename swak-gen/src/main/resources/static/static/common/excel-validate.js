/**JS 校验器*/
var validator = {
	messages: {
		required : "必填信息",
		remote : "请修正该信息",
		email : "请输入正确格式的电子邮件",
		url : "请输入合法的网址",
		date : "请输入合法的日期",
		dateISO : "请输入合法的日期 (ISO).",
		number : "请输入合法的数字",
		digits : "只能输入整数",
		creditcard : "请输入合法的信用卡号",
		equalTo : "请再次输入相同的值",
		accept : "请输入拥有合法后缀名的字符串",
		maxlength : "请输入一个长度最多是 {0} 的字符串",
		minlength : "请输入一个长度最少是 {0} 的字符串",
		rangelength : "请输入一个长度介于 {0} 和 {1} 之间的字符串",
		range : "请输入一个介于 {0} 和 {1} 之间的值",
		max : "请输入一个最大为 {0} 的值",
		min : "请输入一个最小为 {0} 的值",
		ip : "请输入合法的IP地址",
		abc: "请输入字母数字或下划线",
		username: "字母或数字开头，允许字母数字下划线",
		userName: "只能包括中文字、英文字母、数字和下划线",
		noEqualTo: "请再次输入不同的值",
		realName: "只能为2-30个汉字",
		mobile: "请正确填写您的手机号码",
		simplePhone: "请正确填写您的电话号码",
		phone: "格式为:固话为区号(3-4位)号码(7-9位),手机为:13,15,18号段",
		zipCode: "请正确填写您的邮政编码",
		qq: "请正确填写您的QQ号码",
		card: "请输入正确的身份证号码(15-18位)",
		regexp: "请修正该信息"
	},
	tips: {
		required : "必填项",
		remote : "sql验证",
		email : "电子邮件",
		url : "网址",
		date : "日期",
		dateISO : "日期 (ISO)",
		number : "数字",
		digits : "整数",
		creditcard : "信用卡号",
		equalTo : "和X相同",
		accept : "后缀名",
		maxlength : "长度最多是",
		minlength : "长度最少是",
		rangelength : "长度介于",
		range : "值介于",
		max : "最大值",
		min : "最小值",
		ip : "IP地址",
		abc: "字母数字或下划线",
		username: "字母数字下划线（字母数字开头）",
		userName: "中文字母数字下划线",
		noEqualTo: "和X不同",
		realName: "2-30个汉字",
		mobile: "手机号码",
		simplePhone: "电话号码",
		phone: "手机号码或电话号码（严格）",
		zipCode: "邮政编码",
		qq: "QQ号码",
		card: "身份证号码",
		regexp: "正则验证"
	},
	methods : {
		required : function(v) {
			return !!v && v.length > 0
		},
		minlength : function(v, e) {
			var _v = v.length;
			return _v >= e;
		},
		maxlength : function(v, e) {
			var _v = v.length;
			return _v <= e;
		},
		rangelength : function(v, e) {
			var _v = v.length;
			return (_v >= e[0] && _v <= e[1]);
		},
		min : function(v, e) {
			return v >= e;
		},
		max : function(v, e) {
			return v <= e;
		},
		range : function(v, e) {
			return (v >= e[0] && v <= e[1]);
		},
		email : function(v) {
			return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i
			.test(v);
		},
		url : function(v) {
			return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i
					.test(v);
		},
		date : function(v) {
			return !/Invalid|NaN/.test(new Date(v).toString());
		},
		dateISO : function(v) {
			return /^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}$/.test(v);
		},
		number : function(v) {
			return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(v);
		},
		digits : function(v) {
			return /^\d+$/.test(v);
		},
		creditcard : function(f) {
			if (/[^0-9 \-]+/.test(f)) {
				return false
			}
			var g = 0, e = 0, b = false;
			f = f.replace(/\D/g, "");
			for (var h = f.length - 1; h >= 0; h--) {
				var d = f.charAt(h);
				e = parseInt(d, 10);
				if (b) {
					if ((e *= 2) > 9) {
						e -= 9
					}
				}
				g += e;
				b = !b
			}
			return (g % 10) === 0;
		},
		ip : function(c) {
			return (/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(c) && (RegExp.$1 < 256
					&& RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256));
		},
		abc : function(c) {
			return /^[a-zA-Z0-9_]*$/.test(c);
		},
		username : function(c) {
			return /^[a-zA-Z0-9][a-zA-Z0-9_]$/.test(c);
		},
		userName : function(c) {
			return /^[\u0391-\uFFE5\w]+$/.test(c)
		},
		realName : function(c) {
			return /^[\u4e00-\u9fa5]{2,30}$/.test(c);
		},
		mobile : function(c) {
			var b = c.length;
			return (b == 11 && /^1(3|4|5|7|8)\d{9}$/.test(c));
		},
		simplePhone : function(c) {
			var a = /^(\d{3,4}-?)?\d{7,9}$/g;
			return (a.test(c));
		},
		phone : function(c){
			var a = /(^0[1-9]{1}\d{9,10}$)|(^1[3,5,8]\d{9}$)/g;
			return (a.test(c));
		},
		zipCode : function(c) {
			var a = /^[0-9]{6}$/;
			return(a.test(c));
		},
		qq : function(c) {
			var a = /^[1-9][0-9]{4,}$/;
			return (a.test(c));
		},
		card: function(d) {
			d = d.toString();
			var f = new Array(true, false, false, false, false);
			var e = {
				11 : "北京",
				12 : "天津",
				13 : "河北",
				14 : "山西",
				15 : "内蒙古",
				21 : "辽宁",
				22 : "吉林",
				23 : "黑龙江",
				31 : "上海",
				32 : "江苏",
				33 : "浙江",
				34 : "安徽",
				35 : "福建",
				36 : "江西",
				37 : "山东",
				41 : "河南",
				42 : "湖北",
				43 : "湖南",
				44 : "广东",
				45 : "广西",
				46 : "海南",
				50 : "重庆",
				51 : "四川",
				52 : "贵州",
				53 : "云南",
				54 : "西藏",
				61 : "陕西",
				62 : "甘肃",
				63 : "青海",
				64 : "宁夏",
				65 : "新疆",
				71 : "台湾",
				81 : "香港",
				82 : "澳门",
				91 : "国外"
			};
			var d, g, b;
			var c, h;
			var a = new Array();
			a = d.split("");
			if (e[parseInt(d.substr(0, 2))] == null) {
				return f[4]
			}
			switch (d.length) {
			case 15:
				if ((parseInt(d.substr(6, 2)) + 1900) % 4 == 0
						|| ((parseInt(d.substr(6, 2)) + 1900) % 100 == 0 && (parseInt(d
								.substr(6, 2)) + 1900) % 4 == 0)) {
					ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/
				} else {
					ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/
				}
				if (ereg.test(d)) {
					return f[0]
				} else {
					return f[2]
				}
				break;
			case 18:
				if (parseInt(d.substr(6, 4)) % 4 == 0
						|| (parseInt(d.substr(6, 4)) % 100 == 0 && parseInt(d.substr(6,
								4)) % 4 == 0)) {
					ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/
				} else {
					ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/
				}
				if (ereg.test(d)) {
					c = (parseInt(a[0]) + parseInt(a[10])) * 7
							+ (parseInt(a[1]) + parseInt(a[11])) * 9
							+ (parseInt(a[2]) + parseInt(a[12])) * 10
							+ (parseInt(a[3]) + parseInt(a[13])) * 5
							+ (parseInt(a[4]) + parseInt(a[14])) * 8
							+ (parseInt(a[5]) + parseInt(a[15])) * 4
							+ (parseInt(a[6]) + parseInt(a[16])) * 2 + parseInt(a[7])
							* 1 + parseInt(a[8]) * 6 + parseInt(a[9]) * 3;
					g = c % 11;
					h = "F";
					b = "10X98765432";
					h = b.substr(g, 1);
					if (h == a[17]) {
						return f[0]
					} else {
						return f[3]
					}
				} else {
					return f[2]
				}
				break;
			default:
				return f[1];
				break
			}
		},
		regexp : function(c, e) {
			return e.test(c);
		},
		equalTo : function(c) { // 暂时不支持
			return true;
		},
		noEqualTo : function(c) { // 暂时不支持
			return true;
		}
	},
	doValidator: function(v, rules) { // 都是字符串
		try{
			var _rules = eval(rules);
			var r = _rules[0], p = _rules[1], m = _rules[2], bs = [];
			var _m = function(m1, m2) {
				if(!!m2 && m2 instanceof Array) {
					for(var i = 0; i< m2.length; i++ ) {
						var re = new RegExp('\\{'+i+'\\}', 'gm');
						m1 = m1.replace(re, m2[i])
					}
				} else if(!!m2) {
					var re = new RegExp('\\{0\\}', 'gm');
					m1 = m1.replace(re, m2);
				}
				return m1;
			};
			for(var i = 0; i< r.length; i++ ) {
				var j = r[i];
				var g = (function(){
					var _p = p[i];
					return (!!_p && _p.indexOf(',')!= -1)? _p.split(',') : _p ;
				})();
				var b = this.methods[j].call(this, v, g);
				if(!b) {
					bs.push(_m(m[i], g));
				}
			}
			return bs.join(';');
		}catch(e) {
			return e.message;
		}
	}
};