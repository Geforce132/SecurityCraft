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
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public SetPasscode() {}

	public SetPasscode(BlockPos pos, String code) {
		this.pos = pos;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(int entityId, String code) {
		this.entityId = entityId;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeUtf(passcode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		IPasscodeProtected passcodeProtected = getPasscodeProtected(level);

		if (passcodeProtected != null && (!(passcodeProtected instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			passcodeProtected.hashAndSetPasscode(passcode);

			if (pos != null) {
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

	private IPasscodeProtected getPasscodeProtected(Level level) {
		if (pos != null) {
			if (level.getBlockEntity(pos) instanceof IPasscodeProtected pp)
				return pp;
		}
		else if (level.getEntity(entityId) instanceof IPasscodeProtected pp)
			return pp;

		return null;
	}
}
