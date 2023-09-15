package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class KeycardLockBlock extends AbstractPanelBlock {
	public KeycardLockBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return KeycardReaderBlock.<KeycardLockBlockEntity>use(state, level, pos, player, hand, (stack, be) -> {
			if (be.isOwnedBy(player)) {
				if (stack.getItem() instanceof UniversalKeyChangerItem) {
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
					be.reset();
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), ChatFormatting.RED);
			}
		});
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeycardLockBlockEntity(pos, state);
	}
}
