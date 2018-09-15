package com.tmt;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.swak.entity.BaseEntity;
import com.swak.utils.ReflectUtils;
import com.swak.vertx.handler.MethodParameter;

/**
 * 范型的研究
 * 
 * @author lifeng
 */
public class TestMain {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, NoSuchMethodException {

//		// 参数化类型, 也就是 Area 字段的类型
//		Field field = Area.class.getDeclaredField("labels");
//
//		// 是否参数化类型
//		Type type = field.getGenericType();
//		if (type instanceof ParameterizedType) {
//			ParameterizedType ptype = (ParameterizedType) type;
//
//			// 获取实际的类型
//			System.out.println(ptype.getActualTypeArguments()[0]);
//		}
//
//		// 方法中的范型
		Method[] methods = Area.class.getMethods();
		for (Method method : methods) {

			// 方法返回值中的类型
			if (method.getName().equals("getLabels")) {
				Type mtype = method.getGenericReturnType();
				if (mtype instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) mtype;

					// 获取实际的类型
					Class<?> nestType = ReflectUtils.getClass(ptype.getActualTypeArguments()[0]);
					System.out.println("实际的类型：" + nestType);
				}
			}
			
			// 方法参数中的实际类型
			if (method.getName().equals("setLabels")) {
				Type mtype = method.getGenericParameterTypes()[0];
				if (mtype instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) mtype;

					// 获取实际的类型
					System.out.println(ptype.getActualTypeArguments()[0]);
				}
			}
			
			// swak 中的类型工具类
			if (method.getName().equals("setLabels")) {
				int count = method.getParameterTypes().length;
				MethodParameter[] result = new MethodParameter[count];
				for (int i = 0; i < count; i++) {
					MethodParameter parameter = new MethodParameter(Area.class, method, i);
					result[i] = parameter;
				}
				System.out.println("spring 中的处理方式 - ");
				for(MethodParameter p: result) {
					System.out.println(p.getNestedGenericParameterType());
					System.out.println(p.getNestedParameterType());
				}
				System.out.println("spring 中的处理方式 - ");
			}
		}
//		
//		// 父类中的参数化类型 即 id 的类型
//		Field[] declaredFields = Area.class.getDeclaredFields();
//		Field idField = ReflectUtils.getField(Area.class, "id", declaredFields);
//		Type idType = idField.getGenericType();
//		if (idType instanceof TypeVariable) {
//			System.out.println("我是类型变量");
//			// 比较复杂， 直接调用ReflectUtils
//			TypeVariable<?> tv = (TypeVariable<?>) idType;
//			Type acttype = ReflectUtils.getFieldType(Area.class, tv);
//			System.out.println(acttype);
//		}
//		
////		// fieldCache 的处理方式, 只是处理的实际的类型, 一样需要处理范型类型
////		System.out.println("fieldCache 的处理方式");
////		ClassMeta classMeta = FieldCache.set(Area.class);
////		Map<String, FieldMeta> fieldMetas = classMeta.getFields();
////		fieldMetas.values().forEach(s  -> {
////			System.out.println(s.getPropertyName() + ":" + s.getFieldClass());
////		});
//		
//		Pattern OBJECT_PARAM_PATTERN = Pattern.compile("\\w+\\[(\\w+)\\]");
//		String key = "user[name]";
//		Matcher matcher = OBJECT_PARAM_PATTERN.matcher(key);
//		System.out.println(matcher.find());
		
		// JWT 的测试
//		VertxProperties properties = new VertxProperties();
//		JwtAuthProvider provider = new JwtAuthProvider(properties);
//		
//		JWTPayload payload = new JWTPayload();
//		payload.put("id", "123");
//		String token = provider.generateToken(payload);
//		System.out.println(token);
//		
//		payload = provider.verifyToken(token);
//		System.out.println(JsonMapper.toJson(payload));
		
		
		// 自动身成类 AreaServiceFacade 生成 AreaServiceFacadeAsync
	}
}

/**
 * 研究的对象
 * 
 * @author lifeng
 */
class Area extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	private List<String> labels;

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
}