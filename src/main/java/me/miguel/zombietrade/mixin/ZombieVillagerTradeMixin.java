package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    // Usamos Invoke para llamar a los métodos de la clase padre sin romper nada
    @Shadow public abstract TradeOfferList getOffers();
    @Shadow public abstract int getExperience();

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Si el jugador tiene una esmeralda en la mano
        if (itemStack.isOf(Items.EMERALD)) {
            if (!this.getWorld().isClient) {
                TradeOfferList offers = this.getOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    // Abrimos la interfaz de intercambio
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        this.getExperience(), 
                        true, 
                        false
                    );
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene nada que vender todavía..."), true);
                }
            }
            // Devolvemos SUCCESS para activar la animación de la mano
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }
}
