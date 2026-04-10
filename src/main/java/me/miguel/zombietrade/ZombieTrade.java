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
import java.util.Optional;
import java.util.stream.Collectors;

public class ZombieTrade implements ModInitializer {

    @Override
    public void onInitialize() {
        // Registramos el tradeo para el ARMORER nivel 5
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            factories.add((entity, random) -> {
                
                // 1. Buscamos las plantillas
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().contains("smithing_template"))
                        .collect(Collectors.toList());

                // 2. Elegimos una
                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));

                // 3. Precio aleatorio
                int price = 16 + random.nextInt(17);

                // 4. EL CAMBIO CLAVE: Usamos TradedItem y el constructor de 1.21.1
                // Constructor: TradedItem (precio), Optional<TradedItem> (segundo precio), ItemStack (resultado), usos, maxUsos, experiencia, multiplicador, demand...
                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price), 
                        Optional.empty(), 
                        new ItemStack(selectedTrim), 
                        0,      // usos actuales
                        3,      // maxUsos
                        15,     // experiencia
                        0.05f,  // multiplicador
                        0       // demanda
                );
            });
        });
    }
}
