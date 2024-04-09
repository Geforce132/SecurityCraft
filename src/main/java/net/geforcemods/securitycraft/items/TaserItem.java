package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TaserItem extends Item {
	private final boolean powered;

	public TaserItem(boolean isPowered) {
		powered = isPowered;
		setMaxDamage(151);
		setCreativeTab(SecurityCraft.TECHNICAL_TAB);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if ((tab == SecurityCraft.TECHNICAL_TAB || tab == CreativeTabs.SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.isItemDamaged()) {
			if (player.isSneaking() && (player.isCreative() || !powered)) {
				ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

				if (player.isCreative()) {
					if (player.getHeldItem(hand).getItem() == SCContent.taser)
						player.setHeldItem(hand, new ItemStack(SCContent.taserPowered, 1));
					else
						player.setHeldItem(hand, new ItemStack(SCContent.taser, 1));

					return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
				}
				else if (player.inventory.hasItemStack(oneRedstone)) {
					int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
					ItemStack redstoneStack;

					if (redstoneSlot == -1) {
						if (player.getHeldItemOffhand().getItem() == Items.REDSTONE)
							redstoneStack = player.getHeldItemOffhand();
						else
							return ActionResult.newResult(EnumActionResult.PASS, stack);
					}
					else
						redstoneStack = player.inventory.getStackInSlot(redstoneSlot);

					redstoneStack.setCount(redstoneStack.getCount() - 1);

					if (redstoneSlot == -1)
						player.inventory.offHandInventory.set(0, redstoneStack);
					else
						player.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);

					player.setHeldItem(hand, new ItemStack(SCContent.taserPowered, 1));
					return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
				}

				return ActionResult.newResult(EnumActionResult.PASS, stack);
			}

			int range = 11;
			Vec3d startVec = new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ);
			Vec3d lookVec = player.getLook(1.0F).scale(range);
			Vec3d endVec = startVec.add(lookVec);
			AxisAlignedBB boundingBox = player.getEntityBoundingBox().expand(lookVec.x, lookVec.y, lookVec.z).grow(1, 1, 1);
			RayTraceResult entityRayTraceResult = rayTraceEntities(player, startVec, endVec, boundingBox, EntityLivingBase.class::isInstance, range * range);

			world.playSound(player, player.getPosition(), SCSounds.TASERFIRED.event, SoundCategory.PLAYERS, 1.0F, 1.0F);

			if (entityRayTraceResult != null) {
				EntityLivingBase entity = (EntityLivingBase) entityRayTraceResult.entityHit;
				double damage = powered ? ConfigHandler.poweredTaserDamage : ConfigHandler.taserDamage;

				if ((damage == 0.0D || entity.attackEntityFrom(CustomDamageSources.taser(player), (float) damage)) && !entity.isActiveItemStackBlocking()) {
					List<Supplier<PotionEffect>> effects = powered ? ConfigHandler.POWERED_TASER_EFFECTS : ConfigHandler.TASER_EFFECTS;

					effects.forEach(effect -> entity.addPotionEffect(effect.get()));
				}
			}

			if (!player.isCreative()) {
				if (powered) {
					ItemStack taser = new ItemStack(SCContent.taser, 1);

					taser.damageItem(150, player);
					player.setHeldItem(hand, taser);
				}
				else
					stack.damageItem(150, player);
			}

			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	private static RayTraceResult rayTraceEntities(Entity shooter, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<? super Entity> filter, double dist) {
		World world = shooter.world;
		double distance = dist;
		Entity rayTracedEntity = null;
		Vec3d hitVec = null;

		for (Entity entity : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter::test)) {
			AxisAlignedBB boxToCheck = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
			Optional<Vec3d> optional = rayTrace(boxToCheck, startVec, endVec);

			if (boxToCheck.contains(startVec)) {
				if (distance >= 0.0D) {
					rayTracedEntity = entity;
					hitVec = optional.orElse(startVec);
					distance = 0.0D;
				}
			}
			else if (optional.isPresent()) {
				Vec3d vector = optional.get();
				double sqDist = startVec.squareDistanceTo(vector);

				if (sqDist < distance || distance == 0.0D) {
					if (entity.getLowestRidingEntity() == shooter.getLowestRidingEntity() && !entity.canRiderInteract()) {
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

		return rayTracedEntity == null ? null : new RayTraceResult(rayTracedEntity, hitVec);
	}

	private static Optional<Vec3d> rayTrace(AxisAlignedBB aabb, Vec3d startVec, Vec3d endVec) {
		double[] adouble = {
				1.0D
		};
		double d0 = endVec.x - startVec.x;
		double d1 = endVec.y - startVec.y;
		double d2 = endVec.z - startVec.z;
		EnumFacing enumFacing = func_197741_a(aabb, startVec, adouble, null, d0, d1, d2);

		if (enumFacing == null)
			return Optional.empty();
		else {
			double d3 = adouble[0];
			return Optional.of(startVec.add(d3 * d0, d3 * d1, d3 * d2));
		}
	}

	//no clue.
	private static EnumFacing func_197741_a(AxisAlignedBB aabb, Vec3d p_197741_1_, double[] p_197741_2_, @Nullable EnumFacing facing, double p_197741_4_, double p_197741_6_, double p_197741_8_) {
		if (p_197741_4_ > 1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_4_, p_197741_6_, p_197741_8_, aabb.minX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, EnumFacing.WEST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
		else if (p_197741_4_ < -1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_4_, p_197741_6_, p_197741_8_, aabb.maxX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, EnumFacing.EAST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);

		if (p_197741_6_ > 1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_6_, p_197741_8_, p_197741_4_, aabb.minY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, EnumFacing.DOWN, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
		else if (p_197741_6_ < -1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_6_, p_197741_8_, p_197741_4_, aabb.maxY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, EnumFacing.UP, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);

		if (p_197741_8_ > 1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_8_, p_197741_4_, p_197741_6_, aabb.minZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, EnumFacing.NORTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
		else if (p_197741_8_ < -1.0E-7D)
			facing = func_197740_a(p_197741_2_, facing, p_197741_8_, p_197741_4_, p_197741_6_, aabb.maxZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, EnumFacing.SOUTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);

		return facing;
	}

	private static EnumFacing func_197740_a(double[] p_197740_0_, @Nullable EnumFacing p_197740_1_, double p_197740_2_, double p_197740_4_, double p_197740_6_, double p_197740_8_, double p_197740_10_, double p_197740_12_, double p_197740_14_, double p_197740_16_, EnumFacing p_197740_18_, double p_197740_19_, double p_197740_21_, double p_197740_23_) {
		double d0 = (p_197740_8_ - p_197740_19_) / p_197740_2_;
		double d1 = p_197740_21_ + d0 * p_197740_4_;
		double d2 = p_197740_23_ + d0 * p_197740_6_;

		if (0.0D < d0 && d0 < p_197740_0_[0] && p_197740_10_ - 1.0E-7D < d1 && d1 < p_197740_12_ + 1.0E-7D && p_197740_14_ - 1.0E-7D < d2 && d2 < p_197740_16_ + 1.0E-7D) {
			p_197740_0_[0] = d0;
			return p_197740_18_;
		}
		else
			return p_197740_1_;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isSelected) {
		if (!world.isRemote && stack.getItemDamage() >= 1)
			stack.setItemDamage(stack.getItemDamage() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
