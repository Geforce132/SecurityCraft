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
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
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
import net.minecraft.item.BlockItem;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
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

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()), new SendTip());
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

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.CODEBREAKER) && handleCodebreaking(event)) {
					event.setCanceled(true);
					return;
				}

				if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.UNIVERSAL_BLOCK_MODIFIER.get()))
				{
					if(tileEntity instanceof IModuleInventory){
						event.setCanceled(true);

						if(tileEntity instanceof IOwnable && !((IOwnable) tileEntity).getOwner().isOwner(event.getPlayer())){
							if(!(tileEntity instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)tileEntity).getBlockState().getBlock()).getDisguisedStack(world, event.getPos()).getItem()).getBlock() instanceof DisguisableBlock))
								PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(SCContent.UNIVERSAL_BLOCK_MODIFIER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);

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
				}

				if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG) && event.getPlayer().inventory.getCurrentItem().hasDisplayName()){
					event.setCanceled(true);

					if(((INameable) tileEntity).getCustomSCName().equals(event.getPlayer().inventory.getCurrentItem().getDisplayName())) {
						PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(tileEntity.getBlockState().getBlock().getTranslationKey()), ClientUtils.localize("messages.securitycraft:naming.alreadyMatches", ((INameable) tileEntity).getCustomSCName()), TextFormatting.RED);
						return;
					}

					if(!event.getPlayer().isCreative())
						event.getPlayer().inventory.getCurrentItem().shrink(1);

					((INameable) tileEntity).setCustomSCName(event.getPlayer().inventory.getCurrentItem().getDisplayName());
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(tileEntity.getBlockState().getBlock().getTranslationKey()), ClientUtils.localize("messages.securitycraft:naming.named", ((INameable) tileEntity).getCustomSCName()), TextFormatting.RED);
					return;
				}

				if(tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.UNIVERSAL_BLOCK_REMOVER.get())){
					event.setCanceled(true);

					if(!((IOwnable) tileEntity).getOwner().isOwner(event.getPlayer())){
						if(!(block instanceof IBlockMine) && (!(tileEntity instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)tileEntity).getBlockState().getBlock()).getDisguisedStack(world, event.getPos()).getItem()).getBlock() instanceof DisguisableBlock)))
							PlayerUtils.sendMessageToPlayer(event.getPlayer(), ClientUtils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);

						return;
					}

					if(tileEntity instanceof IModuleInventory)
					{
						boolean isChest = tileEntity instanceof KeypadChestTileEntity;

						for(ItemStack module : ((IModuleInventory)tileEntity).getInventory())
						{
							if(isChest)
								((KeypadChestTileEntity)tileEntity).addOrRemoveModuleFromAttached(module, true);

							Block.spawnAsEntity(world, event.getPos(), module);
						}
					}

					if(block == SCContent.LASER_BLOCK.get()){
						CustomizableTileEntity te = (CustomizableTileEntity)world.getTileEntity(event.getPos());

						for(ItemStack module : te.getInventory())
						{
							if(!module.isEmpty())
								te.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[] {module, ((ModuleItem)module.getItem()).getModuleType()}, te);
						}

						world.destroyBlock(event.getPos(), true);
						LaserBlock.destroyAdjacentLasers(event.getWorld(), event.getPos());
						event.getPlayer().inventory.getCurrentItem().damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));
					}else if(block == SCContent.CAGE_TRAP.get() && world.getBlockState(event.getPos()).get(CageTrapBlock.DEACTIVATED)) {
						BlockPos originalPos = event.getPos();
						BlockPos middlePos = originalPos.up(4);

						new CageTrapBlock.BlockModifier(event.getWorld(), new MutableBlockPos(originalPos), ((IOwnable)tileEntity).getOwner()).loop((w, p, o) -> {
							TileEntity te = w.getTileEntity(p);

							if(te instanceof IOwnable && ((IOwnable)te).getOwner().equals(o))
							{
								Block b = w.getBlockState(p).getBlock();

								if(b == SCContent.REINFORCED_IRON_BARS.get() || (p.equals(middlePos) && b == SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get()))
									w.destroyBlock(p, false);
							}
						});

						world.destroyBlock(originalPos, false);
					}else{
						BlockPos pos = event.getPos();

						if((block instanceof ReinforcedDoorBlock || block instanceof SpecialDoorBlock) && state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
							pos = pos.down();

						if(block == SCContent.INVENTORY_SCANNER.get())
						{
							InventoryScannerTileEntity te = InventoryScannerBlock.getConnectedInventoryScanner(world, event.getPos());

							if(te != null)
								te.getInventory().clear();
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

						te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());

						if(te instanceof CustomizableTileEntity)
							((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModuleType() }, (CustomizableTileEntity)te);

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
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(block == SCContent.FAKE_WATER_BLOCK.get() && state.getFluidState().getFluid() == SCContent.FAKE_WATER.get()){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.FAKE_WATER_BUCKET.get(), 1);
		}else if(block == SCContent.FAKE_LAVA_BLOCK.get() && state.getFluidState().getFluid() == SCContent.FAKE_LAVA.get()){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return new ItemStack(SCContent.FAKE_LAVA_BUCKET.get(), 1);
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
		if(ConfigHandler.SERVER.allowCodebreakerItem.get())
		{
			World world = event.getPlayer().world;
			TileEntity tileEntity = event.getPlayer().world.getTileEntity(event.getPos());

			if(tileEntity instanceof IPasswordProtected)
			{
				if(event.getPlayer().getHeldItem(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getPlayer().getHeldItem(event.getHand()).damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(event.getHand()));

				if(event.getPlayer().isCreative() || new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer(), !ConfigHandler.SERVER.allowCodebreakerItem.get());
				else return true;
			}
		}

		return false;
	}

	private static boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof OwnableTileEntity || tileEntity instanceof IOwnable || block instanceof OwnableBlock);
	}
}
