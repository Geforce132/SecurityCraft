package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class ProtectoTileEntity extends CustomizableTileEntity {

	public ProtectoTileEntity()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof LivingEntity && !(entity instanceof SentryEntity) && !EntityUtils.isInvisible(((LivingEntity)entity))) {
			if ((entity instanceof PlayerEntity && (getOwner().isOwner((PlayerEntity) entity) || (hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(((LivingEntity) entity).getName().getFormattedText().toLowerCase())))))
				return false;

			if(!world.isRemote)
				((ServerWorld)world).addLightningBolt(new LightningBoltEntity(world, entity.posX, entity.posY, entity.posZ, false));

			world.setBlockState(pos, getBlockState().with(ProtectoBlock.ACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean canAttack() {
		boolean canAttack = (getAttackCooldown() == 200 && world.canBlockSeeSky(pos) && world.isRaining());

		if(canAttack && !getBlockState().get(ProtectoBlock.ACTIVATED))
			world.setBlockState(pos, getBlockState().with(ProtectoBlock.ACTIVATED, true));
		else if(!canAttack && getBlockState().get(ProtectoBlock.ACTIVATED))
			world.setBlockState(pos, getBlockState().with(ProtectoBlock.ACTIVATED, false));

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
		return new ModuleType[]{ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
