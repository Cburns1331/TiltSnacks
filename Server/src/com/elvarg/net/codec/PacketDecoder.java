package com.elvarg.net.codec;

import java.util.List;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.security.IsaacRandom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Decodes packets that are received from the player's channel.
 * @author Swiffy
 */
public final class PacketDecoder extends ByteToMessageDecoder {

	private final IsaacRandom random;

	public PacketDecoder(IsaacRandom random) {
		this.random = random;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		if (buffer.readableBytes() >= 2) {
			int encryptedOpcode = buffer.readUnsignedByte();
			int opcode = (encryptedOpcode - random.nextInt()) & 0xFF;

			int encryptedSize = buffer.readUnsignedByte();
			int size = (encryptedSize - random.nextInt()) & 0xFF;

			/*CUSTOM: Since 2 slots are taken in the client's buffer array by op_code and pkt_size, subtract them 
			 		to get the actual size of the buffer.*/
			if(size >= 2) {
				size -= 2;
			}
			
			if (buffer.isReadable(size)) {
				try {
					out.add(new Packet(opcode, buffer.readBytes(size)));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
