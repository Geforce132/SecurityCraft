package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMotionActivatedLight extends BlockOwnable implements ICustomWailaDisplay {
	
	public BlockMotionActivatedLight(Material material) {
		super(material);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		int meta = access.getBlockMetadata(x, y, z);

		if(meta == 3)
			setBlockBounds(0.35F, 0.18F, 0F, 0.65F, 0.58F, 0.25F);
		else if(meta == 4)
			setBlockBounds(1F, 0.18F, 0.35F, 0.75F, 0.58F, 0.65F);
		else if(meta == 2)
			setBlockBounds(0.35F, 0.18F, 1F, 0.65F, 0.58F, 0.75F);
		else if(meta == 5) {
			setBlockBounds(0F, 0.18F, 0.35F, 0.25F, 0.58F, 0.65F);
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}
	
	public static void toggleLight(World world, int x, int y, int z, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOn, world.getBlockMetadata(x, y, z), 3);

				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOn, 1, false);
			}
			else
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOff, world.getBlockMetadata(x, y, z), 3);

				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOff, 1, false);
			}
		}
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		return new ItemStack(Item.getItemFromBlock(SCContent.motionActivatedLightOff));
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMotionLight().attacks(EntityPlayer.class, SecurityCraft.config.motionActivatedLightSearchRadius, 1);
	}
	
}
