package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

public class SetStateOnDisguiseModule {
	private BlockState state;
	private StandingOrWallType standingOrWall;

	public SetStateOnDisguiseModule() {}

	public SetStateOnDisguiseModule(BlockState state, StandingOrWallType standingOrWall) {
		this.state = state;
		this.standingOrWall = standingOrWall;
	}

	public SetStateOnDisguiseModule(PacketBuffer buf) {
		state = GameData.getBlockStateIDMap().byId(buf.readInt());
		standingOrWall = buf.readEnum(StandingOrWallType.class);
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(GameData.getBlockStateIDMap().getId(state));
		buf.writeEnum(standingOrWall);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DISGUISE_MODULE.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();

			if (state.isAir()) {
				tag.remove("SavedState");
				tag.remove("StandingOrWall");
				tag.remove("ItemInventory");
			}
			else {
				tag.put("SavedState", NBTUtil.writeBlockState(state));
				tag.putInt("StandingOrWall", standingOrWall.ordinal());
			}
		}
	}
}
