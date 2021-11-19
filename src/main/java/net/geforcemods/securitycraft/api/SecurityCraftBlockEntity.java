package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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
public class SecurityCraftBlockEntity extends OwnableBlockEntity implements INameSetter, ITickingBlockEntity {

	protected boolean intersectsEntities = false;
	private boolean attacks = false;
	private Component customName = TextComponent.EMPTY;
	private double attackRange = 0.0D;
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	private Class<? extends Entity> typeToAttack = Entity.class;

	public SecurityCraftBlockEntity(BlockPos pos, BlockState state)
	{
		this(SCContent.beTypeAbstract, pos, state);
	}

	public SecurityCraftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public void tick(Level world, BlockPos pos, BlockState state) {
		if(intersectsEntities){
			int x = worldPosition.getX();
			int y = worldPosition.getY();
			int z = worldPosition.getZ();
			AABB area = (new AABB(x, y, z, x + 1, y + 1, z + 1));
			List<?> entities = world.getEntitiesOfClass(Entity.class, area);
			Iterator<?> iterator = entities.iterator();
			Entity entity;

			while (iterator.hasNext())
			{
				entity = (Entity)iterator.next();
				entityIntersecting(world, pos, entity);
			}
		}

		if (attacks) {
			if (attackCooldown < getTicksBetweenAttacks()) {
				attackCooldown++;
				return;
			}

			if (canAttack()) {
				AABB area = new AABB(pos).inflate(getAttackRange());
				List<? extends Entity> entities = world.getEntitiesOfClass(entityTypeToAttack(), area);
				Iterator<? extends Entity> iterator = entities.iterator();

				if(!world.isClientSide){
					boolean attacked = false;

					if(!iterator.hasNext())
						attackFailed();

					while (iterator.hasNext()) {
						Entity mobToAttack = iterator.next();

						if (mobToAttack == null || mobToAttack instanceof ItemEntity || !shouldAttackEntityType(mobToAttack))
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

	private static void entityIntersecting(Level world, BlockPos pos, Entity entity) {
		if(!(world.getBlockState(pos).getBlock() instanceof IIntersectable intersectable)) return;

		intersectable.onEntityIntersected(world, pos, entity);
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
	 * Is called when a {@link SecurityCraftBlockEntity} is ready to attack, but cannot for some reason. <p>
	 *
	 * These reasons may include: <p>
	 * - There are no Entities in this block's attack range. <p>
	 * - Only ItemEntitys are in the attack range. <p>
	 * - The Entities in this block's attack range are not of the type set in entityTypeToAttack().
	 */
	public void attackFailed() {}

	/**
	 * Check if your TileEntity is ready to attack. (i.e: block conditions, metadata, etc.) <p>
	 * Different from {@link SecurityCraftBlockEntity}.doesAttack(), which simply returns if your TileEntity <i>does</i> attack.
	 */
	public boolean canAttack() {
		return false;
	}

	public boolean shouldAttackEntityType(Entity entity) {
		return entity instanceof Player || typeToAttack.isAssignableFrom(entity.getClass());
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		tag.putBoolean("intersectsEntities", intersectsEntities);
		tag.putBoolean("attacks", attacks);
		tag.putDouble("attackRange", attackRange);
		tag.putInt("attackCooldown", attackCooldown);
		tag.putInt("ticksBetweenAttacks", ticksBetweenAttacks);
		tag.putString("customName", customName.getString());
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		if (tag.contains("intersectsEntities"))
			intersectsEntities = tag.getBoolean("intersectsEntities");

		if (tag.contains("attacks"))
			attacks = tag.getBoolean("attacks");

		if (tag.contains("attackRange"))
			attackRange = tag.getDouble("attackRange");

		if (tag.contains("attackCooldown"))
			attackCooldown = tag.getInt("attackCooldown");

		if (tag.contains("ticksBetweenAttacks"))
			ticksBetweenAttacks = tag.getInt("ticksBetweenAttacks");

		if (tag.contains("customName"))
			customName = new TextComponent(tag.getString("customName"));
	}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's CompoundNBT
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(level == null) return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	/**
	 * Sets the TileEntity able to be intersected with.
	 * <p>
	 * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, LivingEntity) when a {@link LivingEntity} comes into contact with this block.
	 * <p>
	 * Implement IIntersectable in your Block class in order to do stuff with that event.
	 */
	public SecurityCraftBlockEntity intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	/**
	 * Sets this TileEntity able to attack.
	 * <p>
	 * Calls {@link SecurityCraftBlockEntity}.attackEntity(Entity) when this TE's cooldown equals 0.
	 */
	public SecurityCraftBlockEntity attacks(Class<? extends Entity> type, double range, int cooldown) {
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
	 * @return Gets the number of ticks before {@link SecurityCraftBlockEntity}.attackEntity(Entity) is called.
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
	public Component getName()
	{
		return hasCustomName() ? customName : getDefaultName();
	}

	@Override
	public boolean hasCustomName() {
		Component name = getCustomName();

		return name != null && !TextComponent.EMPTY.equals(name) && !getDefaultName().equals(name);
	}

	@Override
	public Component getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(Component customName) {
		this.customName = customName;
		sync();
	}

	public Component getDefaultName() {
		return Utils.localize(getBlockState().getBlock().getDescriptionId());
	}
}
