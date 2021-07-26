package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

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
public class SecurityCraftTileEntity extends OwnableTileEntity implements INameable {

	protected boolean intersectsEntities = false;
	protected boolean viewActivated = false;
	private boolean attacks = false;
	private boolean canBeNamed = false;
	private Component customName = new TextComponent("name");
	private double attackRange = 0.0D;
	private int viewCooldown = 0;
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	private Class<? extends Entity> typeToAttack = Entity.class;

	public SecurityCraftTileEntity(BlockPos pos, BlockState state)
	{
		this(SCContent.teTypeAbstract, pos, state);
	}

	public SecurityCraftTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, SecurityCraftTileEntity te) {
		if(te.intersectsEntities){
			int x = te.worldPosition.getX();
			int y = te.worldPosition.getY();
			int z = te.worldPosition.getZ();
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

		if(te.viewActivated){
			if(te.viewCooldown > 0){
				te.viewCooldown--;
				return;
			}

			int x = te.worldPosition.getX();
			int y = te.worldPosition.getY();
			int z = te.worldPosition.getZ();
			AABB area = (new AABB(x, y, z, (x), (y), (z)).inflate(5));
			List<?> entities = world.getEntitiesOfClass(LivingEntity.class, area, e -> !(e instanceof Player) || !e.isSpectator());
			Iterator<?> iterator = entities.iterator();
			LivingEntity entity;

			while (iterator.hasNext())
			{
				entity = (LivingEntity)iterator.next();
				double eyeHeight = entity.getEyeHeight();
				boolean isPlayer = (entity instanceof Player);
				Vec3 lookVec = new Vec3((entity.getX() + (entity.getLookAngle().x * 5)), ((eyeHeight + entity.getY()) + (entity.getLookAngle().y * 5)), (entity.getZ() + (entity.getLookAngle().z * 5)));

				BlockHitResult mop = world.clip(new ClipContext(new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), lookVec, Block.COLLIDER, Fluid.NONE, entity));
				if(mop != null && mop.getType() == Type.BLOCK)
					if(mop.getBlockPos().getX() == pos.getX() && mop.getBlockPos().getY() == pos.getY() && mop.getBlockPos().getZ() == pos.getZ())
						if((isPlayer && te.activatedOnlyByPlayer()) || !te.activatedOnlyByPlayer()) {
							te.entityViewed(entity);
							te.viewCooldown = te.getViewCooldown();
						}
			}
		}

		if (te.attacks) {
			if (te.attackCooldown < te.getTicksBetweenAttacks()) {
				te.attackCooldown++;
				return;
			}

			if (te.canAttack()) {
				AABB area = new AABB(pos).inflate(te.getAttackRange());
				List<?> entities = world.getEntitiesOfClass(te.entityTypeToAttack(), area);
				Iterator<?> iterator = entities.iterator();

				if(!world.isClientSide){
					boolean attacked = false;

					if(!iterator.hasNext())
						te.attackFailed();

					while (iterator.hasNext()) {
						Entity mobToAttack = (Entity) iterator.next();

						if (mobToAttack == null || mobToAttack instanceof ItemEntity || !te.shouldAttackEntityType(mobToAttack))
							continue;

						if (te.attackEntity(mobToAttack))
							attacked = true;
					}

					if (attacked || te.shouldRefreshAttackCooldown())
						te.attackCooldown = 0;

					if(attacked || te.shouldSyncToClient())
						te.sync();
				}
			}
		}
	}

	private static void entityIntersecting(Level world, BlockPos pos, Entity entity) {
		if(!(world.getBlockState(pos).getBlock() instanceof IIntersectable)) return;

		((IIntersectable) world.getBlockState(pos).getBlock()).onEntityIntersected(world, pos, entity);
	}

	/**
	 * Called when {@link SecurityCraftTileEntity}.isViewActivated(), and when an entity looks directly at this block.
	 */
	public void entityViewed(LivingEntity entity) {}

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
	 * Is called when a {@link SecurityCraftTileEntity} is ready to attack, but cannot for some reason. <p>
	 *
	 * These reasons may include: <p>
	 * - There are no Entities in this block's attack range. <p>
	 * - Only ItemEntitys are in the attack range. <p>
	 * - The Entities in this block's attack range are not of the type set in entityTypeToAttack().
	 */
	public void attackFailed() {}

	/**
	 * Check if your TileEntity is ready to attack. (i.e: block conditions, metadata, etc.) <p>
	 * Different from {@link SecurityCraftTileEntity}.doesAttack(), which simply returns if your TileEntity <i>does</i> attack.
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
		tag.putBoolean("viewActivated", viewActivated);
		tag.putBoolean("attacks", attacks);
		tag.putBoolean("canBeNamed", canBeNamed);
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

		if (tag.contains("viewActivated"))
			viewActivated = tag.getBoolean("viewActivated");

		if (tag.contains("attacks"))
			attacks = tag.getBoolean("attacks");

		if (tag.contains("canBeNamed"))
			canBeNamed = tag.getBoolean("canBeNamed");

		if (tag.contains("attackRange"))
			attackRange = tag.getDouble("attackRange");

		if (tag.contains("attackCooldown"))
			attackCooldown = tag.getInt("attackCooldown");

		if (tag.contains("ticksBetweenAttacks"))
			ticksBetweenAttacks = tag.getInt("ticksBetweenAttacks");

		if (tag.contains("customName"))
			customName = new TextComponent(tag.getString("customName"));
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		onTileEntityDestroyed();
	}

	public void onTileEntityDestroyed() {}

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
	public SecurityCraftTileEntity intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	/**
	 * Sets this TileEntity able to be activated when a player looks at the block.
	 * <p>
	 * Calls {@link SecurityCraftTileEntity}.entityViewed(LivingEntity) when an {@link LivingEntity} looks at this block.
	 * <p>
	 * Implement IViewActivated in your Block class in order to do stuff with that event.
	 */
	public SecurityCraftTileEntity activatedByView(){
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
	 * @return Can this TileEntity can only be activated by an PlayerEntity?
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
	 * Calls {@link SecurityCraftTileEntity}.attackEntity(Entity) when this TE's cooldown equals 0.
	 */
	public SecurityCraftTileEntity attacks(Class<? extends Entity> type, double range, int cooldown) {
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
	 * @return Gets the number of ticks before {@link SecurityCraftTileEntity}.attackEntity(Entity) is called.
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

	public SecurityCraftTileEntity nameable() {
		canBeNamed = true;
		return this;
	}

	@Override
	public Component getCustomSCName() {
		return customName;
	}

	@Override
	public void setCustomSCName(Component customName) {
		this.customName = customName;
		sync();
	}

	@Override
	public boolean hasCustomSCName() {
		return (customName != null && !customName.getString().equals("name"));
	}

	@Override
	public boolean canBeNamed() {
		return canBeNamed;
	}
}
