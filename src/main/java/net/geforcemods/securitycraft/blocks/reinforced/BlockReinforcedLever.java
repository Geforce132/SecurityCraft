package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityWhitelistOnly;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
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

public class BlockReinforcedLever extends BlockLever implements IReinforcedBlock
{
	public BlockReinforcedLever()
	{
		super();

		setSoundType(SoundType.WOOD);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(isAllowedToPress(world, pos, (TileEntityWhitelistOnly)world.getTileEntity(pos), player))
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		return false;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, TileEntityWhitelistOnly te, EntityPlayer entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(entity.getName().toLowerCase());
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityWhitelistOnly();
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.LEVER);
	}
}
