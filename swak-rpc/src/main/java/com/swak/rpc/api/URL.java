package com.swak.rpc.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.swak.utils.StringUtils;

/**
 * 用于暴露服务
 * 1. 感觉如果暴露方法则需要的东西太多了，
 * 2. 暴露接口，简单点，那应该创建的是一个接口的代理，这个地方还需要研究下。
 *    那方法上不能配置版本，一般来说版本是针对实现而言的。
 * 3. 所以还是先研究下别人的反射那块的代码，dubbo或turborpc的 反射执行的代码
 *    这块研究透了之后再来做其他的
 * @author lifeng
 */
public class URL implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String protocol;
	private final String host;
	private final int port;
	private final String path;
	private final Map<String, String> parameters;

	protected URL() {
		this.protocol = null;
		this.host = null;
		this.port = 0;
		this.path = null;
		this.parameters = null;
	}

	public URL(String protocol, String host, int port) {
		this(protocol, host, port, null, (Map<String, String>) null);
	}

	public URL(String protocol, String host, int port, Map<String, String> parameters) {
		this(protocol, host, port, null, parameters);
	}

	public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
		this.protocol = protocol;
		this.host = host;
		this.port = (port < 0 ? 0 : port);
		// trim the beginning "/"
		while (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		this.path = path;
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		} else {
			parameters = new HashMap<String, String>(parameters);
		}
		this.parameters = Collections.unmodifiableMap(parameters);
	}
	
	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getParameter(String key) {
		String value = parameters.get(key);
		if (value == null || value.length() == 0) {
			value = parameters.get(Constants.PARAM_KEY_PREFIX + key);
		}
		return value;
	}

	public boolean getParameter(String key, boolean defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public String getParameter(String key, String defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return value;
	}

	public String[] getParameterTypes() {
		List<String> pts = parameters.keySet().stream()
				.filter(k -> StringUtils.startsWith(k, Constants.METHOD_PARAM_KEY_PREFIX)).collect(Collectors.toList());
		return pts.toArray(new String[] {});
	}

	public String getAddress() {
		return port <= 0 ? host : host + ":" + port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URL other = (URL) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}
	
	/**
	 * 直接暴露接口名称，省不少事情
	 * 例如： swak://com.tmt.shop.ShopService
	 * @return
	 */
	public String getServiceKey() {
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0) {
			buf.append(protocol);
			buf.append("://");
		}
		String path = getPath();
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}
		return buf.toString();
	}
	
	/**
	 * 服务器key
	 * @return
	 */
	public String getServerKey() {
		StringBuilder buf = new StringBuilder();
		buf.append(this.getHost());
		buf.append(":");
		buf.append(this.getPort());
		return buf.toString();
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0) {
			buf.append(protocol);
			buf.append("://");
		}
		String host = getHost();
		if (host != null && host.length() > 0) {
			buf.append(host);
			if (port > 0) {
				buf.append(":");
				buf.append(port);
			}
		}
		String path = getPath();
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}
		buildParameters(buf, true);
		return buf.toString();
	}

	private void buildParameters(StringBuilder buf, boolean concat, String ... parameters) {
		if (getParameters() != null && getParameters().size() > 0) {
			List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
			boolean first = true;
			for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
				if (entry.getKey() != null && entry.getKey().length() > 0
						&& (includes == null || includes.contains(entry.getKey()))) {
					if (first) {
						if (concat) {
							buf.append("?");
						}
						first = false;
					} else {
						buf.append("&");
					}
					buf.append(entry.getKey());
					buf.append("=");
					buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
				}
			}
		}
	}
	
    /**
     * Parse url string
     *
     * @param url URL string
     * @return URL instance
     * @see URL
     */
    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf("?"); // seperator between body and parameters 
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<String, String>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf("@");
        if (i >= 0) {
            url = url.substring(i + 1);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, path, parameters);
    }
}
