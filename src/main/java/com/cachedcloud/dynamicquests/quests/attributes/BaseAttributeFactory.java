package com.cachedcloud.dynamicquests.quests.attributes;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

public class BaseAttributeFactory<T extends BaseAttribute> {

  @Getter
  private final Factory<T> factory;

  public BaseAttributeFactory(Class<? extends Factory<T>> factoryClass) {
    try {
      factory = factoryClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalArgumentException("Error creating factory instance", e);
    }
  }

}
