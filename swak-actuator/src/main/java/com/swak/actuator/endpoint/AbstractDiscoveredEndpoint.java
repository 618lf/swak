package com.swak.actuator.endpoint;

import java.util.Collection;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import com.swak.actuator.endpoint.annotation.EndpointDiscoverer;
import com.swak.actuator.endpoint.annotation.EndpointDiscoverer.EndpointBean;

public abstract class AbstractDiscoveredEndpoint<O extends Operation> extends AbstractExposableEndpoint<O>
		implements DiscoveredEndpoint<O> {

	private final EndpointDiscoverer<?, ?> discoverer;
	private final Object endpointBean;
	
	public AbstractDiscoveredEndpoint(EndpointDiscoverer<?, ?> discoverer,
			EndpointBean endpointBean, Collection<? extends O> operations) {
		super(endpointBean.getId(), endpointBean.isEnabledByDefault(), operations);
		Assert.notNull(discoverer, "Discoverer must not be null");
		Assert.notNull(endpointBean, "EndpointBean must not be null");
		this.discoverer = discoverer;
		this.endpointBean = endpointBean.getBean();
	}

	@Override
	public Object getEndpointBean() {
		return this.endpointBean;
	}

	@Override
	public boolean wasDiscoveredBy(Class<? extends EndpointDiscoverer<?, ?>> discoverer) {
		return discoverer.isInstance(this.discoverer);
	}

	@Override
	public String toString() {
		ToStringCreator creator = new ToStringCreator(this)
				.append("discoverer", this.discoverer.getClass().getName())
				.append("endpointBean", this.endpointBean.getClass().getName());
		appendFields(creator);
		return creator.toString();
	}

	protected void appendFields(ToStringCreator creator) {}
}
