package com.swak.serializer;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.swak.exception.SerializeException;

/**
 * Created by chenlei on 14-9-28.
 *
 * @author: lifeng
 * @date: 2020/3/29 13:30
 */
public class KryoPoolSerializer implements Serializer {

    /**
     * Kryo 的包装
     */
    private static class KryoHolder {
        private Kryo kryo;
        static final int BUFFER_SIZE = 1024;
        private Output output = new Output(BUFFER_SIZE, -1);     //reuse
        private Input input = new Input();

        KryoHolder(Kryo kryo) {
            this.kryo = kryo;
        }

    }


    interface KryoPool {

        /**
         * get o kryo object
         *
         * @return KryoHolder instance
         */
        KryoHolder get();

        /**
         * return object
         *
         * @param kryo holder
         */
        void offer(KryoHolder kryo);
    }


    /**
     * 由于kryo创建的代价相对较高 ，这里使用空间换时间
     * 对KryoHolder对象进行重用
     */
    public static class KryoPoolImpl implements KryoPool {
        /**
         * thread safe list
         */
        private final Deque<KryoHolder> kryoHolderDeque = new ConcurrentLinkedDeque<>();

        private KryoPoolImpl() {

        }

        /**
         * @return KryoPool instance
         */
        public static KryoPool getInstance() {
            return Singleton.POOL;
        }

        /**
         * get o KryoHolder object
         *
         * @return KryoHolder instance
         */
        @Override
        public KryoHolder get() {
            KryoHolder kryoHolder = kryoHolderDeque.pollFirst();       // Retrieves and removes the head of the queue represented by this table
            return kryoHolder == null ? creatInstnce() : kryoHolder;
        }

        /**
         * create a new kryo object to application use
         *
         * @return KryoHolder instance
         */
        public KryoHolder creatInstnce() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);//
            return new KryoHolder(kryo);
        }

        /**
         * return object
         * Inserts the specified element at the tail of this queue.
         *
         * @param kryoHolder ...
         */
        @Override
        public void offer(KryoHolder kryoHolder) {
            kryoHolderDeque.addLast(kryoHolder);
        }

        /**
         * creat a Singleton
         */
        private static class Singleton {
            private static final KryoPool POOL = new KryoPoolImpl();
        }
    }

    @Override
    public String name() {
        return "kryo_pool";
    }

    /**
     * Serialize object
     *
     * @param obj what to serialize
     * @return return serialize data
     */
    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        if (obj == null) {
            return null;
        }
        KryoHolder kryoHolder = null;
        try {
            kryoHolder = KryoPoolImpl.getInstance().get();
            //clear Output    -->每次调用的时候  重置
            kryoHolder.output.clear();
            kryoHolder.kryo.writeClassAndObject(kryoHolder.output, obj);
            // 无法避免拷贝  ~~~
            return kryoHolder.output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialize obj exception");
        } finally {
            KryoPoolImpl.getInstance().offer(kryoHolder);
        }
    }

    /**
     * Deserialize data
     *
     * @param bytes what to deserialize
     * @return object
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        if (bytes == null) {
            return null;
        }
        KryoHolder kryoHolder = null;
        try {
            kryoHolder = KryoPoolImpl.getInstance().get();
            //call it ,and then use input object  ,discard any array
            kryoHolder.input.setBuffer(bytes, 0, bytes.length);
            return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
        } catch (Exception e) {
            throw new SerializeException("Deserialize bytes exception");
        } finally {
            KryoPoolImpl.getInstance().offer(kryoHolder);
        }
    }
}