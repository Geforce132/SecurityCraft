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

	public static void encode(SetStateOnDisguiseModule message, PacketBuffer buf) {
		buf.writeInt(GameData.getBlockStateIDMap().getId(message.state));
		buf.writeEnum(message.standingOrWall);
	}

	public static SetStateOnDisguiseModule decode(PacketBuffer buf) {
		SetStateOnDisguiseModule message = new SetStateOnDisguiseModule();

		message.state = GameData.getBlockStateIDMap().byId(buf.readInt());
		message.standingOrWall = buf.readEnum(StandingOrWallType.class);
		return message;
	}

	public static void onMessage(SetStateOnDisguiseModule message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.DISGUISE_MODULE.get());

			if (!stack.isEmpty()) {
				CompoundNBT tag = stack.getOrCreateTag();

				tag.put("SavedState", NBTUtil.writeBlockState(message.state));
				tag.putInt("StandingOrWall", message.standingOrWall.ordinal());
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
