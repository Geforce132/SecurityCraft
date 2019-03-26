package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockKeypadChest extends BlockChest implements IPasswordConvertible {

	public BlockKeypadChest(){
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD));
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote) {
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) world.getTileEntity(pos)).openPasswordGUI(player);

			return true;
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, EntityPlayer player){
		if(!isBlocked(world, pos))
			player.displayGUIChest(((BlockChest) BlockUtils.getBlock(world, pos)).getContainer(world.getBlockState(pos), world, pos, false));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(entity instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)entity));

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
	public void onNeighborChange(IBlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);
		TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)world.getTileEntity(pos);

		if (tileentitychest != null)
			tileentitychest.updateContainingBlockInfo();

	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(IBlockReader reader)
	{
		return new TileEntityKeypadChest();
	}

	public static boolean isBlocked(World world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos) || isOcelotSittingOnChest(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos)
	{
		return BlockUtils.isSideSolid(world, pos.up(), EnumFacing.DOWN);
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
		EnumFacing facing = world.getBlockState(pos).get(FACING);
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(pos);
		NBTTagCompound tag = chest.write(new NBTTagCompound());

		chest.clear();
		world.setBlockState(pos, SCContent.keypadChest.getDefaultState().with(FACING, facing));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName().getFormattedText());
		((TileEntityChest)world.getTileEntity(pos)).read(tag);
		return true;
	}
}
