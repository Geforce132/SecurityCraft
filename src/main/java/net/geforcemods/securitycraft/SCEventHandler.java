package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<>();

	static
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!ConfigHandler.CONFIG.sayThanksMessage.get())
			return;

		String tipKey = getRandomTip();
		IFormattableTextComponent message = new StringTextComponent("[")
				.func_230529_a_(new StringTextComponent("SecurityCraft").func_240699_a_(TextFormatting.GOLD))
				.func_230529_a_(new StringTextComponent("] "))
				.func_230529_a_(ClientUtils.localize("messages.securitycraft:thanks",
						SecurityCraft.getVersion(),
						ClientUtils.localize("messages.securitycraft:tip"),
						ClientUtils.localize(tipKey)));

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = message.func_230529_a_(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2]))); //appendSibling

		event.getPlayer().sendMessage(message, Util.DUMMY_UUID);
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
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ(), SCSounds.ELECTRIFIED.path, 0.25F, "blocks"));
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
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()))
		{
			event.setCanceled(true);
			return;
		}

		if(event.getHand() == Hand.MAIN_HAND)
		{
			World world = event.getWorld();

			if(!world.isRemote){
				TileEntity tileEntity = world.getTileEntity(event.getPos());
				BlockState state  = world.getBlockState(event.getPos());
				Block block = state.getBlock();

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.KEY_PANEL))
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

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.CODEBREAKER) && handleCodebreaking(event)) {
					event.setCanceled(true);
					return;
				}

				if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG) && event.getPlayer().inventory.getCurrentItem().hasDisplayName()){
					event.setCanceled(true);

					for(String character : new String[]{"(", ")"})
						if(event.getPlayer().inventory.getCurrentItem().getDisplayName().getString().contains(character)) {
							PlayerUtils.sendMessageToPlayer(event.getPlayer(), new StringTextComponent("Naming"), ClientUtils.localize("messages.securitycraft:naming.error", event.getPlayer().inventory.getCurrentItem().getDisplayName(), character), TextFormatting.RED);
							return;
						}

					if(((INameable) tileEntity).getCustomSCName().equals(event.getPlayer().inventory.getCurrentItem().getDisplayName())) {
						PlayerUtils.sendMessageToPlayer(event.getPlayer(), new StringTextComponent("Naming"), ClientUtils.localize("messages.securitycraft:naming.alreadyMatches", ((INameable) tileEntity).getCustomSCName()), TextFormatting.RED);
						return;
					}

					if(!event.getPlayer().isCreative())
						event.getPlayer().inventory.getCurrentItem().shrink(1);

					((INameable) tileEntity).setCustomSCName(event.getPlayer().inventory.getCurrentItem().getDisplayName());
					return;
				}
			}

			//outside !world.isRemote for properly checking the interaction
			//all the sentry functionality for when the sentry is diguised
			List<SentryEntity> sentries = world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
				event.setCanceled(sentries.get(0).func_230254_b_(event.getPlayer(), event.getHand()) == ActionResultType.SUCCESS); //cancel if an action was taken
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
			BlockPos pos = event.getPos();

			if (!sentries.get(0).getDisguiseModule().isEmpty())
			{
				ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
				List<Block> blocks = ((ModuleItem)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());

				if(blocks.size() > 0)
				{
					if(blocks.get(0) == event.getWorld().getBlockState(pos).getBlock())
						event.setCanceled(true);
				}
			}

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
		if(event.getWorld() instanceof World && !event.getWorld().isRemote()) {
			if(event.getWorld().getTileEntity(event.getPos()) instanceof IModuleInventory){
				IModuleInventory te = (IModuleInventory) event.getWorld().getTileEntity(event.getPos());

				for(int i = 0; i < te.getMaxNumberOfModules(); i++)
					if(!te.getInventory().get(i).isEmpty()){
						ItemStack stack = te.getInventory().get(i);
						ItemEntity item = new ItemEntity((World)event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().addEntity(item));

						te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());

						if(te instanceof CustomizableTileEntity)
							((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, (CustomizableTileEntity)te);

						if(te instanceof SecurityCameraTileEntity)
						{
							SecurityCameraTileEntity cam = (SecurityCameraTileEntity)te;

							cam.getWorld().notifyNeighborsOfStateChange(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).get(SecurityCameraBlock.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock());
						}
					}
			}

			List<SentryEntity> sentries = ((World)event.getWorld()).getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
			{
				BlockPos pos = event.getPos();

				if (!sentries.get(0).getDisguiseModule().isEmpty())
				{
					ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
					List<Block> blocks = ((ModuleItem)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());

					if(blocks.size() > 0)
					{
						BlockState state = blocks.get(0).getDefaultState();

						((World)event.getWorld()).setBlockState(pos, state.getShape(event.getWorld(), pos) == VoxelShapes.fullCube() ? state : Blocks.AIR.getDefaultState());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if((event.getTarget() instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(event.getTarget())) || event.getTarget() instanceof SentryEntity)
			((MobEntity)event.getEntity()).setAttackTarget(null);
	}

	@SubscribeEvent
	public static void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() != null)
		{
			Item held = event.getPlayer().getHeldItemMainhand().getItem();

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
		event.setCanceled(event.getEntity() instanceof WitherEntity && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event)
	{
		if(event.isDismounting() && event.getEntityBeingMounted() instanceof SecurityCameraEntity && event.getEntityMounting() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)event.getEntityMounting();
			TileEntity te = event.getWorldObj().getTileEntity(event.getEntityBeingMounted().func_233580_cy_());

			if(PlayerUtils.isPlayerMountedOnCamera(player) && te instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART))
			{
				((SecurityCameraTileEntity)te).lastPitch = player.rotationPitch;
				((SecurityCameraTileEntity)te).lastYaw = player.rotationYaw;
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
			event.setCanceled(true);
	}

	private static ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.FAKE_WATER_BLOCK.get()){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.FAKE_WATER_BUCKET.get(), 1);
		}else if(block == SCContent.FAKE_LAVA_BLOCK.get()){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.FAKE_LAVA_BUCKET.get(), 1);
		}
		else
			return ItemStack.EMPTY;
	}

	private static void handleOwnableTEs(OwnershipEvent event) {
		if(event.getWorld().getTileEntity(event.getPos()) instanceof IOwnable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) event.getWorld().getTileEntity(event.getPos())).getOwner().set(uuid, name);
		}
	}

	private static boolean handleCodebreaking(PlayerInteractEvent event) {
		if(ConfigHandler.CONFIG.allowCodebreakerItem.get())
		{
			World world = event.getPlayer().world;
			TileEntity tileEntity = event.getPlayer().world.getTileEntity(event.getPos());

			if(tileEntity instanceof IPasswordProtected)
			{
				if(event.getPlayer().getHeldItem(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getPlayer().getHeldItem(event.getHand()).damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));

				if(new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer(), !ConfigHandler.CONFIG.allowCodebreakerItem.get());
				else return true;
			}
		}

		return false;
	}

	private static String getRandomTip(){
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.trello",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}
}
