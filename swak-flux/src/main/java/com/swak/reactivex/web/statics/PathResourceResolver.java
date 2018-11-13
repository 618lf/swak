package com.swak.reactivex.web.statics;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

/**
 * 借助
 * 
 * @author lifeng
 */
public class PathResourceResolver {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获得资源
	 * 
	 * @param requestPath
	 * @param locations
	 * @return
	 */
	public Resource resolveResource(String resourcePath, Set<? extends Resource> locations) {
		for (Resource location : locations) {
			Resource resource = getResource(resourcePath, location);
			if (resource != null) {
				return resource;
			}
		}
		return null;
	}

	/**
	 * Find the resource under the given location.
	 * 
	 * @param resourcePath
	 * @param location
	 * @return
	 */
	protected Resource getResource(String resourcePath, Resource location) {
		try {
			Resource resource = location.createRelative(resourcePath);
			if (resource.isReadable()) {
				if (checkResource(resource, location)) {
					return resource;
				} else if (logger.isDebugEnabled()) {
					logger.debug("Resource path \"" + resourcePath + "\" was successfully resolved " + "but resource \""
							+ resource.getURL() + "\" is neither under the " + "current location \""
							+ location.getURL());
				}
			}
			return null;
		} catch (IOException ex) {
			if (logger.isDebugEnabled()) {
				String error = "Skip location [" + location + "] due to error";
				if (logger.isTraceEnabled()) {
					logger.trace(error, ex);
				} else {
					logger.debug(error + ": " + ex.getMessage());
				}
			}
			return null;
		}
	}

	/**
	 * Perform additional checks on a resolved resource beyond checking whether the
	 * resources exists and is readable. The default implementation also verifies
	 * the resource is either under the location relative to which it was found or
	 * is under one of the {@link #setAllowedLocations allowed locations}.
	 * 
	 * @param resource
	 *            the resource to check
	 * @param location
	 *            the location relative to which the resource was found
	 * @return "true" if resource is in a valid location, "false" otherwise.
	 */
	protected boolean checkResource(Resource resource, Resource location) throws IOException {
		if (isResourceUnderLocation(resource, location)) {
			return true;
		}
		return false;
	}

	private boolean isResourceUnderLocation(Resource resource, Resource location) throws IOException {
		if (resource.getClass() != location.getClass()) {
			return false;
		}

		String resourcePath;
		String locationPath;

		if (resource instanceof UrlResource) {
			resourcePath = resource.getURL().toExternalForm();
			locationPath = StringUtils.cleanPath(location.getURL().toString());
		} else if (resource instanceof ClassPathResource) {
			resourcePath = ((ClassPathResource) resource).getPath();
			locationPath = StringUtils.cleanPath(((ClassPathResource) location).getPath());
		} else {
			resourcePath = resource.getURL().getPath();
			locationPath = StringUtils.cleanPath(location.getURL().getPath());
		}

		if (locationPath.equals(resourcePath)) {
			return true;
		}
		locationPath = (locationPath.endsWith("/") || locationPath.isEmpty() ? locationPath : locationPath + "/");
		return (resourcePath.startsWith(locationPath) && !isInvalidEncodedPath(resourcePath));
	}

	private boolean isInvalidEncodedPath(String resourcePath) {
		if (resourcePath.contains("%")) {
			// Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars...
			try {
				String decodedPath = URLDecoder.decode(resourcePath, "UTF-8");
				if (decodedPath.contains("../") || decodedPath.contains("..\\")) {
					logger.warn("Resolved resource path contains encoded \"../\" or \"..\\\": " + resourcePath);
					return true;
				}
			} catch (UnsupportedEncodingException ex) {
				// Should never happen...
			}
		}
		return false;
	}
}