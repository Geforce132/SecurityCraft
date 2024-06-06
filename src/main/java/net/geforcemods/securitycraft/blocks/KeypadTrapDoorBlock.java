package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseIronTrapDoorBlock;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class KeypadTrapDoorBlock extends BaseIronTrapDoorBlock {
	public KeypadTrapDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (state.getValue(OPEN))
			return ActionResultType.PASS;
		else if (!level.isClientSide) {
			KeypadTrapdoorBlockEntity be = (KeypadTrapdoorBlockEntity) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, level, pos, be.getSignalLength());
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World level, BlockPos pos, int signalLength) {
		level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
		level.getBlockTicks().scheduleTick(pos, this, signalLength);
		playSound(null, level, pos, true);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (state.getValue(OPEN)) {
			level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
			playSound(null, level, pos, false);
		}
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(OPEN, false).setValue(POWERED, false);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeypadTrapdoorBlockEntity();
	}

	public static class Convertible implements IPasscodeConvertible {
		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(SCContent.KEYPAD_TRAPDOOR.get());
		}

		@Override
		public boolean protect(PlayerEntity player, World level, BlockPos pos) {
			return convert(level, pos, SCContent.KEYPAD_TRAPDOOR.get());
		}

		@Override
		public boolean unprotect(PlayerEntity player, World level, BlockPos pos) {
			return convert(level, pos, SCContent.REINFORCED_IRON_TRAPDOOR.get());
		}

		private boolean convert(World level, BlockPos pos, Block convertedBlock) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			Half half = state.getValue(HALF);
			boolean waterlogged = state.getValue(WATERLOGGED);
			TileEntity be = level.getBlockEntity(pos);
			CompoundNBT tag = be.save(new CompoundNBT());

			if (be instanceof IModuleInventory)
				((IModuleInventory) be).dropAllModules();

			tag = be.save(new CompoundNBT());
			level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(OPEN, false).setValue(HALF, half).setValue(POWERED, false).setValue(WATERLOGGED, waterlogged));
			level.getBlockEntity(pos).load(null, tag);
			return true;
		}
	}
}
