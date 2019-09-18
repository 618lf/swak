package com.tmt.api.web;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.swak.annotation.Body;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.Valid;
import com.swak.entity.Model;
import com.swak.entity.Result;
import com.swak.validator.errors.BindErrors;
import com.swak.vertx.annotation.GetMapping;
import com.swak.vertx.annotation.PostMapping;
import com.swak.vertx.annotation.RestController;
import com.swak.vertx.transport.multipart.PlainFile;

/**
 * 各种参数的测试
 * 
 * @author lifeng
 */
@RestController(path = "/api/param")
public class ParamApi {

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@GetMapping("/string")
	public String string() {
		return "Hello World!";
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@GetMapping("/string/:id")
	public String string(String id) {
		return "Hello World! " + id;
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@GetMapping("/string_get_param/:id")
	public String string(String id, String name) {
		return "Hello World! " + id + ":name=" + name;
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@PostMapping("/post_param")
	public String string(Integer p1, String p2, List<String> p3, Map<String, Object> p4) {
		return "Hello World! p1=" + p1 + ":p2=" + p2 + ":p3=" + p3 + ":p4=" + p4;
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@PostMapping("/post_param_obj")
	public String string(Param param) {
		return "Hello World! p1=" + param.getP1() + ":p2=" + param.getP2() + ":p3=" + param.getP3() + ":p4="
				+ param.getP4() + ":oneItem=" + param.getOneItem() + ":items" + param.getItems();
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@PostMapping("/post_param_obj_mutil")
	public String post_param_obj_mutil(Param param) {
		return "Hello World! p1=" + param.getP1() + ":p2=" + param.getP2() + ":p3=" + param.getP3() + ":p4="
				+ param.getP4() + ":oneItem=" + param.getOneItem() + ":items" + param.getItems();
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@PostMapping("/post_param_anno")
	public String string(@Json Map<String, Object> json, @Body byte[] bytes, @Header String name) {
		return "Hello World! json=" + json + ";body=" + bytes + ";header[name]=" + name;
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@GetMapping("/json")
	public Result json() {
		return Result.success("lifre");
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@GetMapping("/xml")
	public Param xml() {
		Param param = new Param();
		param.setP1(1);
		return param;
	}

	/**
	 * 通过 zero copy 的方式输出文本
	 * 
	 * @return
	 */
	@GetMapping("/zerocopy")
	public CompletableFuture<PlainFile> zeroCopy() {
		File file = new File("/home/lifeng/Desktop/bigtext.txt");
		return CompletableFuture.completedFuture(PlainFile.of(file));
	}

	/**
	 * 通过 模板输出数据
	 * 
	 * @return
	 */
	@GetMapping("/html")
	public CompletableFuture<Model> html() {
		return CompletableFuture.completedFuture(Model.use("index.html").addAttribute("name", "lifeng"));
	}

	/**
	 * 输出string 数据
	 * 
	 * @return
	 */
	@PostMapping("/valid")
	public String valid(@Valid Param param, BindErrors errors) {
		return "Hello World! p1=" + param.getP1() + ":p2=" + param.getP2() + ":p3=" + param.getP3() + ":p4="
				+ param.getP4() + ":Errors=" + errors.getErrors();
	}
}