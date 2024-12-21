package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendParticleS2C {
    private final ResourceLocation particleType;
    private final ParticleOptions particleOptions;
    private final double x;
    private final double y;
    private final double z;
    private final double deltaX;
    private final double deltaY;
    private final double deltaZ;

    public SendParticleS2C(ParticleOptions particleOptions, double x, double y, double z, double deltaX, double deltaY, double deltaZ) {
        this.particleOptions = particleOptions;
        this.particleType = BuiltInRegistries.PARTICLE_TYPE.getKey(particleOptions.getType());
        this.x = x;
        this.y = y;
        this.z = z;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
    }

    public SendParticleS2C(FriendlyByteBuf buf) {
        this.particleType = buf.readResourceLocation();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.deltaX = buf.readDouble();
        this.deltaY = buf.readDouble();
        this.deltaZ = buf.readDouble();

        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(this.particleType);
        this.particleOptions = readParticleOptions(buf, type);
    }

    private static <T extends ParticleOptions> ParticleOptions readParticleOptions(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(particleType);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(deltaX);
        buf.writeDouble(deltaY);
        buf.writeDouble(deltaZ);

        particleOptions.writeToNetwork(buf);
    }

    public ResourceLocation getParticle() {
        return particleType;
    }

    public static void handle(SendParticleS2C msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.addAlwaysVisibleParticle(msg.particleOptions, msg.x, msg.y, msg.z, msg.deltaX, msg.deltaY, msg.deltaZ);
            }
        });
        context.setPacketHandled(true);
    }
}