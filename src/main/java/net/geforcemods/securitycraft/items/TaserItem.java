package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class TaserItem extends Item {

	public boolean powered;

	public TaserItem(Item.Properties properties, boolean isPowered){
		super(properties);

		powered = isPowered;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
	{
		if((group == SecurityCraft.groupSCTechnical || group == CreativeModeTab.TAB_SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.TASER.get() && newStack.getItem() == SCContent.TASER_POWERED.get()) || (oldStack.getItem() == SCContent.TASER_POWERED.get() && newStack.getItem() == SCContent.TASER.get()));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);

		if(!stack.isDamaged()){
			if(player.isCrouching() && (player.isCreative() || !powered))
			{
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if(player.isCreative())
				{
					if(player.getItemInHand(hand).getItem() == SCContent.TASER.get())
						player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					else
						player.setItemInHand(hand, new ItemStack(SCContent.TASER.get(), 1));

					return InteractionResultHolder.success(stack);
				}
				else if(player.getInventory().contains(oneRedstone))
				{
					int redstoneSlot = player.getInventory().findSlotMatchingUnusedItem(oneRedstone);
					ItemStack redstoneStack;

					if(redstoneSlot == -1)
					{
						if(player.getOffhandItem().getItem() == Items.REDSTONE)
							redstoneStack = player.getOffhandItem();
						else
							return InteractionResultHolder.pass(stack);
					}
					else
						redstoneStack = player.getInventory().getItem(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);

					if(redstoneSlot == -1)
						player.getInventory().offhand.set(0, redstoneStack);
					else
						player.getInventory().setItem(redstoneSlot, redstoneStack);

					player.setItemInHand(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					return InteractionResultHolder.success(stack);
				}

				return InteractionResultHolder.pass(stack);
			}

			int range = 11;
			Vec3 startVec = player.getEyePosition(1.0F);
			Vec3 lookVec = player.getViewVector(1.0F).scale(range);
			Vec3 endVec = startVec.add(lookVec);
			AABB boundingBox = player.getBoundingBox().expandTowards(lookVec).inflate(1, 1, 1);
			EntityHitResult entityRayTraceResult = ProjectileUtil.getEntityHitResult(player, startVec, endVec, boundingBox, s -> s instanceof LivingEntity, range * range);

			if (!world.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(player.getX(), player.getY(), player.getZ(), SCSounds.TASERFIRED.path, 1.0F, "players"));

			if (entityRayTraceResult != null)
			{
				LivingEntity entity = (LivingEntity)entityRayTraceResult.getEntity();

				if(!entity.isBlocking() && entity.hurt(CustomDamageSources.TASER, powered ? 2.0F : 1.0F))
				{
					int strength = powered ? 4 : 1;
					int length = powered ? 400 : 200;

					entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, length, strength));
					entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, length, strength));
					entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, length, strength));
				}
			}

			if(!player.isCreative())
			{
				if(powered)
				{
					ItemStack taser = new ItemStack(SCContent.TASER.get(), 1);

					taser.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
					player.setItemInHand(hand, taser);
				}
				else
					stack.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
			}

			return InteractionResultHolder.consume(stack);
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isClientSide && stack.getDamageValue() >= 1)
			stack.setDamageValue(stack.getDamageValue() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
