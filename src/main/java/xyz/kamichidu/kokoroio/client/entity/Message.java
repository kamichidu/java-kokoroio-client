package xyz.kamichidu.kokoroio.client.entity;

import java.util.Collections;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class Message
{
    public static enum Status
    {
        @Value("active")
        ACTIVE("active"),

        @Value("deleted_by_publisher")
        DELETED_BY_PUBLISHER("deleted_by_publisher"),

        @Value("deleted_by_another_member")
        DELETED_BY_ANOTHER_MEMBER("deleted_by_another_member"),
        ;

        private final String s;

        private Status(String s)
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
    private int id;

    @Key
    private String idempotentKey;

    @Key
    private String displayName;

    @Key
    private String avatar;

    @Key
    private List<Avatar> avatars= Collections.emptyList();

    @Key
    private Status status;

    @Key
    private String htmlContent;

    @Key
    private String plaintextContent;

    @Key
    private String rawContent;

    @Key
    private List<String> embeddedUrls= Collections.emptyList();

    @Key
    private List<EmbedContent> embedContents= Collections.emptyList();

    @Key
    private DateTime publishedAt;

    @Key
    private boolean nsfw;

    @Key
    private Channel channel;

    @Key
    private Profile profile;
}
