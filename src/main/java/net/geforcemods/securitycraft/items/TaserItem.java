package net.geforcemods.securitycraft.items;

import java.util.Optional;
import java.util.function.Predicate;

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
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TaserItem extends Item {

	public boolean powered;

	public TaserItem(Item.Properties properties, boolean isPowered){
		super(properties);

		powered = isPowered;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if((group == SecurityCraft.groupSCTechnical || group == ItemGroup.SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.TASER.get() && newStack.getItem() == SCContent.TASER_POWERED.get()) || (oldStack.getItem() == SCContent.TASER_POWERED.get() && newStack.getItem() == SCContent.TASER.get()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(!stack.isDamaged()){
			if(player.isCrouching() && (player.isCreative() || !powered))
			{
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if(player.isCreative())
				{
					if(player.getHeldItem(hand).getItem() == SCContent.TASER.get())
						player.setHeldItem(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					else
						player.setHeldItem(hand, new ItemStack(SCContent.TASER.get(), 1));

					return ActionResult.resultSuccess(stack);
				}
				else if(player.inventory.hasItemStack(oneRedstone))
				{
					int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
					ItemStack redstoneStack;

					if(redstoneSlot == -1)
					{
						if(player.getHeldItemOffhand().getItem() == Items.REDSTONE)
							redstoneStack = player.getHeldItemOffhand();
						else
							return ActionResult.resultPass(stack);
					}
					else
						redstoneStack = player.inventory.getStackInSlot(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);

					if(redstoneSlot == -1)
						player.inventory.offHandInventory.set(0, redstoneStack);
					else
						player.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);

					player.setHeldItem(hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					return ActionResult.resultSuccess(stack);
				}

				return ActionResult.resultPass(stack);
			}

			int range = 11;
			Vec3d startVec = player.getEyePosition(1.0F);
			Vec3d lookVec = player.getLook(1.0F).scale(range);
			Vec3d endVec = startVec.add(lookVec);
			AxisAlignedBB boundingBox = player.getBoundingBox().expand(lookVec).grow(1, 1, 1);
			EntityRayTraceResult entityRayTraceResult = rayTraceEntities(player, startVec, endVec, boundingBox, s -> s instanceof LivingEntity, range * range);

			world.playSound(player, player.getPosition(), SCSounds.TASERFIRED.event, SoundCategory.PLAYERS, 1.0F, 1.0F);

			if (entityRayTraceResult != null)
			{
				LivingEntity entity = (LivingEntity)entityRayTraceResult.getEntity();

				if(!entity.isActiveItemStackBlocking() && entity.attackEntityFrom(CustomDamageSources.TASER, powered ? 2.0F : 1.0F))
				{
					int strength = powered ? 4 : 1;
					int length = powered ? 400 : 200;

					entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, length, strength));
					entity.addPotionEffect(new EffectInstance(Effects.NAUSEA, length, strength));
					entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, length, strength));
				}
			}

			if(!player.isCreative())
			{
				if(powered)
				{
					ItemStack taser = new ItemStack(SCContent.TASER.get(), 1);

					taser.damageItem(150, player, p -> p.sendBreakAnimation(hand));
					player.setHeldItem(hand, taser);
				}
				else
					stack.damageItem(150, player, p -> p.sendBreakAnimation(hand));
			}

			return ActionResult.resultConsume(stack);
		}

		return ActionResult.resultPass(stack);
	}

	//copied from ProjectileHelper#rayTraceEntities because that one's only available on the client
	private static EntityRayTraceResult rayTraceEntities(Entity shooter, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double dist)
	{
		World world = shooter.world;
		double distance = dist;
		Entity rayTracedEntity = null;
		Vec3d hitVec = null;

		for(Entity entity : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter))
		{
			AxisAlignedBB boxToCheck = entity.getBoundingBox().grow(entity.getCollisionBorderSize());
			Optional<Vec3d> optional = boxToCheck.rayTrace(startVec, endVec);

			if(boxToCheck.contains(startVec))
			{
				if(distance >= 0.0D)
				{
					rayTracedEntity = entity;
					hitVec = optional.orElse(startVec);
					distance = 0.0D;
				}
			}
			else if(optional.isPresent())
			{
				Vec3d vector = optional.get();
				double sqDist = startVec.squareDistanceTo(vector);

				if(sqDist < distance || distance == 0.0D)
				{
					if(entity.getLowestRidingEntity() == shooter.getLowestRidingEntity() && !entity.canRiderInteract())
					{
						if(distance == 0.0D)
						{
							rayTracedEntity = entity;
							hitVec = vector;
						}
					}
					else
					{
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
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isRemote && stack.getDamage() >= 1)
			stack.setDamage(stack.getDamage() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
