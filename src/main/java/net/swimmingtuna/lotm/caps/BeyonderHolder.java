package net.swimmingtuna.lotm.caps;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.util.CapabilitySyncer.core.PlayerCapability;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.EntityCapabilityStatusPacket;
import net.swimmingtuna.lotm.util.CapabilitySyncer.network.SimpleEntityCapabilityStatusPacket;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = LOTM.MOD_ID)
public class BeyonderHolder extends PlayerCapability {
    public static final int SEQUENCE_MIN = 0;
    public static final int SEQUENCE_MAX = 9;
    private static final String REGISTERED_ABILITIES_KEY = "RegisteredAbilities";
    private final RandomSource random;
    private int currentSequence = -1;
    @Nullable private BeyonderClass currentClass = null;
    private int mentalStrength = 0;
    private double spirituality = 100;
    private double maxSpirituality = 100;
    private double spiritualityRegen = 1;

    protected BeyonderHolder(Player entity) {
        super(entity);
        this.random = RandomSource.create();
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide && event.phase == TickEvent.Phase.END) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(event.player);
            holder.regenSpirituality(event.player);
            if (holder.getCurrentClass() != null) {
                holder.getCurrentClass().tick(event.player, holder.getCurrentSequence());
            }
        }

    }

    public void removeClass() {
        this.currentClass = null;
        this.currentSequence = -1;
        this.mentalStrength = 0;
        this.spirituality = 100;
        this.maxSpirituality = 100;
        this.spiritualityRegen = 1;
        this.player.setHealth(20);
        this.player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
        CompoundTag persistentData = this.player.getPersistentData();
        if (persistentData.contains(REGISTERED_ABILITIES_KEY)) {
            persistentData.remove(REGISTERED_ABILITIES_KEY);
        }
        updateTracking();
    }

    public void setClassAndSequence(BeyonderClass newClass, int sequence) {
        this.currentClass = newClass;
        this.currentSequence = sequence;
        this.maxSpirituality = this.currentClass.spiritualityLevels().get(this.currentSequence);
        this.spirituality = this.maxSpirituality;
        this.mentalStrength = this.currentClass.mentalStrength().get(this.currentSequence);
        this.spiritualityRegen = this.currentClass.spiritualityRegen().get(this.currentSequence);
        this.player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.currentClass.maxHealth().get(sequence));
        this.player.setHealth(this.player.getMaxHealth());
        updateTracking();
    }

    public double getMaxSpirituality() {
        return maxSpirituality;
    }

    public void setMaxSpirituality(int maxSpirituality) {
        this.maxSpirituality = maxSpirituality;
        updateTracking();
    }

    public void setMentalStrength(int mentalStrength) {
        this.mentalStrength = mentalStrength;
        updateTracking();
    }

    public int getMentalStrength() {
        return this.mentalStrength;
    }

    public double getSpiritualityRegen() {
        return this.spiritualityRegen;
    }

    public void setSpiritualityRegen(int spiritualityRegen) {
        this.spiritualityRegen = spiritualityRegen;
        updateTracking();
    }

    public double getSpirituality() {
        return this.spirituality;
    }

    public void setSpirituality(double spirituality) {
        this.spirituality = Mth.clamp(spirituality, 0, this.maxSpirituality);
        updateTracking();
    }

    public @Nullable BeyonderClass getCurrentClass() {
        return this.currentClass;
    }

    public void setCurrentClass(BeyonderClass newClass) {
        this.currentClass = newClass;
        updateTracking();
    }

    public void removeCurrentClass() {
        this.currentClass = null;
        updateTracking();
    }

    public int getCurrentSequence() {
        return this.currentSequence;
    }

    public void setCurrentSequence(int currentSequence) {
        this.currentSequence = currentSequence;
        this.maxSpirituality = this.currentClass.spiritualityLevels().get(currentSequence);
        this.spiritualityRegen = this.currentClass.spiritualityRegen().get(currentSequence);
        this.spirituality = this.maxSpirituality;
        updateTracking();
    }

    public void incrementSequence() {
        if (this.currentSequence > SEQUENCE_MIN) {
            this.currentSequence--;
            this.maxSpirituality = this.currentClass.spiritualityLevels().get(this.currentSequence);
            this.spirituality = this.maxSpirituality;
            this.spiritualityRegen = this.currentClass.spiritualityRegen().get(this.currentSequence);
            updateTracking();
        }
    }

    public void decrementSequence() {
        if (this.currentSequence < SEQUENCE_MAX) {
            this.currentSequence++;
            this.maxSpirituality = this.currentClass.spiritualityLevels().get(this.currentSequence);
            this.spirituality = this.maxSpirituality;
            this.spiritualityRegen = this.currentClass.spiritualityRegen().get(this.currentSequence);
            updateTracking();
        }
    }

    public boolean useSpirituality(int amount) {
        if (this.player.isCreative()) {
            return true;
        }
        if (this.spirituality - amount < 0) {
            return false;
        }
        this.spirituality = Mth.clamp(this.spirituality - amount, 0, this.maxSpirituality);
        updateTracking();
        return true;
    }

    public void increaseSpirituality(int amount) {
        this.spirituality = Mth.clamp(this.spirituality + amount, 0, this.maxSpirituality);
        updateTracking();
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("currentSequence", this.currentSequence);
        tag.putInt("mentalStrength", this.mentalStrength);
        tag.putString("currentClass", this.currentClass == null ? "" : BeyonderClassInit.getRegistry().getKey(this.currentClass).toString());
        tag.putDouble("spirituality", this.spirituality);
        tag.putDouble("maxSpirituality", this.maxSpirituality);
        tag.putDouble("spiritualityRegen", this.spiritualityRegen);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.currentSequence = nbt.getInt("currentSequence");
        this.mentalStrength = nbt.getInt("mentalStrength");
        String className = nbt.getString("currentClass");
        if (!className.isEmpty()) {
            this.currentClass = BeyonderClassInit.getRegistry().getValue(new ResourceLocation(className));
        }
        this.spirituality = nbt.getDouble("spirituality");
        this.maxSpirituality = nbt.getDouble("maxSpirituality");
        this.spiritualityRegen = nbt.getDouble("spiritualityRegen");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), BeyonderHolderAttacher.RESOURCE_LOCATION, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return LOTMNetworkHandler.INSTANCE;
    }

    public void regenSpirituality(Entity entity) {
        if (entity instanceof Player && this.spirituality < this.maxSpirituality) {
            double increase = (Mth.nextDouble(this.random, 0.1, 1.0) * (this.spiritualityRegen * 1.5f)) / 5;
            this.spirituality = Mth.clamp(this.spirituality + increase, 0, this.maxSpirituality);
            updateTracking();
        }
    }

    public boolean currentClassMatches(Supplier<? extends BeyonderClass> beyonderClassSupplier) {
        return currentClassMatches(beyonderClassSupplier.get());
    }

    public boolean currentClassMatches(BeyonderClass beyonderClass) {
        return this.currentClass == beyonderClass;
    }

}
