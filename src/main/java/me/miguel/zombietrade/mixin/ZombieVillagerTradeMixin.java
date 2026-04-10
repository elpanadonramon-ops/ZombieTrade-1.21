package me.miguel.zombietrade.mixin;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.SimpleMerchant;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillagerTradeMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        
        // Si el jugador tiene una esmeralda
        if (itemStack.isOf(Items.EMERALD)) {
            ZombieVillagerEntity zombi = (ZombieVillagerEntity)(Object)this;
            
            if (!player.getWorld().isClient()) {
                // Casteamos el zombi a Merchant (porque internamente lo es)
                // Usamos Architectury para abrir el menú de forma segura
                Merchant merchant = (Merchant) zombi;
                TradeOfferList offers = merchant.getOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    // MenuRegistry es la "magia" de Architectury que evita los crashes de red
                    MenuRegistry.openExtendedMenu(player, new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
                        return new MerchantScreenHandler(syncId, inv, new SimpleMerchant(p) {
                            @Override
                            public TradeOfferList getOffers() { return offers; }
                        });
                    }, Text.literal("Aldeano Zombi Comerciante")));
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene ofertas disponibles."), true);
                }
            }
            // Cancelamos la interacción normal (para que no te muerda al intentar tradear)
            cir.setReturnValue(ActionResult.success(player.getWorld().isClient()));
        }
    }
}
