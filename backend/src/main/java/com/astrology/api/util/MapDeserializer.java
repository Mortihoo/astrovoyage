package com.astrology.api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapDeserializer extends JsonDeserializer<Map<String, Object>> {
    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            while (p.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = p.getCurrentName();
                p.nextToken();
                
                if (p.getCurrentToken() == JsonToken.START_ARRAY) {
                    // Handle array values (for double[])
                    double[] array = new double[0];
                    int i = 0;
                    while (p.nextToken() != JsonToken.END_ARRAY) {
                        if (array.length == i) {
                            double[] newArray = new double[array.length + 1];
                            System.arraycopy(array, 0, newArray, 0, array.length);
                            array = newArray;
                        }
                        array[i++] = p.getDoubleValue();
                    }
                    result.put(fieldName, array);
                } else {
                    // Handle other values
                    result.put(fieldName, p.getValueAsString());
                }
            }
        }
        
        return result;
    }
} 