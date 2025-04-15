package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

public class SetStateOnDisguiseModule {
	private BlockState state;
	private StandingOrWallType standingOrWall;

	public SetStateOnDisguiseModule() {}

	public SetStateOnDisguiseModule(BlockState state, StandingOrWallType standingOrWall) {
		this.state = state;
		this.standingOrWall = standingOrWall;
	}

	public SetStateOnDisguiseModule(FriendlyByteBuf buf) {
		state = GameData.getBlockStateIDMap().byId(buf.readInt());
		standingOrWall = buf.readEnum(StandingOrWallType.class);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(GameData.getBlockStateIDMap().getId(state));
		buf.writeEnum(standingOrWall);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DISGUISE_MODULE.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundTag tag = stack.getOrCreateTag();

			if (state.isAir()) {
				tag.remove("SavedState");
				tag.remove("StandingOrWall");
				tag.remove("ItemInventory");
			}
			else {
				tag.put("SavedState", NbtUtils.writeBlockState(state));
				tag.putInt("StandingOrWall", standingOrWall.ordinal());
			}
		}
	}
}
