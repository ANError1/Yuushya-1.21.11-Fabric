package com.yuushya.datagen.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.yuushya.datagen.utils.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiselingRecipesData {
    private static final Map<ResourceLocation, List<JsonElement>> chiselCache = new HashMap<>();
    public static ResourceLocation addEntry(ResourceLocation groupName, ResourceLocation id){
        ResourceLocation key = new ResourceLocation(groupName.getNamespace(), groupName.getPath());
        chiselCache.putIfAbsent(key,new ArrayList<>());
        chiselCache.get(key).add(setEntry(id.toString()));
        return key;
    }
    public static JsonElement setEntry(String id){
        JsonObject json = new JsonObject();
        json.add("item", new JsonPrimitive(id));
        return json;
    }
    public static JsonElement genRecipes(ResourceLocation dataKey){
        JsonArray array = new JsonArray();
        chiselCache.get(dataKey).forEach(array::add);

        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive("rechiseled:chiseling"));
        json.add("overwrite", new JsonPrimitive(false));
        json.add("entries", array);
        return json;
    }
}
