package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityProtecto extends CustomizableSCTE {

	public TileEntityProtecto()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof EntityLivingBase) {
			if ((entity instanceof EntityPlayer && (getOwner().isOwner((EntityPlayer) entity) || (hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getName().toLowerCase())))) ||
					entity instanceof EntityPigZombie ||
					(entity instanceof EntityCreeper && ((EntityCreeper) entity).getPowered()))
				return false;

			EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);

			world.addWeatherEffect(lightning);
			BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, false);
			return true;
		}

		return false;
	}

	@Override
	public boolean canAttack() {
		boolean canAttack = (getAttackCooldown() == 200 && world.canBlockSeeSky(pos) && world.isRaining());

		if(canAttack && !BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockProtecto.ACTIVATED))
			BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, true);
		else if(!canAttack && BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockProtecto.ACTIVATED))
			BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, false);

		return canAttack;
	}

	@Override
	public boolean shouldAttackEntityType(Entity entity)
	{
		return !(entity instanceof EntityPlayer) && entity instanceof EntityLivingBase;
	}

	@Override
	public boolean shouldRefreshAttackCooldown() {
		return false;
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}
}
