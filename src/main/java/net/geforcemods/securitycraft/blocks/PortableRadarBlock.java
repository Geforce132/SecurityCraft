package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PortableRadarBlock extends ContainerBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SHAPE = Block.makeCuboidShape(5, 0, 5, 11, 7, 11);

	public PortableRadarBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(POWERED, false));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean par5) {
		if(par5 && !world.getBlockState(pos).get(POWERED)){
			BlockUtils.setBlockProperty(world, pos, POWERED, true, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}else if(!par5 && world.getBlockState(pos).get(POWERED)){
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}
	}

	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((CustomizableTileEntity) blockAccess.getTileEntity(pos)).hasModule(CustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new PortableRadarTileEntity().attacks(PlayerEntity.class, ConfigHandler.CONFIG.portableRadarSearchRadius.get(), ConfigHandler.CONFIG.portableRadarDelay.get()).nameable();
	}

}
