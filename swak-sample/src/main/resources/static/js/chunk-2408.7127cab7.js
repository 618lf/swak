(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2408"],{"0210":function(t,e,a){"use strict";var n=a("e1d2"),i={get:function(t){return Object(n["a"])({url:"/api/manage/area/get/"+t,method:"get"})},list:function(){return Object(n["a"])({url:"/api/manage/area/areas",method:"get"})},provinces:function(){return Object(n["a"])({url:"/api/manage/area/provinces",method:"get"})},citys:function(t){return Object(n["a"])({url:"/api/manage/area/citys?province="+t,method:"get"})},save:function(t){return Object(n["a"])({url:"/api/manage/area/save",method:"post",data:t})},delete:function(t){return Object(n["a"])({url:"/api/manage/area/delete",method:"post",data:t})}};e["a"]=i},"0ccd":function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"tax-page area"},[a("h4",[t._v("区域设置")]),a("div",{staticClass:"content"},[t._m(0),a("div",{staticClass:"area-settings"},[a("div",{staticClass:"provinces -settings"},[a("div",{staticClass:"-name"},[t._v("省")]),a("div",{staticClass:"-items"},t._l(t.provinces,function(e){return a("swak-link",{key:e.id,class:t.selected==e.id?"selected":"",attrs:{id:e.id,clazz:"-item"},on:{"link:click":t.provinceSelect}},[t._v(t._s(e.name)+"\n          ")])}))]),a("div",{staticClass:"citys -settings"},[a("div",{staticClass:"-name"},[t._v("市")]),a("div",{staticClass:"-items"},t._l(t.citys,function(e){return a("swak-link",{key:e.id,attrs:{clazz:"-item"}},[t._v(t._s(e.name))])}))])])])])},i=[function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"options"},[a("span",[t._v("设置支持的区域")])])}],s=a("0210"),c=a("882d"),r={components:{SwakLink:c["a"]},name:"areas",data:function(){return{provinces:[],citys:[],selected:""}},mounted:function(){this.$emit("layout:load","area"),this.loadProvinces()},methods:{loadProvinces:function(){var t=this;s["a"].provinces().then(function(e){t.provinces=e.obj})},loadCitys:function(t){var e=this;s["a"].citys(t).then(function(t){e.citys=t.obj})},provinceSelect:function(t){var e=t.target,a=e.getAttribute("id");this.selected=a,this.loadCitys(a)},onDelete:function(t){var e=this,a=t.target.getAttribute("data-id");this.$confirm("确认删除此区域，如果用户已选择此区域，删除会导致用户计税失败","确认删除？",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){s["a"].delete({id:a}).then(function(t){e.list(),e.$notify({title:"成功",message:"删除成功!",type:"success"})})}).catch(function(){})}}},o=r,u=(a("1d89"),a("2877")),l=Object(u["a"])(o,n,i,!1,null,"2be0cdb8",null);l.options.__file="area.vue";e["default"]=l.exports},"1d89":function(t,e,a){"use strict";var n=a("ff7a"),i=a.n(n);i.a},ff7a:function(t,e,a){}}]);
//# sourceMappingURL=chunk-2408.7127cab7.js.map