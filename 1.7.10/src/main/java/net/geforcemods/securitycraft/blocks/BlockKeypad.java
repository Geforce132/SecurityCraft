package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypad extends BlockContainer implements ICustomWailaDisplay, IPasswordConvertible {

	public BlockKeypad(Material par2Material) {
		super(par2Material);
	}

	@SideOnly(Side.CLIENT)
	private IIcon keypadIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFront;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFrontActive;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconDisguised;

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		int l = MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);

		if (l == 1)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);

		if (l == 2)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);

		if (l == 3)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		else
			return;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote)
			return true;
		else {
			if(BlockUtils.isMetadataBetween(par1World, par2, par3, par4, 7, 10))
				return false;

			if(ModuleUtils.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.BLACKLIST))
				return true;

			if(!PlayerUtils.isHoldingItem(par5EntityPlayer, SCContent.codebreaker))
				((IPasswordProtected) par1World.getTileEntity(par2, par3, par4)).openPasswordGUI(par5EntityPlayer);

			return true;
		}
	}

	public static void activate(World par1World, int par2, int par3, int par4){
		par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) + 5, 3);
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, SCContent.keypad);
		par1World.scheduleBlockUpdate(par2, par3, par4, SCContent.keypad, 60);
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if(!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) > 6 && par1World.getBlockMetadata(par2, par3, par4) < 11)
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, int x, int y, int z) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		if(par1 == 3 && par2 == 0)
			return keypadIconFront;

		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side){
		int meta = access.getBlockMetadata(x, y, z);
		TileEntityKeypad tileEntity = ((TileEntityKeypad) access.getTileEntity(x, y, z));

		boolean isDisguised = tileEntity.hasModule(EnumCustomModules.DISGUISE);

		if(isDisguised && !tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).isEmpty()) {
			List<ItemStack> stacks = tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE);

			if(stacks.size() != 0)
			{
				ItemStack stack = stacks.get(0);
				Block disguisedAs = Block.getBlockFromItem(stack.getItem());

				return stack.getHasSubtypes() ? disguisedAs.getIcon(side, stack.getMetadata()) : disguisedAs.getIcon(side, meta);
			}
		}

		if(meta > 6 && meta < 11)
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != (meta - 5) ? blockIcon : keypadIconFrontActive));
		else
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != meta ? blockIcon : keypadIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		blockIcon = par1IconRegister.registerIcon("securitycraft:iron_block");
		keypadIconFront = par1IconRegister.registerIcon("securitycraft:keypadUnactive");
		keypadIconTop = par1IconRegister.registerIcon("securitycraft:iron_block");
		keypadIconFrontActive = par1IconRegister.registerIcon("securitycraft:keypadActive");
		keypadIconDisguised = par1IconRegister.registerIcon("bookshelf");
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World par1World, int par2){
		return new TileEntityKeypad();
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		TileEntityKeypad tileEntity = (TileEntityKeypad) world.getTileEntity(x, y, z);

		if(tileEntity.hasModule(EnumCustomModules.DISGUISE) && !tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).isEmpty())
			return tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).get(0);

		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		TileEntityKeypad tileEntity = (TileEntityKeypad) world.getTileEntity(x, y, z);

		return !tileEntity.hasModule(EnumCustomModules.DISGUISE);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		ItemStack stack = getDisplayStack(world, x, y, z);

		return stack == null ? new ItemStack(this) : stack;
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.frame;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, int x, int y, int z)
	{
		Owner owner = ((IOwnable) world.getTileEntity(x, y, z)).getOwner();

		world.setBlock(x, y, z, SCContent.keypad, world.getBlockMetadata(x, y, z), 3);
		((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(owner);
		return true;
	}
}
