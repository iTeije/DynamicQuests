package com.cachedcloud.dynamicquests.quests.attributes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.UUID;

public interface BaseAttribute {

  UUID getUuid();
  String getName();
  JSONObject getJson(); // the subattributes (i.e. configuration for a specific reward or objective)

  void updateAttribute(String key, String input);
  void parseJson(JSONObject json) throws JSONException, NullPointerException, NoSuchElementException;

}
