package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.mines.BlockIMS;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.PacketDistributor;

public class TileEntityIMS extends CustomizableSCTE {

	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;

	/** The targeting option currently selected for this IMS. PLAYERS = players, PLAYERS_AND_MOBS = hostile mobs & players.**/
	private EnumIMSTargetingMode targetingOption = EnumIMSTargetingMode.PLAYERS_AND_MOBS;

	private boolean updateBombCount = false;

	public TileEntityIMS()
	{
		super(SCContent.teTypeIms);
	}

	@Override
	public void tick(){
		super.tick();

		if(!world.isRemote && updateBombCount){
			BlockUtils.setBlockProperty(world, pos, BlockIMS.MINES, BlockUtils.getBlockPropertyAsInteger(world, pos, BlockIMS.MINES) - 1);
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
			double range = ConfigHandler.imsRange;

			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range, range, range);
			List<?> players = world.getEntitiesWithinAABB(EntityPlayer.class, area);
			List<?> mobs = world.getEntitiesWithinAABB(EntityMob.class, area);
			Iterator<?> playerIterator = players.iterator();
			Iterator<?> mobIterator = mobs.iterator();

			while(targetingOption == EnumIMSTargetingMode.PLAYERS_AND_MOBS && mobIterator.hasNext()){
				EntityLivingBase entity = (EntityLivingBase) mobIterator.next();
				int launchHeight = getLaunchHeight();

				if(PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;

				if(WorldUtils.isPathObstructed(world, pos.getX() + 0.5D, pos.getY() + (((launchHeight - 1) / 3) + 0.5D), pos.getZ() + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ))
					continue;
				if(hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(entity.getName().toLowerCase()))
					continue;

				double targetX = entity.posX - (pos.getX() + 0.5D);
				double targetY = entity.getBoundingBox().minY + entity.height / 2.0F - (pos.getY() + 1.25D);
				double targetZ = entity.posZ - (pos.getZ() + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(world.isRemote)
					SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), "random.bow", 1.0F, "block"));

				bombsRemaining--;
				launchedMine = true;
				updateBombCount = true;
				break;
			}

			while(!launchedMine && playerIterator.hasNext()){
				EntityPlayer entity = (EntityPlayer) playerIterator.next();
				int launchHeight = getLaunchHeight();

				if((entity != null && getOwner().isOwner((entity))) || PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;
				if(WorldUtils.isPathObstructed(world, pos.getX() + 0.5D, pos.getY() + (((launchHeight - 1) / 3) + 0.5D), pos.getZ() + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ))
					continue;
				if(hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(entity.getName()))
					continue;

				double targetX = entity.posX - (pos.getX() + 0.5D);
				double targetY = entity.getBoundingBox().minY + entity.height / 2.0F - (pos.getY() + 1.25D);
				double targetZ = entity.posZ - (pos.getZ() + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(world.isRemote)
					SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), "random.bow", 1.0F, "block"));

				bombsRemaining--;
				updateBombCount = true;
				break;
			}
		}
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityPlayer target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		world.spawnEntity(new EntityIMSBomb(world, target, pos.getX() + addToX, pos.getY(), pos.getZ() + addToZ, x, y, z, launchHeight));
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
	public NBTTagCompound write(NBTTagCompound tag){
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
	public void read(NBTTagCompound tag){
		super.read(tag);

		if (tag.contains("bombsRemaining"))
			bombsRemaining = tag.getInt("bombsRemaining");

		if (tag.contains("targetingOption"))
			targetingOption = EnumIMSTargetingMode.values()[tag.getInt("targetingOption")];

		if (tag.contains("updateBombCount"))
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
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	public static enum EnumIMSTargetingMode {

		PLAYERS(0),
		PLAYERS_AND_MOBS(1);

		public final int modeIndex;

		private EnumIMSTargetingMode(int index){
			modeIndex = index;
		}


	}

}
