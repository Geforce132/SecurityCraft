package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity.IMSTargetingMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncIMSTargetingOption
{
	private BlockPos pos;
	private IMSTargetingMode targetingMode;

	public SyncIMSTargetingOption() {}

	public SyncIMSTargetingOption(BlockPos pos, IMSTargetingMode targetingMode)
	{
		this.pos = pos;
		this.targetingMode = targetingMode;
	}

	public static void encode(SyncIMSTargetingOption message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.targetingMode);
	}

	public static SyncIMSTargetingOption decode(PacketBuffer buf)
	{
		SyncIMSTargetingOption message = new SyncIMSTargetingOption();

		message.pos = buf.readBlockPos();
		message.targetingMode = buf.readEnum(IMSTargetingMode.class);
		return message;
	}

	public static void onMessage(SyncIMSTargetingOption message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.level.getBlockEntity(pos);

			if(te instanceof IMSTileEntity && ((IMSTileEntity)te).getOwner().isOwner(player))
				((IMSTileEntity)te).setTargetingMode(message.targetingMode);
		});

		ctx.get().setPacketHandled(true);
	}

}
