package me.miguel.zombietrade.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.MerchantOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    // Cambiamos el Shadow para que use el nombre correcto de la 1.21.x
    @Shadow public abstract MerchantOffers method_8259(); 

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Si el jugador tiene una esmeralda, intentamos abrir el trade
        if (itemStack.isOf(Items.EMERALD)) {
            MerchantOffers offers = this.method_8259(); // Usamos el método corregido
            
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
