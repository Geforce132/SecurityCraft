package net.geforcemods.securitycraft;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {
	private static final String PREVIOUS_PLAYER_POS_NBT = "SecurityCraftPreviousPlayerPos";

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		Player player = event.getPlayer();

		if(player.getPersistentData().contains(PREVIOUS_PLAYER_POS_NBT))
		{
			BlockPos pos = BlockPos.of(player.getPersistentData().getLong(PREVIOUS_PLAYER_POS_NBT));

			player.getPersistentData().remove(PREVIOUS_PLAYER_POS_NBT);
			player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
		}

		SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new SendTip());
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		Player player = event.getPlayer();

		if(PlayerUtils.isPlayerMountedOnCamera(player))
		{
			BlockPos pos = new BlockPos(((SecurityCameraEntity)player.getVehicle()).getPreviousPlayerPos());

			player.getVehicle().remove();
			player.getPersistentData().putLong(PREVIOUS_PLAYER_POS_NBT, pos.asLong());
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event)
	{
		if(event.getEntity() != null && PlayerUtils.isPlayerMountedOnCamera(event.getEntityLiving())){
			event.setCanceled(true);
			return;
		}

		if(event.getSource() == CustomDamageSources.ELECTRICITY)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), SCSounds.ELECTRIFIED.path, 0.25F, "blocks"));
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()))
		{
			event.setCanceled(true);
			return;
		}

		Level world = event.getWorld();

		if(!world.isClientSide){
			BlockEntity tileEntity = world.getBlockEntity(event.getPos());
			BlockState state  = world.getBlockState(event.getPos());
			Block block = state.getBlock();

			if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.KEY_PANEL, event.getHand()))
			{
				for(IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles())
				{
					if(pc.getOriginalBlock() == block)
					{
						event.setUseBlock(Result.DENY);
						event.setUseItem(Result.ALLOW);
					}
				}

				return;
			}

			if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.CODEBREAKER, event.getHand()) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}

			if(tileEntity instanceof INameable && ((INameable)tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG, event.getHand()) && event.getPlayer().getItemInHand(event.getHand()).hasCustomHoverName()){
				ItemStack nametag = event.getPlayer().getItemInHand(event.getHand());

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);

				if(((INameable) tileEntity).getCustomSCName().equals(nametag.getHoverName())) {
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslatableComponent(tileEntity.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.alreadyMatches", ((INameable)tileEntity).getCustomSCName()), ChatFormatting.RED);
					return;
				}

				if(!event.getPlayer().isCreative())
					nametag.shrink(1);

				((INameable) tileEntity).setCustomSCName(nametag.getHoverName());
				PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslatableComponent(tileEntity.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.named", ((INameable)tileEntity).getCustomSCName()), ChatFormatting.RED);
				return;
			}
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<SentryEntity> sentries = world.getEntitiesOfClass(SentryEntity.class, new AABB(event.getPos()));

		if(!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(event.getPlayer(), event.getHand()) == InteractionResult.SUCCESS); //cancel if an action was taken
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onEntityInteracted(EntityInteract event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		if(!(event.getWorld() instanceof Level))
			return;

		if(!event.getWorld().isClientSide()) {
			if(event.getWorld().getBlockEntity(event.getPos()) instanceof IModuleInventory){
				IModuleInventory te = (IModuleInventory) event.getWorld().getBlockEntity(event.getPos());

				for(int i = 0; i < te.getMaxNumberOfModules(); i++)
					if(!te.getInventory().get(i).isEmpty()){
						ItemStack stack = te.getInventory().get(i);
						ItemEntity item = new ItemEntity((Level)event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().addFreshEntity(item));

						te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());

						if(te instanceof CustomizableTileEntity)
							((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModuleType() }, (CustomizableTileEntity)te);

						if(te instanceof SecurityCameraTileEntity)
						{
							SecurityCameraTileEntity cam = (SecurityCameraTileEntity)te;

							cam.getLevel().updateNeighborsAt(cam.getBlockPos().relative(cam.getLevel().getBlockState(cam.getBlockPos()).getValue(SecurityCameraBlock.FACING), -1), cam.getLevel().getBlockState(cam.getBlockPos()).getBlock());
						}
					}
			}
		}

		List<SentryEntity> sentries = ((Level)event.getWorld()).getEntitiesOfClass(SentryEntity.class, new AABB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty() && !sentries.get(0).getDisguiseModule().isEmpty())
		{
			ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
			List<Block> blocks = ((ModuleItem)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());

			if(blocks.size() > 0)
			{
				if(blocks.get(0) == event.getWorld().getBlockState(event.getPos()).getBlock())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event)
	{
		BlockEntity te = event.getWorld().getBlockEntity(event.getPos());

		if(te instanceof IOwnable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable)te).setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if((event.getTarget() instanceof Player && PlayerUtils.isPlayerMountedOnCamera(event.getTarget())) || event.getTarget() instanceof SentryEntity)
			((Mob)event.getEntity()).setTarget(null);
	}

	@SubscribeEvent
	public static void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() != null)
		{
			Item held = event.getPlayer().getMainHandItem().getItem();

			if(held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
			{
				Block block = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(event.getState().getBlock());

				if(block != null)
					event.setNewSpeed(10000.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event)
	{
		event.setCanceled(event.getEntity() instanceof WitherBoss && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event)
	{
		if(event.isDismounting() && event.getEntityBeingMounted() instanceof SecurityCameraEntity && event.getEntityMounting() instanceof Player)
		{
			Player player = (Player)event.getEntityMounting();
			BlockEntity te = event.getWorldObj().getBlockEntity(event.getEntityBeingMounted().blockPosition());

			if(PlayerUtils.isPlayerMountedOnCamera(player) && te instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART))
			{
				((SecurityCameraTileEntity)te).lastPitch = player.xRot;
				((SecurityCameraTileEntity)te).lastYaw = player.yRot;
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event)
	{
		Item item = event.getItemStack().getItem();

		if(item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ReinforcedCarpetBlock)
			event.setBurnTime(0);
	}

	private static boolean handleCodebreaking(PlayerInteractEvent.RightClickBlock event) {
		Level world = event.getPlayer().level;
		BlockEntity tileEntity = world.getBlockEntity(event.getPos());

		if(tileEntity instanceof IPasswordProtected && ((IPasswordProtected)tileEntity).isCodebreakable())
		{
			if(ConfigHandler.SERVER.allowCodebreakerItem.get())
			{
				if(event.getPlayer().getItemInHand(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getPlayer().getItemInHand(event.getHand()).hurtAndBreak(1, event.getPlayer(), p -> p.broadcastBreakEvent(event.getHand()));

				if(event.getPlayer().isCreative() || new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer());
				else {
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslatableComponent(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
					return true;
				}
			}
			else {
				Block block = world.getBlockState(event.getPos()).getBlock();

				PlayerUtils.sendMessageToPlayer(event.getPlayer(), Utils.localize(block.getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
			}
		}

		return false;
	}
}
