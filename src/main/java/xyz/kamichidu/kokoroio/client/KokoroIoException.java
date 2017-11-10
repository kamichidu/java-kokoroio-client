package xyz.kamichidu.kokoroio.client;

import java.util.Arrays;

import com.google.api.client.json.GenericJson;

@SuppressWarnings("serial")
public class KokoroIoException
    extends Exception
{
    KokoroIoException(GenericJson data)
    {
        super(data.toString());
    }

    KokoroIoException(GenericJson[] errors)
    {
        super(Arrays.toString(errors));
    }
}
