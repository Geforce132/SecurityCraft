package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetScanType{

	private int x, y, z;
	private String type;

	public SetScanType(){

	}

	public SetScanType(int x, int y, int z, String type){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		type = buf.readString(Integer.MAX_VALUE / 4);

	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(type);

	}

	public static void encode(SetScanType message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetScanType decode(PacketBuffer packet)
	{
		SetScanType message = new SetScanType();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetScanType message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			PlayerEntity player = ctx.get().getSender();
			World world = player.world;

			((InventoryScannerTileEntity) world.getTileEntity(pos)).setScanType(message.type);
			world.getPendingBlockTicks().scheduleTick(pos, BlockUtils.getBlock(world, pos), 1);
			Utils.setISinTEAppropriately(world, pos, ((InventoryScannerTileEntity) world.getTileEntity(pos)).getContents(), ((InventoryScannerTileEntity) world.getTileEntity(pos)).getScanType());
		});

		ctx.get().setPacketHandled(true);
	}

}
