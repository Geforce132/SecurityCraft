package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest implements IPasswordConvertible {

	public BlockKeypadChest(int par1){
		super(par1);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(!par1World.isRemote) {
			if(!PlayerUtils.isHoldingItem(par5EntityPlayer, mod_SecurityCraft.codebreaker) && par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) par1World.getTileEntity(pos)).openPasswordGUI(par5EntityPlayer);

			return true;
		}

		return true;
	}

	public static void activate(World par1World, BlockPos pos, EntityPlayer player){
		if(!isBlocked(par1World, pos))
			player.displayGUIChest(((BlockChest) BlockUtils.getBlock(par1World, pos)).getLockableContainer(par1World, pos));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);

		if(par1World.getTileEntity(pos.east()) != null && par1World.getTileEntity(pos.east()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.east())).getPassword());
		else if(par1World.getTileEntity(pos.west()) != null && par1World.getTileEntity(pos.west()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.west())).getPassword());
		else if(par1World.getTileEntity(pos.south()) != null && par1World.getTileEntity(pos.south()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.south())).getPassword());
		else if(par1World.getTileEntity(pos.north()) != null && par1World.getTileEntity(pos.north()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.north())).getPassword());
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block){
		super.onNeighborBlockChange(par1World, pos, state, par5Block);
		TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)par1World.getTileEntity(pos);

		if (tileentitychest != null)
			tileentitychest.updateContainingBlockInfo();

	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World par1World, int par2)
	{
		return new TileEntityKeypadChest();
	}

	public static boolean isBlocked(World worldIn, BlockPos pos)
	{
		return isBelowSolidBlock(worldIn, pos) || isOcelotSittingOnChest(worldIn, pos);
	}

	private static boolean isBelowSolidBlock(World worldIn, BlockPos pos)
	{
		return worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false);
	}

	private static boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
	{
		for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)))
		{
			EntityOcelot entityocelot = (EntityOcelot)entity;

			if (entityocelot.isSitting())
				return true;
		}

		return false;
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.chest;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		EnumFacing enumfacing = world.getBlockState(pos).getValue(FACING);
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();

		chest.writeToNBT(tag);
		chest.clear();
		world.setBlockState(pos, mod_SecurityCraft.keypadChest.getDefaultState().withProperty(FACING, enumfacing));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getName(), player.getUniqueID().toString());
		((TileEntityChest)world.getTileEntity(pos)).readFromNBT(tag);
		return true;
	}
}
