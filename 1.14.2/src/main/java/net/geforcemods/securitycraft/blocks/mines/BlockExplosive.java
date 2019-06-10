package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockExplosive extends BlockOwnable implements IExplosive {

	public BlockExplosive(SoundType soundType, Material material, float baseHardness) {
		super(Block.Properties.create(material).sound(soundType).hardnessAndResistance(baseHardness, 6000000.0F));
	}

	@Override
	public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !CommonConfig.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(!world.isRemote){
			if(player.inventory.getCurrentItem().isEmpty() && explodesWhenInteractedWith() && isActive(world, pos)) {
				explode(world, pos);
				return false;
			}

			if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessMine))
				return false;

			if(isActive(world, pos) && isDefusable() && PlayerUtils.isHoldingItem(player, SCContent.wireCutters)) {
				defuseMine(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player, p -> {});
				return false;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL)) {
				activateMine(world, pos);
				return false;
			}

			if(explodesWhenInteractedWith() && isActive(world, pos))
				explode(world, pos);

			return false;
		}

		return false;
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
