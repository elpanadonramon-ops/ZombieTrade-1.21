package me.miguel.zombietrade.mixin;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // Obtenemos la instancia del zombie sin extender clases
        ZombieVillagerEntity self = (ZombieVillagerEntity) (Object) this;
        ItemStack itemStack = player.getStackInHand(hand);

        // Si el jugador tiene una esmeralda en la mano
        if (itemStack.isOf(Items.EMERALD)) {
            // Verificamos si el zombie tiene la capacidad de ser mercader
            if (self instanceof Merchant merchant) {
                TradeOfferList offers = merchant.getOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    if (!self.getWorld().isClient) {
                        player.sendTradeOffers(
                            player.currentScreenHandler.syncId,
                            offers,
                            1, // Nivel 1
                            merchant.getExperience(),
                            true,
                            false
                        );
                    }
                    // Retornamos éxito para detener la ejecución original (no morder, no curar)
                    cir.setReturnValue(ActionResult.success(self.getWorld().isClient));
                }
            }
        }
    }
}
