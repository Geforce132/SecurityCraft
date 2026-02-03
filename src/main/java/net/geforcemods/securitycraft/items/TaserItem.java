package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class TaserItem extends Item {
	private boolean powered;

	public TaserItem(Item.Properties properties, boolean isPowered) {
		super(properties);

		powered = isPowered;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.isDamaged()) {
			if (player.isCrouching() && (player.isCreative() || !powered)) {
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if (player.isCreative()) {
					if (player.getItemInHand(hand).getItem() == SCContent.TASER.get())
						player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					else
						player.setItemInHand(hand, new ItemStack(SCContent.TASER.get(), 1));

					return InteractionResult.SUCCESS_SERVER;
				}
				else if (player.getInventory().contains(oneRedstone)) {
					int redstoneSlot = player.getInventory().findSlotMatchingCraftingIngredient(oneRedstone.getItemHolder(), ItemStack.EMPTY);
					ItemStack redstoneStack;

					if (redstoneSlot == -1) {
						if (player.getOffhandItem().getItem() == Items.REDSTONE)
							redstoneStack = player.getOffhandItem();
						else
							return InteractionResult.PASS;
					}
					else
						redstoneStack = player.getInventory().getItem(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);

					if (redstoneSlot == -1)
						player.setItemSlot(EquipmentSlot.OFFHAND, redstoneStack);
					else
						player.getInventory().setItem(redstoneSlot, redstoneStack);

					player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					return InteractionResult.SUCCESS_SERVER;
				}

				return InteractionResult.PASS;
			}

			int range = 11;
			Vec3 startVec = player.getEyePosition(1.0F);
			Vec3 lookVec = player.getViewVector(1.0F).scale(range);
			Vec3 endVec = startVec.add(lookVec);
			AABB boundingBox = player.getBoundingBox().expandTowards(lookVec).inflate(1, 1, 1);
			EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(player, startVec, endVec, boundingBox, LivingEntity.class::isInstance, range * range);

			level.playSound(player, player.blockPosition(), SCSounds.TASERFIRED.event, SoundSource.PLAYERS, 1.0F, 1.0F);
			level.gameEvent(player, GameEvent.PROJECTILE_SHOOT, player.blockPosition());

			if (hitResult != null) {
				LivingEntity entity = (LivingEntity) hitResult.getEntity();
				double damage = powered ? ConfigHandler.SERVER.poweredTaserDamage.get() : ConfigHandler.SERVER.taserDamage.get();

				if (level instanceof ServerLevel serverLevel && (damage == 0.0D || entity.hurtServer(serverLevel, CustomDamageSources.taser(player), (float) damage)) && !entity.isBlocking()) {
					List<MobEffectInstance> effects = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).customEffects();

					effects.forEach(entity::addEffect);
				}
			}

			if (!player.isCreative()) {
				if (powered) {
					ItemStack taser = new ItemStack(SCContent.TASER.get(), 1);

					taser.hurtAndBreak(150, player, hand.asEquipmentSlot());
					player.setItemInHand(hand, taser);
				}
				else
					stack.hurtAndBreak(150, player, hand.asEquipmentSlot());
			}

			return InteractionResult.CONSUME;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (stack.getDamageValue() >= 1)
			stack.setDamageValue(stack.getDamageValue() - 1);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}

	public static PotionContents getDefaultEffects() {
		PotionContents effects = new PotionContents(Potions.WATER);

		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.NAUSEA, 200, 1));
		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.SLOWNESS, 200, 1));
		return effects;
	}

	public static PotionContents getDefaultPoweredEffects() {
		PotionContents effects = new PotionContents(Potions.WATER);

		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.WEAKNESS, 400, 4));
		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.NAUSEA, 400, 4));
		effects = effects.withEffectAdded(new MobEffectInstance(MobEffects.SLOWNESS, 400, 4));
		return effects;
	}
}
