(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-c149"],{"0210":function(e,a,t){"use strict";var r=t("e1d2"),n={get:function(e){return Object(r["a"])({url:"/api/manage/area/get/"+e,method:"get"})},list:function(){return Object(r["a"])({url:"/api/manage/area/areas",method:"get"})},provinces:function(){return Object(r["a"])({url:"/api/manage/area/provinces",method:"get"})},citys:function(e){return Object(r["a"])({url:"/api/manage/area/citys?province="+e,method:"get"})},save:function(e){return Object(r["a"])({url:"/api/manage/area/save",method:"post",data:e})},delete:function(e){return Object(r["a"])({url:"/api/manage/area/delete",method:"post",data:e})}};a["a"]=n},4203:function(e,a,t){},"4df5":function(e,a,t){"use strict";t.r(a);var r=function(){var e=this,a=e.$createElement,t=e._self._c||a;return t("div",{staticClass:"tax-page param-add"},[t("h4",[e._v("区域设置"),t("span",[e._v("/")]),e._v(e._s(this.id?"修改":"添加")+"区域")]),t("div",{staticClass:"content"},[t("el-form",{ref:"inputForm",attrs:{model:e.area,rules:e.rules,"label-width":"120px"}},[t("el-form-item",{attrs:{label:"区域名称",prop:"name"}},[t("el-input",{attrs:{placeholder:"请输入区域名称"},model:{value:e.area.name,callback:function(a){e.$set(e.area,"name",a)},expression:"area.name"}}),t("div",{staticClass:"form-tip"},[e._v("请填写区域名称.")])],1),t("el-form-item",{attrs:{label:"区域描述",prop:"remarks"}},[t("el-input",{attrs:{type:"textarea",rows:4,placeholder:"请输入区域描述"},model:{value:e.area.remarks,callback:function(a){e.$set(e.area,"remarks",a)},expression:"area.remarks"}}),t("div",{staticClass:"form-tip"},[e._v("简单描述此区域.")])],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:e.onSubmit}},[e._v("提交")]),t("swak-link",{staticClass:"el-button",attrs:{to:"/system/area"}},[e._v("返回")])],1)],1)],1)])},n=[],s=(t("a481"),t("882d")),i=t("0210"),o={components:{SwakLink:s["a"]},name:"area-add",data:function(){return{area:{id:"",name:"",remarks:""},rules:{name:[{required:!0,message:"请输入区域名称",trigger:"blur"}],remarks:[{required:!0,message:"请输入区域描述",trigger:"blur"}]}}},mounted:function(){this.$emit("layout:load","area");var e=this.$route.params.id;this.load(e)},methods:{load:function(e){var a=this;e&&i["a"].get(e).then(function(e){e.obj&&(a.area=e.obj)})},onSubmit:function(){var e=this;this.$refs["inputForm"].validate(function(a){a?i["a"].save(e.area).then(function(a){e.$notify.success("添加区域成功!"),e.$router.replace("/system/area")}):e.$notify.error("表单错误，请输入必填项目")})}}},u=o,l=(t("ba4b"),t("2877")),c=Object(l["a"])(u,r,n,!1,null,"24529e5e",null);c.options.__file="areaSave.vue";a["default"]=c.exports},ba4b:function(e,a,t){"use strict";var r=t("4203"),n=t.n(r);n.a}}]);
//# sourceMappingURL=chunk-c149.89f7dca7.js.map