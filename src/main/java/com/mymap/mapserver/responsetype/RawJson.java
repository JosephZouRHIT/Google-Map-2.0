package com.mymap.mapserver.responsetype;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;

public class RawJson {
    private final String value;

    public RawJson(String value){
        this.value = value;
    }

    @JsonValue
    @JsonRawValue
    public String getValue() {
        return value;
    }
}
