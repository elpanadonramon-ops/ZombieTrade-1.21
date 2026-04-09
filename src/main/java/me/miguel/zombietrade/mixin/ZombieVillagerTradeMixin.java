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
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity {

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.EMERALD)) {
            // Usamos la variable 'this.getWorld()' de forma que sea compatible con 1.21.11
            World world = player.getWorld(); 

            if (!world.isClient) {
                // Comprobamos si la entidad puede actuar como comerciante
                if ((Object) this instanceof Merchant merchant) {
                    TradeOfferList offers = merchant.getOffers();
                    
                    if (offers != null && !offers.isEmpty()) {
                        player.sendTradeOffers(
                            player.currentScreenHandler.syncId, 
                            offers, 
                            1, 
                            merchant.getExperience(), 
                            true, 
                            false
                        );
                    } else {
                        player.sendMessage(Text.literal("§cEste zombi no tiene ofertas disponibles."), true);
                    }
                }
            }
            // Importante: Usamos la referencia de world que ya tenemos para evitar el error method_37908
            cir.setReturnValue(ActionResult.success(world.isClient));
        }
    }
}
