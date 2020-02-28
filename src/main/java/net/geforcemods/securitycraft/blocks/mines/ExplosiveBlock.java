package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {

	public ExplosiveBlock(SoundType soundType, Material material, float baseHardness) {
		super(Block.Properties.create(material).sound(soundType).hardnessAndResistance(baseHardness, 6000000.0F));
	}

	@Override
	public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isRemote){
			if(player.inventory.getCurrentItem().isEmpty() && explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos)) {
				explode(world, pos);
				return ActionResultType.SUCCESS;
			}

			if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE))
				return ActionResultType.SUCCESS;

			if(isActive(world, pos) && isDefusable() && player.getHeldItem(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
				defuseMine(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player, p -> p.sendBreakAnimation(hand));
				return ActionResultType.SUCCESS;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL)) {
				activateMine(world, pos);
				return ActionResultType.SUCCESS;
			}

			if(explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos))
			{
				explode(world, pos);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.FAIL;
	}

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public abstract void explode(World world, BlockPos pos);

	@Override
	public boolean isDefusable(){
		return true;
	}

}
