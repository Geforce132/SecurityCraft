package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetTrophySystemTarget {

	private BlockPos trophyPos;
	private int targetID;

	public SetTrophySystemTarget() {}

	public SetTrophySystemTarget(BlockPos trophyPos, int targetID) {
		this.trophyPos = trophyPos;
		this.targetID = targetID;
	}

	public static void encode(SetTrophySystemTarget message, PacketBuffer buf) {
		buf.writeBlockPos(message.trophyPos);
		buf.writeInt(message.targetID);
	}

	public static SetTrophySystemTarget decode(PacketBuffer buf) {
		SetTrophySystemTarget message = new SetTrophySystemTarget();

		message.trophyPos = buf.readBlockPos();
		message.targetID = buf.readInt();
		return message;
	}

	public static void onMessage(SetTrophySystemTarget message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().world.getTileEntity(message.trophyPos);

			if (te instanceof TrophySystemTileEntity) {
				TrophySystemTileEntity trophySystemTE = (TrophySystemTileEntity)te;
				Entity target = Minecraft.getInstance().world.getEntityByID(message.targetID);

				if (target instanceof ProjectileEntity) {
					trophySystemTE.setTarget((ProjectileEntity)target);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
