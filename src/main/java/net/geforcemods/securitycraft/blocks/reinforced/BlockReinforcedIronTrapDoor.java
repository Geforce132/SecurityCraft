package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedIronTrapDoor extends BlockTrapDoor implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedIronTrapDoor(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
	{
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if(hasActiveSCBlock != state.getValue(OPEN))
		{
			world.setBlockState(pos, state.withProperty(OPEN, BlockUtils.hasActiveSCBlockNextTo(world, pos)), 2);
			world.markBlockRangeForRenderUpdate(pos, pos);
			playSound((EntityPlayer)null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = this.getDefaultState();

		if (facing.getAxis().isHorizontal())
		{
			state = state.withProperty(FACING, facing).withProperty(OPEN, false);
			state = state.withProperty(HALF, hitY > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
		}
		else
		{
			state = state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
			state = state.withProperty(HALF, facing == EnumFacing.UP ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
		}

		if (BlockUtils.hasActiveSCBlockNextTo(world, pos))
		{
			state = state.withProperty(OPEN, true);
		}

		return state;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.IRON_TRAPDOOR);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}