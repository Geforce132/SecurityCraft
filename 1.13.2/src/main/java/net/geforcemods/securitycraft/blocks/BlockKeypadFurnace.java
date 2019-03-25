package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockKeypadFurnace extends BlockOwnable implements IPasswordConvertible {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	private static final VoxelShape NORTH_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 3, 16, 16, 16), Block.makeCuboidShape(1, 1, 2, 15, 2, 3)), VoxelShapes.combine(Block.makeCuboidShape(4, 1, 0, 12, 2, 2), Block.makeCuboidShape(5, 1, 1, 11, 2, 2), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(1, 2, 3, 15, 15, 4), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape NORTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 3, 16, 16, 16), Block.makeCuboidShape(1, 1, 2, 15, 15, 3)), VoxelShapes.combine(Block.makeCuboidShape(4, 14, 0, 12, 15, 2), Block.makeCuboidShape(5, 14, 1, 11, 15, 2), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape EAST_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 13, 16, 16), Block.makeCuboidShape(13, 1, 1, 14, 2, 15)), VoxelShapes.combine(Block.makeCuboidShape(14, 1, 4, 16, 2, 12), Block.makeCuboidShape(14, 1, 5, 15, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(12, 2, 1, 13, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape EAST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 13, 16, 16), Block.makeCuboidShape(13, 1, 1, 14, 15, 15)), VoxelShapes.combine(Block.makeCuboidShape(14, 14, 4, 16, 15, 12), Block.makeCuboidShape(14, 14, 5, 15, 15, 11), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape SOUTH_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 13), Block.makeCuboidShape(1, 1, 13, 15, 2, 14)), VoxelShapes.combine(Block.makeCuboidShape(4, 1, 14, 12, 2, 16), Block.makeCuboidShape(5, 1, 14, 11, 2, 15), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(1, 2, 12, 15, 15, 13), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape SOUTH_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 16, 13), Block.makeCuboidShape(1, 1, 13, 15, 15, 14)), VoxelShapes.combine(Block.makeCuboidShape(4, 14, 14, 12, 15, 16), Block.makeCuboidShape(5, 14, 14, 11, 15, 15), IBooleanFunction.ONLY_FIRST));
	private static final VoxelShape WEST_OPEN = VoxelShapes.combine(VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(3, 0, 0, 16, 16, 16), Block.makeCuboidShape(2, 1, 1, 3, 2, 15)), VoxelShapes.combine(Block.makeCuboidShape(0, 1, 4, 2, 2, 12), Block.makeCuboidShape(1, 1, 5, 2, 2, 11), IBooleanFunction.ONLY_FIRST)), Block.makeCuboidShape(3, 2, 1, 4, 15, 15), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape WEST_CLOSED = VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(3, 0, 0, 16, 16, 16), Block.makeCuboidShape(2, 1, 1, 3, 15, 15)), VoxelShapes.combine(Block.makeCuboidShape(0, 14, 4, 2, 15, 12), Block.makeCuboidShape(1, 14, 5, 2, 15, 11), IBooleanFunction.ONLY_FIRST));

	public BlockKeypadFurnace(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(OPEN, false));
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		switch(state.get(FACING))
		{
			case NORTH:
				if(state.get(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if(state.get(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if(state.get(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if(state.get(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof IInventory)
		{
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
		{
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker))
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, EntityPlayer player){
		if(!BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockKeypadFurnace.OPEN))
			BlockUtils.setBlockProperty(world, pos, BlockKeypadFurnace.OPEN, true, false);

		if(player instanceof EntityPlayerMP)
		{
			world.playEvent((EntityPlayer)null, 1006, pos, 0);
			NetworkHooks.openGui((EntityPlayerMP)player, new TEInteractionObject(GuiHandler.KEYPAD_FURNACE, world, pos), pos);
		}
	}

	@Override
	public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return (state.get(OPEN) && te != null && te instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace)te).isBurning()) ? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(OPEN, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(OPEN);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityKeypadFurnace();
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.FURNACE;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		EnumFacing facing = world.getBlockState(pos).get(FACING);
		TileEntityFurnace furnace = (TileEntityFurnace)world.getTileEntity(pos);
		NBTTagCompound tag = furnace.write(new NBTTagCompound());

		furnace.clear();
		world.setBlockState(pos, SCContent.keypadFurnace.getDefaultState().with(FACING, facing).with(OPEN, false));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName().getFormattedText());
		((TileEntityKeypadFurnace)world.getTileEntity(pos)).read(tag);
		return true;
	}
}
