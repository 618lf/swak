(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-784f"],{"0210":function(t,s,a){"use strict";var e=a("e1d2"),i={get:function(t){return Object(e["a"])({url:"/api/manage/area/get/"+t,method:"get"})},list:function(){return Object(e["a"])({url:"/api/manage/area/areas",method:"get"})},provinces:function(){return Object(e["a"])({url:"/api/manage/area/provinces",method:"get"})},citys:function(t){return Object(e["a"])({url:"/api/manage/area/citys?province="+t,method:"get"})},save:function(t){return Object(e["a"])({url:"/api/manage/area/save",method:"post",data:t})},delete:function(t){return Object(e["a"])({url:"/api/manage/area/delete",method:"post",data:t})}};s["a"]=i},"0643":function(t,s,a){"use strict";var e=a("e870"),i=a.n(e);i.a},9086:function(t,s,a){},a528:function(t,s,a){"use strict";a.r(s);var e=function(){var t=this,s=t.$createElement,a=t._self._c||s;return a("div",{staticClass:"tax-page home"},[a("h4",[t._v("参数设置")]),a("div",{staticClass:"areas"},[a("div",{staticClass:"item"},[a("span",{staticClass:"-title"},[t._v("您的区域：")]),a("span",{staticClass:"-name"},[t._v(t._s(t.user.areaName||"未设置"))]),a("a",{staticClass:"-ops",on:{click:function(s){return s.stopPropagation(),t.changeArea(s)}}},[t._v(t._s(t.user.areaName?"更换":"设置"))])]),a("div",{staticClass:"item"},[a("span",{staticClass:"-title"},[t._v("计税参数：")]),a("div",{staticClass:"param-settings"},t._l(t.params,function(s){return a("div",{staticClass:"item"},[a("span",[t._v(t._s(s.param.name))]),a("el-input",{attrs:{placeholder:"请输入",maxlength:"10"},on:{change:function(a){t.changeParam(s)}},model:{value:s.config,callback:function(a){t.$set(s,"config",a)},expression:"param.config"}},[a("template",{slot:"append"},[t._v(t._s(s.param.unit))])],2)],1)})),a("el-button",{attrs:{type:"primary"},on:{click:t.saveParam}},[t._v("保存")])],1)]),a("h4",[t._v("订阅服务")]),a("div",{staticClass:"subs"},[t.subs.length>0?a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"apis"},t._l(t.subs,function(s){return a("div",{key:s.id,staticClass:"api"},[a("div",{staticClass:"-inner"},[a("div",{staticClass:"-icon"},[t._v(t._s(s.storeName))]),a("div",{staticClass:"-name"},[t._v(t._s(s.apiName))]),a("div",{staticClass:"-time"},[a("span",{staticClass:"-type"},[t._v(t._s(s.payStateName))]),a("span",[t._v(t._s(s.endDate)+"到期")])])])])})):t._e(),t.subs.length>0?a("div",{staticClass:"more"},[t._v("还可以订阅如下服务")]):t._e(),t.unApis.length>0?a("div",{staticClass:"unsub-apis"},t._l(t.unApis,function(s){return a("div",{key:s.id,staticClass:"api"},[a("div",{staticClass:"-icon"},[t._v(t._s(s.storeName))]),a("div",{staticClass:"-info"},[a("div",{staticClass:"-ops"},[a("span",{staticClass:"-name"},[t._v(t._s(s.apiName))]),a("span",{staticClass:"-price"},[a("span",{staticClass:"-p1"},[t._v("￥")]),a("span",{staticClass:"-p2"},[t._v(t._s(s.price))]),a("span",{staticClass:"-p3"},[t._v("/年")])]),a("a",[t._v("订阅")])]),a("div",{staticClass:"-desc"},[t._v(t._s(s.descs))])])])})):t._e()]),a("h4",[t._v("客户端下载")]),t._m(0),a("h4",[t._v("客户端更新")]),t._m(1),a("area-select",{attrs:{visible:t.areaDialog.visible},on:{"update:visible":function(s){t.$set(t.areaDialog,"visible",s)},"select:area":t.selectArea}})],1)},i=[function(){var t=this,s=t.$createElement,a=t._self._c||s;return a("div",{staticClass:"locals"},[a("div",{staticClass:"item"},[a("div",{staticClass:"image"},[t._v("包")]),a("div",[a("a",[t._v("下载安装包")])])]),a("div",{staticClass:"item"},[a("div",{staticClass:"image"},[t._v("看")]),a("div",[a("a",[t._v("下载指导文档")])])]),a("div",{staticClass:"item"},[a("div",{staticClass:"image"},[t._v("问")]),a("div",[a("a",[t._v("在线提问")])])])])},function(){var t=this,s=t.$createElement,a=t._self._c||s;return a("div",{staticClass:"upgrades"},[a("div",{staticClass:"options"},[a("span",[t._v("在客户端中的软件更新菜单可以下载安装更新包，自动检测需要安装的软件包")])]),a("table",{staticClass:"table sample-table table-bordered table-hover"},[a("thead",[a("tr",[a("th",{attrs:{scope:"col"}},[t._v("版本号")]),a("th",{attrs:{scope:"col"}},[t._v("更新日期")]),a("th",{attrs:{scope:"col"}},[t._v("jar包")]),a("th",{attrs:{scope:"col"}},[t._v("描述")])])]),a("tbody",[a("tr",[a("td",[t._v("PTMS-1.0.0")]),a("td",[t._v("2018年9月12日")]),a("td",[t._v("PTMS-1.0.0-2.jar")]),a("td",[t._v("修复客户端人员信息设置错误")])]),a("tr",[a("td",[t._v("PTMS-1.0.0")]),a("td",[t._v("2018年8月12日")]),a("td",[t._v("PTMS-1.0.0-1.jar")]),a("td",[t._v("客户端升级")])])])])])}],n=(a("ac6a"),a("e1d2")),c={apis:function(){return Object(n["a"])({url:"/api/user/subscribe/apis",method:"get"})},subs:function(){return Object(n["a"])({url:"/api/user/subscribe/subs",method:"get"})}},r=c,o=a("df33"),l={customs:function(){return Object(n["a"])({url:"/api/user/param/customs",method:"get"})},save:function(t){return Object(n["a"])({url:"/api/user/param/save",method:"post",data:{json_params:t}})}},u=l,v=a("ed2b"),d={components:{AreaSelect:v["a"]},name:"home",data:function(){return{apis:[],subs:[],user:this.$store.getters.user.info,areaDialog:{visible:!1},params:[]}},mounted:function(){this.$emit("layout:load","home"),this.loadApis(),this.loadSubs(),this.showArea(),this.loadParams()},computed:{unApis:function(){var t=this,s=[];return this.apis.forEach(function(a){for(var e=!1,i=0;i<t.subs.length;i++)if(a.apiIdent==t.subs[i].apiIdent){e=!0;break}e||s.push(a)}),s}},methods:{showArea:function(){this.user.areaId||(this.areaDialog.visible=!0)},loadParams:function(){var t=this;u.customs().then(function(s){t.params=s.obj})},loadApis:function(){var t=this;r.apis().then(function(s){t.apis=s.obj||[]})},loadSubs:function(){var t=this;r.subs().then(function(s){t.subs=s.obj||[]})},changeArea:function(){this.areaDialog.visible=!0},selectArea:function(t){var s=this;o["a"].saveArea(t).then(function(t){s.$store.dispatch("GetUser").then(function(t){s.user=s.$store.getters.user.info})})},changeParam:function(t){t.config=parseFloat(t.config)},saveParam:function(){var t=this,s=[];this.params.forEach(function(t){s.push({id:t.id,config:t.config})});var a=JSON.stringify(s);u.save(a).then(function(s){t.$notify.success("修改成功！")})}}},p=d,m=(a("c79e"),a("2877")),f=Object(m["a"])(p,e,i,!1,null,"4b6179ee",null);f.options.__file="home.vue";s["default"]=f.exports},c79e:function(t,s,a){"use strict";var e=a("9086"),i=a.n(e);i.a},e870:function(t,s,a){},ed2b:function(t,s,a){"use strict";var e=function(){var t=this,s=t.$createElement,a=t._self._c||s;return a("div",{staticClass:"user-table-select"},[a("el-dialog",{attrs:{title:"请选择区域",visible:t.visible,"before-close":t.doCancel},on:{"update:visible":function(s){t.visible=s}}},[a("div",{staticClass:"area-selected"},[t._v("选择城市："+t._s(t.area.name))]),a("div",{staticClass:"area-settings"},[a("div",{staticClass:"provinces -settings"},[a("div",{staticClass:"-name"},[t._v("省")]),a("div",{staticClass:"-items"},t._l(t.provinces,function(s,e){return a("swak-link",{key:s.id,class:t.s1==s.id?"selected":"",attrs:{id:s.id,index:e,clazz:"-item"},on:{"link:click":t.provinceSelect}},[t._v(t._s(s.name)+"\n          ")])}))]),a("div",{staticClass:"citys -settings"},[a("div",{staticClass:"-name"},[t._v("市")]),a("div",{staticClass:"-items"},t._l(t.citys,function(s,e){return a("swak-link",{key:s.id,class:t.s2==s.id?"selected":"",attrs:{id:s.id,index:e,clazz:"-item"},on:{"link:click":t.citySelect}},[t._v(t._s(s.name)+"\n          ")])}))])]),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:t.doCancel}},[t._v("取 消")]),a("el-button",{attrs:{type:"primary"},on:{click:t.doConfirm}},[t._v("确 定")])],1)])],1)},i=[],n=a("0210"),c=a("882d"),r={components:{SwakLink:c["a"]},name:"area-select",data:function(){return{provinces:[],citys:[],s1:"",s2:"",area:{}}},props:{visible:{type:Boolean,default:!1}},watch:{visible:function(){this.visible&&this.loadProvinces()}},methods:{loadProvinces:function(){var t=this;n["a"].provinces().then(function(s){t.provinces=s.obj})},loadCitys:function(t){var s=this;n["a"].citys(t).then(function(t){s.citys=t.obj})},provinceSelect:function(t){var s=t.target,a=s.getAttribute("id"),e=s.getAttribute("index");this.s1=a,this.area=this.provinces[e],this.loadCitys(a)},citySelect:function(t){var s=t.target,a=s.getAttribute("id"),e=s.getAttribute("index");this.s2=a,this.area=this.citys[e]},doCancel:function(){this.$emit("update:visible",!1)},doConfirm:function(){this.selectArea(),this.doCancel()},selectArea:function(){this.$emit("select:area",this.area)}}},o=r,l=(a("0643"),a("2877")),u=Object(l["a"])(o,e,i,!1,null,"33695449",null);u.options.__file="areaSelect.vue";s["a"]=u.exports}}]);
//# sourceMappingURL=chunk-784f.c2cc4e37.js.map