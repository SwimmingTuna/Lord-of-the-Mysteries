package net.swimmingtuna.lotm.spirituality;

//@Mod.EventBusSubscriber()
public class SpiritualityMain {

//@SubscribeEvent
//    public static void spiritualityChange(TickEvent.PlayerTickEvent event) {
//    Player player = event.player;
//    AttributeInstance maxSpiritualityInstance = player.getAttribute(ModAttributes.MAX_SPIRITUALITY.get());
//    AttributeInstance spiritualityInstance = player.getAttribute(ModAttributes.SPIRITUALITY.get());
//    if (maxSpiritualityInstance == null || spiritualityInstance == null) {
//        return;
//        }
//    double maxSpirituality = maxSpiritualityInstance.getValue();
//    double spirituality = spiritualityInstance.getBaseValue();
//    double intel = player.getAttributeValue(ModAttributes.MAX_SPIRITUALITY.get());
//    double curSpiritualityRegen = player.getAttributeValue(ModAttributes.SPIRITUALITY_REGEN.get());
//    double spiritualityRegen = maxSpirituality / 500 * (1 + curSpiritualityRegen / 100);
//    CompoundTag tag = player.getPersistentData();
//    tag.putDouble("spiritualityRegen", spiritualityRegen);
//
//        if (spirituality > maxSpirituality) {
//            spirituality = maxSpirituality;
//        }
//        if (spirituality > maxSpirituality) {
//            spirituality += spiritualityRegen;
//        }
//        maxSpirituality = intel;
//
//        spiritualityInstance.setBaseValue(spirituality);
//        maxSpiritualityInstance.setBaseValue(maxSpirituality);
//    }
//    public static boolean consumeSpirituality(LivingEntity living, double spiritualityToConsume) {
//    if (spiritualityToConsume <= 0) return true;
//    double spirituality = getSpirituality(living);
//    if (spiritualityToConsume > 0) {
//        spirituality -= spiritualityToConsume;
//    }
//    setSpirituality(living,spirituality);
//    return true;
//    }
//
//    public static double getSpirituality(LivingEntity living) {
//    return AttributeHelper.getSaveAttributeValue(ModAttributes.SPIRITUALITY.get(), living);
//    }
//    public static void setSpirituality(LivingEntity living, double spirituality) {
//    AttributeInstance instance = living.getAttribute(ModAttributes.SPIRITUALITY.get());
//    if (instance != null) {
//        instance.setBaseValue(spirituality);
//    }
//    }
}
