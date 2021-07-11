package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityProtecto extends CustomizableSCTE {

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof EntityLivingBase && !(entity instanceof EntitySentry) && !EntityUtils.isInvisible(((EntityLivingBase)entity))) {
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				if(player.isCreative() || player.isSpectator() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, entity))
					return false;
			}

			EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);

			world.addWeatherEffect(lightning);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockProtecto.ACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean canAttack() {
		IBlockState state = world.getBlockState(pos);
		boolean activated = state.getValue(BlockProtecto.ACTIVATED);
		boolean canAttack = (getAttackCooldown() == getTicksBetweenAttacks() && world.canBlockSeeSky(pos) && world.isRaining());

		if(canAttack && !activated)
			world.setBlockState(pos, state.withProperty(BlockProtecto.ACTIVATED, true));
		else if(!canAttack && activated)
			world.setBlockState(pos, state.withProperty(BlockProtecto.ACTIVATED, false));

		return canAttack;
	}

	@Override
	public boolean shouldAttackEntityType(Entity entity)
	{
		return entity instanceof EntityLivingBase;
	}

	@Override
	public boolean shouldRefreshAttackCooldown() {
		return false;
	}

	@Override
	public int getTicksBetweenAttacks()
	{
		return hasModule(EnumModuleType.SPEED) ? 100 : 200;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.ALLOWLIST, EnumModuleType.SPEED};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
