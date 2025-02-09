package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {
	protected ExplosiveBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.getItemInHand(hand).getItem() == SCContent.MINE_REMOTE_ACCESS_TOOL.get())
			return ActionResultType.SUCCESS;

		if (isActive(level, pos) && isDefusable() && player.getItemInHand(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
			if (defuseMine(level, pos)) {
				if (!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			return ActionResultType.SUCCESS;
		}

		if (!isActive(level, pos) && player.getItemInHand(hand).getItem() == Items.FLINT_AND_STEEL) {
			if (activateMine(level, pos)) {
				if (!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			return ActionResultType.SUCCESS;
		}

		if (explodesWhenInteractedWith() && isActive(level, pos)) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IOwnable && ((IOwnable) te).isOwnedBy(player))
				return ActionResultType.PASS;

			if (te instanceof ICustomizable) {
				ICustomizable mine = (ICustomizable) te;

				for (Option<?> option : mine.customOptions()) {
					if (option instanceof TargetingModeOption) {
						if (!((TargetingModeOption) option).get().allowsPlayers())
							return ActionResultType.PASS;
						else
							break;
					}
				}
			}

			explode(level, pos);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL; //Any push reaction other than PushReaction.DESTROY makes mines non-pushable by pistons due to them having block entities
	}

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}
}
