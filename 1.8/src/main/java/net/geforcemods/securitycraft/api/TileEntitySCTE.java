package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Simple TileEntity that SecurityCraft uses to easily create blocks like
 * the retinal scanner (activated by looking at it) and attacking blocks
 * like the protecto. Everything can be overridden for easy customization
 * or use as an API.
 *
 * @version 1.1.3
 *
 * @author Geforce
 */
public class TileEntitySCTE extends TileEntity implements IUpdatePlayerListBox, INameable {

	protected boolean intersectsEntities = false;
	protected boolean viewActivated = false;
	private boolean attacks = false;
	private boolean canBeNamed = false;

	private String customName = "name";

	private double attackRange = 0.0D;

	private int blockPlaceCooldown = 30;
	private int viewCooldown = getViewCooldown();
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;

	private Class<?> typeToAttack = Entity.class;

	@Override
	public void update() {
		if(intersectsEntities){
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			AxisAlignedBB area = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
			List<?> entities = worldObj.getEntitiesWithinAABB(Entity.class, area);
			Iterator<?> iterator = entities.iterator();
			Entity entity;

			while (iterator.hasNext())
			{
				entity = (Entity)iterator.next();
				entityIntersecting(entity);
			}
		}

		if(viewActivated){
			if(blockPlaceCooldown > 0){
				blockPlaceCooldown--;
				return;
			}

			if(viewCooldown > 0){
				viewCooldown--;
				return;
			}

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			AxisAlignedBB area = (new AxisAlignedBB(x, y, z, (x), (y), (z)).expand(5, 5, 5));
			List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);
			Iterator<?> iterator = entities.iterator();
			EntityLivingBase entity;

			while (iterator.hasNext())
			{
				entity = (EntityLivingBase)iterator.next();
				double eyeHeight = entity.getEyeHeight();
				boolean isPlayer = (entity instanceof EntityPlayer);

				Vec3 lookVec = new Vec3((entity.posX + (entity.getLookVec().xCoord * 5)), ((eyeHeight + entity.posY) + (entity.getLookVec().yCoord * 5)), (entity.posZ + (entity.getLookVec().zCoord * 5)));

				MovingObjectPosition mop = getWorld().rayTraceBlocks(new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), lookVec);
				if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
					if(mop.getBlockPos().getX() == getPos().getX() && mop.getBlockPos().getY() == getPos().getY() && mop.getBlockPos().getZ() == getPos().getZ())
						if((isPlayer && activatedOnlyByPlayer()) || !activatedOnlyByPlayer()) {
							entityViewed(entity);
							viewCooldown = getViewCooldown();
						}
			}
		}

		if (attacks) {
			if (attackCooldown < getTicksBetweenAttacks()) {
				attackCooldown++;
				return;
			}

			if (canAttack()) {
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				AxisAlignedBB area = AxisAlignedBB.fromBounds(x, y, z, x + 1, y + 1, z + 1).expand(getAttackRange(), getAttackRange(), getAttackRange());
				List<?> entities = worldObj.getEntitiesWithinAABB(entityTypeToAttack(), area);
				Iterator<?> iterator = entities.iterator();

				if(!worldObj.isRemote){
					boolean attacked = false;

					if(!iterator.hasNext())
						attackFailed();

					while (iterator.hasNext()) {
						Entity mobToAttack = (Entity) iterator.next();

						if (mobToAttack == null || mobToAttack instanceof EntityItem || !shouldAttackEntityType(mobToAttack))
							continue;

						if (attackEntity(mobToAttack))
							attacked = true;
					}

					if (attacked || shouldRefreshAttackCooldown())
						attackCooldown = 0;

					if(attacked || shouldSyncToClient())
						sync();
				}
			}
		}
	}

	public void entityIntersecting(Entity entity) {
		if(!(worldObj.getBlockState(getPos()).getBlock() instanceof IIntersectable)) return;

		((IIntersectable) worldObj.getBlockState(getPos()).getBlock()).onEntityIntersected(getWorld(), getPos(), entity);
	}

	/**
	 * Called when {@link TileEntitySCTE}.isViewActivated(), and when an entity looks directly at this block.
	 */
	public void entityViewed(EntityLivingBase entity) {}

	/**
	 * Handle your TileEntity's attack to entities here.
	 * ONLY RUNS ON THE SERVER SIDE (to keep the TE's client cooldown in-sync)! If you need something done on the client,
	 * use packets.<p>
	 *
	 * @return True if it successfully attacked, false otherwise.
	 */
	public boolean attackEntity(Entity entity) {
		return false;
	}

	/**
	 * Is called when a {@link TileEntitySCTE} is ready to attack, but cannot for some reason. <p>
	 *
	 * These reasons may include: <p>
	 * - There are no Entities in this block's attack range. <p>
	 * - Only EntityItems are in the attack range. <p>
	 * - The Entities in this block's attack range are not of the type set in entityTypeToAttack().
	 */
	public void attackFailed() {}

	/**
	 * Check if your TileEntity is ready to attack. (i.e: block conditions, metadata, etc.) <p>
	 * Different from {@link TileEntitySCTE}.doesAttack(), which simply returns if your TileEntity <i>does</i> attack.
	 */
	public boolean canAttack() {
		return false;
	}

	public boolean shouldAttackEntityType(Entity entity) {
		if(entity.getClass() == EntityPlayer.class || entity.getClass() == EntityPlayerMP.class)
			return (entity.getClass() == EntityPlayer.class || entity.getClass() == EntityPlayerMP.class || entity.getClass() == EntityPlayerSP.class);
		else
			return (entity.getClass() == typeToAttack);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("intersectsEntities", intersectsEntities);
		tag.setBoolean("viewActivated", viewActivated);
		tag.setBoolean("attacks", attacks);
		tag.setBoolean("canBeNamed", canBeNamed);
		tag.setDouble("attackRange", attackRange);
		tag.setInteger("attackCooldown", attackCooldown);
		tag.setInteger("ticksBetweenAttacks", ticksBetweenAttacks);
		tag.setString("customName", customName);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("intersectsEntities"))
			intersectsEntities = tag.getBoolean("intersectsEntities");

		if (tag.hasKey("viewActivated"))
			viewActivated = tag.getBoolean("viewActivated");

		if (tag.hasKey("attacks"))
			attacks = tag.getBoolean("attacks");

		if (tag.hasKey("canBeNamed"))
			canBeNamed = tag.getBoolean("canBeNamed");

		if (tag.hasKey("attackRange"))
			attackRange = tag.getDouble("attackRange");

		if (tag.hasKey("attackCooldown"))
			attackCooldown = tag.getInteger("attackCooldown");

		if (tag.hasKey("ticksBetweenAttacks"))
			ticksBetweenAttacks = tag.getInteger("ticksBetweenAttacks");

		if (tag.hasKey("customName"))
			customName = tag.getString("customName");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void invalidate() {
		super.invalidate();

		onTileEntityDestroyed();
	}

	public void onTileEntityDestroyed() {}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's NBTTagCompound
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(worldObj == null) return;

		if(worldObj.isRemote)
			ClientUtils.syncTileEntity(this);
		else
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(getDescriptionPacket());
	}

	/**
	 * Sets the TileEntity able to be intersected with.
	 * <p>
	 * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, EntityLivingBase) when a {@link EntityLivingBase} comes into contact with this block.
	 * <p>
	 * Implement IIntersectable in your Block class in order to do stuff with that event.
	 */
	public TileEntitySCTE intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	/**
	 * Sets this TileEntity able to be activated when a player looks at the block.
	 * <p>
	 * Calls {@link TileEntitySCTE}.entityViewed(EntityLivingBase) when an {@link EntityLivingBase} looks at this block.
	 * <p>
	 * Implement IViewActivated in your Block class in order to do stuff with that event.
	 */
	public TileEntitySCTE activatedByView(){
		viewActivated = true;
		return this;
	}

	/**
	 * @return The amount of ticks the block should "cooldown"
	 *         for after an Entity looks at this block.
	 */
	public int getViewCooldown() {
		return 0;
	}

	/**
	 * @return Can this TileEntity can only be activated by an EntityPlayer?
	 */
	public boolean activatedOnlyByPlayer() {
		return true;
	}

	/**
	 * @return If this TileEntity can be activated when an Entity looking at it.
	 */
	public boolean isActivatedByView(){
		return viewActivated;
	}

	/**
	 * Sets this TileEntity able to attack.
	 * <p>
	 * Calls {@link TileEntitySCTE}.attackEntity(Entity) when this TE's cooldown equals 0.
	 */
	public TileEntitySCTE attacks(Class<?> type, double range, int cooldown) {
		attacks = true;
		typeToAttack = type;
		attackRange = range;
		ticksBetweenAttacks = cooldown;
		return this;
	}

	/**
	 * @return The class of the entity that this TileEntity should attack.
	 */
	public Class<?> entityTypeToAttack(){
		return typeToAttack;
	}

	/**
	 * @return The range that this TileEntity checks for attackable entities.
	 */
	public double getAttackRange() {
		return attackRange;
	}

	/**
	 * @return The number of ticks between attacks.
	 */
	public int getTicksBetweenAttacks() {
		return ticksBetweenAttacks;
	}

	/**
	 * @return Gets the number of ticks before {@link TileEntitySCTE}.attackEntity(Entity) is called.
	 */
	public int getAttackCooldown() {
		return attackCooldown;
	}

	/**
	 *  Set this TileEntity's attack cooldown.
	 */
	public void setAttackCooldown(int cooldown) {
		attackCooldown = cooldown;
	}

	/**
	 *  Maxes out this TileEntity's attack cooldown, so it'll attempt to attack next tick.
	 */
	public void attackNextTick() {
		attackCooldown = ticksBetweenAttacks;
	}

	/**
	 * @return If, once this TileEntity's attack cooldown gets to the set maximum,
	 *         it should start again automatically from 0.
	 */
	public boolean shouldRefreshAttackCooldown() {
		return true;
	}

	/**
	 * @return Should this TileEntity send an update packet
	 *         to all clients if it attacks unsuccessfully?
	 */
	public boolean shouldSyncToClient() {
		return true;
	}

	/**
	 * @return If this TileEntity can attack.
	 */
	public boolean doesAttack() {
		return attacks;
	}

	public TileEntitySCTE nameable() {
		canBeNamed = true;
		return this;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
		sync();
	}

	@Override
	public boolean hasCustomName() {
		return (customName != null && !customName.equals("name"));
	}

	@Override
	public boolean canBeNamed() {
		return canBeNamed;
	}

}
