package me.miguel.zombietrade;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.item.ArmorTrimItem;
import java.util.stream.Collectors;

public class ZombieTrade implements ModInitializer {

    @Override
    public void onInitialize() {
        // Registramos el tradeo para el ARMORER (Herrero de armaduras) nivel 5 (Maestro)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            factories.add((entity, random) -> {
                
                // 1. Obtenemos todos los Armor Trims registrados en el juego
                var allTrims = Registries.ITEM.stream()
                        .filter(item -> item instanceof ArmorTrimItem)
                        .collect(Collectors.toList());

                // 2. Si por alguna razón no hay trims, damos uno por defecto (Sentry) para evitar errores
                var selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));

                // 3. Precio aleatorio entre 16 y 32 esmeraldas
                int price = 16 + random.nextInt(17);

                // 4. Retornamos el tradeo: Esmeraldas -> Armor Trim aleatorio
                return new TradeOffer(
                        new ItemStack(Items.EMERALD, price), // Costo
                        new ItemStack(selectedTrim),         // Recompensa
                        3,                                   // Máximo 3 usos antes de que el aldeano necesite trabajar
                        15,                                  // Experiencia para el aldeano
                        0.05f                                // Multiplicador de precio por demanda
                );
            });
        });
    }
}
