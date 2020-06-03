package com.sample.tools.operation.ops;

import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsFile;

//logText.setText("");
//logText.append("选择目录：" + dir + "\n");
//logText.append("创建目录PTMS，初始化增量包结构" + "\n");
//logText.append("**********目录结构*********" + "\n");
//logText.append("***PTMS" + "\n");
//logText.append("***└  JAR" + "\n");
//logText.append("***└  SQLS" + "\n");
//logText.append("***└  LIBS" + "\n");
//logText.append("***└  STATICS" + "\n");
//logText.append("***└  CONFIGS" + "\n");
//logText.append("*************************" + "\n");
//logText.append("请将相关文件放入相应的目录，填入版本号" + "\n");

/**
 * 打包
 * 
 * @author lifeng
 * @date 2020年5月18日 下午10:32:25
 */
public class PackOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		file.compress();
	}
}
