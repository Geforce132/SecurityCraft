package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ProtectoTileEntity extends CustomizableTileEntity {

	public ProtectoTileEntity()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public boolean attackEntity(Entity entity){
		if (entity instanceof LivingEntity) {
			if ((entity instanceof PlayerEntity && (getOwner().isOwner((PlayerEntity) entity) || (hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(((LivingEntity) entity).getName().getFormattedText().toLowerCase())))) ||
					entity instanceof ZombiePigmanEntity ||
					(entity instanceof CreeperEntity && ((CreeperEntity) entity).getPowered()))
				return false;

			LightningBoltEntity lightning = new LightningBoltEntity(world, entity.posX, entity.posY, entity.posZ, false);

			world.addEntity(lightning);
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
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{CustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	@Override
	public void onModuleInserted(ItemStack stack, CustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}

	@Override
	public void onModuleRemoved(ItemStack stack, CustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
	}
}
