package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.mines.BlockIMS;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityIMS extends CustomizableSCTE {

	private OptionInt range = new OptionInt(this::getPos, "range", 12, 1, 30, 1, true);
	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;
	/** The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players, MOBS = hostile mobs.**/
	private EnumIMSTargetingMode targetingOption = EnumIMSTargetingMode.PLAYERS_AND_MOBS;
	private boolean updateBombCount = false;

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && updateBombCount){
			BlockUtils.setBlockProperty(world, pos, BlockIMS.MINES, BlockUtils.getBlockProperty(world, pos, BlockIMS.MINES) - 1);
			updateBombCount = false;
		}

		if(world.getTotalWorldTime() % 80L == 0L)
			launchMine();
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		if(bombsRemaining > 0){
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range.get(), range.get(), range.get());
			EntityLivingBase target = null;

			if((targetingOption == EnumIMSTargetingMode.MOBS || targetingOption == EnumIMSTargetingMode.PLAYERS_AND_MOBS))
			{
				List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if(!mobs.isEmpty())
					target = mobs.get(0);
			}

			if(target == null && (targetingOption == EnumIMSTargetingMode.PLAYERS  || targetingOption == EnumIMSTargetingMode.PLAYERS_AND_MOBS))
			{
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, area, e -> !EntityUtils.isInvisible(e) && canAttackEntity(e));

				if(!players.isEmpty())
					target = players.get(0);
			}

			if (target != null) {
				int launchHeight = getLaunchHeight();
				double targetX = target.posX - (pos.getX() + 0.5D);
				double targetY = target.getEntityBoundingBox().minY + target.height / 2.0F - (pos.getY() + 1.25D);
				double targetZ = target.posZ - (pos.getZ() + 0.5D);

				this.spawnMine(target, targetX, targetY, targetZ, launchHeight);

				if (!world.isRemote)
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				bombsRemaining--;
				updateBombCount = true;
			}
		}
	}

	public boolean canAttackEntity(EntityLivingBase entity)
	{
		return entity != null
				&& (!(entity instanceof EntityPlayer) || !getOwner().isOwner((EntityPlayer)entity) && !PlayerUtils.isPlayerMountedOnCamera(entity) && !((EntityPlayer)entity).isCreative()) //PlayerEntity checks
				&& !(hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(entity.getName().toLowerCase())); //checks for all entities
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityLivingBase target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		world.spawnEntity(new EntityIMSBomb(world, target, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, x, y, z, launchHeight));
	}

	/**
	 * Returns the amount of ticks the {@link EntityIMSBomb} should float in the air before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for(height = 1; height <= 9; height++)
			if(BlockUtils.getBlock(getWorld(), getPos().up(height)) == null || BlockUtils.getBlock(getWorld(), getPos().up(height)) == Blocks.AIR)
				continue;
			else
				break;

		return height * 3;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		tag.setInteger("bombsRemaining", bombsRemaining);
		tag.setInteger("targetingOption", targetingOption.ordinal());
		tag.setBoolean("updateBombCount", updateBombCount);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		bombsRemaining = tag.getInteger("bombsRemaining");
		targetingOption = EnumIMSTargetingMode.values()[tag.getInteger("targetingOption")];
		updateBombCount = tag.getBoolean("updateBombCount");
	}

	public int getBombsRemaining() {
		return bombsRemaining;
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
	}

	public EnumIMSTargetingMode getTargetingOption() {
		return targetingOption;
	}

	public void setTargetingOption(EnumIMSTargetingMode targetingOption) {
		this.targetingOption = targetingOption;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{range};
	}

	public static enum EnumIMSTargetingMode {
		PLAYERS, PLAYERS_AND_MOBS, MOBS;
	}
}
