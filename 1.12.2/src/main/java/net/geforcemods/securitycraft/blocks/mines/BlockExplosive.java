package net.geforcemods.securitycraft.blocks.mines;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockWithNoDrops;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockExplosive extends BlockOwnable implements IExplosive, IBlockWithNoDrops {

	public BlockExplosive(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(player.inventory.getCurrentItem().isEmpty() && explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos)) {
				explode(world, pos);
				return false;
			}

			if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessMine) || PlayerUtils.isHoldingItem(player, SCContent.universalOwnerChanger))
				return false;

			if(isActive(world, pos) && isDefusable() && PlayerUtils.isHoldingItem(player, SCContent.wireCutters)) {
				defuseMine(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player);
				return false;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL)) {
				activateMine(world, pos);
				return false;
			}

			if(explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos))
				explode(world, pos);
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
	public boolean isDefusable(){
		return true;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Collections.emptyList();
	}
}
