package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class Membership
{
    public static enum Authority
    {
        @Value("administrator")
        ADMINISTRATOR("administrator"),

        @Value("maintainer")
        MAINTAINER("maintainer"),

        @Value("member")
        MEMBER("member"),

        @Value("invited")
        INVITED("invited"),
        ;

        private final String s;

        private Authority(String s)
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
    private Channel channel;

    @Key
    private Authority authority;

    @Key("disable_notification")
    private boolean disableNotification;

    @Key("unread_count")
    private int unreadCount;

    @Key
    private Profile profile;
}
