package me.miguel.zombietrade.mixin;

import me.miguel.zombietrade.ZombieMerchant;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerTradeMixin extends ZombieEntity implements ZombieMerchant {

    @Shadow public abstract TradeOfferList getOffers();
    @Shadow public abstract int getExperience();

    public ZombieVillagerTradeMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public TradeOfferList getZombieOffers() { return this.getOffers(); }

    @Override
    public int getZombieExperience() { return this.getExperience(); }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.EMERALD)) {
            if (!player.getWorld().isClient()) {
                TradeOfferList offers = this.getZombieOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    player.sendTradeOffers(
                        player.currentScreenHandler.syncId, 
                        offers, 
                        1, 
                        this.getZombieExperience(), 
                        true, 
                        false
                    );
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene nada que vender..."), true);
                }
            }
            cir.setReturnValue(ActionResult.success(player.getWorld().isClient()));
        }
    }
}
