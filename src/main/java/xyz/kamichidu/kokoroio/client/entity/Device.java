package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

import lombok.Data;

@Data
public class Device
{
    public static enum Kind
    {
        @Value("unknown")
        UNKNOWN("unknown"),

        @Value("ios")
        IOS("ios"),

        @Value("android")
        ANDROID("android"),

        @Value("uwp")
        UWP("uwp"),

        @Value("chrome")
        CHROME("chrome"),

        @Value("firefox")
        FIREFOX("firefox"),

        @Value("official_web")
        OFFICIAL_WEB("official_web"),
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
    private String name;

    @Key
    private Kind kind;

    @Key("device_identifier")
    private String deviceIdentifier;

    @Key("notification_identifier")
    private String notificationIdentifier;

    @Key("subscribe_notification")
    private boolean subscribeNotification;

    // XXX: want java.util.Date or standard class, not a type library provided
    @Key("last_activity_at")
    private DateTime lastActivityAt;

    @Key("push_registered")
    private boolean pushRegistered;

    @Key("access_token")
    private AccessToken accessToken;
}
