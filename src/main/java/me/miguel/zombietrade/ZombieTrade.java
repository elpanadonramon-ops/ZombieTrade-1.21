package me.miguel.zombietrade;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.item.Item;
import java.util.List;
import java.util.stream.Collectors;
// Eliminada: import java.util.Optional;

public class ZombieTrade implements ModInitializer {
    @Override
    public void onInitialize() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            // ⚠️ Lambda corregido: solo recibe 'random', no 'entity, random'
            factories.add((random) -> {
                // Filtra únicamente las plantillas de armadura
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().endsWith("_armor_trim_smithing_template"))
                        .collect(Collectors.toList());

                // Elige una plantilla al azar (fallback por seguridad)
                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));
                
                // Precio aleatorio entre 16 y 30 esmeraldas (16 + [0..14] = 16..30)
                int price = 16 + random.nextInt(15);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price), // Primera ranura: esmeraldas
                        null,                                 // Segunda ranura: null (vacía)
                        new ItemStack(selectedTrim),          // Resultado: plantilla elegida
                        3,                                    // Usos máximos antes de bloquearse
                        15,                                   // Experiencia que gana el aldeano (int)
                        0.05f,                                // Multiplicador de precio (float)
                        true                                  // demandBonus (booleano obligatorio desde 1.20.5)
                );
            });
        });
    }
}
