package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;

import lombok.Data;

@Data
public class EmbedDataImageInfo
{
    @Key
    private String url;

    @Key
    private int width;

    @Key
    private int height;
}
