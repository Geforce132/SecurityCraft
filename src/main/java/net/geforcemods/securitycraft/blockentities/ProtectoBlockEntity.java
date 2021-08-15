package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class ProtectoBlockEntity extends CustomizableBlockEntity {

	public ProtectoBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeProtecto, pos, state);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof LivingEntity lEntity && !(entity instanceof Sentry) && !EntityUtils.isInvisible(lEntity)) {
			if (entity instanceof Player player)
			{
				if(player.isCreative() || player.isSpectator() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, entity))
					return false;
			}

			if(!level.isClientSide)
				WorldUtils.spawnLightning(level, entity.position(), false);

			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean canAttack() {
		boolean canAttack = (getAttackCooldown() >= getTicksBetweenAttacks() && level.canSeeSkyFromBelowWater(worldPosition) && level.isRaining());

		if(canAttack && !getBlockState().getValue(ProtectoBlock.ACTIVATED))
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, true));
		else if(!canAttack && getBlockState().getValue(ProtectoBlock.ACTIVATED))
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));

		return canAttack;
	}

	@Override
	public boolean shouldAttackEntityType(Entity entity)
	{
		return entity instanceof LivingEntity;
	}

	@Override
	public boolean shouldRefreshAttackCooldown() {
		return false;
	}

	@Override
	public int getTicksBetweenAttacks()
	{
		return hasModule(ModuleType.SPEED) ? 100 : 200;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.SPEED};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
