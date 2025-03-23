package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class IMSBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private IntOption range = new IntOption("range", 15, 1, 30, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int bombsRemaining = 4;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	public IMSBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.IMS_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide && updateBombCount) {
			int mineCount = state.getValue(IMSBlock.MINES);

			if (mineCount != bombsRemaining)
				level.setBlockAndUpdate(pos, state.setValue(IMSBlock.MINES, bombsRemaining));

			if (bombsRemaining < 4) {
				BlockEntity be = level.getBlockEntity(pos.below());

				if (be != null) {
					IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, be.getBlockState(), be, Direction.UP);

					if (handler != null) {
						for (int i = 0; i < handler.getSlots(); i++) {
							if (handler.getStackInSlot(i).getItem() == SCContent.BOUNCING_BETTY.get().asItem()) {
								handler.extractItem(i, 1, false);
								bombsRemaining++;
								return;
							}
						}
					}
				}
			}
			else
				updateBombCount = false;
		}

		if (!isDisabled() && attackTime-- == 0) {
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
			TargetingMode mode = getTargetingMode();

			level.getEntitiesOfClass(LivingEntity.class, area, e -> (e instanceof Player || e instanceof Monster) && mode.canAttackEntity(e, this, respectInvisibility::isConsideredInvisible)).stream().findFirst().ifPresent(e -> {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = e.getX() - pos.getX();
				double accelerationY = e.getBoundingBox().minY + e.getBbHeight() / 2.0F - pos.getY() - launchHeight;
				double accelerationZ = e.getZ() - pos.getZ();

				if (!level.isClientSide) {
					level.addFreshEntity(new IMSBomb(level, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, new Vec3(accelerationX, accelerationY, accelerationZ), launchHeight, this));
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
				}

				bombsRemaining--;
				updateBombCount = true;
				setChanged();
			});
		}
	}

	/**
	 * Returns the amount of blocks the {@link IMSBomb} should move up before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for (height = 1; height <= 9; height++) {
			BlockState state = getLevel().getBlockState(getBlockPos().above(height));

			if ((state != null && !state.isAir()))
				break;
		}

		return height;
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		tag.putInt("bombsRemaining", bombsRemaining);
		tag.putBoolean("updateBombCount", updateBombCount);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		bombsRemaining = tag.getIntOr("bombsRemaining", 4);
		updateBombCount = tag.getBooleanOr("updateBombCount", false);

		if (tag.contains("targetingOption"))
			targetingMode.setValue(TargetingMode.values()[tag.getIntOr("targetingOption", 1)]);
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
		setChanged();
	}

	public TargetingMode getTargetingMode() {
		return targetingMode.get();
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
				range, disabled, ignoreOwner, targetingMode, respectInvisibility
		};
	}

	public int getAttackInterval() {
		return isModuleEnabled(ModuleType.SPEED) ? 40 : 80;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}
}
