package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockBlockPocketManager extends BlockOwnable
{
	public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockBlockPocketManager()
	{
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
			NetworkHooks.openGui((EntityPlayerMP)player, new TEInteractionObject(GuiHandler.BLOCK_POCKET_MANAGER, world, pos), pos);

		return true;
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityBlockPocketManager();
	}
}
