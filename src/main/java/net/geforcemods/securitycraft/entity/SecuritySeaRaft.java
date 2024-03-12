package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SecuritySeaRaft extends ChestBoat {
    public SecuritySeaRaft(EntityType<? extends Boat> type, Level level) {
        super(SCContent.SECURITY_SEA_RAFT_ENTITY.get(), level);
    }

    public SecuritySeaRaft(Level level, double x, double y, double z) {
        super(SCContent.SECURITY_SEA_RAFT_ENTITY.get(), level);
        setPos(x, y, z);
        xo = y;
        yo = y;
        zo = z;
    }

    @Override
    public Item getDropItem() {
        return SCContent.SECURITY_SEA_RAFT_ITEM.get();
    }
}
