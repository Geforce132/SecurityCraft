package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class KeypadBlock extends ContainerBlock implements IOverlayDisplay, IPasswordConvertible {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeypadBlock(Material material) {
		super(Block.Properties.create(material).sound(SoundType.STONE).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.getShape(world, pos, ctx);
		else return super.getShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.getCollisionShape(world, pos, ctx);
		else return super.getCollisionShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.getRenderShape(world, pos);
		else return super.getRenderShape(state, world, pos);
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.isNormalCube(world, pos);
		else return super.isNormalCube(state, world, pos);
	}

	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.causesSuffocation(world, pos);
		else return super.causesSuffocation(state, world, pos);
	}

	@Override //getAmbientOcclusionLightValue, mapped starting with 20191104
	public float func_220080_a(BlockState state, IBlockReader world, BlockPos pos)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.func_215703_d(world, pos);
		else return super.func_220080_a(state, world, pos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	public boolean shouldSideBeRendered(IBlockReader world, BlockPos pos, Direction side) {
		if(world.getTileEntity(pos) == null)
			return true;

		CustomizableTileEntity tileEntity = (CustomizableTileEntity) world.getTileEntity(pos);

		if(tileEntity.hasModule(CustomModules.DISGUISE))
		{
			ItemStack disguiseModule = tileEntity.getModule(CustomModules.DISGUISE);
			List<Block> blocks = ((ModuleItem) disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());

			if(blocks.size() != 0)
			{
				Block blockToDisguiseAs = blocks.get(0);

				// If the keypad has a disguise module added with a transparent block inserted.
				if(!blockToDisguiseAs.getDefaultState().getShape(world, pos).equals(VoxelShapes.fullCube()))
					return checkForSideTransparency(world, pos, world.getBlockState(pos.offset(side)), side);
			}
		}

		return true;
	}

	public boolean checkForSideTransparency(IBlockReader world, BlockPos keypadPos, BlockState neighborState, Direction side) {
		if(neighborState.isAir(world, keypadPos.offset(side)))
			return true;

		Block neighborBlock = neighborState.getBlock();

		// Slightly cheating here, checking if the block is an instance of BlockBreakable
		// and a vanilla block instead of checking for specific blocks, since all vanilla
		// BlockBreakable blocks are transparent.
		if(neighborBlock instanceof BreakableBlock && neighborBlock.getRegistryName().getNamespace().equals("minecraft"))
			return false;

		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(world.isRemote)
			return true;
		else {
			if(state.get(POWERED).booleanValue())
				return false;

			if(ModuleUtils.checkForModule(world, pos, player, CustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, CustomModules.BLACKLIST)){
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
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
		world.getPendingBlockTicks().scheduleTick(pos, SCContent.keypad, 60);
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random){
		BlockUtils.setBlockProperty(world, pos, POWERED, false);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
	}

	@Override
	public boolean canProvidePower(BlockState state){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(POWERED, false);
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	public BlockState getDisguisedBlockState(IBlockReader world, BlockPos pos) {
		if(world.getTileEntity(pos) instanceof KeypadTileEntity) {
			KeypadTileEntity te = (KeypadTileEntity) world.getTileEntity(pos);
			ItemStack module = te.hasModule(CustomModules.DISGUISE) ? te.getModule(CustomModules.DISGUISE) : ItemStack.EMPTY;

			if(!module.isEmpty() && !((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).isEmpty())
				return ((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).get(0).getDefaultState();
		}

		return null;
	}

	public static ItemStack getDisguisedStack(IBlockReader world, BlockPos pos) {
		if(world != null && world.getTileEntity(pos) instanceof KeypadTileEntity) {
			KeypadTileEntity te = (KeypadTileEntity) world.getTileEntity(pos);
			ItemStack stack = te.hasModule(CustomModules.DISGUISE) ? te.getModule(CustomModules.DISGUISE) : ItemStack.EMPTY;

			if(!stack.isEmpty() && !((ModuleItem) stack.getItem()).getBlockAddons(stack.getTag()).isEmpty()) {
				ItemStack disguisedStack = ((ModuleItem) stack.getItem()).getAddons(stack.getTag()).get(0);

				if(Block.getBlockFromItem(disguisedStack.getItem()) != SCContent.keypad)
					return disguisedStack;
			}
		}

		return new ItemStack(SCContent.keypad);
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(IBlockReader world){
		return new KeypadTileEntity();
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return getDisguisedStack(world, pos);
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.frame;
	}

	@Override
	public boolean convert(PlayerEntity player, World world, BlockPos pos)
	{
		world.setBlockState(pos, SCContent.keypad.getDefaultState().with(KeypadBlock.FACING, world.getBlockState(pos).get(FrameBlock.FACING)).with(KeypadBlock.POWERED, false));
		((IOwnable) world.getTileEntity(pos)).setOwner(player.getUniqueID().toString(), player.getName().getFormattedText());
		return true;
	}
}
