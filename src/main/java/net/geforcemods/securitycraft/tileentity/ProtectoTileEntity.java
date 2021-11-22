package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

public class ProtectoTileEntity extends DisguisableTileEntity implements ITickableTileEntity {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = hasModule(ModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;

	public ProtectoTileEntity()
	{
		super(SCContent.teTypeProtecto);
	}

	@Override
	public void tick() {
		if(cooldown++ < ticksBetweenAttacks)
			return;

		if(world.isRaining() && world.canBlockSeeSky(pos)) {
			List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos).grow(ATTACK_RANGE));

			if(!getBlockState().get(ProtectoBlock.ACTIVATED))
				world.setBlockState(pos, getBlockState().with(ProtectoBlock.ACTIVATED, true));

			if(entities.size() != 0) {
				boolean shouldDeactivate = false;

				for(LivingEntity entity : entities) {
					if (!(entity instanceof SentryEntity) && !EntityUtils.isInvisible(entity)) {
						if (entity instanceof PlayerEntity)
						{
							PlayerEntity player = (PlayerEntity)entity;

							if(player.isCreative() || player.isSpectator() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, entity))
								continue;
						}

						if(!world.isRemote)
							((ServerWorld)world).addLightningBolt(new LightningBoltEntity(world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), false));

						shouldDeactivate = true;
					}
				}

				if(shouldDeactivate)
					world.setBlockState(pos, getBlockState().with(ProtectoBlock.ACTIVATED, false));
			}

			cooldown = 0;
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module) {
		super.onModuleInserted(stack, module);

		if(module == ModuleType.SPEED)
			ticksBetweenAttacks = FAST_SPEED;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module) {
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.SPEED)
			ticksBetweenAttacks = SLOW_SPEED;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.SPEED, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
