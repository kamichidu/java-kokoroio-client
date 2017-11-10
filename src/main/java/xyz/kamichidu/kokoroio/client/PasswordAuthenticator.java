package xyz.kamichidu.kokoroio.client;

import java.io.IOException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Base64;

public class PasswordAuthenticator
    implements Authenticator
{
    private final String email;

    private final String password;

    public PasswordAuthenticator(String email, String password)
    {
        this.email= email;
        this.password= password;
    }

    @Override
    public void initialize(HttpRequest request)
        throws IOException
    {
        String credential= Base64.encodeBase64String((this.email + ":" + this.password).getBytes());
        request.getHeaders().set("X-Account-Token", credential);
    }
}
