/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.swak.mongo.options;

/**
 * Enum representing the mongoDB Java Driver's {@link com.mongodb.WriteConcern}
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public enum WriteOption {
	/**
	 * @see com.mongodb.WriteConcern#ACKNOWLEDGED
	 */
	ACKNOWLEDGED,
	/**
	 * @see com.mongodb.WriteConcern#UNACKNOWLEDGED
	 */
	UNACKNOWLEDGED,
	/**
	 * @see com.mongodb.WriteConcern#FSYNCED
	 */
	FSYNCED,
	/**
	 * @see com.mongodb.WriteConcern#JOURNALED
	 */
	JOURNALED,
	/**
	 * @see com.mongodb.WriteConcern#REPLICA_ACKNOWLEDGED
	 */
	REPLICA_ACKNOWLEDGED,
	/**
	 * @see com.mongodb.WriteConcern#MAJORITY
	 */
	MAJORITY
}
