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

public class ZombieTrade implements ModInitializer {
    @Override
    public void onInitialize() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            factories.add((entity, random) -> {
                // Filtra todos los items que contengan "smithing_template" en su nombre interno
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().contains("smithing_template"))
                        .collect(Collectors.toList());

                // Elige una plantilla al azar de la lista
                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));
                
                // Precio aleatorio entre 16 y 32 esmeraldas
                int price = 16 + random.nextInt(17);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price), 
                        null,                                // Cambiado de Optional.empty() a null
                        new ItemStack(selectedTrim),          
                        3,                                   // Usos máximos
                        15,                                  // Experiencia para el aldeano
                        0.05f,                               // Multiplicador de precio
                        true                                 // demandBonus (nuevo parámetro obligatorio)
                );
            });
        });
    }
}
