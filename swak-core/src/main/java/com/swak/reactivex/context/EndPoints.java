package com.swak.reactivex.context;

import java.util.List;

import com.swak.utils.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 端点集合
 * 
 * @author lifeng
 * @date 2020年5月11日 下午4:56:42
 */
@Getter
@Setter
@Accessors(chain = true)
public class EndPoints {

	/**
	 * 地址
	 */
	private String host;

	/**
	 * 所有发布的端口
	 */
	private List<EndPoint> endPoints;

	/**
	 * 格式化输出
	 */
	@Override
	public String toString() {
		StringBuilder ports = new StringBuilder();
		for (int i = 0; i < endPoints.size(); i++) {
			EndPoint point = endPoints.get(i);
			ports.append(point.getScheme()).append(":").append(point.getPort()).append("[").append(point.getParallel())
					.append("]");
			if (i != endPoints.size() - 1) {
				ports.append(", ");
			}
		}
		return StringUtils.format("Host: %s, %s", host, ports.toString());
	}

	/**
	 * 具体的端口
	 * 
	 * @author lifeng
	 * @date 2020年5月11日 下午5:00:16
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class EndPoint {

		/**
		 * 协议
		 */
		private com.swak.annotation.Server scheme;

		/**
		 * 地址
		 */
		private String host;

		/**
		 * 端口
		 */
		private int port;

		/**
		 * 并行数
		 */
		private int parallel;

		/**
		 * 格式化输出
		 */
		@Override
		public String toString() {
			return StringUtils.format("%s://%s:%s/", scheme, host, port);
		}
	}
}