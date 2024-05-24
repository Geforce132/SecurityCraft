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
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;

public class IMSBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 15, 1, 30, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int bombsRemaining = 4;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	public IMSBlockEntity() {
		super(SCContent.IMS_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (!level.isClientSide && updateBombCount) {
			int mineCount = getBlockState().getValue(IMSBlock.MINES);

			if (mineCount != bombsRemaining)
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(IMSBlock.MINES, bombsRemaining));

			if (bombsRemaining < 4) {
				TileEntity be = level.getBlockEntity(worldPosition.below());

				if (be != null) {
					be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
						for (int i = 0; i < handler.getSlots(); i++) {
							if (handler.getStackInSlot(i).getItem() == SCContent.BOUNCING_BETTY.get().asItem()) {
								handler.extractItem(i, 1, false);
								bombsRemaining++;
								return;
							}
						}
					});
				}
			}
			else
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
		if (bombsRemaining > 0) {
			AxisAlignedBB area = new AxisAlignedBB(worldPosition).inflate(range.get());
			TargetingMode mode = getTargetingMode();

			level.getEntitiesOfClass(LivingEntity.class, area, e -> (e instanceof PlayerEntity || e instanceof MonsterEntity) && mode.canAttackEntity(e, this, respectInvisibility::isConsideredInvisible)).stream().findFirst().ifPresent(e -> {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = e.getX() - worldPosition.getX();
				double accelerationY = e.getBoundingBox().minY + e.getBbHeight() / 2.0F - worldPosition.getY() - launchHeight;
				double accelerationZ = e.getZ() - worldPosition.getZ();

				level.addFreshEntity(new IMSBomb(level, worldPosition.getX() + addToX, worldPosition.getY(), worldPosition.getZ() + addToZ, accelerationX, accelerationY, accelerationZ, launchHeight, this));

				if (!level.isClientSide)
					level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				updateBombCount = true;
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

			if (state != null && !state.isAir(getLevel(), getBlockPos()))
				break;
		}

		return height;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		tag.putInt("bombsRemaining", bombsRemaining);
		tag.putBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		bombsRemaining = tag.getInt("bombsRemaining");
		updateBombCount = tag.getBoolean("updateBombCount");

		if (tag.contains("targetingOption"))
			targetingMode.setValue(TargetingMode.values()[tag.getInt("targetingOption")]);
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
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
