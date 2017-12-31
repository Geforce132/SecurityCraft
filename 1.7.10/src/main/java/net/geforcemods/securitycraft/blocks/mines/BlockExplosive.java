package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public abstract class BlockExplosive extends BlockOwnable implements IExplosive {

	public BlockExplosive(Material par1) {
		super(par1);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		if(!par1World.isRemote){
			if(par5EntityPlayer.getCurrentEquippedItem() == null && explodesWhenInteractedWith() && isActive(par1World, par2, par3, par4)) {
				explode(par1World, par2, par3, par4);
				return false;
			}

			if(PlayerUtils.isHoldingItem(par5EntityPlayer, SCContent.remoteAccessMine))
				return false;

			if(isActive(par1World, par2, par3, par4) && isDefusable() && PlayerUtils.isHoldingItem(par5EntityPlayer, SCContent.wireCutters)) {
				defuseMine(par1World, par2, par3, par4);
				par5EntityPlayer.getCurrentEquippedItem().damageItem(1, par5EntityPlayer);
				return false;
			}

			if(!isActive(par1World, par2, par3, par4) && PlayerUtils.isHoldingItem(par5EntityPlayer, Items.flint_and_steel)) {
				activateMine(par1World, par2, par3, par4);
				return false;
			}

			if(explodesWhenInteractedWith() && isActive(par1World, par2, par3, par4))
				explode(par1World, par2, par3, par4);

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
	public abstract void explode(World world, int par2, int par3, int par4);

	@Override
	public boolean isDefusable(){
		return true;
	}

}
