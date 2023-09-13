package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncIMSTargetingOption {
	private BlockPos pos;
	private TargetingMode targetingMode;

	public SyncIMSTargetingOption() {}

	public SyncIMSTargetingOption(BlockPos pos, TargetingMode targetingMode) {
		this.pos = pos;
		this.targetingMode = targetingMode;
	}

	public SyncIMSTargetingOption(PacketBuffer buf) {
		pos = buf.readBlockPos();
		targetingMode = buf.readEnum(TargetingMode.class);
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(targetingMode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (te instanceof IMSBlockEntity && ((IMSBlockEntity) te).isOwnedBy(player))
			((IMSBlockEntity) te).setTargetingMode(targetingMode);
	}
}
