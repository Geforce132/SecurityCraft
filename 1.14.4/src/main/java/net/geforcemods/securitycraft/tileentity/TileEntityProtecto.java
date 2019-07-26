package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TileEntityProtecto extends CustomizableSCTE {

	public TileEntityProtecto()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof LivingEntity) {
			if ((entity instanceof PlayerEntity && (getOwner().isOwner((PlayerEntity) entity) || (hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(((LivingEntity) entity).getName().getFormattedText().toLowerCase())))) ||
					entity instanceof ZombiePigmanEntity ||
					(entity instanceof CreeperEntity && ((CreeperEntity) entity).getPowered()))
				return false;

			LightningBoltEntity lightning = new LightningBoltEntity(world, entity.posX, entity.posY, entity.posZ, false);

			world.addEntity(lightning);
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
		return !(entity instanceof PlayerEntity) && entity instanceof LivingEntity;
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
