package com.swak.country;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.swak.entity.ColumnMapper;
import com.swak.entity.DataType;
import com.swak.entity.Result;
import com.swak.excel.ExcelUtils;
import com.swak.excel.impl.DefaultExcelMapper;
import com.swak.utils.ChineseUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 协定国家
 * 
 * @author lifeng
 * @date 2020年9月1日 下午3:36:50
 */
public class OecdCountryTest {

	public static List<Country> countrys() throws IOException {
		Result result = ExcelUtils.read(new StringExcelMapper(), CountryTest.class.getResourceAsStream("协定国家.xls"));
		List<Imp> imps = result.getObj();
		Set<String> names = Sets.newHashSet();
		for (Imp imp : imps) {
			if (ChineseUtils.isChinese(imp.name)) {
				names.add(imp.name);
			}
		}
		List<Country> oecds = Lists.newArrayList();
		for (String name : names) {
			Country country = new Country();
			country.address = name;
			oecds.add(country);
		}

		// Oecd 协定国家
		List<Country> countrys = CountryTest.countrys();
		for (Country oecd : oecds) {
			Country found = null;
			for (Country country : countrys) {
				if (oecd.address.equals(country.address)) {
					found = country;
					break;
				}
				if (oecd.address.equals("②捷克斯洛伐克（适用于斯洛伐克）") && country.address.equals("斯洛伐克")) {
					found = country;
					break;
				}
				if (oecd.address.equals("①德国") && country.address.equals("德国")) {
					found = country;
					break;
				}
				if (oecd.address.equals("巴新") && country.address.equals("巴布亚新几内亚")) {
					found = country;
					break;
				}
				if (oecd.address.equals("③南斯拉夫 （适用于波斯尼亚和黑塞哥维那） YUGOSLAVIA（BOSNIA AND HERZEGOVINA）")
						&& country.address.equals("南斯拉夫")) {
					found = country;
					break;
				}
				if (oecd.address.equals("菲律宾THE PHILIPPINES") && country.address.equals("菲律宾")) {
					found = country;
					break;
				}
				if (oecd.address.equals("蒙古") && country.address.equals("蒙古国")) {
					found = country;
					break;
				}
				if (oecd.address.equals("卡塔尔国") && country.address.equals("卡塔尔")) {
					found = country;
					break;
				}
				if (oecd.address.equals("吉尔吉斯") && country.address.equals("吉尔吉斯斯坦")) {
					found = country;
					break;
				}
				if (oecd.address.equals("刚果（布）") && country.address.equals("刚果共和国")) {
					found = country;
					break;
				}
				if (oecd.address.equals("孟加拉国") && country.address.equals("孟加拉")) {
					found = country;
					break;
				}
			}
			if (found != null) {
				oecd.address = found.address;
				oecd.code = found.code;
				oecd.py = found.py;
				continue;
			}
			System.out.println(oecd);
		}
		Lists.sort(oecds, new Comparator<Country>() {
			@Override
			public int compare(Country o1, Country o2) {
				return o1.code.compareTo(o2.code);
			}
		});
		return oecds;
	}

	public static void main(String[] args) throws IOException {

		// 所有的国家
		List<Country> countrys = countrys();

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
		System.out.println(JsonMapper.toJson(types));
	}
}

class StringExcelMapper extends DefaultExcelMapper<Imp> {
	@Override
	protected List<ColumnMapper> getRowMapper() {
		List<ColumnMapper> columns = Lists.newArrayList();
		columns.add(ColumnMapper.build("国家", "B", DataType.STRING, "name"));
		return columns;
	}

	@Override
	public int getStartRow() {
		return 1;
	}

}

@ToString
@Getter
@Setter
class Imp {
	String name;
}
