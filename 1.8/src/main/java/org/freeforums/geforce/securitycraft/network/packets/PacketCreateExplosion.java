package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.interfaces.IExplosive;

@SuppressWarnings("static-access")
public class PacketCreateExplosion implements IMessage{
	
	private int x, y, z;
	
	public PacketCreateExplosion(){
		
	}
	
	public PacketCreateExplosion(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCreateExplosion, IMessage> {

	public IMessage onMessage(PacketCreateExplosion packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

		//getWorld(par1EntityPlayer).destroyBlock(new BlockPos(x, y, z), false);
		//this.newExplosion((Entity)null, (double) x, (double) y, (double) z,  3.0F, true, true, getWorld(par1EntityPlayer));
		((IExplosive) getWorld(par1EntityPlayer).getBlockState(new BlockPos(x, y, z)).getBlock()).explode(getWorld(par1EntityPlayer), new BlockPos(x, y, z));
		
		return null;
	}

	public void newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10, World par11World){
		//Explosion explosion = new Explosion(par11World, par1Entity, par2, par4, par6, par8);
		//if(mod_SecurityCraft.instance.configHandler.shouldSpawnFire){
		//	 explosion.isFlaming = true;
		//}else{
		//	 explosion.isFlaming = false;
		//} //TODO
		//	 explosion.isSmoking = par10;
		//	 explosion.doExplosionA();
		//	      
		//	      
		//	 explosion.doExplosionB(true);
		//	 return explosion;
		par11World.createExplosion(par1Entity, par2, par4, par6, par8, par9);
	}
}

}
