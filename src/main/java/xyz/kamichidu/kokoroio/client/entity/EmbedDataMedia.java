package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class EmbedDataMedia
{
    public static enum MediaType
    {
        @Value("Image")
        Image("Image"),

        @Value("Video")
        Video("Video"),

        @Value("Audio")
        Audio("Audio"),
        ;

        private final String s;

        private MediaType(String s)
        {
            this.s= s;
        }

        @Override
        public String toString()
        {
            return this.s;
        }
    }

    @Key
    private String rawUrl;

    @Key
    private RestrictionPolicy restrictionPolicy;

    @Key
    private MediaType type;

    @Key
    private EmbedDataImageInfo thumbnail;
}
