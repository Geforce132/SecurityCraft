package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityIronFence;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockReinforcedFenceGate extends BlockFenceGate implements ITileEntityProvider {

	public BlockReinforcedFenceGate(){
		super(BlockPlanks.EnumType.OAK);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Material.IRON, 18);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if(state.getValue(OPEN))
			return;

		if(entity instanceof EntityItem)
			return;
		else if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			if(((TileEntityOwnable)world.getTileEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true);

			creeper.onStruckByLightning(lightning);
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if(!world.isRemote) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

			if (isPoweredSCBlock || block.getDefaultState().canProvidePower())
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					world.setBlockState(pos, state.withProperty(OPEN, true).withProperty(POWERED, true), 2);
					world.playEvent(null, Constants.WorldEvents.IRON_DOOR_OPEN_SOUND, pos, 0);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					world.setBlockState(pos, state.withProperty(OPEN, false).withProperty(POWERED, false), 2);
					world.playEvent(null, Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, pos, 0);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED))
					world.setBlockState(pos, state.withProperty(POWERED, isPoweredSCBlock), 2);
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int par5, int par6){
		super.eventReceived(state, world, pos, par5, par6);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityIronFence();
	}

}
