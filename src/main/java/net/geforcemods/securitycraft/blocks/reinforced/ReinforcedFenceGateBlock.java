package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceGateBlock extends FenceGateBlock implements IIntersectable {

	public ReinforcedFenceGateBlock(Block.Properties properties){
		super(properties);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		return ActionResultType.FAIL;
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(world.getBlockState(pos).getValue(OPEN))
			return;

		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)entity;

			if(((OwnableTileEntity)world.getBlockEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(!world.isClientSide && entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningBoltEntity lightning = WorldUtils.createLightning(world, Vector3d.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerWorld)world, lightning);
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!world.isClientSide) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

			if (isPoweredSCBlock || block.defaultBlockState().isSignalSource())
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					world.setBlock(pos, state.setValue(OPEN, true).setValue(POWERED, true), 2);
					world.levelEvent(null, 1008, pos, 0);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					world.setBlock(pos, state.setValue(OPEN, false).setValue(POWERED, false), 2);
					world.levelEvent(null, 1014, pos, 0);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED))
					world.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock), 2);
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int par5, int par6){
		super.triggerEvent(state, world, pos, par5, par6);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity != null ? tileentity.triggerEvent(par5, par6) : false;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SecurityCraftTileEntity().intersectsEntities();
	}

}
