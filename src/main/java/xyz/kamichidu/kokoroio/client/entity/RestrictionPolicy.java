package xyz.kamichidu.kokoroio.client.entity;

import com.google.api.client.util.Value;

public enum RestrictionPolicy
{
    @Value("Unknown")
    UNKNOWN("Unknown"),

    @Value("Safe")
    SAFE("Safe"),

    @Value("NotSafe")
    NOT_SAFE("NotSafe"),
    ;

    private final String s;

    private RestrictionPolicy(String s)
    {
        this.s= s;
    }

    @Override
    public String toString()
    {
        return this.s;
    }
}
