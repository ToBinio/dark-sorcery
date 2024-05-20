package tobinio.darksorcery.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tobinio.darksorcery.blocks.altar.AltarEntity;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelBlock;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelEntity;

import java.util.Optional;

/**
 * Created: 19.05.24
 *
 * @author Tobias Frischmann
 */
public class BloodStaff extends Item {

    public static final String CONNECT_FUNNEL_KEY = "blood_funnel";

    public BloodStaff(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        BlockEntity entity = world.getBlockEntity(blockPos);

        if (entity instanceof BloodFunnelEntity) {
            setConnectedFunnel(stack, blockPos);
            return ActionResult.SUCCESS;
        }

        if (entity instanceof AltarEntity altar) {
            Optional<BlockPos> connectedFunnel = getConnectedFunnel(stack);

            if (connectedFunnel.isPresent()) {
                BlockPos funnelPos = connectedFunnel.get();
                altar.toggleConnectedFunnel(funnelPos, context.getPlayer());
            }

            clearConnectedFunnel(stack);
            return ActionResult.SUCCESS;
        }

        clearConnectedFunnel(stack);
        return super.useOnBlock(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        var holdItem = false;

        for (ItemStack handItem : entity.getHandItems()) {
            if (handItem == stack) {
                holdItem = true;
                break;
            }
        }

        Optional<BlockPos> connectedFunnel = getConnectedFunnel(stack);

        if (!holdItem || connectedFunnel.isEmpty()) return;

        if (world.isClient()) {
            BloodFunnelBlock.highlightBLock(connectedFunnel.get(), 1);
        }
    }

    private Optional<BlockPos> getConnectedFunnel(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt == null) return Optional.empty();

        NbtCompound compound = nbt.getCompound(CONNECT_FUNNEL_KEY);

        if (nbt.isEmpty()) return Optional.empty();

        return Optional.of(NbtHelper.toBlockPos(compound));
    }

    private void setConnectedFunnel(ItemStack stack, BlockPos pos) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put(CONNECT_FUNNEL_KEY, NbtHelper.fromBlockPos(pos));
    }

    private void clearConnectedFunnel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove(CONNECT_FUNNEL_KEY);
    }
}
