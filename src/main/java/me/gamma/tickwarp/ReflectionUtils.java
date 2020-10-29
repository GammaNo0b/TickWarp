package me.gamma.tickwarp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

	@SuppressWarnings("unchecked")
	public static <T, C> T getValueFromFirstFieldByType(Class<C> owner, C instance, Class<T> type) {
		for (Field field : owner.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(type)) {
				try {
					field.setAccessible(true);
					return (T) field.get(instance);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return null;
	}

	public static <C> void setValueToFirstFinalField(Class<C> owner, C instance, Object value) {
		for (Field field : owner.getDeclaredFields()) {
			if (Modifier.isFinal(field.getModifiers())) {
				try {
					field.setAccessible(true);
					field.set(instance, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

}
