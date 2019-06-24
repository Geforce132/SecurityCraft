package net.geforcemods.securitycraft.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

public interface IProxy
{
	public void registerScreens();
	public void registerKeypadChestItem(Register<Item> event);
	public List<Block> getOrPopulateToTint();
	public void cleanup();
	public void registerKeybindings();
	public World getClientWorld();
	public PlayerEntity getClientPlayer();
}
