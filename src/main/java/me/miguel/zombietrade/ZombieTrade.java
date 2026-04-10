package me.miguel.zombietrade;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.item.Item;
import java.util.List;
import java.util.stream.Collectors;

public class ZombieTrade implements ModInitializer {

    @Override
    public void onInitialize() {
        // Registramos el tradeo para el ARMORER (Herrero de armaduras) nivel 5 (Maestro)
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            factories.add((entity, random) -> {
                
                // 1. Buscamos todos los items que sean plantillas de herrería (Armor Trims)
                // Usamos el ID del item para identificar cuáles son plantillas
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().contains("smithing_template"))
                        .collect(Collectors.toList());

                // 2. Elegimos uno al azar. Si la lista está vacía (por seguridad), usamos Sentry.
                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));

                // 3. Precio aleatorio entre 16 y 32 esmeraldas
                int price = 16 + random.nextInt(17);

                // 4. Retornamos el tradeo: Esmeraldas -> Armor Trim aleatorio
                // Usamos el constructor de TradeOffer compatible con 1.21.1
                return new TradeOffer(
                        new ItemStack(Items.EMERALD, price), // Costo
                        new ItemStack(selectedTrim),         // Recompensa
                        3,                                   // Máximo 3 usos
                        15,                                  // Experiencia
                        0.05f                                // Multiplicador
                );
            });
        });
    }
}
