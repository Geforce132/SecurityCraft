package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ProtectoTileEntity extends CustomizableTileEntity {

	public ProtectoTileEntity()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof LivingEntity && !(entity instanceof SentryEntity) && !EntityUtils.isInvisible(((LivingEntity)entity))) {
			if ((entity instanceof PlayerEntity && (getOwner().isOwner((PlayerEntity) entity) || (hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(((LivingEntity) entity).getName().getString().toLowerCase())))))
				return false;

			if(!world.isRemote)
				WorldUtils.spawnLightning(world, entity.getPositionVec(), false);

			BlockUtils.setBlockProperty(world, pos, ProtectoBlock.ACTIVATED, false);
			return true;
		}

		return false;
	}

	@Override
	public boolean canAttack() {
		boolean canAttack = (getAttackCooldown() == 200 && world.canBlockSeeSky(pos) && world.isRaining());

		if(canAttack && !BlockUtils.getBlockProperty(world, pos, ProtectoBlock.ACTIVATED))
			BlockUtils.setBlockProperty(world, pos, ProtectoBlock.ACTIVATED, true);
		else if(!canAttack && BlockUtils.getBlockProperty(world, pos, ProtectoBlock.ACTIVATED))
			BlockUtils.setBlockProperty(world, pos, ProtectoBlock.ACTIVATED, false);

		return canAttack;
	}

	@Override
	public boolean shouldAttackEntityType(Entity entity)
	{
		return !(entity instanceof PlayerEntity) && entity instanceof LivingEntity;
	}

	@Override
	public boolean shouldRefreshAttackCooldown() {
		return false;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
