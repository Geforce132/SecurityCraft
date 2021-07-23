package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class IMSTileEntity extends CustomizableTileEntity implements MenuProvider {

	private IntOption range = new IntOption(this::getBlockPos, "range", 12, 1, 30, 1, true);
	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;
	/** The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players, MOBS = hostile mobs.**/
	private IMSTargetingMode targetingMode = IMSTargetingMode.PLAYERS_AND_MOBS;
	private boolean updateBombCount = false;
	private int attackTime = getAttackInterval();

	public IMSTileEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeIms, pos, state);
	}

	@Override
	public void tick(){
		super.tick();

		if(!level.isClientSide && updateBombCount){
			int mineCount = getBlockState().getValue(IMSBlock.MINES);

			if(!(mineCount - 1 < 0 || mineCount > 4))
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(IMSBlock.MINES, mineCount - 1));

			updateBombCount = false;
		}

		if(attackTime-- == 0)
		{
			attackTime = getAttackInterval();
			launchMine();
		}
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		if(bombsRemaining > 0){
			AABB area = new AABB(worldPosition).inflate(range.get());
			LivingEntity target = null;

			if(targetingMode == IMSTargetingMode.MOBS || targetingMode == IMSTargetingMode.PLAYERS_AND_MOBS)
			{
				List<Monster> mobs = level.<Monster>getEntitiesOfClass(Monster.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if(!mobs.isEmpty())
					target = mobs.get(0);
			}

			if(target == null && (targetingMode == IMSTargetingMode.PLAYERS  || targetingMode == IMSTargetingMode.PLAYERS_AND_MOBS))
			{
				List<Player> players = level.<Player>getEntitiesOfClass(Player.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if(!players.isEmpty())
					target = players.get(0);
			}

			if (target != null) {
				double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 0.84375D : 0.0D; //0.84375 is the offset towards the bomb's position in the model
				double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 0.84375D : 0.0D;
				int launchHeight = getLaunchHeight();
				double accelerationX = target.getX() - worldPosition.getX();
				double accelerationY = target.getBoundingBox().minY + target.getBbHeight() / 2.0F - worldPosition.getY() - launchHeight;
				double accelerationZ = target.getZ() - worldPosition.getZ();

				level.addFreshEntity(new IMSBombEntity(level, worldPosition.getX() + addToX, worldPosition.getY(), worldPosition.getZ() + addToZ, accelerationX, accelerationY, accelerationZ, launchHeight, this));

				if (!level.isClientSide)
					level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				updateBombCount = true;
			}
		}
	}

	public boolean canAttackEntity(LivingEntity entity)
	{
		return entity != null
				&& (!(entity instanceof Player) || !getOwner().isOwner((Player)entity) && !PlayerUtils.isPlayerMountedOnCamera(entity) && !((Player)entity).isCreative() && !((Player)entity).isSpectator()) //PlayerEntity checks
				&& !(ModuleUtils.isAllowed(this, entity)); //checks for all entities
	}

	/**
	 * Returns the amount of blocks the {@link IMSBombEntity} should move up before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for(height = 1; height <= 9; height++)
		{
			BlockState state = getLevel().getBlockState(getBlockPos().above(height));

			if(state == null || state.isAir())
				continue;
			else
				break;
		}

		return height;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag save(CompoundTag tag){
		super.save(tag);

		tag.putInt("bombsRemaining", bombsRemaining);
		tag.putInt("targetingOption", targetingMode.ordinal());
		tag.putBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void load(CompoundTag tag){
		super.load(tag);

		bombsRemaining = tag.getInt("bombsRemaining");
		targetingMode = IMSTargetingMode.values()[tag.getInt("targetingOption")];
		updateBombCount = tag.getBoolean("updateBombCount");
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
	}

	public IMSTargetingMode getTargetingMode() {
		return targetingMode;
	}

	public void setTargetingMode(IMSTargetingMode targetingOption) {
		this.targetingMode = targetingOption;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.SPEED};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{range};
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
	{
		return new GenericTEContainer(SCContent.cTypeIMS, windowId, level, worldPosition);
	}

	@Override
	public Component getDisplayName()
	{
		return new TranslatableComponent(SCContent.IMS.get().getDescriptionId());
	}

	public int getAttackInterval()
	{
		return hasModule(ModuleType.SPEED) ? 40 : 80;
	}

	public static enum IMSTargetingMode {
		PLAYERS, PLAYERS_AND_MOBS, MOBS;
	}
}
