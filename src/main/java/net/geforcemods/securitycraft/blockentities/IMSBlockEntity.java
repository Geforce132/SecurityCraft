package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class IMSBlockEntity extends CustomizableBlockEntity implements ITickable {
	private OptionInt range = new OptionInt(this::getPos, "range", 15, 1, 30, 1, true);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;
	/**
	 * The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players, MOBS =
	 * hostile mobs.
	 **/
	private EnumIMSTargetingMode targetingMode = EnumIMSTargetingMode.PLAYERS_AND_MOBS;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	@Override
	public void update() {
		if (!world.isRemote && updateBombCount) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(IMSBlock.MINES, bombsRemaining));
			updateBombCount = false;
		}

		if (!isDisabled() && attackTime-- == 0) {
			attackTime = getAttackInterval();
			launchMine();
		}
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		if (bombsRemaining > 0 && !world.isRemote) {
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range.get(), range.get(), range.get());
			EntityLivingBase target = null;

			if (targetingMode == EnumIMSTargetingMode.MOBS || targetingMode == EnumIMSTargetingMode.PLAYERS_AND_MOBS) {
				List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if (!mobs.isEmpty())
					target = mobs.get(0);
			}

			if (target == null && (targetingMode == EnumIMSTargetingMode.PLAYERS || targetingMode == EnumIMSTargetingMode.PLAYERS_AND_MOBS)) {
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if (!players.isEmpty())
					target = players.get(0);
			}

			if (target != null) {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = target.posX - pos.getX();
				double accelerationY = target.getEntityBoundingBox().minY + target.height / 2.0F - pos.getY() - launchHeight;
				double accelerationZ = target.posZ - pos.getZ();

				world.spawnEntity(new IMSBomb(world, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, accelerationX, accelerationY, accelerationZ, launchHeight, this));
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				bombsRemaining--;
				updateBombCount = true;
			}
		}
	}

	public boolean canAttackEntity(EntityLivingBase entity) {
		return entity != null && (!(entity instanceof EntityPlayer) || !(isOwnedBy((EntityPlayer) entity) && ignoresOwner()) && !((EntityPlayer) entity).isCreative() && !((EntityPlayer) entity).isSpectator()) //PlayerEntity checks
				&& !isAllowed(entity); //checks for all entities
	}

	/**
	 * Returns the amount of blocks the {@link IMSBomb} should move up before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for (height = 1; height <= 9; height++) {
			BlockPos upPos = pos.up(height);
			IBlockState state = world.getBlockState(upPos);

			if (!state.getBlock().isAir(state, world, upPos))
				break;
		}

		return height;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("bombsRemaining", bombsRemaining);
		tag.setInteger("targetingOption", targetingMode.ordinal());
		tag.setBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bombsRemaining = tag.getInteger("bombsRemaining");
		targetingMode = EnumIMSTargetingMode.values()[tag.getInteger("targetingOption")];
		updateBombCount = tag.getBoolean("updateBombCount");
	}

	public int getBombsRemaining() {
		return bombsRemaining;
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
	}

	public EnumIMSTargetingMode getTargetingMode() {
		return targetingMode;
	}

	public void setTargetingMode(EnumIMSTargetingMode targetingMode) {
		this.targetingMode = targetingMode;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.SPEED
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, disabled, ignoreOwner
		};
	}

	public int getAttackInterval() {
		return isModuleEnabled(ModuleType.SPEED) ? 40 : 80;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public static enum EnumIMSTargetingMode {
		PLAYERS,
		PLAYERS_AND_MOBS,
		MOBS;
	}
}
