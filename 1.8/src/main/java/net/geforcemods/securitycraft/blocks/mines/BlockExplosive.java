package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockExplosive extends BlockOwnable implements IExplosive {

	public BlockExplosive(Material par1) {
		super(par1);
	}

	@Override
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9) {
		if(!par1World.isRemote){
			if(par5EntityPlayer.getCurrentEquippedItem() == null && explodesWhenInteractedWith() && isActive(par1World, pos)) {
				explode(par1World, pos);
				return false;
			}

			if(PlayerUtils.isHoldingItem(par5EntityPlayer, SCContent.remoteAccessMine))
				return false;

			if(isActive(par1World, pos) && isDefusable() && PlayerUtils.isHoldingItem(par5EntityPlayer, SCContent.wireCutters)) {
				defuseMine(par1World, pos);
				par5EntityPlayer.getCurrentEquippedItem().damageItem(1, par5EntityPlayer);
				return false;
			}

			if(!isActive(par1World, pos) && PlayerUtils.isHoldingItem(par5EntityPlayer, Items.flint_and_steel)) {
				activateMine(par1World, pos);
				return false;
			}

			if(explodesWhenInteractedWith() && isActive(par1World, pos))
				explode(par1World, pos);

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
