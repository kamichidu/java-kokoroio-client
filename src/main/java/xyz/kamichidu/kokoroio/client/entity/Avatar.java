package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;

import lombok.Data;

@Data
public class Avatar {
    @Key
    private int size;

    @Key
    private String url;

    @Key("is_default")
    private boolean isDefault;
}
