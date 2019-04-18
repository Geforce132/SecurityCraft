package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
	public float getBlockHardness(IBlockState blockState, IBlockReader world, BlockPos pos)
	{
		return !ServerConfig.CONFIG.ableToBreakMines.get() ? -1F : super.getBlockHardness(blockState, world, pos);
	}

	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
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
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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
				else if(player instanceof EntityPlayerMP)
					NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.IMS), pos);

				return true;
			}
		}

		return true;
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(IBlockState state, World world, BlockPos pos, Random random){
		if(state.get(MINES) == 0){
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.addParticle(Particles.SMOKE, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(Particles.SMOKE, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(Particles.SMOKE, false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(Particles.SMOKE, false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(Particles.SMOKE, false, x, y, z, 0.0D, 0.0D, 0.0D);

			world.addParticle(Particles.FLAME, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(Particles.FLAME, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
	{
		int mines = state.get(MINES);

		if(mines != 0)
			drops.add(new ItemStack(SCContent.bouncingBetty, mines));
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(MINES, 4);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(MINES);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityIMS();
	}

}
