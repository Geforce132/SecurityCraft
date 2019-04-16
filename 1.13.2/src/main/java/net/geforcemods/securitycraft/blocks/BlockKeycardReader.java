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
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockKeycardReader extends BlockOwnable  {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public BlockKeycardReader(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(POWERED, false));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		IBlockState north = world.getBlockState(pos.north());
		IBlockState south = world.getBlockState(pos.south());
		IBlockState west = world.getBlockState(pos.west());
		IBlockState east = world.getBlockState(pos.east());
		EnumFacing facing = state.get(FACING);

		if (facing == EnumFacing.NORTH && north.isFullCube() && !south.isFullCube())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullCube() && !north.isFullCube())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullCube() && !east.isFullCube())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullCube() && !west.isFullCube())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.with(FACING, facing), 2);
	}

	public void insertCard(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
		if(ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.BLACKLIST))
			return;

		int securityLevel = 0;

		if(((TileEntityKeycardReader)world.getTileEntity(pos)).getPassword() != null)
			securityLevel = Integer.parseInt(((TileEntityKeycardReader)world.getTileEntity(pos)).getPassword());

		if((!((TileEntityKeycardReader)world.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel <= ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack) || ((TileEntityKeycardReader)world.getTileEntity(pos)).doesRequireExactKeycard() && securityLevel == ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack))){
			if(((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack) == 6 && stack.getTag() != null && !player.isCreative()){
				stack.getTag().putInt("Uses", stack.getTag().getInt("Uses") - 1);

				if(stack.getTag().getInt("Uses") <= 0)
					stack.shrink(1);
			}

			BlockKeycardReader.activate(world, pos);
		}

		if(world.isRemote)
		{
			if(securityLevel != 0)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.required").replace("#r", ((IPasswordProtected) world.getTileEntity(pos)).getPassword()).replace("#c", "" + ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack)), TextFormatting.RED);
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.notSet"), TextFormatting.RED);
		}
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
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
	public void tick(IBlockState state, World world, BlockPos pos, Random random){
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, POWERED, false);
			world.notifyNeighborsOfStateChange(pos, SCContent.keycardReader);
		}
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	public void animateTick(IBlockState state, World world, BlockPos pos, Random rand){
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
	public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side)
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
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(POWERED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityKeycardReader();
	}

}
