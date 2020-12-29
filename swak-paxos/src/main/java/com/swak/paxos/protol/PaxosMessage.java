/*
 * Copyright (C) 2005-present, 58.com.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swak.paxos.protol;

import com.swak.exception.SerializeException;

/**
 * PaxosMsg proto
 */
public class PaxosMessage implements Proto {

	private int msgType;
	private long instanceID;
	private long nodeID;
	private long proposalID;
	private long timestamp;
	private long preAcceptID;
	private long preAcceptNodeID;
	private long rejectByPromiseID;
	private long nowInstanceID;
	private long minChosenInstanceID;
	private int lastChecksum;
	private int flag;
	private byte[] systemVariables;
	private byte[] masterVariables;
	private byte[] value;
	private long proposalNodeID;

	public PaxosMessage() {
		super();
		this.timestamp = System.currentTimeMillis();
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public long getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}

	public long getNodeID() {
		return nodeID;
	}

	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}

	public long getProposalID() {
		return proposalID;
	}

	public void setProposalID(long proposalID) {
		this.proposalID = proposalID;
	}

	public long getProposalNodeID() {
		return proposalNodeID;
	}

	public void setProposalNodeID(long proposalNodeID) {
		this.proposalNodeID = proposalNodeID;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public long getPreAcceptID() {
		return preAcceptID;
	}

	public void setPreAcceptID(long preAcceptID) {
		this.preAcceptID = preAcceptID;
	}

	public long getPreAcceptNodeID() {
		return preAcceptNodeID;
	}

	public void setPreAcceptNodeID(long preAcceptNodeID) {
		this.preAcceptNodeID = preAcceptNodeID;
	}

	public long getRejectByPromiseID() {
		return rejectByPromiseID;
	}

	public void setRejectByPromiseID(long rejectByPromiseID) {
		this.rejectByPromiseID = rejectByPromiseID;
	}

	public long getNowInstanceID() {
		return nowInstanceID;
	}

	public void setNowInstanceID(long nowInstanceID) {
		this.nowInstanceID = nowInstanceID;
	}

	public long getMinChosenInstanceID() {
		return minChosenInstanceID;
	}

	public void setMinchosenInstanceID(long minChosenInstanceID) {
		this.minChosenInstanceID = minChosenInstanceID;
	}

	public int getLastChecksum() {
		return lastChecksum;
	}

	public void setLastChecksum(int lastChecksum) {
		this.lastChecksum = lastChecksum;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public byte[] getSystemVariables() {
		return systemVariables;
	}

	public void setSystemVariables(byte[] systemVariables) {
		this.systemVariables = systemVariables;
	}

	public byte[] getMasterVariables() {
		return masterVariables;
	}

	public void setMasterVariables(byte[] masterVariables) {
		this.masterVariables = masterVariables;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] serializeToBytes() throws SerializeException {
		return null;
	}

	public void parseFromBytes(byte[] buf, int len) throws SerializeException {

	}
}
