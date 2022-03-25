package com.soccermanager.config;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.junit.jupiter.api.Order;

import static java.lang.Integer.*;
import static java.util.Comparator.comparingInt;

/**
 * @author akif
 * @since 3/23/22
 */
public class TestClassOrderConfig implements ClassOrderer {

	@Override
	public void orderClasses(ClassOrdererContext classOrdererContext) {
		classOrdererContext.getClassDescriptors().sort(comparingInt(TestClassOrderConfig::getOrder));
	}

	private static int getOrder(ClassDescriptor classDescriptor) {
		return classDescriptor.findAnnotation(Order.class).isPresent() ?
			classDescriptor.findAnnotation(Order.class).get().value() : MAX_VALUE;
	}
}
