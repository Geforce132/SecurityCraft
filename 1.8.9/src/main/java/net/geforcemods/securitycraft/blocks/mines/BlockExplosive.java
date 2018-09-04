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

	public BlockExplosive(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(player.getCurrentEquippedItem() == null && explodesWhenInteractedWith() && isActive(world, pos)) {
				explode(world, pos);
				return false;
			}

			if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessMine))
				return false;

			if(isActive(world, pos) && isDefusable() && PlayerUtils.isHoldingItem(player, SCContent.wireCutters)) {
				defuseMine(world, pos);
				player.getCurrentEquippedItem().damageItem(1, player);
				return false;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.flint_and_steel)) {
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
