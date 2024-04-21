package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RefreshDisguisableModel(BlockPos pos, boolean insert, ItemStack stack, boolean toggled) implements CustomPacketPayload {

	public static final Type<RefreshDisguisableModel> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "refresh_disguisable_model"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RefreshDisguisableModel> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RefreshDisguisableModel::pos,
			ByteBufCodecs.BOOL, RefreshDisguisableModel::insert,
			ItemStack.STREAM_CODEC, RefreshDisguisableModel::stack,
			ByteBufCodecs.BOOL, RefreshDisguisableModel::toggled,
			RefreshDisguisableModel::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		BlockEntity be = ctx.player().level().getBlockEntity(pos);

		if (be instanceof IModuleInventory moduleInv) {
			if (insert)
				moduleInv.insertModule(stack, toggled);
			else
				moduleInv.removeModule(ModuleType.DISGUISE, toggled);

			ClientHandler.refreshModelData(be);
		}
	}
}
