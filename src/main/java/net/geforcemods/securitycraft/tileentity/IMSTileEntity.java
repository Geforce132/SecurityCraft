package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IMSTileEntity extends CustomizableTileEntity implements INamedContainerProvider {

	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;
	/** The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players, MOBS = hostile mobs.**/
	private IMSTargetingMode targetingOption = IMSTargetingMode.PLAYERS_AND_MOBS;
	private boolean updateBombCount = false;

	public IMSTileEntity()
	{
		super(SCContent.teTypeIms);
	}

	@Override
	public void tick(){
		super.tick();

		if(!world.isRemote && updateBombCount){
			int mineCount = BlockUtils.getBlockProperty(world, pos, IMSBlock.MINES);

			if(!(mineCount - 1 < 0 || mineCount > 4))
				BlockUtils.setBlockProperty(world, pos, IMSBlock.MINES, BlockUtils.getBlockProperty(world, pos, IMSBlock.MINES) - 1);

			updateBombCount = false;
		}

		if(world.getGameTime() % 80L == 0L)
			launchMine();
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		boolean launchedMine = false;

		if(bombsRemaining > 0){
			double range = ConfigHandler.CONFIG.imsRange.get();

			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range, range, range);
			List<?> players = world.getEntitiesWithinAABB(PlayerEntity.class, area, e -> !EntityUtils.isInvisible(e));
			List<?> mobs = world.getEntitiesWithinAABB(MonsterEntity.class, area, e -> !EntityUtils.isInvisible(e));
			Iterator<?> playerIterator = players.iterator();
			Iterator<?> mobIterator = mobs.iterator();

			// Targets players and mobs
			while(targetingOption == IMSTargetingMode.PLAYERS_AND_MOBS && mobIterator.hasNext()){
				LivingEntity entity = (LivingEntity) mobIterator.next();
				int launchHeight = getLaunchHeight();

				if(PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;

				if(hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(entity.getName().getFormattedText().toLowerCase()))
					continue;

				double targetX = entity.getPosX() - (pos.getX() + 0.5D);
				double targetY = entity.getBoundingBox().minY + entity.getHeight() / 2.0F - (pos.getY() + 1.25D);
				double targetZ = entity.getPosZ() - (pos.getZ() + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(!world.isRemote)
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				launchedMine = true;
				updateBombCount = true;
				break;
			}

			// Targets only hostile mobs
			while(!launchedMine && targetingOption == IMSTargetingMode.MOBS && mobIterator.hasNext()){
				MonsterEntity entity = (MonsterEntity) mobIterator.next();
				int launchHeight = getLaunchHeight();

				if(hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(entity.getName().getFormattedText().toLowerCase()))
					continue;

				double targetX = entity.getPosX() - (pos.getX() + 0.5D);
				double targetY = entity.getBoundingBox().minY + entity.getHeight() / 2.0F - (pos.getY() + 1.25D);
				double targetZ = entity.getPosZ() - (pos.getZ() + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(!world.isRemote)
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				launchedMine = true;
				updateBombCount = true;
				break;
			}

			// Targets only other players
			while(!launchedMine && targetingOption == IMSTargetingMode.PLAYERS && playerIterator.hasNext()){
				PlayerEntity entity = (PlayerEntity) playerIterator.next();
				int launchHeight = getLaunchHeight();

				if((entity != null && getOwner().isOwner((entity))) || PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;
				if(WorldUtils.isPathObstructed(entity, world, pos.getX() + 0.5D, pos.getY() + (((launchHeight - 1) / 3) + 0.5D), pos.getZ() + 0.5D, entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ()))
					continue;
				if(hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(entity.getName().getFormattedText()))
					continue;

				double targetX = entity.getPosX() - (pos.getX() + 0.5D);
				double targetY = entity.getBoundingBox().minY + entity.getHeight() / 2.0F - (pos.getY() + 1.25D);
				double targetZ = entity.getPosZ() - (pos.getZ() + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(!world.isRemote)
					world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				updateBombCount = true;
				break;
			}
		}
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(PlayerEntity target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		world.addEntity(new IMSBombEntity(world, target, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, x, y, z, launchHeight));
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(LivingEntity target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		world.addEntity(new IMSBombEntity(world, target, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, x, y, z, launchHeight));
	}

	/**
	 * Returns the amount of ticks the {@link IMSBombEntity} should float in the air before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for(height = 1; height <= 9; height++)
		{
			BlockState state = getWorld().getBlockState(getPos().up(height));

			if(state == null || state.isAir(getWorld(), getPos()))
				continue;
			else
				break;
		}

		return height * 3;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag){
		super.write(tag);

		tag.putInt("bombsRemaining", bombsRemaining);
		tag.putInt("targetingOption", targetingOption.modeIndex);
		tag.putBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(CompoundNBT tag){
		super.read(tag);

		if (tag.contains("bombsRemaining"))
			bombsRemaining = tag.getInt("bombsRemaining");

		if (tag.contains("targetingOption"))
			targetingOption = IMSTargetingMode.values()[tag.getInt("targetingOption")];

		if (tag.contains("updateBombCount"))
			updateBombCount = tag.getBoolean("updateBombCount");
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
	}

	public IMSTargetingMode getTargetingOption() {
		return targetingOption;
	}

	public void setTargetingOption(IMSTargetingMode targetingOption) {
		this.targetingOption = targetingOption;
	}

	@Override
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{CustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeIMS, windowId, world, pos);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.IMS.get().getTranslationKey());
	}

	public static enum IMSTargetingMode {

		PLAYERS(0),
		PLAYERS_AND_MOBS(1),
		MOBS(2);

		public final int modeIndex;

		private IMSTargetingMode(int index){
			modeIndex = index;
		}
	}
}
