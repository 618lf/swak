package com.swak.jre;

import java.io.File;
import java.util.List;

import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * 创建jre
 * 
 * @author lifeng
 * @date 2020年5月24日 上午10:34:20
 */
public class JreBuilder {

	public static void main(String[] args) {
		String java_home = "D:\\java\\jdk-13.0.2";
		List<String> names = Lists.newArrayList();
		File[] modes = new File(java_home, "jmods").listFiles();
		for (File mode : modes) {
			if (mode.getName().startsWith("jdk.incubator.jpackage")
					|| mode.getName().startsWith("jdk.incubator.foreign")) {
				continue;
			}
			names.add(StringUtils.substringBeforeLast(mode.getName(), "."));
		}
		System.out.println(
				"bin\\jlink.exe --module-path jmods --add-modules " + StringUtils.join(names, ",") + " --output jre");
	}
}
