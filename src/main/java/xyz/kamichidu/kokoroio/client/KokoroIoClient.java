package xyz.kamichidu.kokoroio.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import xyz.kamichidu.kokoroio.client.entity.Channel;
import xyz.kamichidu.kokoroio.client.entity.Device;

public final class KokoroIoClient
{
    public static final class Builder
        implements Cloneable
    {
        private String baseUrl= "https://kokoro.io";

        private Authenticator authenticator;

        public Builder setBaseUrl(String baseUrl)
        {
            Builder builder= this.clone();
            builder.baseUrl= baseUrl;
            return builder;
        }

        public Builder setAuthenticator(Authenticator authenticator)
        {
            Builder builder= this.clone();
            builder.authenticator= authenticator;
            return builder;
        }

        public KokoroIoClient build()
        {
            return new KokoroIoClient(this);
        }

        @Override
        public Builder clone()
        {
            try
            {
                return (Builder)super.clone();
            }
            catch(CloneNotSupportedException e)
            {
                throw new AssertionError(e);
            }
        }
    }

    private static final HttpTransport transport= new NetHttpTransport.Builder().build();

    private static final JsonFactory jsonFactory= GsonFactory.getDefaultInstance();

    private final GenericUrl baseUrl;

    private final HttpRequestFactory requestFactory;

    public static Builder builder()
    {
        return new Builder();
    }

    KokoroIoClient(Builder builder)
    {
        this.baseUrl= new GenericUrl(builder.baseUrl);
        final Authenticator authenticator= builder.authenticator;
        this.requestFactory= transport.createRequestFactory(new HttpRequestInitializer(){
            @Override
            public void initialize(HttpRequest request)
                throws IOException
            {
                request.setParser(jsonFactory.createJsonObjectParser());
                request.getHeaders().setContentType("application/json; charset=utf-8");
                // handle 4xx and 5xx response error by status code
                request.setThrowExceptionOnExecuteError(false);
                if(authenticator != null)
                {
                    authenticator.initialize(request);
                }
            }
        });
    }

    public Device registerDevice(Device device)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/devices");
        JsonHttpContent content= new JsonHttpContent(jsonFactory, device);
        return this.doRequest(HttpMethods.POST, endpoint, content, Device.class);
    }

    public List<Channel> listChannels()
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels");
        Channel[] items= this.doRequest(HttpMethods.GET, endpoint, null, Channel[].class);
        return Arrays.asList(items);
    }

    public List<Channel> listChannels(boolean archived)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels");
        endpoint.set("archived", archived);
        Channel[] items= this.doRequest(HttpMethods.GET, endpoint, null, Channel[].class);
        return Arrays.asList(items);
    }

    public Channel getChannel(String channelId)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channelId);
        return this.doRequest(HttpMethods.GET, endpoint, null, Channel.class);
    }

    private GenericUrl createEndpoint(GenericUrl baseUrl, String... pathItems)
    {
        try
        {
            // avoid to break caller's instance
            GenericUrl endpoint= baseUrl.clone();
            for(String pathItem : pathItems)
            {
                // avoid to unexpected path junction, e.g. "a" + "b" => "ab"
                endpoint.appendRawPath("/" + pathItem);
            }
            // normalize "//" => "/"
            return new GenericUrl(new URL(endpoint.build()).toURI().normalize());
        }
        catch(MalformedURLException | URISyntaxException e)
        {
            throw new AssertionError(e);
        }
    }

    private <T> T doRequest(String httpMethod, GenericUrl endpoint, HttpContent content, Class<T> responseClazz)
        throws IOException, KokoroIoException
    {
        HttpRequest request= this.requestFactory.buildRequest(httpMethod, endpoint, content);

        HttpResponse response= request.execute();
        try
        {
            if(!response.isSuccessStatusCode())
            {
                // TODO: create concrete error class
                switch(response.getStatusCode())
                {
                    case HttpStatusCodes.STATUS_CODE_BAD_REQUEST:
                        GenericJson[] errors= response.parseAs(GenericJson[].class);
                        throw new KokoroIoException(errors);
                    default:
                        GenericJson error= response.parseAs(GenericJson.class);
                        throw new KokoroIoException(error);
                }
            }

            return response.parseAs(responseClazz);
        }
        finally
        {
            response.disconnect();
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args)
        throws Exception
    {
        System.setProperty("https.protocols", "TLSv1.2");

        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));

        Logger logger= Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.ALL);
        Handler handler= new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        if(false)
        {
            KokoroIoClient kokoro= KokoroIoClient.builder()
                .setAuthenticator(new AccessTokenAuthenticator(System.getProperty("kokoroio.token")))
                .build();
            List<Channel> channels= kokoro.listChannels();
            for(Channel channel : channels)
            {
                System.out.println(channel);
            }
        }
        else
        {
            KokoroIoClient kokoro= KokoroIoClient.builder()
                .setAuthenticator(new PasswordAuthenticator(System.getProperty("kokoroio.email"), System.getProperty("kokoroio.password")))
                .build();
            Device device= new Device();
            device.setName("java-kokoroio-client");
            device.setKind(Device.Kind.ANDROID);
            device.setDeviceIdentifier("java-kokoroio-client");
            device= kokoro.registerDevice(device);

            kokoro= KokoroIoClient.builder()
                .setAuthenticator(new AccessTokenAuthenticator(device.getAccessToken().getToken()))
                .build();

            List<Channel> channels= kokoro.listChannels(true);
            for(Channel channel : channels)
            {
                System.out.println(channel);

                System.out.println(kokoro.getChannel(channel.getId()));
            }
        }
    }
}
