package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityProtecto extends TileEntityDisguisable {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = hasModule(EnumModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;

	@Override
	public void update() {
		super.update();

		if(cooldown++ < ticksBetweenAttacks)
			return;

		if(world.isRaining() && world.canBlockSeeSky(pos)) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(ATTACK_RANGE));
			IBlockState state = world.getBlockState(pos);

			if(!state.getValue(BlockProtecto.ACTIVATED))
				world.setBlockState(pos, state.withProperty(BlockProtecto.ACTIVATED, true));

			if(entities.size() != 0) {
				boolean shouldDeactivate = false;

				for(EntityLivingBase entity : entities) {
					if (!(entity instanceof EntitySentry) && !EntityUtils.isInvisible(entity)) {
						if (entity instanceof EntityPlayer)
						{
							EntityPlayer player = (EntityPlayer)entity;

							if(player.isCreative() || player.isSpectator() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, entity))
								continue;
						}

						world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false));
						shouldDeactivate = true;
					}
				}

				if(shouldDeactivate)
					world.setBlockState(pos, state.withProperty(BlockProtecto.ACTIVATED, false));
			}

			cooldown = 0;
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module) {
		super.onModuleInserted(stack, module);

		if(module == EnumModuleType.SPEED)
			ticksBetweenAttacks = FAST_SPEED;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module) {
		super.onModuleRemoved(stack, module);

		if(module == EnumModuleType.SPEED)
			ticksBetweenAttacks = SLOW_SPEED;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.ALLOWLIST, EnumModuleType.SPEED, EnumModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
