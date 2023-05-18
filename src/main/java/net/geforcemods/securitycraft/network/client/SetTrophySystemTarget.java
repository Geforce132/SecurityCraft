package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
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

	public SetTrophySystemTarget(PacketBuffer buf) {
		trophyPos = buf.readBlockPos();
		targetID = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(trophyPos);
		buf.writeInt(targetID);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(trophyPos);

		if (te instanceof TrophySystemBlockEntity) {
			TrophySystemBlockEntity trophySystemTE = (TrophySystemBlockEntity) te;
			Entity target = Minecraft.getInstance().level.getEntity(targetID);

			if (target instanceof ProjectileEntity)
				trophySystemTE.setTarget((ProjectileEntity) target);
		}
	}
}
