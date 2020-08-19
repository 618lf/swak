package com.swak.redis;

import java.util.Arrays;
import java.util.Random;

import com.swak.exception.SerializeException;
import com.swak.serializer.SerializationUtils;

/**
 * 命令消息封装
 * 格式：
 * 第1个字节为命令代码，长度1 [OPT]
 * 第2、3个字节为为 key 长度，长度2 [K_LEN]
 * 第4、N 为 key值，长度为 [K_LEN]
 * @author root
 */
public class Command {

	private final static int SRC_ID = genRandomSrc(); //命令源标识，随机生成
	public final static byte OPT_DELETE_KEY = 0x01; 	//删除缓存
	public final static byte OPT_CLEAR_KEY = 0x02; 		//清除缓存
	
	private int src;
	private byte operator;
	private Object key;
	
	private static int genRandomSrc() {
		long ct = System.currentTimeMillis();
		Random rnd_seed = new Random(ct);
		return (int)(rnd_seed.nextInt(10000) * 1000 + ct % 1000);
	}
	
	public Command(byte o,Object k){
		this.operator = o;
		this.key = k;
		this.src = SRC_ID;
	}
	
	public byte[] toBuffers(){
		byte[] keyBuffers = null;
		try {
			keyBuffers = SerializationUtils.serialize(key);
		} catch (SerializeException e) {
			e.printStackTrace();
			return null;
		}
		int k_len = keyBuffers.length;
		byte[] buffers = new byte[9 + k_len];
		int idx = 0;
		System.arraycopy(int2bytes(this.src), 0, buffers, idx, 4);
		idx += 4;
		buffers[idx] = operator;
		idx += 1;
		System.arraycopy(int2bytes(k_len), 0, buffers, idx, 4);
		idx += 4;
		System.arraycopy(keyBuffers, 0, buffers, idx, k_len);
		return buffers;
	}
	
	private static byte[] int2bytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (0xff&i);
        b[1] = (byte) ((0xff00&i) >> 8);
        b[2] = (byte) ((0xff0000&i) >> 16);
        b[3] = (byte) ((0xff000000&i) >> 24);
        return b;
	}
	
	private static int bytes2int(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}
	
	public boolean isLocalCommand() {
		return this.src == SRC_ID;
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}

	public byte getOperator() {
		return operator;
	}

	public void setOperator(byte operator) {
		this.operator = operator;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}
	
	public static Command parse(byte[] buffers) {
		Command cmd = null;
		try{
			int idx = 4;
			byte opt = buffers[idx++];
			int k_len = bytes2int(Arrays.copyOfRange(buffers, idx, idx + 4));
			idx += 4;
			if(k_len > 0){
				byte[] keyBuffers = new byte[k_len];
				System.arraycopy(buffers, idx, keyBuffers, 0, k_len);
				Object key = SerializationUtils.deserialize(keyBuffers);
				cmd = new Command(opt, key);
				cmd.src = bytes2int(buffers);
			}
		}catch(Exception e){e.printStackTrace();}
		return cmd;
	}
}