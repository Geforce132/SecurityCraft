package net.geforcemods.securitycraft.items;

import java.util.Optional;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class TaserItem extends Item {

	public boolean powered;

	public TaserItem(Item.Properties properties, boolean isPowered){
		super(properties);

		powered = isPowered;
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
	{
		if((group == SecurityCraft.groupSCTechnical || group == ItemGroup.TAB_SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.TASER.get() && newStack.getItem() == SCContent.TASER_POWERED.get()) || (oldStack.getItem() == SCContent.TASER_POWERED.get() && newStack.getItem() == SCContent.TASER.get()));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);

		if(!stack.isDamaged()){
			if(player.isCrouching() && (player.isCreative() || !powered))
			{
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if(player.isCreative())
				{
					if(player.getItemInHand(hand).getItem() == SCContent.TASER.get())
						setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					else
						setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER.get(), 1));

					return ActionResult.success(stack);
				}
				else if(player.inventory.contains(oneRedstone))
				{
					int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
					ItemStack redstoneStack = player.inventory.getItem(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);
					player.inventory.setItem(redstoneSlot, redstoneStack);
					setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER_POWERED.get(), 1));
					return ActionResult.success(stack);
				}

				return ActionResult.pass(stack);
			}

			int range = 11;
			Vector3d startVec = player.getEyePosition(1.0F);
			Vector3d lookVec = player.getViewVector(1.0F).scale(range);
			Vector3d endVec = startVec.add(lookVec);
			AxisAlignedBB boundingBox = player.getBoundingBox().expandTowards(lookVec).inflate(1, 1, 1);
			EntityRayTraceResult entityRayTraceResult = rayTraceEntities(player, startVec, endVec, boundingBox, s -> s instanceof LivingEntity, range * range);

			if (!world.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(player.getX(), player.getY(), player.getZ(), SCSounds.TASERFIRED.path, 1.0F, "players"));

			if (entityRayTraceResult != null)
			{
				LivingEntity entity = (LivingEntity)entityRayTraceResult.getEntity();

				if(!entity.isBlocking() && entity.hurt(CustomDamageSources.TASER, powered ? 2.0F : 1.0F))
				{
					int strength = powered ? 4 : 1;
					int length = powered ? 400 : 200;

					entity.addEffect(new EffectInstance(Effects.WEAKNESS, length, strength));
					entity.addEffect(new EffectInstance(Effects.CONFUSION, length, strength));
					entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, length, strength));
				}
			}

			if(!player.isCreative())
			{
				if(powered)
				{
					ItemStack taser = new ItemStack(SCContent.TASER.get(), 1);

					taser.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
					setSlotBasedOnHand(player, hand, taser);
				}
				else
					stack.hurtAndBreak(150, player, p -> p.broadcastBreakEvent(hand));
			}

			return ActionResult.consume(stack);
		}

		return ActionResult.pass(stack);
	}

	//Copied from ProjectileHelper to get rid of the @OnlyIn(Dist.CLIENT) annotation
	private static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double dist)
	{
		World world = shooter.level;
		double distance = dist;
		Entity rayTracedEntity = null;
		Vector3d hitVec = null;

		for(Entity entity : world.getEntities(shooter, boundingBox, filter))
		{
			AxisAlignedBB boxToCheck = entity.getBoundingBox().inflate(entity.getPickRadius());
			Optional<Vector3d> optional = boxToCheck.clip(startVec, endVec);

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
				Vector3d vector = optional.get();
				double sqDist = startVec.distanceToSqr(vector);

				if(sqDist < distance || distance == 0.0D)
				{
					if(entity.getRootVehicle() == shooter.getRootVehicle() && !entity.canRiderInteract())
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

	private void setSlotBasedOnHand(PlayerEntity player, Hand hand, ItemStack taser)
	{
		if(hand == Hand.MAIN_HAND)
			player.setItemSlot(EquipmentSlotType.MAINHAND, taser);
		else
			player.setItemSlot(EquipmentSlotType.OFFHAND, taser);
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isClientSide)
			if(par1ItemStack.getDamageValue() >= 1)
				par1ItemStack.setDamageValue(par1ItemStack.getDamageValue() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
