package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceGateBlock extends FenceGateBlock implements IIntersectable {

	public ReinforcedFenceGateBlock(Block.Properties properties){
		super(properties);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return InteractionResult.FAIL;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));
	}

	@Override
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity) {
		if(world.getBlockState(pos).getValue(OPEN))
			return;

		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof Player)
		{
			Player player = (Player)entity;

			if(((OwnableTileEntity)world.getBlockEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(!world.isClientSide && entity instanceof Creeper)
		{
			Creeper creeper = (Creeper)entity;
			LightningBolt lightning = WorldUtils.createLightning(world, Vec3.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerLevel)world, lightning);
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
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
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int par5, int par6){
		super.triggerEvent(state, world, pos, par5, par6);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity != null ? tileentity.triggerEvent(par5, par6) : false;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new SecurityCraftTileEntity().intersectsEntities();
	}

}
