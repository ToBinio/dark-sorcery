package tobinio.darksorcery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.items.ModItems;

/**
 * Created: 20.05.24
 *
 * @author Tobias Frischmann
 */
@Mixin (BucketItem.class)
public class BucketItemMixin {

    @Inject (method = "use", at = @At (value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, @Local ItemStack itemStack,
            @Local (ordinal = 0) BlockPos blockPos, @Local Direction direction) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, blockPos, direction);

        if (storage == null) return;

        try (Transaction transaction = Transaction.openOuter()) {
            long extract = storage.extract(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BUCKET, transaction);

            if (extract == FluidConstants.BUCKET) {
                transaction.commit();
                user.setStackInHand(hand, ItemUsage.exchangeStack(itemStack, user, new ItemStack(ModItems.BLOOD_BUCKET)));
                user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);

                cir.setReturnValue(TypedActionResult.success(itemStack));
                return;
            }
        }

        cir.setReturnValue(TypedActionResult.fail(itemStack));
    }

}
