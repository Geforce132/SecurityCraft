package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeypad extends BlockContainer implements ICustomWailaDisplay, IPasswordConvertible {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockKeypad(Material material) {
		super(material);
		setSoundType(SoundType.STONE);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityKeypad && ((TileEntityKeypad)te).hasModule(EnumCustomModules.DISGUISE))
		{
			ItemStack module = ((TileEntityKeypad)te).getModule(EnumCustomModules.DISGUISE);

			return ((ItemModule)module.getItem()).getBlockAddons(module.getTagCompound()).get(0).getDefaultState().getBlockFaceShape(world, pos, face);
		}

		return BlockFaceShape.SOLID;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
		if(world.getTileEntity(pos) == null)
			return true;

		CustomizableSCTE tileEntity = (CustomizableSCTE) world.getTileEntity(pos);

		if(tileEntity.hasModule(EnumCustomModules.DISGUISE))
		{
			ItemStack disguiseModule = tileEntity.getModule(EnumCustomModules.DISGUISE);
			List<Block> blocks = ((ItemModule) disguiseModule.getItem()).getBlockAddons(disguiseModule.getTagCompound());

			if(blocks.size() != 0)
			{
				Block blockToDisguiseAs = blocks.get(0);

				// If the keypad has a disguise module added with a transparent block inserted.
				if(!blockToDisguiseAs.getDefaultState().isOpaqueCube() || !blockToDisguiseAs.getDefaultState().isFullCube())
					return checkForSideTransparency(world, pos, world.getBlockState(pos.offset(side)).getBlock(), side);
			}
		}

		return true;
	}

	public boolean checkForSideTransparency(IBlockAccess world, BlockPos keypadPos, Block neighborBlock, EnumFacing side) {
		if(neighborBlock == Blocks.AIR)
			return true;

		// Slightly cheating here, checking if the block is an instance of BlockBreakable
		// and a vanilla block instead of checking for specific blocks, since all vanilla
		// BlockBreakable blocks are transparent.
		if(neighborBlock instanceof BlockBreakable && neighborBlock.toString().replace("Block{", "").startsWith("minecraft:"))
			return false;

		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else {
			if(state.getValue(POWERED).booleanValue())
				return false;

			if(ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.BLACKLIST)){
				activate(world, pos);
				return true;
			}

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && !PlayerUtils.isHoldingItem(player, SCContent.keyPanel))
				((IPasswordProtected) world.getTileEntity(pos)).openPasswordGUI(player);

			return true;
		}
	}

	public static void activate(World world, BlockPos pos){
		BlockUtils.setBlockProperty(world, pos, POWERED, true);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad, false);
		world.scheduleUpdate(pos, SCContent.keypad, 60);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
		BlockUtils.setBlockProperty(world, pos, POWERED, false);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad, false);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
		IBlockState north = world.getBlockState(pos.north());
		IBlockState south = world.getBlockState(pos.south());
		IBlockState west = world.getBlockState(pos.west());
		IBlockState east = world.getBlockState(pos.east());
		EnumFacing facing = state.getValue(FACING);

		if (facing == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.withProperty(FACING, facing), 2);
	}

	@Override
	public boolean canProvidePower(IBlockState state){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta == 15) return getDefaultState();

		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getProperties().containsKey(POWERED) && state.getValue(POWERED).booleanValue())
			return (state.getValue(FACING).getIndex() + 6);
		else{
			if(!state.getProperties().containsKey(FACING)) return 15;

			return state.getValue(FACING).getIndex();
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	public IBlockState getDisguisedBlockState(IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
			TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);
			ItemStack module = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : ItemStack.EMPTY;

			if(!module.isEmpty() && !((ItemModule) module.getItem()).getBlockAddons(module.getTagCompound()).isEmpty()) {
				ItemStack disguisedStack = ((ItemModule) module.getItem()).getAddons(module.getTagCompound()).get(0);
				Block block = Block.getBlockFromItem(disguisedStack.getItem());
				boolean hasMeta = disguisedStack.getHasSubtypes();

				IBlockState disguisedModel = block.getStateFromMeta(hasMeta ? disguisedStack.getItemDamage() : getMetaFromState(world.getBlockState(pos)));

				if (block != this)
					return disguisedModel.getActualState(world, pos);
			}
		}

		return null;
	}

	public ItemStack getDisguisedStack(IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
			TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);
			ItemStack stack = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : ItemStack.EMPTY;

			if(!stack.isEmpty() && !((ItemModule) stack.getItem()).getBlockAddons(stack.getTagCompound()).isEmpty()) {
				ItemStack disguisedStack = ((ItemModule) stack.getItem()).getAddons(stack.getTagCompound()).get(0);

				if(Block.getBlockFromItem(disguisedStack.getItem()) != this)
					return disguisedStack;
			}
		}

		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityKeypad();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		ItemStack stack = getDisguisedStack(world, pos);

		return !stack.isEmpty() ? stack : new ItemStack(this);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos).isEmpty();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		ItemStack stack = getDisguisedStack(world, pos);

		return stack.isEmpty() ? new ItemStack(this) : stack;
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.frame;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		world.setBlockState(pos, SCContent.keypad.getDefaultState().withProperty(BlockKeypad.FACING, world.getBlockState(pos).getValue(BlockFrame.FACING)).withProperty(BlockKeypad.POWERED, false));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(((IOwnable)world.getTileEntity(pos)).getOwner());
		return true;
	}
}
