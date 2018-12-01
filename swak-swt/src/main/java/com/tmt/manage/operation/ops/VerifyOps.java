package com.tmt.manage.operation.ops;

import com.tmt.manage.operation.AbsOps;
import com.tmt.manage.operation.OpsException;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.widgets.MD5s;

/**
 * 验证操作
 * 
 * @author lifeng
 */
public class VerifyOps extends AbsOps {

	/**
	 * 验证文件的 - MD5
	 * 
	 * @throws Exception
	 */
	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			String md51 = new String(file.md5().getData());
			String md52 = MD5s.encode(file.all().getData(), SALT.getBytes());
			if (!(md51 != null && md52 != null && md52.equals(md51))) {
				file.error("升级包验证失败");
			}
		} catch (Exception e) {
			throw new OpsException("升级包验证失败");
		}
	}
}