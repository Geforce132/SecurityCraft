package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TaserItem extends Item {
	private final boolean powered;

	public TaserItem(Item.Properties properties, boolean isPowered) {
		super(properties);

		powered = isPowered;
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if ((group == SecurityCraft.TECHNICAL_TAB || group == ItemGroup.TAB_SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.isDamaged()) {
			if (player.isCrouching() && (player.isCreative() || !powered)) {
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if (player.isCreative()) {
					if (player.getItemInHand(hand).getItem() == SCContent.TASER.get())
						player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					else
						player.setItemInHand(hand, new ItemStack(SCContent.TASER.get(), 1));

					return ActionResult.success(stack);
				}
				else if (player.inventory.contains(oneRedstone)) {
					int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
					ItemStack redstoneStack;

					if (redstoneSlot == -1) {
						if (player.getOffhandItem().getItem() == Items.REDSTONE)
							redstoneStack = player.getOffhandItem();
						else
							return ActionResult.pass(stack);
					}
					else
						redstoneStack = player.inventory.getItem(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);

					if (redstoneSlot == -1)
						player.inventory.offhand.set(0, redstoneStack);
					else
						player.inventory.setItem(redstoneSlot, redstoneStack);

					player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					return ActionResult.success(stack);
				}

				return ActionResult.pass(stack);
			}

			int range = 11;
			Vector3d startVec = player.getEyePosition(1.0F);
			Vector3d lookVec = player.getViewVector(1.0F).scale(range);
			Vector3d endVec = startVec.add(lookVec);
			AxisAlignedBB boundingBox = player.getBoundingBox().expandTowards(lookVec).inflate(1, 1, 1);
			EntityRayTraceResult entityRayTraceResult = rayTraceEntities(player, startVec, endVec, boundingBox, LivingEntity.class::isInstance, range * range);

			world.playSound(player, player.blockPosition(), SCSounds.TASERFIRED.event, SoundCategory.PLAYERS, 1.0F, 1.0F);

			if (entityRayTraceResult != null) {
				LivingEntity entity = (LivingEntity) entityRayTraceResult.getEntity();
				double damage = powered ? ConfigHandler.SERVER.poweredTaserDamage.get() : ConfigHandler.SERVER.taserDamage.get();

				if ((damage == 0.0D || entity.hurt(CustomDamageSources.taser(player), (float) damage)) && !entity.isBlocking()) {
					List<Supplier<EffectInstance>> effects = powered ? ConfigHandler.SERVER.poweredTaserEffects : ConfigHandler.SERVER.taserEffects;

					effects.forEach(effect -> entity.addEffect(effect.get()));
				}
			}

			if (!player.isCreative()) {
				if (powered) {
					ItemStack taser = new ItemStack(SCContent.TASER.get(), 1);

					taser.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
					player.setItemInHand(hand, taser);
				}
				else
					stack.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
			}

			return ActionResult.consume(stack);
		}

		return ActionResult.pass(stack);
	}

	//copied from ProjectileHelper#rayTraceEntities because that one's only available on the client
	private static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double dist) {
		World world = shooter.level;
		double distance = dist;
		Entity rayTracedEntity = null;
		Vector3d hitVec = null;

		for (Entity entity : world.getEntities(shooter, boundingBox, filter)) {
			AxisAlignedBB boxToCheck = entity.getBoundingBox().inflate(entity.getPickRadius());
			Optional<Vector3d> optional = boxToCheck.clip(startVec, endVec);

			if (boxToCheck.contains(startVec)) {
				if (distance >= 0.0D) {
					rayTracedEntity = entity;
					hitVec = optional.orElse(startVec);
					distance = 0.0D;
				}
			}
			else if (optional.isPresent()) {
				Vector3d vector = optional.get();
				double sqDist = startVec.distanceToSqr(vector);

				if (sqDist < distance || distance == 0.0D) {
					if (entity.getRootVehicle() == shooter.getRootVehicle() && !entity.canRiderInteract()) {
						if (distance == 0.0D) {
							rayTracedEntity = entity;
							hitVec = vector;
						}
					}
					else {
						rayTracedEntity = entity;
						hitVec = vector;
						distance = sqDist;
					}
				}
			}
		}

		return rayTracedEntity == null ? null : new EntityRayTraceResult(rayTracedEntity, hitVec);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slotIndex, boolean isSelected) {
		if (!world.isClientSide && stack.getDamageValue() >= 1)
			stack.setDamageValue(stack.getDamageValue() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
