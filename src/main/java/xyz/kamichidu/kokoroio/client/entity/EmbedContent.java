package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;

import lombok.Data;

@Data
public class EmbedContent
{
    @Key
    private String url;

    @Key
    private int position;

    @Key
    private EmbedData data;
}
