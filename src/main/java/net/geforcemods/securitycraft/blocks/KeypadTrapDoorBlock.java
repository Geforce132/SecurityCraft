package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseIronTrapDoorBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class KeypadTrapDoorBlock extends BaseIronTrapDoorBlock {
	public KeypadTrapDoorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.getValue(OPEN))
			return InteractionResult.PASS;
		else if (!level.isClientSide) {
			KeypadTrapdoorBlockEntity be = (KeypadTrapdoorBlockEntity) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(state, level, pos, be.getSignalLength());
				}
				else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get()))
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, int signalLength) {
		level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
		level.scheduleTick(pos, this, signalLength);
		playSound(null, level, pos, true);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(OPEN)) {
			level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
			playSound(null, level, pos, false);
		}
	}

	//here for making it accessible without AT
	@Override
	public void playSound(Player player, Level level, BlockPos pos, boolean isOpened) {
		super.playSound(player, level, pos, isOpened);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
				SaltData.removeSalt(be.getSaltKey());

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof INameSetter nameable)
			nameable.setCustomName(stack.getHoverName());
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadTrapdoorBlockEntity(pos, state);
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isValidStateForConversion(BlockState state) {
			return state.is(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		}

		@Override
		public boolean convert(Player player, Level level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			boolean open = state.getValue(OPEN);
			Half half = state.getValue(HALF);
			boolean powered = state.getValue(POWERED);
			boolean waterlogged = state.getValue(WATERLOGGED);
			BlockEntity trapdoor = level.getBlockEntity(pos);
			CompoundTag tag = trapdoor.saveWithFullMetadata();

			level.setBlockAndUpdate(pos, SCContent.KEYPAD_TRAPDOOR.get().defaultBlockState().setValue(FACING, facing).setValue(OPEN, open).setValue(HALF, half).setValue(POWERED, powered).setValue(WATERLOGGED, waterlogged));
			trapdoor = level.getBlockEntity(pos);
			trapdoor.load(tag);
			((IOwnable) trapdoor).setOwner(player.getUUID().toString(), player.getName().getString());
			return true;
		}
	}
}
