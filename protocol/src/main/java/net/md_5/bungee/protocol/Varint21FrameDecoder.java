package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.error.Errors;

public class Varint21FrameDecoder extends ByteToMessageDecoder
{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        // If we decode an invalid packet and an exception is thrown (thus triggering a close of the connection),
        // the Netty ByteToMessageDecoder will continue to frame more packets and potentially call fireChannelRead()
        // on them, likely with more invalid packets. Therefore, check if the connection is no longer active and if so
        // sliently discard the packet.
        if ( !ctx.channel().isActive() )
        {
            in.skipBytes( in.readableBytes() );
            return;
        }

        in.markReaderIndex();

        int i = 3;
        while ( i-- > 0 )
        {
            if ( !in.isReadable() )
            {
                in.resetReaderIndex();
                return;
            }

            byte read = in.readByte();
            if ( read >= 0 )
            {
                in.resetReaderIndex();
                int packetLength = DefinedPacket.readVarInt( in );

                if ( packetLength <= 0 )
                {
                    super.setSingleDecode( true );
                    Errors.emptyPacket();
                    return;
                }

                if ( in.readableBytes() < packetLength )
                {
                    in.resetReaderIndex();
                    return;
                }
                out.add( in.readRetainedSlice( packetLength ) );
                return;
            }
        }

        super.setSingleDecode( true );
        Errors.badFrameLength();
    }
}
