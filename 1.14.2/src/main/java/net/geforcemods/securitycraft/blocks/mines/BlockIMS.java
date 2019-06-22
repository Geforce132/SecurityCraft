package net.geforcemods.securitycraft.blocks.mines;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockIMS extends BlockOwnable {

	public static final IntegerProperty MINES = IntegerProperty.create("mines", 0, 4);
	private static final VoxelShape SHAPE = Block.makeCuboidShape(4, 0, 5, 12, 7, 11);
	private static final VoxelShape SHAPE_1_MINE = VoxelShapes.or(SHAPE, Block.makeCuboidShape(0, 0, 0, 5, 5, 5));
	private static final VoxelShape SHAPE_2_MINES = VoxelShapes.or(SHAPE_1_MINE, Block.makeCuboidShape(0, 0, 11, 5, 5, 16));
	private static final VoxelShape SHAPE_3_MINES = VoxelShapes.or(SHAPE_2_MINES, Block.makeCuboidShape(11, 0, 0, 16, 5, 5));
	private static final VoxelShape SHAPE_4_MINES = VoxelShapes.or(SHAPE_3_MINES, Block.makeCuboidShape(11, 0, 11, 16, 5, 16));

	public BlockIMS(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(0.7F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(MINES, 4));
	}

	@Override
	public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !CommonConfig.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.get(MINES))
		{
			case 4: return SHAPE_4_MINES;
			case 3: return SHAPE_3_MINES;
			case 2: return SHAPE_2_MINES;
			case 1: return SHAPE_1_MINE;
			default: return SHAPE;
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isRemote)
		{
			if(((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player))
			{
				ItemStack held = player.getHeldItem(hand);
				int mines = state.get(MINES);

				if(held.getItem() == SCContent.bouncingBetty.asItem() && mines < 4)
				{
					if(!player.isCreative())
						held.shrink(1);

					world.setBlockState(pos, state.with(MINES, mines + 1));
					((TileEntityIMS)world.getTileEntity(pos)).setBombsRemaining(mines + 1);
				}
				else if(player instanceof ServerPlayerEntity)
					NetworkHooks.openGui((ServerPlayerEntity)player, new BaseInteractionObject(GuiHandler.IMS), pos);

				return true;
			}
		}

		return true;
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random){
		if(state.get(MINES) == 0){
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.addParticle(ParticleTypes.SMOKE, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y, z, 0.0D, 0.0D, 0.0D);

			world.addParticle(ParticleTypes.FLAME, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> drops = NonNullList.from(ItemStack.EMPTY);
		int mines = state.get(MINES);

		if(mines != 0)
			drops.add(new ItemStack(SCContent.bouncingBetty, mines));
		return super.getDrops(state, builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(MINES, 4);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(MINES);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityIMS();
	}

}
