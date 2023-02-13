package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.blockentities.KeypadBarrelBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;

public class KeypadBarrelBlock extends BarrelBlock {
	public KeypadBarrelBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (entity instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			KeypadBarrelBlockEntity be = (KeypadBarrelBlockEntity) level.getBlockEntity(pos);

			if (be.verifyPasswordSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(state, level, pos, player);
				}
				else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
					be.openPasswordGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			MenuProvider menuProvider = getMenuProvider(state, level, pos);

			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_BARREL));
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadBarrelBlockEntity(pos, state);
	}

	public static class Convertible implements IPasswordConvertible {
		@Override
		public boolean isValidStateForConversion(BlockState state) {
			return state.is(Tags.Blocks.BARRELS_WOODEN);
		}

		@Override
		public boolean convert(Player player, Level level, BlockPos pos) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);

			convertBarrel(player, level, pos, facing);
			return true;
		}

		private void convertBarrel(Player player, Level level, BlockPos pos, Direction facing) {
			BarrelBlockEntity barrel = (BarrelBlockEntity) level.getBlockEntity(pos);
			CompoundTag tag;

			barrel.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = barrel.saveWithFullMetadata();
			barrel.clearContent();
			level.setBlockAndUpdate(pos, SCContent.KEYPAD_BARREL.get().defaultBlockState().setValue(FACING, facing));
			((BarrelBlockEntity) level.getBlockEntity(pos)).load(tag);
			((IOwnable) level.getBlockEntity(pos)).setOwner(player.getUUID().toString(), player.getName().getString());
		}
	}
}
