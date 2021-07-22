package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {

	public ExplosiveBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos)
	{
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, world, pos);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE, hand))
			return InteractionResult.SUCCESS;

		if(isActive(world, pos) && isDefusable() && player.getItemInHand(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
			if(defuseMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			}

			return InteractionResult.SUCCESS;
		}

		if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL, hand)) {
			if(activateMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
			}

			return InteractionResult.SUCCESS;
		}

		if(explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos))
		{
			explode(world, pos);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public boolean isDefusable(){
		return true;
	}
}
