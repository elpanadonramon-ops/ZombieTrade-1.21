package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    // Referencias a métodos internos necesarios para la 1.21.11
    @Shadow public abstract TradeOfferList method_8259(); // getOffers()
    @Shadow public abstract int getExperience(); // Añadimos este Shadow para corregir el error

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Activamos el intercambio si el jugador tiene una esmeralda
        if (itemStack.isOf(Items.EMERALD)) {
            TradeOfferList offers = this.method_8259();
            
            if (offers != null && !offers.isEmpty()) {
                if (!this.getWorld().isClient) {
                    // En 1.21.11, el ID se obtiene de forma distinta para abrir la interfaz
                    OptionalInt menuId = player.openEditSignScreen(null); // Método auxiliar para obtener ID de red
                    
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
