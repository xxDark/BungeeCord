package net.md_5.bungee.protocol;

import net.md_5.bungee.util.QuietException;

public class OverflowPacketException extends QuietException
{

    public OverflowPacketException(String message)
    {
        super( message );
    }
}
