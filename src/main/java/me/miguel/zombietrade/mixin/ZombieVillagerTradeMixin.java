package me.miguel.zombietrade.mixin;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.network.PacketByteBuf;
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
        
        if (itemStack.isOf(Items.EMERALD)) {
            ZombieVillagerEntity zombi = (ZombieVillagerEntity)(Object)this;
            
            if (!player.getWorld().isClient()) {
                Merchant merchant = (Merchant) zombi;
                TradeOfferList offers = merchant.getOffers();
                
                if (offers != null && !offers.isEmpty()) {
                    // Usamos ExtendedMenuProvider para cumplir con Architectury
                    MenuRegistry.openExtendedMenu((ServerPlayerEntity) player, new ExtendedMenuProvider() {
                        @Override
                        public void saveExtraData(PacketByteBuf buf) {
                            // No necesitamos enviar datos extra por ahora
                        }

                        @Override
                        public Text getDisplayName() {
                            return Text.literal("Aldeano Zombi Comerciante");
                        }

                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                            return new MerchantScreenHandler(syncId, inv, new SimpleMerchant(p) {
                                @Override
                                public TradeOfferList getOffers() { return offers; }
                            });
                        }
                    });
                } else {
                    player.sendMessage(Text.literal("§cEste zombi no tiene ofertas disponibles."), true);
                }
            }
            cir.setReturnValue(ActionResult.success(player.getWorld().isClient()));
        }
    }
}
