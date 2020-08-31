package com.swak.country;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.swak.utils.IOUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import lombok.Getter;
import lombok.ToString;

/**
 * 国家测试
 * 
 * @author lifeng
 * @date 2020年8月31日 下午3:28:13
 */
public class CountryTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<String> lines = IOUtils.readLines(CountryTest.class.getResourceAsStream("国家地区列表.txt"));
		List<Country> countrys = Lists.newArrayList();
		for (int i = 0; i < lines.size(); i = i + 3) {
			Country country = new Country();
			country.address = lines.get(i);
			country.code = lines.get(i + 2);
			country.py = StringUtils.upperCase(String.valueOf(country.code.charAt(0)));
			countrys.add(country);
		}

		Map<String, Classify> classifys = Maps.newOrderMap();
		for (Country country : countrys) {
			Classify classify = classifys.computeIfAbsent(country.py, (key) -> {
				return new Classify(key);
			});
			classify.items.add(country);
		}
		List<Classify> types = Lists.newArrayList(classifys.values());
		Lists.sort(types, new Comparator<Classify>() {
			@Override
			public int compare(Classify o1, Classify o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		for (Classify type : types) {
			System.out.println(type.getName());
		}
		System.out.println(JsonMapper.toJson(types));
	}
}

@Getter
@ToString
class Country {
	String address;
	String code;
	String py;
}

@Getter
@ToString
class Classify {
	String name;
	List<Country> items = Lists.newArrayList();

	public Classify(String type) {
		this.name = type;
	}
}