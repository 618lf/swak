(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2507"],{"4dc3":function(e,t,a){"use strict";a.r(t);var i=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"main-content"},[a("div",{staticClass:"title"},[e._v("注册单位帐号")]),a("div",{staticClass:"register-form"},[a("el-form",{ref:"inputForm",attrs:{model:e.manager,rules:e.rules,"label-width":"120px"}},[a("el-form-item",{attrs:{label:"帐号名称",prop:"userName"}},[a("el-input",{attrs:{placeholder:"请输入帐号名称"},model:{value:e.manager.userName,callback:function(t){e.$set(e.manager,"userName",t)},expression:"manager.userName"}}),a("div",{staticClass:"form-tip"},[e._v("可以输入单位名称，支持中文.")])],1),a("el-form-item",{attrs:{label:"帐号管理员",prop:"openId"}},[a("div",{staticClass:"manager",class:this.manager.openId?"scaned":""},[a("a",{staticClass:"qrcode",on:{click:function(t){return t.stopPropagation(),e.doMock(t)}}},[a("img",{attrs:{src:this.qrcode}})]),a("div",{staticClass:"user"},[e.manager.headimg?a("div",{staticClass:"headimg"},[a("img",{staticClass:"image",attrs:{src:e.manager.headimg}})]):e.manager.name?a("div",{staticClass:"headimg"},[a("span",{staticClass:"image"},[e._v(e._s(e.manager.name.substring(0,1)))])]):a("div",{staticClass:"headimg"},[a("span",{staticClass:"image"})]),a("div",{staticClass:"name"},[e._v(e._s(e.manager.name))])])]),a("div",{staticClass:"form-tip"},[e._v("用微信扫描二维码，将成为单位帐号的管理员，拥有单位管理的相关权限")])]),a("el-form-item",{attrs:{prop:"checked"}},[a("el-checkbox",{model:{value:e.manager.checked,callback:function(t){e.$set(e.manager,"checked",t)},expression:"manager.checked"}},[e._v("我同意并遵守《税务公社服务协议》")])],1)],1)],1),a("div",{staticClass:"-btn"},[a("el-button",{attrs:{loading:e.loading,type:"success",disabled:e.disable},on:{click:e.doReqister}},[e._v("注册")])],1)])},s=[],n=(a("a481"),a("7f7f"),a("db0d")),r={name:"department",data:function(){return{manager:{openId:"",userName:"",type:1,headimg:"",name:""},token:"",qrcode:"",timer:null,loading:!1}},mounted:function(){null!=this.timer&&clearTimeout(this.timer),this.loadQrcode()},computed:{disable:function(){return!this.manager.checked}},methods:{loadQrcode:function(){var e=this;n["a"].qrcode().then(function(t){e.token=t.obj.token,e.qrcode=t.obj.qrcode,e.touchTask()})},touchTask:function(){var e=this;null!=this.timer&&clearTimeout(this.timer),this.timer=setTimeout(function(){e.doTouch()},1e3)},doTouch:function(){var e=this;n["a"].touch(this.token).then(function(t){t.success?e.doRender(t.obj):e.touchTask()})},doRender:function(e){null!=this.timer&&clearTimeout(this.timer),this.manager.openId=e.openId,this.manager.name=e.name,this.manager.headimg=e.headimg},doReqister:function(){var e=this;this.manager.openId&&this.manager.userName?(null!=this.timer&&clearTimeout(this.timer),this.loading=!0,this.$store.dispatch("Reqister",{manager:this.manager,token:this.token}).then(function(t){e.loading=!1,t.success?e.$router.replace("/"):e.$alert(t.msg,"注册失败",{confirmButtonText:"确定"})})):this.$notify.error("请填写帐号名称，并用微信扫描二维码")},doMock:function(){n["a"].doMock(this.token).then(function(e){})}},destroyed:function(){null!=this.timer&&clearTimeout(this.timer)}},o=r,c=(a("e530"),a("2877")),m=Object(c["a"])(o,i,s,!1,null,"62d679f6",null);m.options.__file="depart.vue";t["default"]=m.exports},"5e79":function(e,t,a){},e530:function(e,t,a){"use strict";var i=a("5e79"),s=a.n(i);s.a}}]);
//# sourceMappingURL=chunk-2507.b12df302.js.map