package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class IMSBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 12, 1, 30, 1, true);
	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;
	/**
	 * The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players,
	 * MOBS = hostile mobs.
	 **/
	private IMSTargetingMode targetingMode = IMSTargetingMode.PLAYERS_AND_MOBS;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	public IMSBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.IMS_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide && updateBombCount) {
			int mineCount = state.getValue(IMSBlock.MINES);

			if (!(mineCount - 1 < 0 || mineCount > 4))
				level.setBlockAndUpdate(pos, state.setValue(IMSBlock.MINES, mineCount - 1));

			updateBombCount = false;
		}

		if (attackTime-- == 0) {
			attackTime = getAttackInterval();
			launchMine(level, pos);
		}
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine(Level level, BlockPos pos) {
		if (bombsRemaining > 0) {
			AABB area = new AABB(pos).inflate(range.get());
			LivingEntity target = null;

			if (targetingMode == IMSTargetingMode.MOBS || targetingMode == IMSTargetingMode.PLAYERS_AND_MOBS) {
				List<Monster> mobs = level.getEntitiesOfClass(Monster.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if (!mobs.isEmpty())
					target = mobs.get(0);
			}

			if (target == null && (targetingMode == IMSTargetingMode.PLAYERS || targetingMode == IMSTargetingMode.PLAYERS_AND_MOBS)) {
				List<Player> players = level.getEntitiesOfClass(Player.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if (!players.isEmpty())
					target = players.get(0);
			}

			if (target != null) {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = target.getX() - pos.getX();
				double accelerationY = target.getBoundingBox().minY + target.getBbHeight() / 2.0F - pos.getY() - launchHeight;
				double accelerationZ = target.getZ() - pos.getZ();

				level.addFreshEntity(new IMSBomb(level, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, accelerationX, accelerationY, accelerationZ, launchHeight, this));

				if (!level.isClientSide)
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				updateBombCount = true;
				setChanged();
			}
		}
	}

	public boolean canAttackEntity(LivingEntity entity) {
		return entity != null && (!(entity instanceof Player player) || !getOwner().isOwner(player) && !player.isCreative() && !player.isSpectator()) //Player checks
				&& !(ModuleUtils.isAllowed(this, entity)); //checks for all entities
	}

	/**
	 * Returns the amount of blocks the {@link IMSBomb} should move up before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for (height = 1; height <= 9; height++) {
			BlockState state = getLevel().getBlockState(getBlockPos().above(height));

			if (state == null || state.isAir())
				continue;
			else
				break;
		}

		return height;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);

		tag.putInt("bombsRemaining", bombsRemaining);
		tag.putInt("targetingOption", targetingMode.ordinal());
		tag.putBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		bombsRemaining = tag.getInt("bombsRemaining");
		targetingMode = IMSTargetingMode.values()[tag.getInt("targetingOption")];
		updateBombCount = tag.getBoolean("updateBombCount");
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
		setChanged();
	}

	public IMSTargetingMode getTargetingMode() {
		return targetingMode;
	}

	public void setTargetingMode(IMSTargetingMode targetingOption) {
		this.targetingMode = targetingOption;
		setChanged();
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
				range
		};
	}

	public int getAttackInterval() {
		return hasModule(ModuleType.SPEED) ? 40 : 80;
	}

	public static enum IMSTargetingMode {
		PLAYERS,
		PLAYERS_AND_MOBS,
		MOBS;
	}
}
