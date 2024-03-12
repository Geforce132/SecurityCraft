package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.SecuritySeaRaft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SecuritySeaRaftItem extends BoatItem {
    public SecuritySeaRaftItem(Item.Properties properties) {
        super(true, Boat.Type.BAMBOO, properties);
    }

    @Override
    public Boat getBoat(Level level, HitResult hitResult, ItemStack stack, Player player) {
        Vec3 vec3 = hitResult.getLocation();
        SecuritySeaRaft raft = new SecuritySeaRaft(level, vec3.x, vec3.y, vec3.z);

        if (level instanceof ServerLevel serverLevel)
            EntityType.createDefaultStackConfig(serverLevel, stack, player).accept(raft);

        return raft;
    }
}
