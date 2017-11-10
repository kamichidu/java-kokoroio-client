package xyz.kamichidu.kokoroio.client;

import java.io.IOException;

import com.google.api.client.http.HttpRequest;

public class AccessTokenAuthenticator
    implements Authenticator
{
    private final String token;

    public AccessTokenAuthenticator(String token)
    {
        this.token= token;
    }

    @Override
    public void initialize(HttpRequest request)
        throws IOException
    {
        request.getHeaders().set("X-Access-Token", this.token);
    }
}
