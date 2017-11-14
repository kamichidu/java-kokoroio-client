package xyz.kamichidu.kokoroio.client.entity;

import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class EmbedData
{
    public static enum ResourceType
    {
        @Value("MixedContent")
        MIXED_CONTENT("MixedContent"),

        @Value("SingleImage")
        SINGLE_IMAGE("SingleImage"),

        @Value("SingleVideo")
        SINGLE_VIDEO("SingleVideo"),

        @Value("SingleAudio")
        SINGLE_AUDIO("SingleAudio"),
        ;

        private final String s;

        private ResourceType(String s)
        {
            this.s= s;
        }

        @Override
        public String toString()
        {
            return this.s;
        }
    }

    private int cacheAge;

    private List<EmbedDataMedia> medias= Collections.emptyList();

    private List<EmbedDataMedia> metadataImage= Collections.emptyList();

    private RestrictionPolicy restrictionPolicy;

    private String title;

    private ResourceType type;

    private String url;
}
