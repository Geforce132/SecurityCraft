package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedIronBars;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedIronBars extends BlockPane implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedIronBars(Material material, boolean par2) {
		super(material, par2);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param){
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(id, param) : false;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return new ItemStack(SCContent.reinforcedIronBars);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityReinforcedIronBars && ((TileEntityReinforcedIronBars)te).canDrop())
			drops.add(new ItemStack(SCContent.reinforcedIronBars));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityReinforcedIronBars();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.IRON_BARS);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
