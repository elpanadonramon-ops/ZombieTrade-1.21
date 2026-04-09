package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList; // Cambio aquí
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    // En 1.21.x, getOffers() se mapea como method_8259 y devuelve TradeOfferList
    @Shadow public abstract TradeOfferList method_8259(); 

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Activamos el tradeo solo si el jugador tiene una esmeralda en la mano
        if (itemStack.isOf(Items.EMERALD)) {
            TradeOfferList offers = this.method_8259();
            
            if (offers != null && !offers.isEmpty()) {
                if (!this.getWorld().isClient) {
                    player.sendTradeOffers(
                        player.getNextScreenHandlerFactoryId(), 
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
