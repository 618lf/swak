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
package com.swak.paxos.node;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * acceptor current state
 */
@Getter
@Setter
@Accessors(chain = true)
public class AcceptorState {
	private BallotNumber promiseBallot = new BallotNumber(0, 0);
	private BallotNumber acceptedBallot = new BallotNumber(0, 0);
	private byte[] acceptedValue;
	private int checkSum;
	private int syncTimes;

	public AcceptorState() {
		this.syncTimes = 0;
		this.acceptedBallot = new BallotNumber(0, 0);
		this.acceptedBallot.reset();
		this.acceptedValue = new byte[] {};
		this.checkSum = 0;
	}

	public BallotNumber getAcceptedBallot() {
		return acceptedBallot;
	}

	public void setAcceptedBallot(BallotNumber acceptedBallot) {
		this.acceptedBallot = acceptedBallot;
	}

	public byte[] getAcceptedValue() {
		return acceptedValue;
	}

	public void setAcceptedValue(byte[] acceptedValue) {
		this.acceptedValue = acceptedValue;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(int checkSum) {
		this.checkSum = checkSum;
	}

	/**
	 * 持久化存储
	 * 
	 * @param instanceID
	 * @param lastChecksum
	 * @return
	 */
	public int persist(long instanceID, int lastChecksum) {

		return 0;
	}

	/**
	 * acceptor启动 state初始化。
	 * 
	 * @param instanceID
	 * @return
	 * @throws Exception
	 */
	public int load() {

		return 0;
	}

	public BallotNumber getPromiseBallot() {
		return this.promiseBallot;
	}

	public void setPromiseBallot(BallotNumber promiseBallot) {
		this.promiseBallot = promiseBallot;
	}
}
