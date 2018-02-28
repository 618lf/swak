/**
 * 
 */
package com.swak.common.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * 使用 FST 实现序列化
 * @author winterlau
 */
public class FSTSerializer implements Serializer {
	
	@Override
	public String name() {
		return "fst";
	}
	
	@Override
	public byte[] serialize(Object obj) throws SerializeException {
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try {
			out = new ByteArrayOutputStream();
			fout = new FSTObjectOutput(out);
			fout.writeObject(obj);
			fout.flush();
			return out.toByteArray();
		} catch (Exception e) {
			throw new SerializeException(e);
		} finally {
			if(fout != null)
			try {
				fout.close();
			} catch (IOException e) {}
			obj = null; //GC
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializeException {
		if(bytes == null || bytes.length == 0)
			return null;
		FSTObjectInput in = null;
		try {
			in = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return in.readObject();
		} catch (Exception e) {
			throw new SerializeException(e);
		} finally {
			if(in != null)
			try {
				in.close();
			} catch (IOException e) {}
			bytes = null; //GC
		}
	}
}
