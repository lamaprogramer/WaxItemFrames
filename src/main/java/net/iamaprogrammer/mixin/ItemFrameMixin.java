package net.iamaprogrammer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.iamaprogrammer.WaxItemFrames;
import net.iamaprogrammer.util.WaxedItemFrameAccess;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameMixin implements WaxedItemFrameAccess {
    @Unique
    private final ItemFrameEntity THIS = (ItemFrameEntity)(Object)this;
    @Unique
    private boolean waxed;

    @Inject(
        method = "interact",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"
        ),
        cancellable = true
    )
    private void waxRotationLock(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (this.isWaxed()) {
            THIS.playSound(SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL, 1.0f, 1.0f);
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void waxDropLock(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.isWaxed()) {
            THIS.playSound(SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL, 1.0f, 1.0f);
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "interact",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void wax(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir,
                                @Local ItemStack playerHandStack) {
        if (!player.getWorld().isClient() && !playerHandStack.isEmpty() && player.isSneaking()) {
            if (!this.isWaxed() && playerHandStack.getItem() instanceof HoneycombItem) {
                this.setWaxed(true);
                playerHandStack.decrementUnlessCreative(1, player);
                THIS.playSound(SoundEvents.ITEM_HONEYCOMB_WAX_ON, 1.0f, 1.0f);
                player.getWorld().syncWorldEvent(null, 3003, THIS.getBlockPos(), 0);
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (this.isWaxed() && playerHandStack.getItem() instanceof AxeItem) {
                this.setWaxed(false);
                playerHandStack.damage(1, player);
                THIS.playSound(SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL, 1.0f, 1.0f);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "canStayAttached", at = @At("HEAD"), cancellable = true)
    private void waxFixed(CallbackInfoReturnable<Boolean> cir) {
        if (WaxItemFrames.CONFIG.isItemFrameFixedWhenWaxed() && this.isWaxed()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("Waxed", this.isWaxed());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setWaxed(nbt.getBoolean("Waxed").get());
    }

    @Override
    public void setWaxed(boolean waxed) {
        this.waxed = waxed;
    }

    @Override
    public boolean isWaxed() {
        return this.waxed;
    }
}
