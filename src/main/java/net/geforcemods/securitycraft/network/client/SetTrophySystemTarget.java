package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetTrophySystemTarget {

	private BlockPos trophyPos;
	private int targetID;

	public SetTrophySystemTarget() {}

	public SetTrophySystemTarget(BlockPos trophyPos, int targetID) {
		this.trophyPos = trophyPos;
		this.targetID = targetID;
	}

	public static void encode(SetTrophySystemTarget message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.trophyPos);
		buf.writeInt(message.targetID);
	}

	public static SetTrophySystemTarget decode(FriendlyByteBuf buf) {
		SetTrophySystemTarget message = new SetTrophySystemTarget();

		message.trophyPos = buf.readBlockPos();
		message.targetID = buf.readInt();
		return message;
	}

	public static void onMessage(SetTrophySystemTarget message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(message.trophyPos);

			if(tile instanceof TrophySystemTileEntity te && Minecraft.getInstance().level.getEntity(message.targetID) instanceof Projectile projectile)
				te.setTarget(projectile);
		});

		ctx.get().setPacketHandled(true);
	}
}
