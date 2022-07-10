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
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

public class SetStateOnDisguiseModule {
	private BlockState state;
	private StandingOrWallType standingOrWall;

	public SetStateOnDisguiseModule() {}

	public SetStateOnDisguiseModule(BlockState state, StandingOrWallType standingOrWall) {
		this.state = state;
		this.standingOrWall = standingOrWall;
	}

	public static void encode(SetStateOnDisguiseModule message, FriendlyByteBuf buf) {
		buf.writeInt(GameData.getBlockStateIDMap().getId(message.state));
		buf.writeEnum(message.standingOrWall);
	}

	public static SetStateOnDisguiseModule decode(FriendlyByteBuf buf) {
		SetStateOnDisguiseModule message = new SetStateOnDisguiseModule();

		message.state = GameData.getBlockStateIDMap().byId(buf.readInt());
		message.standingOrWall = buf.readEnum(StandingOrWallType.class);
		return message;
	}

	public static void onMessage(SetStateOnDisguiseModule message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.DISGUISE_MODULE.get());

			if (!stack.isEmpty()) {
				CompoundTag tag = stack.getOrCreateTag();

				tag.put("SavedState", NbtUtils.writeBlockState(message.state));
				tag.putInt("StandingOrWall", message.standingOrWall.ordinal());
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
