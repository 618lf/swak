(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-8724"],{"09e0":function(t,a,n){"use strict";n.r(a);var e=function(){var t=this,a=t.$createElement,n=t._self._c||a;return n("div",{staticClass:"tax-page param"},[n("h4",[t._v("参数设置")]),n("div",{staticClass:"content"},[n("div",{staticClass:"options"},[n("span",[t._v("设置计税需要的参数,只要税法有调整，请变更新版本后修改")]),n("div",{staticClass:"btns"},[null!=this.version?n("div",{staticClass:"version"},[this.hasPrev?n("a",{on:{click:t.prevVersion}},[n("i",{staticClass:"iconfont icon-jiantou-left"})]):t._e(),n("span",[t._v(t._s(t.version.name))]),this.hasNext?n("a",{on:{click:t.nextVersion}},[n("i",{staticClass:"iconfont icon-jiantou-right"})]):t._e()]):t._e(),n("el-button-group",[n("el-button",{attrs:{type:"primary"},on:{click:t.addParam}},[t._v("添加")]),n("el-button",{attrs:{type:"primary"},on:{click:t.addVersion}},[t._v("版本化")])],1)],1)]),n("table",{staticClass:"table sample-table table-bordered table-hover"},[t._m(0),n("draggable",{attrs:{element:"tbody"},on:{end:t.onEnd},model:{value:t.params,callback:function(a){t.params=a},expression:"params"}},[t._l(t.params,function(a){return n("tr",{key:a.id},[n("td",[t._l(a.labels.split(","),function(a){return a?n("span",{staticClass:"type"},[t._v(t._s(a))]):t._e()}),t._v(t._s(a.name))],2),n("td",[t._v(t._s(a.config)+t._s(a.unit))]),n("td",[n("el-switch",{attrs:{"active-color":"#13ce66"},on:{change:function(n){t.customChange(a)}},model:{value:a.customFlag,callback:function(n){t.$set(a,"customFlag",n)},expression:"item.customFlag"}})],1),n("td",[n("span",{staticClass:"area-params"},t._l(a.areas,function(a){return n("span",{key:a.id},[t._v(t._s(a.areaName)+",")])})),n("swak-link",{staticClass:"ops",attrs:{to:"/system/param/update/"+a.id}},[t._v("配置")])],1)])}),0==t.params.length?n("tr",[n("td",{staticClass:"td-none",attrs:{colspan:"4"}},[t._v("点击添加，设置参数")])]):t._e()],2)],1)])])},s=[function(){var t=this,a=t.$createElement,n=t._self._c||a;return n("thead",[n("tr",[n("th",{attrs:{scope:"col",width:"220"}},[t._v("参数名称")]),n("th",{attrs:{scope:"col"}},[t._v("参数值")]),n("th",{attrs:{scope:"col",width:"80"}},[t._v("自定义")]),n("th",{attrs:{scope:"col",width:"220"}},[t._v("配置")])])])}],o=(n("55dd"),n("ac6a"),n("882d")),i=n("2c75"),r=n("1516"),c=n.n(r),u={components:{SwakLink:o["a"],Draggable:c.a},name:"tax-param",data:function(){return{version:null,hasPrev:!0,hasNext:!1,params:[],value5:!0}},mounted:function(){this.$emit("layout:load","param"),this.loadVersion()},methods:{loadVersion:function(){var t=this;i["b"].newest().then(function(a){t.changeVersion(a.obj)})},changeVersion:function(t){this.version=t,this.version&&this.loadParams(t.id)},customChange:function(t){t.custom=t.customFlag?1:0,i["a"].custom(t)},loadParams:function(t){var a=this;i["a"].list(t).then(function(t){var n=t.obj||[];n.forEach(function(t){t.areas=t.areas||[]}),a.params=n,a.loadParamAreas()})},loadParamAreas:function(){this.params.forEach(function(t){i["a"].areas(t.id).then(function(a){t.areas=a.obj||[]})})},prevVersion:function(){var t=this,a=this.version.id;i["b"].prev(a).then(function(a){null!=a.obj?(t.changeVersion(a.obj),t.hasNext=!0):(t.$notify.info("没有上一个版本了"),t.hasPrev=!1)})},nextVersion:function(){var t=this,a=this.version.id;i["b"].next(a).then(function(a){null!=a.obj?(t.changeVersion(a.obj),t.hasPrev=!0):(t.$notify.info("没有下一个版本了"),t.hasNext=!1)})},addVersion:function(){var t=this;this.$confirm("确认添加新的版本?","操作提醒",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){i["b"].build().then(function(a){a.obj?(t.$notify.success("创建版本成功"),t.loadVersion()):t.$notify.error("创建版本失败，每个月只能创建一个版本")})}).catch(function(){})},addParam:function(){var t=this.version?this.version.id:"";this.$router.push({path:"/system/param/add",query:{versionId:t}})},onEnd:function(){var t=this,a=[];this.params.forEach(function(t,n){t.sort=n,a.push({id:t.id,sort:t.sort})});var n=JSON.stringify(a);i["a"].sort(n).then(function(a){t.$notify.success("排序成功！")})}}},l=u,d=(n("6e58"),n("2877")),m=Object(d["a"])(l,e,s,!1,null,"3e9dab00",null);m.options.__file="param.vue";a["default"]=m.exports},"2c75":function(t,a,n){"use strict";n.d(a,"b",function(){return s}),n.d(a,"a",function(){return o});var e=n("e1d2"),s={newest:function(){return Object(e["a"])({url:"/api/manage/version/newest",method:"get"})},prev:function(t){return Object(e["a"])({url:"/api/manage/version/prev/"+t,method:"get"})},next:function(t){return Object(e["a"])({url:"/api/manage/version/next/"+t,method:"get"})},build:function(){return Object(e["a"])({url:"/api/manage/version/build",method:"post"})}},o={get:function(t){return Object(e["a"])({url:"/api/manage/param/get/"+t,method:"get"})},save:function(t){return Object(e["a"])({url:"/api/manage/param/save",method:"post",data:t})},sort:function(t){return Object(e["a"])({url:"/api/manage/param/sort",method:"post",data:{json_params:t}})},custom:function(t){return Object(e["a"])({url:"/api/manage/param/custom",method:"post",data:{id:t.id,custom:t.custom}})},list:function(t){return Object(e["a"])({url:"/api/manage/param/list/"+t,method:"get"})},areas:function(t){return Object(e["a"])({url:"/api/manage/param/areas/"+t,method:"get"})}}},"3f24":function(t,a,n){},"6e58":function(t,a,n){"use strict";var e=n("3f24"),s=n.n(e);s.a}}]);
//# sourceMappingURL=chunk-8724.29ef7675.js.map