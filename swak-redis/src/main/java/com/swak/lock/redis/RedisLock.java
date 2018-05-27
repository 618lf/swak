package com.swak.lock.redis;


import java.util.Map;

import com.swak.Constants;
import com.swak.cache.SafeEncoder;
import com.swak.eventbus.Event;
import com.swak.eventbus.EventConsumer;
import com.swak.eventbus.EventProducer;
import com.swak.lock.LockEntity;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.Operators;

/**
 * 基于 redis 的 local
 * @author lifeng
 */
public class RedisLock implements EventConsumer {
	
	// 需要改为 可以多线程操作的集合
	private Map<String, LockEntity<?>> entitys;
	private EventProducer eventProducer;
	
	/**
	 * 尝试去获取锁 获取之后才能继续后面的操作
	 * 获取不到锁则 立即返回，适时激活
	 * @param resource
	 * @return
	 */
    public <T> Mono<T> tryLock(LockEntity<T> entity) {
    	return Mono.create((sink) -> {
    		tryLock(sink, entity);
    	});
    }
    
    private <T> void tryLock(MonoSink<T> sink, LockEntity<T> entity) {
    	if (lock(entity)) {
    		entity.doHandle().thenApply((v) ->{
    			this.unlock(entity);
    			return v;
    		}).whenComplete((v, e) -> {
			 try {
	                if (e != null) {
	                	sink.error(e);
	                }
	                else if (v != null) {
	                    sink.success(v);
	                }
	                else {
	                    sink.success();
	                }
	            }
	            catch (Throwable e1) {
	                Operators.onErrorDropped(e1, sink.currentContext());
	                throw Exceptions.bubble(e1);
	            }
			});
    		
    		// 返回
    		return;
    	}
    	
    	// 需要启动一个线程来处理这个，检查超时
    	entitys.put(entity.getResource() + "@" + entity.hashCode(), entity.sink(sink));
    }
    
    /**
     * 获取资源的实现
     * @param resource
     * @return
     */
    private <T> boolean lock(LockEntity<T> entity) {
    	return true;
    }
    
    /**
     * 获取资源的实现
     * --- 通知其他 Sink 可以处理了
     * @param resource
     * @return
     */
    private <T> boolean unlock(LockEntity<T> entity) {
    	try {
    		return true;
    	}finally {
        	eventProducer.publish(getChannel(), SafeEncoder.encode(entity.getResource()));
		}
    }

    /**
     * 订阅这个主题
     */
	@Override
	public String getChannel() {
		return Constants.LOCK_TOPIC;
	}

	/**
	 * 激活 entitys 中的 sink
	 */
	@Override
	public void onMessge(Event event) {
		String resource = event.getMessage();
		entitys.keySet().stream().filter(s -> s.startsWith(resource)).forEach(s ->{
			// LockEntity<?> entity = entitys.get(s);
			// this.tryLock(entity.sink(), entity);
		});
	}
}