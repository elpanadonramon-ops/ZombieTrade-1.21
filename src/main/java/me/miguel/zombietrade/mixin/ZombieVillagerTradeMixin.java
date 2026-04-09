package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    // En la 1.21.11, ZombieVillagerEntity ya tiene estos métodos internamente, 
    // pero necesitamos hacerles Shadow para usarlos aquí.
    @Shadow public abstract TradeOfferList getOffers();
    @Shadow public abstract int getExperience();

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Si el jugador tiene una esmeralda, abrimos el menú de tradeo
        if (itemStack.isOf(Items.EMERALD)) {
            TradeOfferList offers = this.getOffers();
            
            if (offers != null && !offers.isEmpty()) {
                if (!this.getWorld().isClient) {
                    // Usamos el método estándar de PlayerEntity para abrir tradeos
                    // Esto evita errores de IDs de red manuales
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        this.getExperience(), 
                        true, 
                        false
                    );
                }
                cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
            }
        }
    }
}
