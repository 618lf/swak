(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2f91"],{1017:function(t,e,a){},"1d94":function(t,e,a){"use strict";var s=a("1017"),n=a.n(s);n.a},bfe3:function(t,e,a){"use strict";a.r(e);var s=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"tax-page tax-subject"},[a("h4",[t._v("个税项目")]),a("div",{staticClass:"content"},[t._m(0),a("table",{staticClass:"table sample-table table-bordered table-hover"},[t._m(1),a("tbody",t._l(t.pays,function(e){return a("tr",{key:e.id},[a("td",[t._v(t._s(e.name))]),a("td",[t._v(t._s(e.includeTypeName))]),a("td",[t._v(t._s(e.subjectDesc))]),a("td",{staticClass:"td-ops"},[a("swak-link",{staticClass:"ops",attrs:{to:"/system/taxitem/cfg/"+e.id}},[t._v("配置")])],1)])}))])])])},n=[function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"options"},[a("span",[t._v("设置个税项目，个税项目是计税的最小单位，是我们总结的一套计算单元")])])},function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("thead",[a("tr",[a("th",{attrs:{scope:"col",width:"250"}},[t._v("项目名称")]),a("th",{attrs:{scope:"col",width:"100"}},[t._v("项目类型")]),a("th",{attrs:{scope:"col"}},[t._v("税法描述")]),a("th",{attrs:{scope:"col",width:"40"}},[t._v("配置")])])])}],i=a("c538"),c={name:"user",data:function(){return{pays:[]}},mounted:function(){this.$emit("layout:load","taxitem"),this.loadPays()},methods:{loadPays:function(){var t=this;i["a"].list().then(function(e){t.pays=e.obj||[]})}}},o=c,r=(a("1d94"),a("2877")),l=Object(r["a"])(o,s,n,!1,null,"11f46fd3",null);l.options.__file="taxitem.vue";e["default"]=l.exports},c538:function(t,e,a){"use strict";var s=a("e1d2"),n={get:function(t){return Object(s["a"])({url:"/api/manage/payItem/get/"+t,method:"get"})},list:function(){return Object(s["a"])({url:"/api/manage/payItem/list",method:"get"})},save:function(t){return Object(s["a"])({url:"/api/manage/payItem/save",method:"post",data:t})}};e["a"]=n}}]);
//# sourceMappingURL=chunk-2f91.9d10f385.js.map