(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-11b5"],{"0303":function(t,e,l){"use strict";var i=l("6dec"),a=l.n(i);a.a},"6dec":function(t,e,l){},"9c18":function(t,e,l){"use strict";var i=l("e1d2"),a={list:function(){return Object(i["a"])({url:"/api/user/template/list",method:"get"})},tools:function(){return Object(i["a"])({url:"/api/user/template/tools",method:"get"})},def:function(t){return Object(i["a"])({url:"/api/user/template/def?type="+t,method:"get"})},get:function(t){return Object(i["a"])({url:"/api/user/template/get/"+t,method:"get"})},save:function(t){return Object(i["a"])({url:"/api/user/template/save",method:"post",data:t})},forceSave:function(t){return Object(i["a"])({url:"/api/user/template/forceSave",method:"post",data:t})},xml:function(){return Object(i["a"])({url:"/api/user/template/xml",method:"post",responseType:"blob",data:{}})},excel:function(){return Object(i["a"])({url:"/api/user/template/excel",method:"post",responseType:"blob"})},excelOne:function(t){return Object(i["a"])({url:"/api/user/template/excel/"+t,method:"post",responseType:"blob"})}};e["a"]=a},d188:function(t,e,l){"use strict";var i=function(){var t=this,e=t.$createElement,l=t._self._c||e;return l("button",{staticClass:"el-button",class:[t.type?"el-button--"+t.type:"",t.buttonSize?"el-button--"+t.buttonSize:"",{"is-disabled":t.buttonDisabled,"is-loading":t.loading,"is-plain":t.plain,"is-round":t.round,"is-circle":t.circle}],attrs:{disabled:t.buttonDisabled||t.loading,autofocus:t.autofocus,type:t.nativeType},on:{click:t.handleClick}},[t.loading?l("i",{staticClass:"el-icon-loading"}):t._e(),t.icon&&!t.loading?l("i",{class:t.icon}):t._e(),t.$slots.default?l("span",[t._t("default")],2):t._e()])},a=[],n={name:"ElButton",inject:{elForm:{default:""},elFormItem:{default:""}},props:{type:{type:String,default:"default"},size:String,icon:{type:String,default:""},nativeType:{type:String,default:"button"},loading:Boolean,disabled:Boolean,plain:Boolean,autofocus:Boolean,round:Boolean,circle:Boolean},computed:{_elFormItemSize:function(){return(this.elFormItem||{}).elFormItemSize},buttonSize:function(){return this.size||this._elFormItemSize||(this.$ELEMENT||{}).size},buttonDisabled:function(){return this.disabled||(this.elForm||{}).disabled}},methods:{handleClick:function(t){this.$emit("click",t)}}},o=n,s=l("2877"),u=Object(s["a"])(o,i,a,!1,null,null,null);u.options.__file="button.vue";e["a"]=u.exports},d545:function(t,e,l){"use strict";l.r(e);var i=function(){var t=this,e=t.$createElement,l=t._self._c||e;return l("div",{staticClass:"tax-page tempate"},[l("h4",[t._v("模板管理")]),l("div",{staticClass:"content"},[l("div",{staticClass:"options"},[l("span",[t._v("配置需要使用的模板,列举了所有可能会用到的报表")]),l("div",{staticClass:"btns"},[l("el-button",{attrs:{type:"primary",loading:t.multipartFile.loading},on:{click:t.downloadXml}},[t._v("导出Xml")]),l("el-button",{attrs:{type:"success",loading:t.multipartFile.loading},on:{click:t.downloadExcel}},[t._v("导出Excel")])],1)]),l("table",{staticClass:"table sample-table table-bordered table-hover"},[t._m(0),l("tbody",t._l(t.templates,function(e){return 999!=e.sort||t.shows?l("tr",{key:e.id,class:1!=e.configAble?"tr-none":""},[l("td",[e.id&&e.multiAble?l("span",{staticClass:"-child"}):t._e(),l("span",[t._v(t._s(e.templateName))]),e.id&&e.multiAble?t._e():l("span",{staticClass:"td-tip"},[t._v(t._s(e.templateTypeDescs))])]),l("td",{staticClass:"td-ops"},[e.id?l("swak-link",{attrs:{to:"/func/template/cfg/"+e.id}},[t._v("配置")]):l("swak-link",{attrs:{to:"/func/template/add?type="+e.templeteType}},[t._v("配置")])],1)]):t._e()}))]),t.shows?t._e():l("swak-link",{staticClass:"show-more",on:{"link:click":function(e){t.shows=!t.shows}}},[t._v("显示更多...")])],1),l("multi-file",{attrs:{visible:t.multipartFile.visible,file:t.multipartFile},on:{"update:visible":function(e){t.$set(t.multipartFile,"visible",e)}}})],1)},a=[function(){var t=this,e=t.$createElement,l=t._self._c||e;return l("thead",[l("tr",[l("th",{attrs:{scope:"col"}},[t._v("模板名称")]),l("th",{attrs:{scope:"col",width:"80"}},[t._v("操作")])])])}],n=(l("7f7f"),l("882d")),o=l("9c18"),s=l("d188"),u=function(){var t=this,e=t.$createElement,l=t._self._c||e;return l("div",{staticClass:"user-table-select"},[l("el-dialog",{attrs:{title:"文件下载成功",visible:t.visible,"before-close":t.doCancel},on:{"update:visible":function(e){t.visible=e}}},[l("div",{staticClass:"multi-file"},[t.file?l("a",[l("span",{staticClass:"-name"},[t._v(t._s(t.file.name))]),l("span",{staticClass:"-size"},[t._v(t._s(t.file.name))]),l("span",{staticClass:"-ops"},[t._v("点击下载")])]):t._e()])])],1)},r=[],c={name:"multi-download",props:{visible:{type:Boolean,default:!1},file:{type:Object,default:null}},methods:{doCancel:function(){this.$emit("update:visible",!1)}}},d=c,p=(l("0303"),l("2877")),m=Object(p["a"])(d,u,r,!1,null,"7f557ef4",null);m.options.__file="multiFile.vue";var f=m.exports,b={components:{MultiFile:f,ElButton:s["a"],SwakLink:n["a"]},name:"manager",data:function(){return{templates:[],shows:!1,multipartFile:{loading:!1,visible:!1}}},mounted:function(){this.$emit("layout:load","template"),this.loadTemplates()},methods:{loadTemplates:function(){var t=this;o["a"].list().then(function(e){t.templates=e.obj})},downloadXml:function(){var t=this;this.multipartFile.loading=!0,o["a"].xml().then(function(e){t.multipartFile.loading=!1,t.multipartFile.name=decodeURI(e.fileName),t.multipartFile.data=e.data,t.doDownload()},function(){t.$notify.error("error!")})},downloadExcel:function(){var t=this;this.multipartFile.loading=!0,o["a"].excel().then(function(e){t.multipartFile.loading=!1,t.multipartFile.name=decodeURI(e.fileName),t.multipartFile.data=e.data,t.doDownload()},function(){t.$notify.error("error!")})},doDownload:function(){var t=window.URL.createObjectURL(new Blob([this.multipartFile.data])),e=document.createElement("a");e.style.display="none",e.href=t,e.setAttribute("download",this.multipartFile.name),document.body.appendChild(e),e.click()}}},h=b,v=(l("fc0a"),Object(p["a"])(h,i,a,!1,null,"217b8562",null));v.options.__file="template.vue";e["default"]=v.exports},e957:function(t,e,l){},fc0a:function(t,e,l){"use strict";var i=l("e957"),a=l.n(i);a.a}}]);
//# sourceMappingURL=chunk-11b5.b257230d.js.map