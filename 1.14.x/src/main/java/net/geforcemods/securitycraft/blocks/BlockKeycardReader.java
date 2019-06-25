package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockKeycardReader extends BlockOwnable  {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public BlockKeycardReader(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		//		BlockState north = world.getBlockState(pos.north());
		//		BlockState south = world.getBlockState(pos.south());
		//		BlockState west = world.getBlockState(pos.west());
		//		BlockState east = world.getBlockState(pos.east());
		Direction facing = state.get(FACING);

		if (facing == Direction.NORTH)// && north.isFullCube() && !south.isFullCube())
			facing = Direction.SOUTH;
		else if (facing == Direction.SOUTH)// && south.isFullCube() && !north.isFullCube())
			facing = Direction.NORTH;
		else if (facing == Direction.WEST)// && west.isFullCube() && !east.isFullCube())
			facing = Direction.EAST;
		else if (facing == Direction.EAST)// && east.isFullCube() && !west.isFullCube())
			facing = Direction.WEST;

		world.setBlockState(pos, state.with(FACING, facing), 2);
	}

	public void insertCard(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
		if(ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.BLACKLIST))
			return;

		int requiredLevel = -1;
		int cardLvl = ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack);

		if(((TileEntityKeycardReader)world.getTileEntity(pos)).getPassword() != null)
			requiredLevel = Integer.parseInt(((TileEntityKeycardReader)world.getTileEntity(pos)).getPassword());

		if((!((TileEntityKeycardReader)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel <= cardLvl || ((TileEntityKeycardReader)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel == cardLvl)){
			if(cardLvl == 6 && stack.getTag() != null && !player.isCreative()){
				stack.getTag().putInt("Uses", stack.getTag().getInt("Uses") - 1);

				if(stack.getTag().getInt("Uses") <= 0)
					stack.shrink(1);
			}

			BlockKeycardReader.activate(world, pos);
		}

		if(world.isRemote)
		{
			if(requiredLevel != -1 && ((TileEntityKeycardReader)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel != cardLvl)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.required").replace("#r", ((IPasswordProtected) world.getTileEntity(pos)).getPassword()).replace("#c", "" + ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack)), TextFormatting.RED);
			else if(requiredLevel == -1)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.notSet"), TextFormatting.RED);
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(player.inventory.getCurrentItem().isEmpty() || (!(player.inventory.getCurrentItem().getItem() instanceof ItemKeycardBase) && player.inventory.getCurrentItem().getItem() != SCContent.adminTool))
			((TileEntityKeycardReader) world.getTileEntity(pos)).openPasswordGUI(player);
		else if(player.inventory.getCurrentItem().getItem() == SCContent.adminTool)
			((BlockKeycardReader) BlockUtils.getBlock(world, pos)).insertCard(world, pos, new ItemStack(SCContent.limitedUseKeycard, 1), player);
		else
			((BlockKeycardReader) BlockUtils.getBlock(world, pos)).insertCard(world, pos, player.inventory.getCurrentItem(), player);

		return true;
	}

	public static void activate(World world, BlockPos pos){
		BlockUtils.setBlockProperty(world, pos, POWERED, true);
		world.notifyNeighborsOfStateChange(pos, SCContent.keycardReader);
		world.getPendingBlockTicks().scheduleTick(pos, SCContent.keycardReader, 60);
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random){
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, POWERED, false);
			world.notifyNeighborsOfStateChange(pos, SCContent.keycardReader);
		}
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand){
		if((state.get(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float f1 = 0.6F + 0.4F;
			float f2 = Math.max(0.0F, 0.7F - 0.5F);
			float f3 = Math.max(0.0F, 0.6F - 0.7F);

			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if((blockState.get(POWERED)))
			return 15;
		else
			return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
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
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityKeycardReader();
	}

}
