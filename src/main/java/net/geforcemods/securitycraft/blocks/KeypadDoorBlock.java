package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class KeypadDoorBlock extends SpecialDoorBlock {
	public KeypadDoorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.getValue(OPEN))
			return InteractionResult.PASS;
		else if (!level.isClientSide) {
			KeypadDoorBlockEntity be = (KeypadDoorBlockEntity) level.getBlockEntity(pos);

			if (ModuleUtils.isDenied(be, player)) {
				if (be.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			}
			else if (ModuleUtils.isAllowed(be, player)) {
				if (be.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

				activate(state, level, pos, be.getSignalLength());
			}
			else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				be.openPasswordGUI(player);
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(BlockState state, Level level, BlockPos pos, int signalLength) {
		boolean open = !state.getValue(OPEN);

		level.levelEvent(null, open ? 1005 : 1011, pos, 0);
		level.setBlockAndUpdate(pos, state.setValue(OPEN, open));
		level.updateNeighborsAt(pos, SCContent.KEYPAD_DOOR.get());

		if (open && signalLength > 0)
			level.scheduleTick(pos, SCContent.KEYPAD_DOOR.get(), signalLength);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadDoorBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BaseEntityBlock.createTickerHelper(type, SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public Item getDoorItem() {
		return SCContent.KEYPAD_DOOR_ITEM.get();
	}
}
