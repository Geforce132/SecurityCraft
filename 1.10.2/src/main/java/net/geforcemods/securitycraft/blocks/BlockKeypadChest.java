package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest implements IPasswordConvertible {

	public BlockKeypadChest(){
		super(Type.BASIC);
		setSoundType(SoundType.WOOD);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote) {
			if(!PlayerUtils.isHoldingItem(playerIn, mod_SecurityCraft.codebreaker) && worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) worldIn.getTileEntity(pos)).openPasswordGUI(playerIn);

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

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(world, pos, neighbor);
		TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)world.getTileEntity(pos);

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
		return worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.DOWN);
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
		return Blocks.CHEST;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		EnumFacing enumfacing = world.getBlockState(pos).getValue(FACING);
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos);
		NBTTagCompound tag = chest.writeToNBT(new NBTTagCompound());

		chest.clear();
		world.setBlockState(pos, mod_SecurityCraft.keypadChest.getDefaultState().withProperty(FACING, enumfacing));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getName(), player.getUniqueID().toString());
		((TileEntityChest)world.getTileEntity(pos)).readFromNBT(tag);
		return true;
	}
}
