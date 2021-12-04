package net.geforcemods.securitycraft;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!ConfigHandler.SERVER.disableThanksMessage.get())
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getPlayer()), new SendTip());
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		ServerPlayer player = (ServerPlayer)event.getPlayer();

		if(player.getCamera() instanceof SecurityCamera cam)
		{
			if(player.level.getBlockEntity(cam.blockPosition()) instanceof SecurityCameraBlockEntity camBe)
				camBe.stopViewing();

			cam.discard();
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		Level level = entity.level;

		if(event.getSource() == CustomDamageSources.ELECTRICITY)
			level.playSound(null, entity.blockPosition(), SCSounds.ELECTRIFIED.event, SoundSource.BLOCKS, 0.25F, 1.0F);

		if(!level.isClientSide && entity instanceof ServerPlayer player && PlayerUtils.isPlayerMountedOnCamera(entity))
			((SecurityCamera)player.getCamera()).stopViewing(player);
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	//side effect for keypad door: it is now only openable with an empty hand
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if(!stack.isEmpty() && item != SCContent.UNIVERSAL_BLOCK_REMOVER.get() && item != SCContent.UNIVERSAL_BLOCK_MODIFIER.get() && item != SCContent.UNIVERSAL_OWNER_CHANGER.get())
		{
			if(!(item instanceof BlockItem))
			{
				Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

				if(block == SCContent.KEYPAD_DOOR.get() || block == SCContent.REINFORCED_DOOR.get() || block == SCContent.REINFORCED_IRON_TRAPDOOR.get() || block == SCContent.SCANNER_DOOR.get())
					event.setCanceled(true);
			}
		}
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
			BlockEntity be = world.getBlockEntity(event.getPos());
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

			if(be instanceof INameSetter nameable && (be instanceof SecurityCameraBlockEntity || be instanceof PortableRadarBlockEntity) && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG, event.getHand()) && event.getPlayer().getItemInHand(event.getHand()).hasCustomHoverName()){
				ItemStack nametag = event.getPlayer().getItemInHand(event.getHand());

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);

				if(nameable.getCustomName().equals(nametag.getHoverName())) {
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslatableComponent(be.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.alreadyMatches", nameable.getCustomName()), ChatFormatting.RED);
					return;
				}

				if(!event.getPlayer().isCreative())
					nametag.shrink(1);

				nameable.setCustomName(nametag.getHoverName());
				PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslatableComponent(be.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.named", nameable.getCustomName()), ChatFormatting.RED);
				return;
			}
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = world.getEntitiesOfClass(Sentry.class, new AABB(event.getPos()));

		if(!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(event.getPlayer(), event.getHand()) == InteractionResult.SUCCESS); //cancel if an action was taken
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
			return;
		}

		ItemStack stack = event.getPlayer().getMainHandItem();
		Item held = stack.getItem();
		Level level = event.getWorld();
		BlockPos pos = event.getPos();

		if(held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
			UniversalBlockReinforcerItem.convertBlock(level.getBlockState(pos), level, stack, pos, event.getPlayer());
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		if(!(event.getWorld() instanceof Level level))
			return;

		if(!level.isClientSide() && level.getBlockEntity(event.getPos()) instanceof IModuleInventory be){
			for(int i = 0; i < be.getMaxNumberOfModules(); i++)
				if(!be.getInventory().get(i).isEmpty()){
					ItemStack stack = be.getInventory().get(i);
					ItemEntity item = new ItemEntity(level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
					WorldUtils.addScheduledTask(level, () -> level.addFreshEntity(item));

					be.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());

					if(be instanceof LinkableBlockEntity lbe)
						lbe.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModuleType() }, lbe);

					if(be instanceof SecurityCameraBlockEntity cam)
						level.updateNeighborsAt(cam.getBlockPos().relative(level.getBlockState(cam.getBlockPos()).getValue(SecurityCameraBlock.FACING), -1), level.getBlockState(cam.getBlockPos()).getBlock());
				}
		}

		List<Sentry> sentries = ((Level)event.getWorld()).getEntitiesOfClass(Sentry.class, new AABB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty() && !sentries.get(0).getDisguiseModule().isEmpty())
		{
			ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
			Block block = ((ModuleItem)disguiseModule.getItem()).getBlockAddon(disguiseModule.getTag());

			if(block == event.getWorld().getBlockState(event.getPos()).getBlock())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event)
	{
		BlockEntity te = event.getWorld().getBlockEntity(event.getPos());

		if(te instanceof IOwnable ownable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			ownable.setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.getTarget() instanceof Sentry)
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
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event)
	{
		Item item = event.getItemStack().getItem();

		if(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ReinforcedCarpetBlock)
			event.setBurnTime(0);
	}

	private static boolean handleCodebreaking(PlayerInteractEvent.RightClickBlock event) {
		Level world = event.getPlayer().level;
		BlockEntity tile = world.getBlockEntity(event.getPos());

		if(tile instanceof IPasswordProtected passwordProtected && passwordProtected.isCodebreakable())
		{
			if(ConfigHandler.SERVER.allowCodebreakerItem.get())
			{
				if(event.getPlayer().getItemInHand(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getPlayer().getItemInHand(event.getHand()).hurtAndBreak(1, event.getPlayer(), p -> p.broadcastBreakEvent(event.getHand()));

				if(event.getPlayer().isCreative() || new Random().nextInt(3) == 1)
					return passwordProtected.onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer());
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
