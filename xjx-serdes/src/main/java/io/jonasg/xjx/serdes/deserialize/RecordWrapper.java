package io.jonasg.xjx.serdes.deserialize;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class RecordWrapper<T> {
	private final Map<String, Object> fieldMapping = new HashMap<>();

	private final Class<T> type;

	public RecordWrapper(Class<T> type) {
		this.type = type;
	}

	public void set(String name, Object value) {
		this.fieldMapping.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public T record() {
		try {
			Constructor<?>[] constructors = type.getDeclaredConstructors();

			Constructor<?> constructor = constructors[0];
			constructor.setAccessible(true);

			Object[] args = new Object[constructor.getParameterCount()];
			var parameters = constructor.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				String paramName = parameters[i].getName();
				Class<?> paramType = parameters[i].getType();
				Object paramValue = fieldMapping.get(paramName);
				if (paramValue != null) {
					args[i] = paramValue;
				}
				else {
					if (paramType == boolean.class) {
						args[i] = false;
					}
					else if (paramType == long.class) {
						args[i] = 0;
					}
					else if (paramType == double.class) {
						args[i] = 0;
					}
					else if (paramType == char.class) {
						args[i] = '\000';
					}
					else {
						args[i] = null;
					}
				}
			}

			return (T) constructor.newInstance(args);

		}
		catch (Exception e) {
			throw new RuntimeException("Error creating record", e);
		}
	}

}
