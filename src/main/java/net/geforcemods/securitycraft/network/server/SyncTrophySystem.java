package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncTrophySystem {
	private BlockPos pos;
	private ResourceLocation projectileTypeLocation;
	private boolean allowed;

	public SyncTrophySystem() {}

	public SyncTrophySystem(BlockPos pos, EntityType<?> projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileTypeLocation = projectileType.getRegistryName();
		this.allowed = allowed;
	}

	public SyncTrophySystem(PacketBuffer buf) {
		pos = buf.readBlockPos();
		projectileTypeLocation = buf.readResourceLocation();
		allowed = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeResourceLocation(projectileTypeLocation);
		buf.writeBoolean(allowed);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		EntityType<?> projectileType = ForgeRegistries.ENTITIES.getValue(projectileTypeLocation);

		if (projectileType != null) {
			World world = ctx.get().getSender().level;
			TileEntity te = world.getBlockEntity(pos);

			if (te instanceof TrophySystemBlockEntity && ((TrophySystemBlockEntity) te).isOwnedBy(ctx.get().getSender())) {
				BlockState state = world.getBlockState(pos);

				((TrophySystemBlockEntity) te).setFilter(projectileType, allowed);
				world.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
