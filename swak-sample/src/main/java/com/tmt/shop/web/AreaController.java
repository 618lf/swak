package com.tmt.shop.web;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.swak.entity.Result;
import com.swak.flux.web.annotation.GetMapping;
import com.swak.flux.web.annotation.RestController;
import com.swak.http.builder.RequestBuilder;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.tmt.shop.entity.Area;

import reactor.core.publisher.Mono;

/**
 * 区域管理
 * @author root
 */
@RestController(path = "/admin/system/area")
public class AreaController {

	/**
	 * 从中华人民共和国国家统计局网站统计数据
	 * 统计到街道
	 * @return
	 */
	@GetMapping("sync")
	public Mono<Result> sync_stats_gov_cn() {
		String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/";
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/index.html";
		AreaParse parse = new AreaParse(baseUrl);
		CompletableFuture<String> fhtml = RequestBuilder.get().setUrl(url).text().charset(Charset.forName("gb2312")).future();
		CompletableFuture<Void> task = fhtml.thenCompose((html) -> {
			List<CompletableFuture<Void>> futures = parse.parseProvinces(html).stream().map(province ->{
				return citys(parse, province);
			}).collect(Collectors.toList());
			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		});
		return Mono.fromFuture(task).map((v) -> {
			return Result.success();
		});
	}

	private CompletableFuture<Void> citys(AreaParse parse, Area province) {
		List<Area> areas = Lists.newArrayList(); areas.add(province);
		String pcode = province.getCode();
		String url = parse.getAddress() + pcode;
		CompletableFuture<String> fhtml = RequestBuilder.get().setUrl(url).text().charset(Charset.forName("gb2312")).future();
		return fhtml.thenCompose((html) -> {
			List<CompletableFuture<Void>> futures = parse.parseOthers(html, "citytr").stream().map(city ->{
				city.setParentId(province.getId());
				areas.add(city);
				return countys(parse, province, city, areas);
			}).collect(Collectors.toList());
			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		}).thenCompose((v) ->{
			return this.save_parse_result(areas);
		});
	}
	
	private CompletableFuture<Void> countys(AreaParse parse, Area province, Area city, List<Area> areas) {
		String ccode = city.getCode();
		String url = parse.getAddress() + ccode;
		CompletableFuture<String> fhtml = RequestBuilder.get().setUrl(url).text().charset(Charset.forName("gb2312")).future();
		return fhtml.thenCompose((html) ->{
			List<CompletableFuture<Void>> futures = parse.parseOthers(html, "countytr").stream().map(county ->{
				county.setParentId(city.getId());
				areas.add(county);
				return towns(parse, province, city, county, areas);
			}).collect(Collectors.toList());
			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		});
	}
	
	private CompletableFuture<Void> towns(AreaParse parse, Area province, Area city, Area county, List<Area> areas) {
		String cocode = county.getCode();
		county.setParentId(city.getId());
		if (StringUtils.endsWith(cocode, ".html")) {
			String url = parse.getAddress() + province.getId() + "/" + cocode;
			CompletableFuture<String> fhtml = RequestBuilder.get().setUrl(url).text().charset(Charset.forName("gb2312")).future();
			return fhtml.thenAccept((html) -> {
				List<Area> towns = parse.parseOthers(html, "towntr");
				towns.stream().forEach((town) ->{
					town.setParentId(county.getId());
					town.setName(StringUtils.remove(town.getName(), "办事处"));
				});
				areas.addAll(towns);
			});
		}
		return CompletableFuture.runAsync(() ->{});
	}
	
	private CompletableFuture<Void> save_parse_result(List<Area> areas) {
		return CompletableFuture.runAsync(() ->{
			System.out.println("save one province");
		});
	}
}