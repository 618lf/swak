package com.swak.paxos.protol;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 提案
 * 
 * @author lifeng
 * @date 2020年12月29日 下午5:04:35
 */
@Getter
@Setter
@Accessors(chain = true)
public class Propoal {
	private long propoalID;
	private int group;
	private byte[] value;
	private long timeoutMs;
}