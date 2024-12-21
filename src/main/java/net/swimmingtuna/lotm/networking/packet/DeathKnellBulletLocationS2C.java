package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.entity.DeathKnellBulletEntity;

import java.util.function.Supplier;

public class DeathKnellBulletLocationS2C {
    private final double x;
    private final double y;
    private final double z;
    private final int entityId;

    public DeathKnellBulletLocationS2C(double x, double y, double z, int entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
    }

    // Serializer
    public static void encode(DeathKnellBulletLocationS2C msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.entityId);
    }

    // Deserializer
    public static DeathKnellBulletLocationS2C decode(FriendlyByteBuf buf) {
        return new DeathKnellBulletLocationS2C(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readInt()
        );
    }

    public static void handle(DeathKnellBulletLocationS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.level != null) {
                net.minecraft.world.entity.Entity entity = minecraft.level.getEntity(msg.entityId);
                if (entity instanceof DeathKnellBulletEntity) {
                    entity.setPos(msg.x, msg.y, msg.z);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}