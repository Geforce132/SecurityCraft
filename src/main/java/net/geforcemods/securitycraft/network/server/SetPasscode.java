package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetPasscode(BlockPos pos, String passcode) implements CustomPacketPayload {
	public static final Type<SetPasscode> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_passcode"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetPasscode> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SetPasscode::pos,
			ByteBufCodecs.STRING_UTF8, packet -> PasscodeUtils.hashPasscodeWithoutSalt(packet.passcode),
			SetPasscode::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof IPasscodeProtected be && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			be.hashAndSetPasscode(passcode);

			if (be instanceof KeypadChestBlockEntity chestBe)
				checkAndUpdateAdjacentChest(chestBe, level, pos, passcode, be.getSalt());
			else if (be instanceof KeypadDoorBlockEntity doorBe)
				checkAndUpdateAdjacentDoor(doorBe, level, passcode, be.getSalt());
		}
	}

	private static void checkAndUpdateAdjacentChest(KeypadChestBlockEntity be, Level level, BlockPos pos, String codeToSet, byte[] salt) {
		if (be.getBlockState().getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			BlockPos offsetPos = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
			BlockEntity otherBe = level.getBlockEntity(offsetPos);

			if (otherBe instanceof KeypadChestBlockEntity chestBe && be.getOwner().owns(chestBe)) {
				chestBe.hashAndSetPasscode(codeToSet, salt);
				level.sendBlockUpdated(offsetPos, otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		}
	}

	private static void checkAndUpdateAdjacentDoor(KeypadDoorBlockEntity be, Level level, String codeToSet, byte[] salt) {
		be.runForOtherHalf(otherBe -> {
			if (be.getOwner().owns(otherBe)) {
				otherBe.hashAndSetPasscode(codeToSet, salt);
				level.sendBlockUpdated(otherBe.getBlockPos(), otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		});
	}
}
