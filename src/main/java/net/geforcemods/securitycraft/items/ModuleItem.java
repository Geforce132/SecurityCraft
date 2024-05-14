package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleItem extends Item {
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean containsCustomData;
	private boolean canBeCustomized;
	private int guiToOpen;

	public ModuleItem(ModuleType module, boolean containsCustomData) {
		this(module, containsCustomData, false, -1);
	}

	public ModuleItem(ModuleType module, boolean containsCustomData, boolean canBeCustomized, int guiToOpen) {
		this.module = module;
		this.containsCustomData = containsCustomData;
		this.canBeCustomized = canBeCustomized;
		this.guiToOpen = guiToOpen;

		setMaxStackSize(1);
		setCreativeTab(SecurityCraft.TECHNICAL_TAB);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IModuleInventory) {
			IModuleInventory inv = (IModuleInventory) te;
			ItemStack stack = player.getHeldItem(hand);
			ModuleType type = ((ModuleItem) stack.getItem()).getModuleType();

			if (te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player))
				return EnumActionResult.PASS;

			if (inv.acceptsModule(type) && !inv.hasModule(type)) {
				if (!world.isRemote) {
					inv.insertModule(stack, false);

					if (inv instanceof LinkableBlockEntity) {
						LinkableBlockEntity linkable = (LinkableBlockEntity) inv;

						linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), false), linkable);
					}

					if (!player.isCreative())
						stack.shrink(1);
				}

				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!player.isSneaking()) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			if (canBeCustomized()) {
				player.openGui(SecurityCraft.instance, guiToOpen, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		if (containsCustomData || canBeCustomized())
			list.add(Utils.localize("tooltip.securitycraft:module.modifiable").getFormattedText());
		else
			list.add(Utils.localize("tooltip.securitycraft:module.notModifiable").getFormattedText());

		if (canBeCustomized()) {
			Block addon = getBlockAddon(stack);

			if (addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", TextFormatting.GRAY + Utils.localize(addon).getFormattedText()).getFormattedText());
		}

		if (containsCustomData) {
			boolean affectsEveryone = false;
			int playerCount = 0;
			int teamCount = 0;

			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();

				affectsEveryone = tag.getBoolean("affectEveryone");

				if (!affectsEveryone) {
					playerCount = ModuleItem.getPlayersFromModule(stack).size();
					teamCount = tag.getTagList("ListedTeams", Constants.NBT.TAG_STRING).tagCount();
				}
			}

			if (affectsEveryone)
				list.add(Utils.localize("tooltip.securitycraft.component.list_module_data.affects_everyone").setStyle(Utils.GRAY_STYLE).getFormattedText());
			else {
				list.add(Utils.localize("tooltip.securitycraft.component.list_module_data.added_players", playerCount).setStyle(Utils.GRAY_STYLE).getFormattedText());
				list.add(Utils.localize("tooltip.securitycraft.component.list_module_data.added_teams", teamCount).setStyle(Utils.GRAY_STYLE).getFormattedText());
			}
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public static Block getBlockAddon(ItemStack moduleStack) {
		ItemStack stack = getAddonAsStack(moduleStack);

		if (stack.getItem() instanceof ItemBlock)
			return ((ItemBlock) stack.getItem()).getBlock();
		else
			return null;
	}

	public static ItemStack getAddonAsStack(ItemStack stack) {
		if (!stack.hasTagCompound())
			return ItemStack.EMPTY;

		NBTTagList items = stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		if (items != null && !items.isEmpty())
			return new ItemStack(items.getCompoundTagAt(0));

		return ItemStack.EMPTY;
	}

	public boolean canBeCustomized() {
		return canBeCustomized;
	}

	public static boolean doesModuleHaveTeamOf(ItemStack module, String name, World level) {
		ScorePlayerTeam team = level.getScoreboard().getPlayersTeam(name);

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		//@formatter:off
		return team != null && StreamSupport.stream(module.getTagCompound().getTagList("ListedTeams", Constants.NBT.TAG_STRING).spliterator(), false)
				.filter(NBTTagString.class::isInstance)
				.map(tag -> ((NBTTagString) tag).getString())
				.anyMatch(team.getName()::equals);
		//@formatter:on
	}

	public static List<String> getPlayersFromModule(ItemStack stack) {
		List<String> list = new ArrayList<>();

		if (stack.getItem() instanceof ModuleItem && stack.hasTagCompound()) {
			for (int i = 1; i <= MAX_PLAYERS; i++) {
				if (stack.getTagCompound().getString("Player" + i) != null && !stack.getTagCompound().getString("Player" + i).isEmpty())
					list.add(stack.getTagCompound().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}
}
