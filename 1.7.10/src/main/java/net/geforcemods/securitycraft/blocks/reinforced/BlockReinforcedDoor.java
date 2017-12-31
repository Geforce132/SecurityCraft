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
	private IIcon[] field_150017_a;
	@SideOnly(Side.CLIENT)
	private IIcon[] field_150016_b;

	public BlockReinforcedDoor(Material p_i45402_1_){
		super(p_i45402_1_);
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
	public boolean isPassable(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_){
		int l = func_150012_g(p_149655_1_, p_149655_2_, p_149655_3_, p_149655_4_);
		return (l & 4) != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_){
		setBlockBoundsBasedOnState(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
		return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_){
		setBlockBoundsBasedOnState(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
		return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_){
		func_150011_b(func_150012_g(p_149719_1_, p_149719_2_, p_149719_3_, p_149719_4_));
	}

	public int func_150013_e(IBlockAccess p_150013_1_, int p_150013_2_, int p_150013_3_, int p_150013_4_){
		return func_150012_g(p_150013_1_, p_150013_2_, p_150013_3_, p_150013_4_) & 3;
	}

	public boolean func_150015_f(IBlockAccess p_150015_1_, int p_150015_2_, int p_150015_3_, int p_150015_4_){
		return (func_150012_g(p_150015_1_, p_150015_2_, p_150015_3_, p_150015_4_) & 4) != 0;
	}

	private void func_150011_b(int p_150011_1_){
		float f = 0.1875F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int j = p_150011_1_ & 3;
		boolean flag = (p_150011_1_ & 4) != 0;
		boolean flag1 = (p_150011_1_ & 16) != 0;

		if (j == 0){
			if (flag){
				if (!flag1)
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				else
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}else if (j == 1){
			if (flag){
				if (!flag1)
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}else if (j == 2){
			if (flag){
				if (!flag1)
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				else
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
			}
			else
				setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}else if (j == 3)
			if (flag){
				if (!flag1)
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				else
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			else
				setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
	}

	public void func_150014_a(World p_150014_1_, int p_150014_2_, int p_150014_3_, int p_150014_4_, boolean p_150014_5_){
		int l = func_150012_g(p_150014_1_, p_150014_2_, p_150014_3_, p_150014_4_);
		boolean flag1 = (l & 4) != 0;

		if (flag1 != p_150014_5_){
			int i1 = l & 7;
			i1 ^= 4;

			if ((l & 8) == 0){
				p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_, p_150014_4_, i1, 2);
				p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
			}else{
				p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_ - 1, p_150014_4_, i1, 2);
				p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_ - 1, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
			}

			p_150014_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, p_150014_2_, p_150014_3_, p_150014_4_, 0);
		}
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block){
		int l = par1World.getBlockMetadata(par2, par3, par4);

		if ((l & 8) == 0){
			boolean flag = false;

			if (par1World.getBlock(par2, par3 + 1, par4) != this){
				par1World.setBlockToAir(par2, par3, par4);
				flag = true;
			}

			if (flag){
				if (!par1World.isRemote)
					this.dropBlockAsItem(par1World, par2, par3, par4, l, 0);
			}
			else if (par5Block.canProvidePower() && par5Block != this)
				if(hasActiveKeypadNextTo(par1World, par2, par3, par4) || hasActiveKeypadNextTo(par1World, par2, par3 + 1, par4) || hasActiveInventoryScannerNextTo(par1World, par2, par3, par4) || hasActiveInventoryScannerNextTo(par1World, par2, par3 + 1, par4) || hasActiveReaderNextTo(par1World, par2, par3, par4) || hasActiveReaderNextTo(par1World, par2, par3 + 1, par4) || hasActiveScannerNextTo(par1World, par2, par3, par4) || hasActiveScannerNextTo(par1World, par2, par3 + 1, par4) || hasActiveLaserNextTo(par1World, par2, par3, par4) || hasActiveLaserNextTo(par1World, par2, par3 + 1, par4))
					func_150014_a(par1World, par2, par3, par4, true);
				else
					func_150014_a(par1World, par2, par3, par4, false);
		}else{
			if (par1World.getBlock(par2, par3 - 1, par4) != this)
				par1World.setBlockToAir(par2, par3, par4);

			if (par5Block != this)
				onNeighborBlockChange(par1World, par2, par3 - 1, par4, par5Block);
		}
	}

	private boolean hasActiveLaserNextTo(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.laserBlock && par1World.getBlockMetadata(par2 + 1, par3, par4) == 2)
			return ((IOwnable) par1World.getTileEntity(par2 + 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.laserBlock && par1World.getBlockMetadata(par2 - 1, par3, par4) == 2)
			return ((IOwnable) par1World.getTileEntity(par2 - 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.laserBlock && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 + 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 - 1) == SCContent.laserBlock && par1World.getBlockMetadata(par2, par3, par4 - 1) == 2)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 - 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else
			return false;
	}

	private boolean hasActiveScannerNextTo(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.retinalScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 + 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 + 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.retinalScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 - 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 - 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.retinalScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 + 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 + 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 - 1) == SCContent.retinalScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 - 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 - 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else
			return false;
	}

	private boolean hasActiveKeypadNextTo(World par1World, int par2, int par3, int par4){
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.keypad && par1World.getBlockMetadata(par2 + 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 + 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 + 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.keypad && par1World.getBlockMetadata(par2 - 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 - 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 - 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.keypad && par1World.getBlockMetadata(par2, par3, par4 + 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 + 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 + 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 - 1) == SCContent.keypad && par1World.getBlockMetadata(par2, par3, par4 - 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 - 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 - 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else
			return false;
	}

	private boolean hasActiveReaderNextTo(World par1World, int par2, int par3, int par4){
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.keycardReader && par1World.getBlockMetadata(par2 + 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 + 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 + 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.keycardReader && par1World.getBlockMetadata(par2 - 1, par3, par4) > 6 && par1World.getBlockMetadata(par2 - 1, par3, par4) < 11)
			return ((IOwnable) par1World.getTileEntity(par2 - 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.keycardReader && par1World.getBlockMetadata(par2, par3, par4 + 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 + 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 + 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 - 1) == SCContent.keycardReader && par1World.getBlockMetadata(par2, par3, par4 - 1) > 6 && par1World.getBlockMetadata(par2, par3, par4 - 1) < 11)
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 - 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else
			return false;
	}

	private boolean hasActiveInventoryScannerNextTo(World par1World, int par2, int par3, int par4){
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 1, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 1, par3, par4)).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(par2 + 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 1, par3, par4)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 1, par3, par4)).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(par2 - 1, par3, par4)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 1)).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 + 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 1)).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 1)).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(par2, par3, par4 - 1)).getOwner().owns((IOwnable)par1World.getTileEntity(par2, par3, par4));
		else
			return false;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return (p_149650_1_ & 8) != 0 ? null : SCContent.reinforcedDoorItem;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_){
		setBlockBoundsBasedOnState(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
		return super.collisionRayTrace(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_, p_149731_5_, p_149731_6_);
	}

	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_){
		return p_149742_3_ >= 255 ? false : World.doesBlockHaveSolidTopSurface(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) && super.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) && super.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_ + 1, p_149742_4_);
	}

	@Override
	public int getMobilityFlag(){
		return 1;
	}

	public int func_150012_g(IBlockAccess p_150012_1_, int p_150012_2_, int p_150012_3_, int p_150012_4_){
		int l = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_, p_150012_4_);
		boolean flag = (l & 8) != 0;
		int i1;
		int j1;

		if (flag){
			i1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ - 1, p_150012_4_);
			j1 = l;
		}else{
			i1 = l;
			j1 = p_150012_1_.getBlockMetadata(p_150012_2_, p_150012_3_ + 1, p_150012_4_);
		}

		boolean flag1 = (j1 & 1) != 0;
		return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_){
		return SCContent.reinforcedDoorItem;
	}

	@Override
	public void onBlockHarvested(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_){
		if (p_149681_6_.capabilities.isCreativeMode && (p_149681_5_ & 8) != 0 && p_149681_1_.getBlock(p_149681_2_, p_149681_3_ - 1, p_149681_4_) == this)
			p_149681_1_.setBlockToAir(p_149681_2_, p_149681_3_ - 1, p_149681_4_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_){
		return field_150016_b[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_){
		if (p_149673_5_ != 1 && p_149673_5_ != 0){
			int i1 = func_150012_g(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_);
			int j1 = i1 & 3;
			boolean flag = (i1 & 4) != 0;
			boolean flag1 = false;
			boolean flag2 = (i1 & 8) != 0;

			if (flag){
				if (j1 == 0 && p_149673_5_ == 2)
					flag1 = !flag1;
				else if (j1 == 1 && p_149673_5_ == 5)
					flag1 = !flag1;
				else if (j1 == 2 && p_149673_5_ == 3)
					flag1 = !flag1;
				else if (j1 == 3 && p_149673_5_ == 4)
					flag1 = !flag1;
			}else{
				if (j1 == 0 && p_149673_5_ == 5)
					flag1 = !flag1;
				else if (j1 == 1 && p_149673_5_ == 3)
					flag1 = !flag1;
				else if (j1 == 2 && p_149673_5_ == 4)
					flag1 = !flag1;
				else if (j1 == 3 && p_149673_5_ == 2)
					flag1 = !flag1;

				if ((i1 & 16) != 0)
					flag1 = !flag1;
			}

			return flag2 ? field_150017_a[flag1?1:0] : field_150016_b[flag1?1:0];
		}
		else
			return field_150016_b[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_149651_1_){
		field_150017_a = new IIcon[2];
		field_150016_b = new IIcon[2];
		field_150017_a[0] = p_149651_1_.registerIcon("securitycraft:reinforcedDoorUpper");
		field_150016_b[0] = p_149651_1_.registerIcon("securitycraft:reinforcedDoorLower");
		field_150017_a[1] = new IconFlipped(field_150017_a[0], true, false);
		field_150016_b[1] = new IconFlipped(field_150016_b[0], true, false);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
}