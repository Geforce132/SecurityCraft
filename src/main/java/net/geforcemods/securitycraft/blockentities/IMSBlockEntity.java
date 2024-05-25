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
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class IMSBlockEntity extends CustomizableBlockEntity implements ITickable {
	private IntOption range = new IntOption(this::getPos, "range", 15, 1, 30, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int bombsRemaining = 4;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	@Override
	public void update() {
		if (!world.isRemote && updateBombCount) {
			IBlockState state = world.getBlockState(pos);
			int mineCount = state.getValue(IMSBlock.MINES);

			if (mineCount != bombsRemaining)
				world.setBlockState(pos, state.withProperty(IMSBlock.MINES, bombsRemaining));

			if (bombsRemaining < 4) {
				TileEntity be = world.getTileEntity(pos.down());

				if (be != null) {
					IItemHandler handler = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

					if (handler != null) {
						for (int i = 0; i < handler.getSlots(); i++) {
							if (handler.getStackInSlot(i).getItem() == Item.getItemFromBlock(SCContent.bouncingBetty)) {
								handler.extractItem(i, 1, false);
								bombsRemaining++;
								break;
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
			launchMine();
		}
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		if (bombsRemaining > 0 && !world.isRemote) {
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range.get(), range.get(), range.get());
			TargetingMode mode = getTargetingMode();

			world.getEntitiesWithinAABB(EntityLivingBase.class, area, e -> (e instanceof EntityPlayer || e instanceof EntityMob) && mode.canAttackEntity(e, this, respectInvisibility::isConsideredInvisible)).stream().findFirst().ifPresent(e -> {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = e.posX - pos.getX();
				double accelerationY = e.getEntityBoundingBox().minY + e.height / 2.0F - pos.getY() - launchHeight;
				double accelerationZ = e.posZ - pos.getZ();

				world.spawnEntity(new IMSBomb(world, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, accelerationX, accelerationY, accelerationZ, launchHeight, this));
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
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
		tag.setBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bombsRemaining = tag.getInteger("bombsRemaining");
		updateBombCount = tag.getBoolean("updateBombCount");

		if (tag.hasKey("targetingOption"))
			targetingMode.setValue(TargetingMode.values()[tag.getInteger("targetingOption")]);
	}

	public int getBombsRemaining() {
		return bombsRemaining;
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
