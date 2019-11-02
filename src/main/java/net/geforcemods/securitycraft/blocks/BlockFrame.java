package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrame extends BlockOwnable {

	public BlockFrame(Material meterial) {
		super(meterial);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		switch(world.getBlockMetadata(x, y, z))
		{
			case 2: return side != ForgeDirection.NORTH;
			case 3: return side != ForgeDirection.SOUTH;
			case 4: return side != ForgeDirection.EAST;
			case 5: return side != ForgeDirection.WEST;
			default: return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if(entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if(entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if(entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if(entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote){
			if(SecurityCraft.config.fiveMinAutoShutoff && ((TileEntityFrame) world.getTileEntity(x, y, z)).hasCameraLocation()){
				((TileEntityFrame) world.getTileEntity(x, y, z)).enableView();
				return true;
			}
		}else{
			if(!(world.getTileEntity(x, y, z) instanceof TileEntityFrame))
				return false;

			if(!((TileEntityFrame) world.getTileEntity(x, y, z)).hasCameraLocation() && (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != SCContent.cameraMonitor)){
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:keypadFrame.name"), StatCollector.translateToLocal("messages.securitycraft:frame.rightclick"), EnumChatFormatting.RED);
				return false;
			}

			if(PlayerUtils.isHoldingItem(player, SCContent.keyPanel))
				return false;
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityFrame();
	}

}
