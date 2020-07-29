package net.md_5.bungee.util;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.md_5.bungee.error.Errors;

@UtilityClass
public class ChannelUtil
{
    private final String DISCARD_HANDLER = "discard";

    @SneakyThrows
    public void shutdownChannel(Channel channel, Throwable t, boolean closeForcibly)
    {
        if ( closeForcibly )
        {
            channel.unsafe().closeForcibly();
            return;
        }

        val pipeline = channel.pipeline();
        if ( pipeline.first() != ChannelDiscardHandler.INSTANCE )
        {
            channel.config().setAutoRead( false );
            pipeline.addFirst( DISCARD_HANDLER, ChannelDiscardHandler.INSTANCE );
            (  ( ByteToMessageDecoder ) pipeline.get( "frame-decoder" ) ).setSingleDecode( true );
            channel.close();
            if ( Errors.isDebug() && t != null )
            {
                throw t;
            }
        }
    }

    public void shutdownChannel(Channel channel, Throwable t)
    {
        shutdownChannel( channel, t, false );
    }
}
