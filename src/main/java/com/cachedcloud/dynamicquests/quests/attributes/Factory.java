package com.cachedcloud.dynamicquests.quests.attributes;

import org.json.JSONObject;

import java.util.UUID;

public interface Factory<T extends BaseAttribute> {

  T getAttribute(String type, UUID uuid, String name, JSONObject json);

}
