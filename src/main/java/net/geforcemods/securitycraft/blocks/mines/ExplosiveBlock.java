package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {

	public ExplosiveBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos)
	{
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE, hand))
			return ActionResultType.SUCCESS;

		if(isActive(world, pos) && isDefusable() && player.getHeldItem(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
			if(defuseMine(world, pos))
			{
				if(!player.isCreative())
					player.getHeldItem(hand).damageItem(1, player, p -> p.sendBreakAnimation(hand));

				world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			return ActionResultType.SUCCESS;
		}

		if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL, hand)) {
			if(activateMine(world, pos))
			{
				if(!player.isCreative())
					player.getHeldItem(hand).damageItem(1, player, p -> p.sendBreakAnimation(hand));

				world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			return ActionResultType.SUCCESS;
		}

		if(explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos))
		{
			explode(world, pos);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
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
