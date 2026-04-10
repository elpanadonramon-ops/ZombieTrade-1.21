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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import java.util.List;
import java.util.stream.Collectors;
// ✅ Eliminada: import java.util.Optional; (no se necesita)

public class ZombieTrade implements ModInitializer {
    @Override
    public void onInitialize() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, factories -> {
            // ✅ Lambda corregido: 3 parámetros según TradeOffers.Factory [[2]]
            factories.add((ServerWorld world, Entity entity, Random random) -> {
                // Filtra únicamente plantillas de armadura
                List<Item> allTrims = Registries.ITEM.stream()
                        .filter(item -> Registries.ITEM.getId(item).getPath().endsWith("_armor_trim_smithing_template"))
                        .collect(Collectors.toList());

                // Elige una plantilla al azar (fallback por seguridad)
                Item selectedTrim = allTrims.isEmpty() ? Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE : allTrims.get(random.nextInt(allTrims.size()));
                
                // ✅ Precio aleatorio entre 16 y 30 esmeraldas (16 + [0..14] = 16..30)
                int price = 16 + random.nextInt(15);

                // ✅ Constructor de 5 parámetros (NO usa Optional, compatible con 1.21.11) [[1]]
                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price), // Item de compra
                        new ItemStack(selectedTrim),           // Item de venta
                        3,                                     // maxUses
                        15,                                    // merchantExperience
                        0.05f                                  // priceMultiplier (float)
                );
            });
        });
    }
}
