package xyz.kamichidu.kokoroio.client.entity;

import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class Channel
{
    public static enum Kind
    {
        @Value("public_channel")
        PUBLIC("public_channel"),

        @Value("private_channel")
        PRIVATE("private_channel"),

        @Value("direct_message")
        DIRECT_MESSAGE("direct_message"),
        ;

        private final String s;

        private Kind(String s)
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
    private String id;

    @Key("channel_name")
    private String channelName;

    @Key
    private Kind kind;

    @Key
    private boolean archived;

    @Key
    private String description;

    @Key
    private Membership membership;

    @Key
    private List<Membership> memberships= Collections.emptyList();
}
