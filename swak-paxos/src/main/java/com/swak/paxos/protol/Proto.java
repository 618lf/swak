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
 * 消息协议
 * 
 * @author lifeng
 * @date 2020年12月29日 上午11:28:17
 */
public interface Proto {

	public byte[] serializeToBytes() throws SerializeException;

	public void parseFromBytes(byte[] buf, int len) throws SerializeException;

}
