package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.registries.GameData;

public class SetStateOnDisguiseModule implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_state_on_disguise_module");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(GameData.getBlockStateIDMap().getId(state));
		buf.writeEnum(standingOrWall);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
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
