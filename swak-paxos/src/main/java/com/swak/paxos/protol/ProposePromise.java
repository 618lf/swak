package com.swak.paxos.protol;

import com.swak.paxos.enums.ResultCode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 提案结果: 异步结果
 * 
 * @author DELL
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProposePromise {

	private ResultCode ret;
	private Proposal proposal;
	private long timeoutTimerId;
}
