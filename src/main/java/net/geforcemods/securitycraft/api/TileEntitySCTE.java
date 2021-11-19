package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
public class TileEntitySCTE extends TileEntityOwnable implements ITickable, INameSetter {

	protected boolean intersectsEntities = false;
	private boolean attacks = false;
	private String customName = "";
	private double attackRange = 0.0D;
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	private Class<? extends Entity> typeToAttack = Entity.class;

	@Override
	public void update() {
		if(intersectsEntities){
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			AxisAlignedBB area = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
			List<?> entities = world.getEntitiesWithinAABB(Entity.class, area);
			Iterator<?> iterator = entities.iterator();
			Entity entity;

			while (iterator.hasNext())
			{
				entity = (Entity)iterator.next();
				entityIntersecting(entity);
			}
		}

		if (attacks) {
			if (attackCooldown < getTicksBetweenAttacks()) {
				attackCooldown++;
				return;
			}

			if (canAttack()) {
				AxisAlignedBB area = new AxisAlignedBB(pos).grow(getAttackRange(), getAttackRange(), getAttackRange());
				List<?> entities = world.getEntitiesWithinAABB(entityTypeToAttack(), area, e -> !(e instanceof EntityPlayer) || !((EntityPlayer)e).isSpectator());
				Iterator<?> iterator = entities.iterator();

				if(!world.isRemote){
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
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof IIntersectable)
			((IIntersectable)state.getBlock()).onEntityIntersected(world, pos, state, entity);
	}

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
		return entity instanceof EntityPlayer || typeToAttack.isAssignableFrom(entity.getClass());
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("intersectsEntities", intersectsEntities);
		tag.setBoolean("attacks", attacks);
		tag.setDouble("attackRange", attackRange);
		tag.setInteger("attackCooldown", attackCooldown);
		tag.setInteger("ticksBetweenAttacks", ticksBetweenAttacks);
		tag.setString("customName", customName);
		return tag;
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

		if (tag.hasKey("attacks"))
			attacks = tag.getBoolean("attacks");

		if (tag.hasKey("attackRange"))
			attackRange = tag.getDouble("attackRange");

		if (tag.hasKey("attackCooldown"))
			attackCooldown = tag.getInteger("attackCooldown");

		if (tag.hasKey("ticksBetweenAttacks"))
			ticksBetweenAttacks = tag.getInteger("ticksBetweenAttacks");

		if (tag.hasKey("customName"))
			customName = tag.getString("customName");
	}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's NBTTagCompound
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(world == null) return;

		if(world.isRemote)
			ClientUtils.syncTileEntity(this);
		else
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getUpdatePacket());
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
	 * Sets this TileEntity able to attack.
	 * <p>
	 * Calls {@link TileEntitySCTE}.attackEntity(Entity) when this TE's cooldown equals 0.
	 */
	public TileEntitySCTE attacks(Class<? extends Entity> type, double range, int cooldown) {
		attacks = true;
		typeToAttack = type;
		attackRange = range;
		ticksBetweenAttacks = cooldown;
		return this;
	}

	/**
	 * @return The class of the entity that this TileEntity should attack.
	 */
	public Class<? extends Entity> entityTypeToAttack(){
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

	@Override
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return !customName.isEmpty() && !customName.equals(getDefaultName().getFormattedText());
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(customName) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(blockType.getTranslationKey() + ".name");
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
		sync();
	}
}
