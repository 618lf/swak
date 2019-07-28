/*
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.api.motan.rpc;

import java.util.concurrent.CompletableFuture;

public interface ResponseFuture extends Future, Response {
    void onSuccess(Response response);

    void onFailure(Response response);

    long getCreateTime();

    void setReturnType(Class<?> clazz);
    
	/**
	 * 转换为 CompletionStage 方便后面的代码串接起来
	 * 
	 * @return
	 */
	default CompletableFuture<Object> toFuture() {
		CompletableFuture<Object> completableFuture = new CompletableFuture<>();
		this.addListener(f -> {
			if (f.isSuccess()) {
				completableFuture.complete(f.getValue());
			} else {
				completableFuture.completeExceptionally(f.getException());
			}
		});
		return completableFuture;
	}
}
