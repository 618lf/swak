package com.swak.country;

import java.io.InputStreamReader;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.swak.utils.IOUtils;
import com.swak.utils.JsonMapper;

public class PickerCheckTest {

	// 加载国家数据
	public static List<Country> countrys() {
		ScriptEngineManager SEM = new ScriptEngineManager();
		ScriptEngine SE = SEM.getEngineByName("JavaScript");
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(PickerCheckTest.class.getResourceAsStream("jquery.countrypicker.data.js"),
					"UTF-8");
			SE.eval(reader);
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(reader);
		}
		Bindings params = SE.getBindings(ScriptContext.ENGINE_SCOPE);
		try {
			String countrys = (String) SE.eval(new StringBuilder("WorldCountrys.getCountrys()").toString(), params);
			List<Country> _countrys = JsonMapper.fromJsonToList(countrys, Country.class);
			return _countrys;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

		List<Country> countrys = countrys();
		System.out.println(countrys.size());
		
		for (Country country : countrys) {
			System.out.println(country);
		}
		
	}
}
