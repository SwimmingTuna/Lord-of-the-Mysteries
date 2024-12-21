package net.swimmingtuna.lotm.util;

import java.util.HashMap;
import java.util.Map;

public class ClientAbilitiesData {
    private static final Map<String, String> registeredAbilities = new HashMap<>();

    public static void setAbilities(String combination, String abilityName) {
        registeredAbilities.put(combination, abilityName);
    }
    public static void clearAbilities() {
        registeredAbilities.clear();
    }

    public static Map<String, String> getAbilities() {
        return new HashMap<>(registeredAbilities);
    }

    public static String getAbilityByCombination(String combination) {
        return registeredAbilities.get(combination);
    }
}