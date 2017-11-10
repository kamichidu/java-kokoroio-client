package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class AccessToken
{
    public static enum Kind
    {
        @Value("user")
        USER("user"),

        @Value("device")
        DEVICE("device"),

        @Value("essential")
        ESSENTIAL("essential"),
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

    @Key
    private String name;

    @Key
    private String token;

    @Key
    private Kind kind;
}
