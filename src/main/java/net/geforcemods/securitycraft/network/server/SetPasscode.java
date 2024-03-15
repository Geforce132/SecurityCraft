package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetPasscode implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_passcode");
	private boolean isEntity;
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public SetPasscode() {}

	public SetPasscode(BlockPos pos, String code) {
		this.isEntity = false;
		this.pos = pos;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(int entityId, String code) {
		this.isEntity = true;
		this.entityId = entityId;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(FriendlyByteBuf buf) {
		isEntity = buf.readBoolean();

		if (isEntity)
			entityId = buf.readVarInt();
		else
			pos = buf.readBlockPos();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(isEntity);

		if (isEntity)
			buf.writeVarInt(entityId);
		else
			buf.writeBlockPos(pos);

		buf.writeUtf(passcode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		IPasscodeProtected passcodeProtected = null;

		if (isEntity) {
			if (level.getEntity(entityId) instanceof IPasscodeProtected pp)
				passcodeProtected = pp;
		}
		else if (level.getBlockEntity(pos) instanceof IPasscodeProtected pp)
			passcodeProtected = pp;

		if (passcodeProtected != null && (!(passcodeProtected instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			passcodeProtected.hashAndSetPasscode(passcode);

			if (!isEntity) {
				if (passcodeProtected instanceof KeypadChestBlockEntity chestBe)
					checkAndUpdateAdjacentChest(chestBe, level, pos, passcode, passcodeProtected.getSalt());
				else if (passcodeProtected instanceof KeypadDoorBlockEntity doorBe)
					checkAndUpdateAdjacentDoor(doorBe, level, passcode, passcodeProtected.getSalt());
			}
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
