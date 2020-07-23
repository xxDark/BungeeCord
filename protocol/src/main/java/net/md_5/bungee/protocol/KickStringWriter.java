package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class KickStringWriter extends MessageToByteEncoder<String>
{

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception
    {
        out.writeByte( 0xFF );
        int len = msg.length();
        out.writeShort( len );
        for ( int i = 0; i < len; i++ )
        {
            out.writeChar( msg.charAt( i ) );
        }
    }
}
