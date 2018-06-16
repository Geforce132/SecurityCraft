package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockExplosive extends BlockOwnable implements IExplosive {

	public BlockExplosive(Material par1) {
		super(par1);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			if(playerIn.inventory.getCurrentItem() == null && explodesWhenInteractedWith() && isActive(worldIn, pos)) {
				explode(worldIn, pos);
				return false;
			}

			if(PlayerUtils.isHoldingItem(playerIn, SCContent.remoteAccessMine))
				return false;

			if(isActive(worldIn, pos) && isDefusable() && PlayerUtils.isHoldingItem(playerIn, SCContent.wireCutters)) {
				defuseMine(worldIn, pos);
				playerIn.inventory.getCurrentItem().damageItem(1, playerIn);
				return false;
			}

			if(!isActive(worldIn, pos) && PlayerUtils.isHoldingItem(playerIn, Items.FLINT_AND_STEEL)) {
				activateMine(worldIn, pos);
				return false;
			}

			if(explodesWhenInteractedWith() && isActive(worldIn, pos))
				explode(worldIn, pos);

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
