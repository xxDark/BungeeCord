package net.md_5.bungee.protocol;

import net.md_5.bungee.util.QuietException;

public class BadPacketException extends QuietException
{

    public BadPacketException(String message)
    {
        super( message );
    }

    public BadPacketException(String message, Throwable cause)
    {
        super( message, cause );
    }
}
