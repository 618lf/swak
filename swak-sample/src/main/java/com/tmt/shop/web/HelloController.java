package com.tmt.shop.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.Constants;
import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.entity.Result;
import com.swak.http.builder.RequestBuilder;
import com.swak.persistence.QueryCondition;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.entity.Shop;
import com.tmt.shop.entity.ShopXml;
import com.tmt.shop.service.ShopService;

import reactor.core.publisher.Mono;

/**
 * 测试的 demo
 * 
 * @author lifeng
 */
@RestController(path = "/admin/hello")
public class HelloController {

	@Autowired
	private ShopService shopService;

	@Autowired(required = false)
	private CacheManager cacheManager;

	/**
	 * 重定向
	 * 
	 * @return
	 */
	@GetMapping("/rt")
	public CompletableFuture<String> rt() {
		return CompletableFuture.completedFuture(Constants.REDIRECT_URL_PREFIX + "http://www.catax.cn/admin");
	}

	/**
	 * from async apis
	 * 
	 * @return
	 */
	@GetMapping("/say/cache_put")
	public void sayPut() {
		Cache<Object> cache = cacheManager.getCache("sys");
		cache.putObject("shop-1", new Shop());
		cache.putObject("shop-2", "shop");
	}

	/**
	 * from async apis
	 * 
	 * @return
	 */
	@GetMapping("/say/cache_get")
	public void sayCache() {
		Cache<String> cache = cacheManager.getCache("sys");
		System.out.println(cache.getObject("shop-1"));
		Cache<Shop> cache2 = cacheManager.getCache("sys");
		System.out.println(cache2.getObject("shop-2"));
	}

	/**
	 * 返回 null 的问题
	 * 
	 * @return
	 */
	public void sayVoid() {
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回 null 的问题
	 * 
	 * @return
	 */
	@GetMapping("/say/mono-void")
	public Mono<Void> sayMonoVoid() {
		return Mono.empty();
	}

	/**
	 * 抛出异常
	 * 
	 * @return
	 */
	@GetMapping("/say/error")
	public Mono<String> sayError() {
		return Mono.fromSupplier(() -> {
			int i = 1 / 0;
			return "lifeng" + i;
		});
	}

	/**
	 * 输出string 类型
	 * 
	 * @return
	 */
	@GetMapping("/say/string")
	public String sayString() {
		return "lifeng";
	}

	/**
	 * 输出其他对象
	 * 
	 * @return
	 */
	@GetMapping("/say/object")
	public Shop sayObject() {
		return new Shop();
	}

	/**
	 * 输出 Xml
	 * 
	 * @return
	 */
	@GetMapping("/say/xml")
	public ShopXml sayXml() {
		ShopXml xml = new ShopXml();
		xml.setName("lifeng");
		return xml;
	}

	/**
	 * 返回 mono 对象
	 * 
	 * @return
	 */
	@GetMapping("/say/mono")
	public Mono<String> sayMono() {
		return Mono.fromSupplier(() -> {
			QueryCondition qc = new QueryCondition();
			List<Shop> shops = shopService.queryForLimitList(qc, 10);
			System.out.println("数据：" + shops.size());
			return shopService.say();
		});
	}

	// /**
	// * 返回 mono 对象
	// *
	// * @return
	// */
	// @GetMapping("/say/future")
	// public Mono<Result> sayFuture(String name) {
	// Shop shop = new Shop();
	// shop.setName(name);
	// return Mono.fromFuture(Workers.future(() -> shopService.save(shop))).map(s ->
	// Result.success(s));
	// }

	// /**
	// * 返回 mono 对象
	// *
	// * @return
	// */
	// @Async
	// @GetMapping("/say/future2")
	// public Result sayFuture2(String name) {
	// Shop shop = new Shop();
	// shop.setName(name);
	// shopService.save(shop);
	// return Result.success();
	// }

	// /**
	// * 返回 mono 对象
	// *
	// * @return
	// */
	// @SuppressWarnings("deprecation")
	// @GetMapping("/say/stream")
	// public Mono<Result> sayStream(String name) {
	// Stream<CompletableFuture<Shop>> optional = Stream.of(name).map(s -> {
	// Shop shop = new Shop();
	// shop.setName(name);
	// return shop;
	// }).map(s -> {
	// return Workers.future(() -> shopService.save(s));
	// });
	// return Workers.stream(optional).map(s -> Result.success(s));
	// }

	/**
	 * 返回 mono 对象
	 * 
	 * @return
	 */
	@GetMapping("/say/optional")
	public Mono<Result> sayOptional(String name) {
		Optional<String> so = Optional.of(name).filter(s -> {
			return s != null;
		});
		return Mono.just(so.get()).map(s -> Result.success(s));
	}

	/**
	 * 返回 mono 对象
	 * 
	 * @return
	 */
	@GetMapping("/say/http")
	public Mono<Result> sayHttp(String name) {
		return RequestBuilder.get().setUrl("https://www.2345.com/").text().reactive().map(s -> Result.success(s));
	}

	/**
	 * 协程 -- 只能用来处理 io 的问题 如果仅仅是cpu 的事情，反而慢，所有只有一个场景可用，那就是 网络IO
	 * 而且必须是异步IO，不知道是否会自动切协程，同步IO不会自动切协程 但如果是异步IO，那协程仅仅将异步代码变为同步代码。
	 * 
	 * @param id
	 * @return
	 */
	// @GetMapping("/say/xc")
	// public Mono<Shop> sayXc() {
	// return MonosKt.create(() -> {
	// shopService.say();
	// return new Shop();
	// });
	// }

	// /**
	// * 输出string 类型
	// *
	// * @return
	// * @throws IOException
	// */
	// @SuppressWarnings("deprecation")
	// @GetMapping("/say/compute")
	// public Mono<String> sayCompute(HttpServerRequest request) throws IOException
	// {
	// String biaodashi = WebUtils.getCleanParam(request, "name");
	// if (StringUtils.isNotBlank(biaodashi) && biaodashi.equals("1 1")) {
	// return Workers.sink(() -> {
	// try {
	// // 模拟计算一分钟，其实和实际的计算是有差别的，sleep 之后这个线程不能做其他的事情了
	// Thread.sleep(60000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// return "lifeng say 2";
	// });
	// }
	// return Mono.just("lifeng say 1");
	// }

	// /**
	// * 返回 mono 对象
	// *
	// * @return
	// */
	// @GetMapping("/say/excel")
	// public CompletableFuture<Result> sayExcel() {
	// return Workers.future(() -> {
	//
	// // 创建文件
	// File excelFile = ExcelUtils.write(new
	// File(System.getProperty("java.io.tmpdir"), UUIdGenerator.uuid()),
	// "测试创建文件", "测试创建文件", toMapper(), toValues(), null, null);
	//
	// // 读取文件
	// Result result = ExcelUtils.read(new DefaultExcelMapper<Excel>() {
	// @Override
	// protected List<ColumnMapper> getRowMapper() {
	// return toMapper();
	// }
	// }, excelFile);
	// return result;
	// });
	// }

	// private List<Map<String, Object>> toValues() {
	// List<Map<String, Object>> excels = Lists.newArrayList();
	// Map<String, Object> excel = Maps.newHashMap();
	// excel.put("a", "A1");
	// excel.put("b", "B1");
	// excel.put("c", "12");
	// excels.add(excel);
	// excel = Maps.newHashMap();
	// excel.put("a", "A2");
	// excel.put("b", "B2");
	// excel.put("c", "12.0");
	// excels.add(excel);
	// return excels;
	// }
	//
	// // 创建模板
	// private List<ColumnMapper> toMapper() {
	// List<ColumnMapper> mappers = Lists.newArrayList();
	// ColumnMapper mapper = new ColumnMapper();
	// mapper.setTitle("A列");
	// mapper.setColumn("A");
	// mapper.setDataType(DataType.STRING);
	// mapper.setProperty("a");
	// mappers.add(mapper);
	// mapper = new ColumnMapper();
	// mapper.setTitle("B列");
	// mapper.setColumn("B");
	// mapper.setDataType(DataType.STRING);
	// mapper.setProperty("b");
	// mappers.add(mapper);
	// mapper = new ColumnMapper();
	// mapper.setTitle("C列");
	// mapper.setColumn("C");
	// mapper.setDataType(DataType.STRING);
	// mapper.setProperty("c");
	// mappers.add(mapper);
	// return mappers;
	// }

	// excel 数据
	public static class Excel {
		private String a;
		private String b;
		private BigDecimal c;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public String getB() {
			return b;
		}

		public void setB(String b) {
			this.b = b;
		}

		public BigDecimal getC() {
			return c;
		}

		public void setC(BigDecimal c) {
			this.c = c;
		}
	}
}