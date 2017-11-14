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
import com.google.gson.JsonObject;

import xyz.kamichidu.kokoroio.client.entity.Channel;
import xyz.kamichidu.kokoroio.client.entity.Device;
import xyz.kamichidu.kokoroio.client.entity.Message;
import xyz.kamichidu.kokoroio.client.entity.Profile;

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

    public Channel createChannel(Channel channel)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels");
        JsonHttpContent content= new JsonHttpContent(jsonFactory, channel);
        return this.doRequest(HttpMethods.POST, endpoint, content, Channel.class);
    }

    public Channel createDirectMessageChannel(Profile profile)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels/direct_message");
        JsonObject params= new JsonObject();
        params.addProperty("target_user_profile_id", profile.getId());
        JsonHttpContent content= new JsonHttpContent(jsonFactory, params);
        return this.doRequest(HttpMethods.POST, endpoint, content, Channel.class);
    }

    public List<Channel> getChannels()
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels");
        Channel[] items= this.doRequest(HttpMethods.GET, endpoint, null, Channel[].class);
        return Arrays.asList(items);
    }

    public List<Channel> getChannels(boolean archived)
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

    public Channel updateChannel(Channel channel)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId());
        JsonHttpContent content= new JsonHttpContent(jsonFactory, channel);
        return this.doRequest(HttpMethods.PUT, endpoint, content, Channel.class);
    }

    public Channel archiveChannel(Channel channel)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "archive");
        return this.doRequest(HttpMethods.DELETE, endpoint, null, Channel.class);
    }

    public Channel unarchiveChannel(Channel channel)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "unarchive");
        return this.doRequest(HttpMethods.PUT, endpoint, null, Channel.class);
    }

    public Message createMessage(Channel channel, String message)
        throws IOException, KokoroIoException
    {
        return this.createMessage(channel, message, false);
    }

    public Message createMessage(Channel channel, String message, boolean nsfw)
        throws IOException, KokoroIoException
    {
        return this.createMessage(channel, message, nsfw, null);
    }

    public Message createMessage(Channel channel, String message, boolean nsfw, String idempotentKey)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "messages");
        JsonObject params= new JsonObject();
        params.addProperty("message", message);
        params.addProperty("nsfw", nsfw);
        if(idempotentKey == null)
        {
            params.addProperty("idempotent_key", idempotentKey);
        }
        JsonHttpContent content= new JsonHttpContent(jsonFactory, params);
        return this.doRequest(HttpMethods.POST, endpoint, content, Message.class);
    }

    public List<Message> getMessages(Channel channel, int limit)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "messages");
        endpoint.set("limit", limit);
        Message[] items= this.doRequest(HttpMethods.GET, endpoint, null, Message[].class);
        return Arrays.asList(items);
    }

    public List<Message> getOlderMessages(Channel channel, int limit, Message before)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "messages");
        endpoint.set("limit", limit);
        endpoint.set("before_id", before.getId());
        Message[] items= this.doRequest(HttpMethods.GET, endpoint, null, Message[].class);
        return Arrays.asList(items);
    }

    public List<Message> getNewerMessages(Channel channel, int limit, Message after)
        throws IOException, KokoroIoException
    {
        GenericUrl endpoint= this.createEndpoint(this.baseUrl, "/api/v1/channels", channel.getId(), "messages");
        endpoint.set("limit", limit);
        endpoint.set("after_id", after.getId());
        Message[] items= this.doRequest(HttpMethods.GET, endpoint, null, Message[].class);
        return Arrays.asList(items);
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
            List<Channel> channels= kokoro.getChannels();
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

            List<Channel> channels= kokoro.getChannels(true);
            for(Channel channel : channels)
            {
                System.out.println(channel);

                System.out.println(kokoro.getChannel(channel.getId()));
            }
        }
    }
}
