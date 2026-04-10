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
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, (factories) -> {
            factories.add((entity, random) -> {
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().contains("smithing_template"))
                        .collect(Collectors.toList());

                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));
                int price = 16 + random.nextInt(17);

                // Usamos el constructor más básico para evitar errores de campos inexistentes
                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price),
                        new ItemStack(selectedTrim),
                        3,  // maxUses
                        15, // merchantExperience
                        0.05f // priceMultiplier
                );
            });
        });
    }
}
