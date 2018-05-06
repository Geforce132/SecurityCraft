package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedDoor extends BlockContainer{

	@SideOnly(Side.CLIENT)
	private IIcon[] upperIcons;
	@SideOnly(Side.CLIENT)
	private IIcon[] lowerIcons;

	public BlockReinforcedDoor(Material material){
		super(material);
		isBlockContainer = true;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 7;
	}

	@Override
	public boolean isPassable(IBlockAccess access, int x, int y, int z){
		int l = getDoorMeta(access, x, y, z);
		return (l & 4) != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z){
		setBoundsBasedOnMeta(getDoorMeta(access, x, y, z));
	}

	private void setBoundsBasedOnMeta(int meta){
		float f = 0.1875F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int facing = meta & 3;
		boolean isOpen = (meta & 4) != 0;
		boolean isRightDoor = (meta & 16) != 0;

		if (facing == 0){
			if (isOpen){
				if (!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				else
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}else if (facing == 1){
			if (isOpen){
				if (!isRightDoor)
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}else if (facing == 2){
			if (isOpen){
				if (!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
			}
			else
				setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}else if (facing == 3)
			if (isOpen){
				if (!isRightDoor)
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				else
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
	}

	public void changeDoorState(World world, int x, int y, int z, boolean open){
		int meta = getDoorMeta(world, x, y, z);
		boolean isOpen = (meta & 4) != 0;

		if (isOpen != open){
			int newMeta = meta & 7;
			newMeta ^= 4;

			//the switch statements in here check whether this door is part of a double door and if it is, opens the other door as well
			if ((meta & 8) == 0){ //lower half
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);

				switch(meta)
				{
					case 0:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 16)
						{
							world.setBlockMetadataWithNotify(x, y, z + 1, open ? 20 : 0, 2);
							world.markBlockRangeForRenderUpdate(x, y, z + 1, x, y, z);
						}
						break;
					case 16:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 0)
						{
							world.setBlockMetadataWithNotify(x, y, z - 1, open ? 4 : 16, 2);
							world.markBlockRangeForRenderUpdate(x, y, z - 1, x, y, z);
						}
						break;
					case 1:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 17)
						{
							world.setBlockMetadataWithNotify(x - 1, y, z, open ? 21 : 1, 2);
							world.markBlockRangeForRenderUpdate(x - 1, y, z, x, y, z);
						}
						break;
					case 17:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 1)
						{
							world.setBlockMetadataWithNotify(x + 1, y, z, open ? 5 : 17, 2);
							world.markBlockRangeForRenderUpdate(x + 1, y, z, x, y, z);
						}
						break;
					case 2:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 18)
						{
							world.setBlockMetadataWithNotify(x, y, z - 1, open ? 22 : 2, 2);
							world.markBlockRangeForRenderUpdate(x, y, z - 1, x, y, z);
						}
						break;
					case 18:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 2)
						{
							world.setBlockMetadataWithNotify(x, y, z + 1, open ? 6 : 18, 2);
							world.markBlockRangeForRenderUpdate(x, y, z + 1, x, y, z);
						}
						break;
					case 3:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 19)
						{
							world.setBlockMetadataWithNotify(x + 1, y, z, open ? 23 : 3, 2);
							world.markBlockRangeForRenderUpdate(x + 1, y, z, x, y, z);
						}
						break;
					case 19:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 3)
						{
							world.setBlockMetadataWithNotify(x - 1, y, z, open ? 7 : 19, 2);
							world.markBlockRangeForRenderUpdate(x - 1, y, z, x, y, z);
						}
						break;
				}

			}else{ //upper half
				world.setBlockMetadataWithNotify(x, y - 1, z, newMeta, 2);
				world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);

				switch(meta)
				{
					case 24:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 8)
						{
							world.setBlockMetadataWithNotify(x, y, z + 1, open ? 12 : 24, 2);
							world.markBlockRangeForRenderUpdate(x, y, z + 1, x, y, z);
						}
						break;
					case 8:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 24)
						{
							world.setBlockMetadataWithNotify(x, y, z - 1, open ? 28 : 8, 2);
							world.markBlockRangeForRenderUpdate(x, y, z - 1, x, y, z);
						}
						break;
					case 25:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 9)
						{
							world.setBlockMetadataWithNotify(x - 1, y, z, open ? 13 : 25, 2);
							world.markBlockRangeForRenderUpdate(x - 1, y, z, x, y, z);
						}
						break;
					case 9:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 25)
						{
							world.setBlockMetadataWithNotify(x + 1, y, z, open ? 29 : 9, 2);
							world.markBlockRangeForRenderUpdate(x + 1, y, z, x, y, z);
						}
						break;
					case 26:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 10)
						{
							world.setBlockMetadataWithNotify(x, y, z - 1, open ? 14 : 26, 2);
							world.markBlockRangeForRenderUpdate(x, y, z - 1, x, y, z);
						}
						break;
					case 10:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 26)
						{
							world.setBlockMetadataWithNotify(x, y, z + 1, open ? 30 : 10, 2);
							world.markBlockRangeForRenderUpdate(x, y, z + 1, x, y, z);
						}
						break;
					case 27:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 11)
						{
							world.setBlockMetadataWithNotify(x + 1, y, z, open ? 15 : 27, 2);
							world.markBlockRangeForRenderUpdate(x + 1, y, z, x, y, z);
						}
						break;
					case 11:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 27)
						{
							world.setBlockMetadataWithNotify(x - 1, y, z, open ? 31 : 11, 2);
							world.markBlockRangeForRenderUpdate(x - 1, y, z, x, y, z);
						}
						break;
				}
			}

			world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
		}

		if (!open)//closing
		{
			if ((meta & 8) == 0){ //lower half
				switch(meta)
				{
					case 4:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 20)
							changeDoorState(world, x, y, z + 1, false);
						break;
					case 20:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 4)
							changeDoorState(world, x, y, z - 1, false);
						break;
					case 5:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 21)
							changeDoorState(world, x - 1, y, z, false);
						break;
					case 21:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 5)
							changeDoorState(world, x + 1, y, z, false);
						break;
					case 6:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 22)
							changeDoorState(world, x, y, z - 1, false);
						break;
					case 22:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 6)
							changeDoorState(world, x, y, z + 1, false);
						break;
					case 7:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 23)
							changeDoorState(world, x + 1, y, z, false);
						break;
					case 23:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 7)
							changeDoorState(world, x - 1, y, z, false);
						break;
				}

			}else{ //upper half
				switch(meta)
				{
					case 28:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 12)
							changeDoorState(world, x, y, z + 1, false);
						break;
					case 12:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 28)
							changeDoorState(world, x, y, z - 1, false);
						break;
					case 29:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 13)
							changeDoorState(world, x - 1, y, z, false);
						break;
					case 13:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 29)
							changeDoorState(world, x + 1, y, z, false);
						break;
					case 30:
						if(world.getBlock(x, y, z - 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z - 1) == 14)
							changeDoorState(world, x, y, z - 1, false);
						break;
					case 14:
						if(world.getBlock(x, y, z + 1) == SCContent.reinforcedDoor && getDoorMeta(world, x, y, z + 1) == 30)
							changeDoorState(world, x, y, z + 1, false);
						break;
					case 31:
						if(world.getBlock(x + 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x + 1, y, z) == 15)
							changeDoorState(world, x + 1, y, z, false);
						break;
					case 15:
						if(world.getBlock(x - 1, y, z) == SCContent.reinforcedDoor && getDoorMeta(world, x - 1, y, z) == 31)
							changeDoorState(world, x - 1, y, z, false);
						break;
				}
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) == 0){
			boolean flag = false;

			if (world.getBlock(x, y + 1, z) != this){
				world.setBlockToAir(x, y, z);
				flag = true;
			}

			if (flag){
				if (!world.isRemote)
					this.dropBlockAsItem(world, x, y, z, meta, 0);
			}
			else if (block.canProvidePower() && block != this)
				if(hasActiveKeypadNextTo(world, x, y, z) || hasActiveKeypadNextTo(world, x, y + 1, z) || hasActiveInventoryScannerNextTo(world, x, y, z) || hasActiveInventoryScannerNextTo(world, x, y + 1, z) || hasActiveReaderNextTo(world, x, y, z) || hasActiveReaderNextTo(world, x, y + 1, z) || hasActiveScannerNextTo(world, x, y, z) || hasActiveScannerNextTo(world, x, y + 1, z) || hasActiveLaserNextTo(world, x, y, z) || hasActiveLaserNextTo(world, x, y + 1, z))
					changeDoorState(world, x, y, z, true);
				else
					changeDoorState(world, x, y, z, false);
		}else{
			if (world.getBlock(x, y - 1, z) != this)
				world.setBlockToAir(x, y, z);

			if (block != this)
				onNeighborBlockChange(world, x, y - 1, z, block);
		}
	}

	private boolean hasActiveLaserNextTo(World world, int x, int y, int z) {
		if(world.getBlock(x + 1, y, z) == SCContent.laserBlock && world.getBlockMetadata(x + 1, y, z) == 2)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.laserBlock && world.getBlockMetadata(x - 1, y, z) == 2)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.laserBlock && world.getBlockMetadata(x, y, z + 1) == 2)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.laserBlock && world.getBlockMetadata(x, y, z - 1) == 2)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private boolean hasActiveScannerNextTo(World world, int x, int y, int z) {
		if(world.getBlock(x + 1, y, z) == SCContent.retinalScanner && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.retinalScanner && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.retinalScanner && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.retinalScanner && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private boolean hasActiveKeypadNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.keypad && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.keypad && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.keypad && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.keypad && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private boolean hasActiveReaderNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.keycardReader && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.keycardReader && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.keycardReader && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.keycardReader && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private boolean hasActiveInventoryScannerNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x + 1, y, z)).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x + 1, y, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x - 1, y, z)).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x - 1, y, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z + 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z + 1)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z - 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z - 1)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return (meta & 8) != 0 ? null : SCContent.reinforcedDoorItem;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, start, end);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z){
		return y >= 255 ? false : World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
	}

	@Override
	public int getMobilityFlag(){
		return 1;
	}

	public int getDoorMeta(IBlockAccess access, int x, int y, int z){
		int meta = access.getBlockMetadata(x, y, z);
		boolean isOpen = (meta & 8) != 0;
		int i1;
		int j1;

		if (isOpen){
			i1 = access.getBlockMetadata(x, y - 1, z);
			j1 = meta;
		}else{
			i1 = meta;
			j1 = access.getBlockMetadata(x, y + 1, z);
		}

		boolean isRightDoor = (j1 & 1) != 0;
		return i1 & 7 | (isOpen ? 8 : 0) | (isRightDoor ? 16 : 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return SCContent.reinforcedDoorItem;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player){
		if (player.capabilities.isCreativeMode && (meta & 8) != 0 && world.getBlock(x, y - 1, z) == this)
			world.setBlockToAir(x, y - 1, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return lowerIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side){
		if (side != 1 && side != 0){
			int meta = getDoorMeta(access, x, y, z);
			int facing = meta & 3;
			boolean flag = (meta & 4) != 0;
			boolean flag1 = false;
			boolean flag2 = (meta & 8) != 0;

			if (flag){
				if (facing == 0 && side == 2)
					flag1 = !flag1;
				else if (facing == 1 && side == 5)
					flag1 = !flag1;
				else if (facing == 2 && side == 3)
					flag1 = !flag1;
				else if (facing == 3 && side == 4)
					flag1 = !flag1;
			}else{
				if (facing == 0 && side == 5)
					flag1 = !flag1;
				else if (facing == 1 && side == 3)
					flag1 = !flag1;
				else if (facing == 2 && side == 4)
					flag1 = !flag1;
				else if (facing == 3 && side == 2)
					flag1 = !flag1;

				if ((meta & 16) != 0)
					flag1 = !flag1;
			}

			return flag2 ? upperIcons[flag1?1:0] : lowerIcons[flag1?1:0];
		}
		else
			return lowerIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		upperIcons = new IIcon[2];
		lowerIcons = new IIcon[2];
		upperIcons[0] = register.registerIcon("securitycraft:reinforcedDoorUpper");
		lowerIcons[0] = register.registerIcon("securitycraft:reinforcedDoorLower");
		upperIcons[1] = new IconFlipped(upperIcons[0], true, false);
		lowerIcons[1] = new IconFlipped(lowerIcons[0], true, false);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}