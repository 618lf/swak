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

import com.swak.paxos.common.Crc32;
import com.swak.paxos.config.Def;

/**
 * leaner状态封装
 */
public class LearnerState {
	private byte[] learnedValue;
	private boolean isLearned;
	private int newChecksum;

	public LearnerState() {
		this.learnedValue = new byte[]{};
		this.isLearned = false;
		this.newChecksum = 0;
	}

	public int learnValue(long instanceID, BallotNumber learnedBallot, byte[] value, int lastChecksum) {
		if(instanceID > 0 && lastChecksum == 0) {
			this.newChecksum = 0;
		} else if(value.length > 0) {
			this.newChecksum = Crc32.crc32(lastChecksum, value, value.length, Def.CRC32SKIP);
		}
		
		learnValueWithoutWrite(instanceID, value, this.newChecksum);
		return 0;
	}

	public void learnValueWithoutWrite(long instanceID, byte[] value, int newCheckSum) {
		this.learnedValue = value;
		this.isLearned = true;
		this.newChecksum = newCheckSum;
	}

	public byte[] getLearnValue() {
		return this.learnedValue;
	}

	public boolean getIsLearned() {
		return this.isLearned;
	}

	public int getNewChecksum() {
		return this.newChecksum;
	}
}

















