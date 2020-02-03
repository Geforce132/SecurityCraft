package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.BaseKeycardItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

public class KeycardReaderBlock extends DisguisableBlock  {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeycardReaderBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.METAL));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	public void insertCard(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
		if(ModuleUtils.checkForModule(world, pos, player, CustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, CustomModules.BLACKLIST))
			return;

		int requiredLevel = -1;
		int cardLvl = ((BaseKeycardItem) stack.getItem()).getKeycardLvl(stack);

		if(((KeycardReaderTileEntity)world.getTileEntity(pos)).getPassword() != null)
			requiredLevel = Integer.parseInt(((KeycardReaderTileEntity)world.getTileEntity(pos)).getPassword());

		if((!((KeycardReaderTileEntity)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel <= cardLvl || ((KeycardReaderTileEntity)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel == cardLvl)){
			if(cardLvl == 6 && stack.getTag() != null && !player.isCreative()){
				stack.getTag().putInt("Uses", stack.getTag().getInt("Uses") - 1);

				if(stack.getTag().getInt("Uses") <= 0)
					stack.shrink(1);
			}

			KeycardReaderBlock.activate(world, pos);
		}

		if(world.isRemote)
		{
			if(requiredLevel != -1 && ((KeycardReaderTileEntity)world.getTileEntity(pos)).doesRequireExactKeycard() && requiredLevel != cardLvl)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.required").replace("#r", ((IPasswordProtected) world.getTileEntity(pos)).getPassword()).replace("#c", "" + ((BaseKeycardItem) stack.getItem()).getKeycardLvl(stack)), TextFormatting.RED);
			else if(requiredLevel == -1)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.notSet"), TextFormatting.RED);
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(player.inventory.getCurrentItem().isEmpty() || (!(player.inventory.getCurrentItem().getItem() instanceof BaseKeycardItem) && player.inventory.getCurrentItem().getItem() != SCContent.adminTool))
			((KeycardReaderTileEntity) world.getTileEntity(pos)).openPasswordGUI(player);
		else if(player.inventory.getCurrentItem().getItem() == SCContent.adminTool)
			((KeycardReaderBlock) BlockUtils.getBlock(world, pos)).insertCard(world, pos, new ItemStack(SCContent.limitedUseKeycard, 1), player);
		else
			((KeycardReaderBlock) BlockUtils.getBlock(world, pos)).insertCard(world, pos, player.inventory.getCurrentItem(), player);

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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeycardReaderTileEntity();
	}

}
