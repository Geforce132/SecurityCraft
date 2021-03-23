package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockProjector extends BlockDisguisable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 10.0F / 16.0F, 15.0F / 16.0F);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1.0F / 16.0F, 0.0F, 0.0F, 1.0F, 10.0F / 16.0F, 1.0F);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0.0F, 0.0F, 1.0F / 16.0F, 1.0F, 10.0F / 16.0F, 1.0F);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 15.0F / 16.0F, 10.0F / 16.0F, 1.0F);

	public BlockProjector() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setSoundType(SoundType.METAL);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		switch(state.getValue(FACING))
		{
			case NORTH: return NORTH;
			case EAST: return EAST;
			case SOUTH: return SOUTH;
			case WEST: return WEST;
			default: return super.getBoundingBox(state, world, pos);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		IBlockState actualState = getDisguisedBlockState(world, pos);

		if(actualState != null && actualState.getBlock() != this)
			return actualState.getCollisionBoundingBox(world, pos);
		else
			return getBoundingBox(state, world, pos);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof TileEntityProjector))
			return false;

		boolean isOwner = ((TileEntityProjector)te).getOwner().isOwner(player);

		if(!world.isRemote && isOwner)
			player.openGui(SecurityCraft.instance, GuiHandler.PROJECTOR, world, pos.getX(), pos.getY(), pos.getZ());

		return isOwner;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileEntityProjector)
		{
			// Drop the block being projected
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), ((TileEntityProjector) world.getTileEntity(pos)).getStackInSlot(36));
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(item));
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(!world.isRemote)
		{
			if(world.getTileEntity(pos) instanceof TileEntityProjector && ((TileEntityProjector) world.getTileEntity(pos)).isActivatedByRedstone())
			{
				((TileEntityProjector) world.getTileEntity(pos)).setActive(world.isBlockPowered(pos));
				((TileEntityProjector) world.getTileEntity(pos)).sync();
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!world.isBlockPowered(pos) && world.getTileEntity(pos) instanceof TileEntityProjector && ((TileEntityProjector) world.getTileEntity(pos)).isActivatedByRedstone())
		{
			((TileEntityProjector) world.getTileEntity(pos)).setActive(false);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityProjector();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TextComponentTranslation("tooltip.securitycraft:projector").getFormattedText());
	}
}
