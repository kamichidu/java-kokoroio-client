package xyz.kamichidu.kokoroio.client.entity;

import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class Profile
{
    public static enum Type
    {
        @Value("user")
        USER("user"),

        @Value("bot")
        BOT("bot"),
        ;

        private final String s;

        private Type(String s)
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

    @Key
    private Type type;

    @Key("screen_name")
    private String screenName;

    @Key("display_name")
    private String displayName;

    @Key
    private String avatar;

    @Key
    private List<Avatar> avatars= Collections.emptyList();

    @Key("invited_channels_count")
    private int invitedChannelsCount;
}
