package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<String, String>();

	static
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!event.getPlayer().getEntityWorld().isRemote || !ConfigHandler.CONFIG.sayThanksMessage.get())
			return;

		String tipKey = getRandomTip();
		ITextComponent message;

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = new StringTextComponent("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		else
			message = new StringTextComponent("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey));

		event.getPlayer().sendMessage(message);
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getPlayer().getRidingEntity() instanceof SecurityCameraEntity)
			event.getPlayer().getRidingEntity().remove();
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event)
	{
		if(event.getEntity() != null && PlayerUtils.isPlayerMountedOnCamera(event.getEntityLiving())){
			event.setCanceled(true);
			return;
		}

		if(event.getSource() == CustomDamageSources.ELECTRICITY)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, SCSounds.ELECTRIFIED.path, 0.25F, "blocks"));
	}

	@SubscribeEvent
	public static void onBucketUsed(FillBucketEvent event){
		if(event.getTarget() == null || event.getTarget().getType() == Type.BLOCK)
			return;

		ItemStack result = fillBucket(event.getWorld(), ((BlockRayTraceResult)event.getTarget()).getPos());
		if(result.isEmpty())
			return;
		event.setFilledBucket(result);
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public static void onRightClickBlock(RightClickBlock event){
		if(event.getHand() == Hand.MAIN_HAND)
		{
			World world = event.getWorld();

			if(!world.isRemote){
				TileEntity tileEntity = world.getTileEntity(event.getPos());
				BlockState state  = world.getBlockState(event.getPos());
				Block block = state.getBlock();

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.keyPanel))
				{
					for(Block pc : IPasswordConvertible.BLOCKS)
					{
						if(((IPasswordConvertible)pc).getOriginalBlock() == block)
						{
							event.setUseBlock(Result.DENY);
							event.setUseItem(Result.ALLOW);
						}
					}

					return;
				}

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.codebreaker) && handleCodebreaking(event)) {
					event.setCanceled(true);
					return;
				}

				if(tileEntity != null && tileEntity instanceof CustomizableTileEntity && PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.universalBlockModifier)){
					event.setCanceled(true);

					if(!((IOwnable) tileEntity).getOwner().isOwner(event.getPlayer())){
						PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(SCContent.universalBlockModifier.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);
						return;
					}

					if(event.getPlayer() instanceof ServerPlayerEntity)
					{
						NetworkHooks.openGui((ServerPlayerEntity)event.getPlayer(), new INamedContainerProvider() {
							@Override
							public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
							{
								return new CustomizeBlockContainer(windowId, world, event.getPos(), inv);
							}

							@Override
							public ITextComponent getDisplayName()
							{
								return new TranslationTextComponent(tileEntity.getBlockState().getBlock().getTranslationKey());
							}
						}, event.getPos());
					}

					return;
				}

				if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG) && event.getPlayer().inventory.getCurrentItem().hasDisplayName()){
					event.setCanceled(true);

					for(String character : new String[]{"(", ")"})
						if(event.getPlayer().inventory.getCurrentItem().getDisplayName().getFormattedText().contains(character)) {
							PlayerUtils.sendMessageToPlayer(event.getPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.error").replace("#n", event.getPlayer().inventory.getCurrentItem().getDisplayName().getFormattedText()).replace("#c", character), TextFormatting.RED);
							return;
						}

					if(((INameable) tileEntity).getCustomSCName().equals(event.getPlayer().inventory.getCurrentItem().getDisplayName())) {
						PlayerUtils.sendMessageToPlayer(event.getPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomSCName().getFormattedText()), TextFormatting.RED);
						return;
					}

					if(!event.getPlayer().isCreative())
						event.getPlayer().inventory.getCurrentItem().shrink(1);

					((INameable) tileEntity).setCustomSCName(event.getPlayer().inventory.getCurrentItem().getDisplayName());
					return;
				}

				if(tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.universalBlockRemover)){
					event.setCanceled(true);

					if(!((IOwnable) tileEntity).getOwner().isOwner(event.getPlayer())){
						PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(SCContent.universalBlockRemover.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);
						return;
					}

					if(block == SCContent.laserBlock){
						CustomizableTileEntity te = (CustomizableTileEntity)world.getTileEntity(event.getPos());

						for(ItemStack module : te.modules)
						{
							if(!module.isEmpty())
								te.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[] {module, ((ModuleItem)module.getItem()).getModule()}, te);
						}

						world.destroyBlock(event.getPos(), true);
						LaserBlock.destroyAdjacentLasers(event.getWorld(), event.getPos());
						event.getPlayer().inventory.getCurrentItem().damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));
					}else if(block == SCContent.cageTrap && world.getBlockState(event.getPos()).get(CageTrapBlock.DEACTIVATED)) {
						BlockPos originalPos = event.getPos();
						BlockPos middlePos = originalPos.up(4);

						new CageTrapBlock.BlockModifier(event.getWorld(), new MutableBlockPos(originalPos), ((IOwnable)tileEntity).getOwner()).loop((w, p, o) -> {
							TileEntity te = w.getTileEntity(p);

							if(te instanceof IOwnable && ((IOwnable)te).getOwner().equals(o))
							{
								Block b = w.getBlockState(p).getBlock();

								if(b == SCContent.reinforcedIronBars || (p.equals(middlePos) && b == SCContent.horizontalReinforcedIronBars))
									w.destroyBlock(p, false);
							}
						});

						world.destroyBlock(originalPos, false);
					}else{
						BlockPos pos = event.getPos();

						if((block instanceof ReinforcedDoorBlock || block instanceof ScannerDoorBlock) && state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
							pos = pos.down();

						if(block == SCContent.inventoryScanner)
						{
							InventoryScannerTileEntity te = InventoryScannerBlock.getConnectedInventoryScanner(world, event.getPos());

							if(te != null)
								te.modules.clear();
						}

						world.destroyBlock(pos, true);
						world.removeTileEntity(pos);
						event.getPlayer().inventory.getCurrentItem().damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));
					}

					return;
				}
			}

			//outside !world.isRemote for properly checking the interaction
			//all the sentry functionality for when the sentry is diguised
			List<SentryEntity> sentries = world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
				event.setCanceled(sentries.get(0).processInteract(event.getPlayer(), event.getHand())); //cancel if an action was taken
		}
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		if(!(event.getWorld() instanceof World))
			return;

		List<SentryEntity> sentries = ((World)event.getWorld()).getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty())
		{
			event.setCanceled(true);
			return;
		}

		sentries = ((World)event.getWorld()).getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(event.getPos().up()));

		//remove sentry if block below is broken
		if(!sentries.isEmpty())
			sentries.get(0).remove();
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event)
	{
		handleOwnableTEs(event);
	}

	@SubscribeEvent
	public static void onBlockBroken(BreakEvent event){
		if(event.getWorld() instanceof World && !event.getWorld().isRemote())
			if(event.getWorld().getTileEntity(event.getPos()) != null && event.getWorld().getTileEntity(event.getPos()) instanceof CustomizableTileEntity){
				CustomizableTileEntity te = (CustomizableTileEntity) event.getWorld().getTileEntity(event.getPos());

				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++)
					if(!te.modules.get(i).isEmpty()){
						ItemStack stack = te.modules.get(i);
						ItemEntity item = new ItemEntity((World)event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().addEntity(item));

						te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());
						te.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, te);

						if(te instanceof SecurityCameraTileEntity)
							te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock());
					}
			}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.getTarget() instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(event.getTarget()))
			((MobEntity)event.getEntity()).setAttackTarget(null);
		else if(event.getTarget() instanceof SentryEntity)
			((MobEntity)event.getEntity()).setAttackTarget(null);
	}

	@SubscribeEvent
	public static void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() != null)
		{
			Item held = event.getPlayer().getHeldItemMainhand().getItem();

			if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			{
				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

					if(reinforcedBlock.getVanillaBlock() == event.getState().getBlock())
					{
						event.setNewSpeed(10000.0F);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event)
	{
		event.setCanceled(event.getEntity() instanceof WitherEntity && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event)
	{
		if(event.isDismounting() && event.getEntityBeingMounted() instanceof SecurityCameraEntity && event.getEntityMounting() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)event.getEntityMounting();
			TileEntity te = event.getWorldObj().getTileEntity(event.getEntityBeingMounted().getPosition());

			if(PlayerUtils.isPlayerMountedOnCamera(player) && te instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)te).hasModule(CustomModules.SMART))
			{
				((SecurityCameraTileEntity)te).lastPitch = player.rotationPitch;
				((SecurityCameraTileEntity)te).lastYaw = player.rotationYaw;
			}
		}
	}

	private static ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.fakeWaterBlock){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.fWaterBucket, 1);
		}else if(block == SCContent.fakeLavaBlock){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.fLavaBucket, 1);
		}
		else
			return ItemStack.EMPTY;
	}

	private static void handleOwnableTEs(OwnershipEvent event) {
		if(event.getWorld().getTileEntity(event.getPos()) instanceof IOwnable) {
			String name = event.getPlayer().getName().getFormattedText();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) event.getWorld().getTileEntity(event.getPos())).getOwner().set(uuid, name);
		}
	}

	private static boolean handleCodebreaking(PlayerInteractEvent event) {
		World world = event.getPlayer().world;
		TileEntity tileEntity = event.getPlayer().world.getTileEntity(event.getPos());

		if(ConfigHandler.CONFIG.allowCodebreakerItem.get() && event.getPlayer().getHeldItem(event.getHand()).getItem() == SCContent.codebreaker) //safety so when codebreakers are disabled they can't take damage
			event.getPlayer().getHeldItem(event.getHand()).damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));

		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1)
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer(), !ConfigHandler.CONFIG.allowCodebreakerItem.get());

		return false;
	}

	private static String getRandomTip(){
		String[] tips = {
				"messages.tip.scHelp",
				"messages.tip.trello",
				"messages.tip.patreon",
				"messages.tip.discord",
				"messages.tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}

	private static boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof OwnableTileEntity || tileEntity instanceof IOwnable || block instanceof OwnableBlock);
	}

}
