package tobinio.darksorcery.items.fluidItems;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.items.ModItems;

/**
 * Created: 20.05.24
 *
 * @author Tobias Frischmann
 */
public class TintedBottle extends Item {
    public TintedBottle(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(context.getWorld(), context.getBlockPos(), context.getSide());

        if (storage == null)
            return ActionResult.PASS;

        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();

        ItemStack item = player.getStackInHand(hand);

        try (Transaction transaction = Transaction.openOuter()) {
            long extract = storage.extract(FluidVariant.of(ModFluids.BLOOD), FluidConstants.BOTTLE, transaction);

            if (extract == FluidConstants.BOTTLE) {
                transaction.commit();
                player.setStackInHand(hand, ItemUsage.exchangeStack(item, player, new ItemStack(ModItems.BLOODY_TINTED_GLASS_BOTTLE)));
                player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }
}
