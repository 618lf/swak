(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-28ac"],{"0210":function(a,e,t){"use strict";var i=t("e1d2"),r={get:function(a){return Object(i["a"])({url:"/api/manage/area/get/"+a,method:"get"})},list:function(){return Object(i["a"])({url:"/api/manage/area/areas",method:"get"})},provinces:function(){return Object(i["a"])({url:"/api/manage/area/provinces",method:"get"})},citys:function(a){return Object(i["a"])({url:"/api/manage/area/citys?province="+a,method:"get"})},save:function(a){return Object(i["a"])({url:"/api/manage/area/save",method:"post",data:a})},delete:function(a){return Object(i["a"])({url:"/api/manage/area/delete",method:"post",data:a})}};e["a"]=r},"0643":function(a,e,t){"use strict";var i=t("e870"),r=t.n(i);r.a},1933:function(a,e,t){},"28a5":function(a,e,t){t("214f")("split",2,function(a,e,i){"use strict";var r=t("aae3"),s=i,n=[].push,l="split",o="length",c="lastIndex";if("c"=="abbc"[l](/(b)*/)[1]||4!="test"[l](/(?:)/,-1)[o]||2!="ab"[l](/(?:ab)*/)[o]||4!="."[l](/(.?)(.?)/)[o]||"."[l](/()()/)[o]>1||""[l](/.?/)[o]){var u=void 0===/()??/.exec("")[1];i=function(a,e){var t=String(this);if(void 0===a&&0===e)return[];if(!r(a))return s.call(t,a,e);var i,l,d,p,m,v=[],f=(a.ignoreCase?"i":"")+(a.multiline?"m":"")+(a.unicode?"u":"")+(a.sticky?"y":""),h=0,b=void 0===e?4294967295:e>>>0,g=new RegExp(a.source,f+"g");u||(i=new RegExp("^"+g.source+"$(?!\\s)",f));while(l=g.exec(t)){if(d=l.index+l[0][o],d>h&&(v.push(t.slice(h,l.index)),!u&&l[o]>1&&l[0].replace(i,function(){for(m=1;m<arguments[o]-2;m++)void 0===arguments[m]&&(l[m]=void 0)}),l[o]>1&&l.index<t[o]&&n.apply(v,l.slice(1)),p=l[0][o],h=d,v[o]>=b))break;g[c]===l.index&&g[c]++}return h===t[o]?!p&&g.test("")||v.push(""):v.push(t.slice(h)),v[o]>b?v.slice(0,b):v}}else"0"[l](void 0,0)[o]&&(i=function(a,e){return void 0===a&&0===e?[]:s.call(this,a,e)});return[function(t,r){var s=a(this),n=void 0==t?void 0:t[e];return void 0!==n?n.call(t,s,r):i.call(String(s),t,r)},i]})},"2c75":function(a,e,t){"use strict";t.d(e,"b",function(){return r}),t.d(e,"a",function(){return s});var i=t("e1d2"),r={newest:function(){return Object(i["a"])({url:"/api/manage/version/newest",method:"get"})},prev:function(a){return Object(i["a"])({url:"/api/manage/version/prev/"+a,method:"get"})},next:function(a){return Object(i["a"])({url:"/api/manage/version/next/"+a,method:"get"})},build:function(){return Object(i["a"])({url:"/api/manage/version/build",method:"post"})}},s={get:function(a){return Object(i["a"])({url:"/api/manage/param/get/"+a,method:"get"})},save:function(a){return Object(i["a"])({url:"/api/manage/param/save",method:"post",data:a})},sort:function(a){return Object(i["a"])({url:"/api/manage/param/sort",method:"post",data:{json_params:a}})},custom:function(a){return Object(i["a"])({url:"/api/manage/param/custom",method:"post",data:{id:a.id,custom:a.custom}})},list:function(a){return Object(i["a"])({url:"/api/manage/param/list/"+a,method:"get"})},areas:function(a){return Object(i["a"])({url:"/api/manage/param/areas/"+a,method:"get"})}}},"4cb2":function(a,e,t){"use strict";t.r(e);var i=function(){var a=this,e=a.$createElement,t=a._self._c||e;return t("div",{staticClass:"tax-page param-add"},[t("h4",[a._v("参数设置"),t("span",[a._v("/")]),a._v(a._s(this.param.id?"修改":"添加")+"参数")]),t("div",{staticClass:"content"},[t("el-form",{ref:"inputForm",attrs:{model:a.param,rules:a.rules,"label-width":"120px"}},[t("el-form-item",{attrs:{label:"参数名称",prop:"name"}},[t("el-input",{attrs:{placeholder:"请输入区域名称"},model:{value:a.param.name,callback:function(e){a.$set(a.param,"name",e)},expression:"param.name"}}),t("div",{staticClass:"form-tip"},[a._v("请设置合理的参数名称，设置后不建议修改.")])],1),t("el-form-item",{attrs:{label:"参数类型"}},[t("div",{staticClass:"types"},a._l(a.pLabels,function(e){return t("a",{key:e.name,class:e.checked?"cur":"",attrs:{"data-label":e.name},on:{click:function(e){return e.stopPropagation(),a.togglelabel(e)}}},[a._v(a._s(e.name))])})),t("div",{staticClass:"form-tip"},[a._v("参数的类型，可以不设置，类型只是一个分类管理")])]),t("el-form-item",{attrs:{label:"参数标识",prop:"ident"}},[t("el-input",{attrs:{placeholder:"请输入参数标识"},model:{value:a.param.ident,callback:function(e){a.$set(a.param,"ident",e)},expression:"param.ident"}}),t("div",{staticClass:"form-tip"},[a._v("参与计算，设置时请注意前缀.")])],1),t("el-form-item",{attrs:{label:"参数数值",prop:"config"}},[t("el-input",{attrs:{placeholder:"请输入参数数值"},model:{value:a.param.config,callback:function(e){a.$set(a.param,"config",e)},expression:"param.config"}}),t("div",{staticClass:"form-tip"},[a._v("可以是一个具体的值或者是一段表达式.")])],1),t("el-form-item",{attrs:{label:"参数单位",prop:"unit"}},[t("el-input",{attrs:{placeholder:"请输入参数单位"},model:{value:a.param.unit,callback:function(e){a.$set(a.param,"unit",e)},expression:"param.unit"}}),t("div",{staticClass:"form-tip"},[a._v("比例为百分比，金额为元.")])],1),t("el-form-item",{attrs:{label:"参数描述",prop:"remarks"}},[t("el-input",{attrs:{placeholder:"请输入参数描述"},model:{value:a.param.remarks,callback:function(e){a.$set(a.param,"remarks",e)},expression:"param.remarks"}}),t("div",{staticClass:"form-tip"},[a._v("简单描述此参数的具体用途.")])],1),t("el-form-item",{attrs:{label:"区域配置"}},[t("table",{staticClass:"table sample-table table-bordered table-hover"},[t("thead",[t("tr",[t("th",{attrs:{scope:"col",width:"120"}},[a._v("区域")]),t("th",{attrs:{scope:"col"}},[a._v("参数值")]),t("th",{attrs:{scope:"col",width:"120"}},[a._v("操作")])])]),t("tbody",a._l(a.param.areas,function(e,i){return t("tr",{key:e.id},[t("td",[a._v(a._s(e.areaName))]),t("td",[e.areaId?t("el-input",{attrs:{placeholder:"请输入内容"},model:{value:e.config,callback:function(t){a.$set(e,"config",t)},expression:"item.config"}}):a._e()],1),t("td",{staticClass:"td-ops"},[t("a",{on:{click:function(e){e.stopPropagation(),a.addArea(i)}}},[a._v("添加")]),t("a",{on:{click:function(e){e.stopPropagation(),a.delArea(i)}}},[a._v("删除")])])])}))]),t("div",{staticClass:"form-tip"},[a._v("参数标识可以用到上面的表达式中参与计算")])]),t("el-form-item",[t("swak-link",{staticClass:"el-button el-button--primary",on:{"link:click":a.save}},[a._v("提交")]),t("swak-link",{staticClass:"el-button",attrs:{to:"/system/param"}},[a._v("返回")])],1)],1),t("area-select",{attrs:{visible:a.dialogVisible},on:{"update:visible":function(e){a.dialogVisible=e},"select:area":a.handleAreaChange}})],1)])},r=[],s=(t("a481"),t("7f7f"),t("28a5"),t("0210"),t("2c75")),n=t("882d"),l=t("ed2b"),o={components:{AreaSelect:l["a"],SwakLink:n["a"]},name:"param-add",data:function(){return{labels:["税率","标准","个税优惠","社评工资","五险一金","年金","商业保险"],areas:[],dialogVisible:!1,loading:!0,selectedAreas:[],addAreaIndex:0,param:{id:"",versionId:"",labels:"",name:"",ident:"",config:"",unit:"",remarks:"",areas:[{}]},rules:{name:[{required:!0,message:"请输入参数名称",trigger:"blur"}],ident:[{required:!0,message:"请输入参数标识",trigger:"blur"}],config:[{required:!0,message:"请输入参数值",trigger:"blur"}],remarks:[{required:!0,message:"请输入参数描述",trigger:"blur"}]}}},mounted:function(){this.$emit("layout:load","param");var a=this.$route.params.id,e=this.$route.query.versionId;e&&(this.param.versionId=e),this.load(a)},computed:{pLabels:function(){for(var a=[],e=0;e<this.labels.length;e++){var t=this.labels[e],i=!1;-1!=this.param.labels.indexOf(","+t+",")&&(i=!0),a.push({name:t,checked:i})}return a}},methods:{load:function(a){var e=this;a&&s["a"].get(a).then(function(a){e.param=a.obj,0==e.param.areas.length&&e.param.areas.push({})})},togglelabel:function(a){var e=","+a.target.getAttribute("data-label")+",";if(-1!=this.param.labels.indexOf(e)){var t=this.param.labels.split(e).join(",");this.param.labels=t}else{t=(this.param.labels+e).split(",,").join(",");this.param.labels=t}},save:function(){var a=this;this.$refs["inputForm"].validate(function(e){if(e){var t={id:a.param.id,versionId:a.param.versionId,labels:a.param.labels,name:a.param.name,ident:a.param.ident,config:a.param.config,unit:a.param.unit,remarks:a.param.remarks,json_areas:JSON.stringify(a.param.areas)};s["a"].save(t).then(function(e){a.$router.replace("/system/param"),a.$notify.success("保存参数成功!")})}else a.$notify.error("表单错误")})},addArea:function(a){this.addAreaIndex=a,this.dialogVisible=!0},handleAreaChange:function(a){0!=a?(this.param.areas[this.addAreaIndex].areaId||this.param.areas.splice(this.addAreaIndex,1),this.param.areas.push({areaId:a.id,areaName:a.name})):this.$notify.warning("请选择一个区域")},delArea:function(a){this.param.areas.splice(a,1),0==this.param.areas.length&&this.param.areas.push({})}}},c=o,u=(t("6572"),t("2877")),d=Object(u["a"])(c,i,r,!1,null,"44bb74e0",null);d.options.__file="paramSave.vue";e["default"]=d.exports},6572:function(a,e,t){"use strict";var i=t("1933"),r=t.n(i);r.a},e870:function(a,e,t){},ed2b:function(a,e,t){"use strict";var i=function(){var a=this,e=a.$createElement,t=a._self._c||e;return t("div",{staticClass:"user-table-select"},[t("el-dialog",{attrs:{title:"请选择区域",visible:a.visible,"before-close":a.doCancel},on:{"update:visible":function(e){a.visible=e}}},[t("div",{staticClass:"area-selected"},[a._v("选择城市："+a._s(a.area.name))]),t("div",{staticClass:"area-settings"},[t("div",{staticClass:"provinces -settings"},[t("div",{staticClass:"-name"},[a._v("省")]),t("div",{staticClass:"-items"},a._l(a.provinces,function(e,i){return t("swak-link",{key:e.id,class:a.s1==e.id?"selected":"",attrs:{id:e.id,index:i,clazz:"-item"},on:{"link:click":a.provinceSelect}},[a._v(a._s(e.name)+"\n          ")])}))]),t("div",{staticClass:"citys -settings"},[t("div",{staticClass:"-name"},[a._v("市")]),t("div",{staticClass:"-items"},a._l(a.citys,function(e,i){return t("swak-link",{key:e.id,class:a.s2==e.id?"selected":"",attrs:{id:e.id,index:i,clazz:"-item"},on:{"link:click":a.citySelect}},[a._v(a._s(e.name)+"\n          ")])}))])]),t("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:a.doCancel}},[a._v("取 消")]),t("el-button",{attrs:{type:"primary"},on:{click:a.doConfirm}},[a._v("确 定")])],1)])],1)},r=[],s=t("0210"),n=t("882d"),l={components:{SwakLink:n["a"]},name:"area-select",data:function(){return{provinces:[],citys:[],s1:"",s2:"",area:{}}},props:{visible:{type:Boolean,default:!1}},watch:{visible:function(){this.visible&&this.loadProvinces()}},methods:{loadProvinces:function(){var a=this;s["a"].provinces().then(function(e){a.provinces=e.obj})},loadCitys:function(a){var e=this;s["a"].citys(a).then(function(a){e.citys=a.obj})},provinceSelect:function(a){var e=a.target,t=e.getAttribute("id"),i=e.getAttribute("index");this.s1=t,this.area=this.provinces[i],this.loadCitys(t)},citySelect:function(a){var e=a.target,t=e.getAttribute("id"),i=e.getAttribute("index");this.s2=t,this.area=this.citys[i]},doCancel:function(){this.$emit("update:visible",!1)},doConfirm:function(){this.selectArea(),this.doCancel()},selectArea:function(){this.$emit("select:area",this.area)}}},o=l,c=(t("0643"),t("2877")),u=Object(c["a"])(o,i,r,!1,null,"33695449",null);u.options.__file="areaSelect.vue";e["a"]=u.exports}}]);
//# sourceMappingURL=chunk-28ac.5d6b78cb.js.map