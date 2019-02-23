package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote) {
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) world.getTileEntity(pos)).openPasswordGUI(player);

			return true;
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, EntityPlayer player){
		if(!isBlocked(world, pos))
			player.displayGUIChest(((BlockChest) BlockUtils.getBlock(world, pos)).getLockableContainer(world, pos));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(world.getTileEntity(pos.east()) != null && world.getTileEntity(pos.east()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(world.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) world.getTileEntity(pos.east())).getPassword());
		else if(world.getTileEntity(pos.west()) != null && world.getTileEntity(pos.west()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(world.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) world.getTileEntity(pos.west())).getPassword());
		else if(world.getTileEntity(pos.south()) != null && world.getTileEntity(pos.south()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(world.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) world.getTileEntity(pos.south())).getPassword());
		else if(world.getTileEntity(pos.north()) != null && world.getTileEntity(pos.north()) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest)(world.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) world.getTileEntity(pos.north())).getPassword());
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
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityKeypadChest();
	}

	public static boolean isBlocked(World world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos) || isOcelotSittingOnChest(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).isSideSolid(world, pos.up(), EnumFacing.DOWN);
	}

	private static boolean isOcelotSittingOnChest(World world, BlockPos pos)
	{
		for (Entity entity : world.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)))
		{
			EntityOcelot ocelot = (EntityOcelot)entity;

			if (ocelot.isSitting())
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
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos);
		NBTTagCompound tag = chest.writeToNBT(new NBTTagCompound());

		chest.clear();
		world.setBlockState(pos, SCContent.keypadChest.getDefaultState().withProperty(FACING, facing));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName());
		((TileEntityChest)world.getTileEntity(pos)).readFromNBT(tag);
		return true;
	}
}
