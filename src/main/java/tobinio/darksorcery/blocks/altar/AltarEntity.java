package tobinio.darksorcery.blocks.altar;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tobinio.darksorcery.DarkSorcery;
import tobinio.darksorcery.blocks.AltarBloodContainer;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelBlock;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelEntity;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.recipe.AltarRecipe;
import tobinio.darksorcery.tags.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static tobinio.darksorcery.blocks.altar.AltarBlock.LIT_CANDLES;

/**
 * Created: 09.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarEntity extends BlockEntity {


    public static final long BASE_BLOOD_CAPACITY = FluidConstants.BOTTLE;

    public static final List<Vec3i> TOWER_LOCATIONS = List.of(new Vec3i(5, -1, -1), new Vec3i(4, -1, 1), new Vec3i(2, -1, 2), new Vec3i(0, -1, 3), new Vec3i(-2, -1, 2), new Vec3i(-4, -1, 1), new Vec3i(-5, -1, -1));

    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        protected boolean canInsert(FluidVariant variant) {
            return variant.equals(FluidVariant.of(ModFluids.BLOOD));
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return bloodCapacity;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    public final SingleVariantStorage<ItemVariant> itemStorage = new SingleVariantStorage<>() {
        @Override
        protected ItemVariant getBlankVariant() {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant) {
            return 1;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    private int craftingTime = 0;

    private int altarLevel = 0;
    private long bloodCapacity = 0;

    private final List<BlockPos> connectionFunnels = new ArrayList<>();
    private final List<BlockPos> extractingFunnels = new ArrayList<>();
    private int extractionTick = 0;

    private final float[] towerFilledHeights = new float[TOWER_LOCATIONS.size()];

    public AltarEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ALTAR_ENTITY_TYPE, pos, state);
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        super.markDirty();
    }

    public static void tick(AltarEntity entity) {
        assert entity.world != null;

        //done client and server side for particles
        entity.removeDeletedFunnels();

        if (entity.world.isClient) {
            entity.displayFunnelExtractionProgress();
            entity.displayCraftingProgress();
        } else {
            entity.updateAltar();

            entity.updateCandleLevel();

            entity.extractFromFunnel();

            entity.craft();

            entity.markDirty();
        }
    }

    private void craft() {
        if (craftingTime != 0 && getCurrentRecipe() != null) {
            //crafting in process
            if (continueCrafting()) {
                craftingTime--;
            }

            if (craftingTime == 0) {
                finishCrafting();
            }
        } else {
            AltarRecipe altarRecipe = getCurrentRecipe();

            if (altarRecipe != null) {
                craftingTime = altarRecipe.getTime();
            }
        }
    }

    private AltarRecipe getCurrentRecipe() {
        var item = itemStorage.getResource().toStack();
        var recipe = world.getRecipeManager()
                .getFirstMatch(AltarRecipe.Type.INSTANCE, new SimpleInventory(item), world);

        return recipe.map(RecipeEntry::value).orElse(null);
    }

    private void finishCrafting() {
        try (Transaction transaction = Transaction.openOuter()) {
            AltarRecipe currentRecipe = getCurrentRecipe();

            ItemVariant storedItem = this.itemStorage.getResource();
            long extract = this.itemStorage.extract(storedItem, 1, transaction);

            if (extract == 1) {
                AltarBlock.spawnPopoutItem(world, pos, currentRecipe.getOutput());
                transaction.commit();
            }
        }
    }

    // returns rather the continuation was successful
    private boolean continueCrafting() {
        try (Transaction transaction = Transaction.openOuter()) {
            int bloodPerTick = getCurrentRecipe().getBloodConsumption() / getCurrentRecipe().getTime();

            long extract = this.fluidStorage.extract(FluidVariant.of(ModFluids.BLOOD), bloodPerTick, transaction);

            if (extract == bloodPerTick) {
                transaction.commit();
            } else {
                return false;
            }
        }

        return true;
    }

    public void removeCurrentItem() {
        craftingTime = 0;

        ItemVariant storedItem = this.itemStorage.getResource();

        if (!storedItem.isBlank()) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extract = this.itemStorage.extract(storedItem, 1, transaction);

                if (extract == 1) {
                    AltarBlock.spawnPopoutItem(world, pos, storedItem.toStack());
                    transaction.commit();
                }
            }
        }
    }


    private void updateCandleLevel() {
        int level = this.getAltarLevel();
        Integer lit_candles = this.getCachedState().get(LIT_CANDLES);

        //todo - kill crafting in case the level changes / gets lower

        if (lit_candles != level && world != null) {
            world.setBlockState(pos, this.getCachedState().with(LIT_CANDLES, level));
        }
    }

    private void displayFunnelExtractionProgress() {
        var progress = (20f - this.getExtractionTick()) / 20;

        for (BlockPos extractingFunnel : this.getExtractingFunnels()) {

            Vec3d altarPos = this.getPos().toCenterPos().add(0, 0.5, 0);
            Vec3d FunnelPos = extractingFunnel.toCenterPos().add(0, 0.5, 0);

            Vec3d dif = altarPos.subtract(FunnelPos);
            var offset = dif.multiply(progress);
            var currentPos = FunnelPos.add(offset);

            //todo better particle
            this.world.addParticle(ParticleTypes.CRIT, currentPos.getX(), currentPos.getY(), currentPos.getZ(), 0, 0, 0);
        }
    }

    private void displayCraftingProgress() {
        if (this.craftingTime == 0) return;

        //todo - from recipe level
        var lineCount = 3;

        var value = (this.craftingTime % 40) / 40f * Math.PI * 2;
        var pos = getPos().toCenterPos().add(0, 0.5, 0);

        var radius = (Math.log(this.craftingTime + 2) - Math.log(2)) / 3;

        var offsetPer = (Math.PI * 2) / lineCount;
        for (int i = lineCount; i > 0; i--) {
            var offset = offsetPer * i;

            this.world.addParticle(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, pos.getX() + Math.cos(value + offset) * radius, pos.getY() + Math.sin((value + offset) * 2) / 4, pos.getZ() + Math.sin(value + offset) * radius, 0, 0, 0);
        }

    }

    private void removeDeletedFunnels() {
        this.connectionFunnels.removeIf(blockPos -> {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);

            if (blockEntity instanceof BloodFunnelEntity) {
                return false;
            }

            BloodFunnelBlock.disableEffect(world, blockPos);
            return true;
        });
    }

    private long getMaxExtraction() {
        return FluidConstants.INGOT * altarLevel;
    }

    private void extractFromFunnel() {
        this.extractionTick--;

        if (this.extractionTick <= 0) {
            this.extractingFunnels.clear();

            var toBeExtracted = getMaxExtraction();

            outer:
            while (true) {
                var hasExtracted = false;

                for (BlockPos connectionFunnel : this.connectionFunnels) {
                    if (toBeExtracted < FluidConstants.NUGGET) {
                        break outer;
                    }

                    BlockEntity blockEntity = world.getBlockEntity(connectionFunnel);

                    if (blockEntity instanceof BloodFunnelEntity bloodFunnelEntity) {
                        try (var transaction = Transaction.openOuter()) {
                            long extract = bloodFunnelEntity.storage.extract(FluidVariant.of(ModFluids.BLOOD), FluidConstants.NUGGET, transaction);

                            if (extract != 0) {
                                long insert = this.fluidStorage.insert(FluidVariant.of(ModFluids.BLOOD), extract, transaction);

                                if (insert == extract) {
                                    hasExtracted = true;
                                    toBeExtracted -= extract;
                                    transaction.commit();

                                    if (!this.extractingFunnels.contains(connectionFunnel)) {
                                        this.extractingFunnels.add(connectionFunnel);
                                    }
                                }
                            }
                        }
                    }
                }

                if (!hasExtracted) {
                    break;
                }
            }

            this.extractionTick = 20;
        }
    }

    private void updateAltar() {
        altarLevel = calculateAltarLevel();
    }

    private int calculateAltarLevel() {
        if (world == null) {
            DarkSorcery.LOGGER.error("trying to get AltarLevel but world is null");
            return 0;
        }

        boolean isValidStructure = true;

        //get ground
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!this.world.getBlockState(pos.add(x, -1, z)).isIn(ModTags.ALTAR_GROUND)) {
                    isValidStructure = false;
                }
            }
        }

        //check towers
        int tier1 = 0;
        int tier2 = 0;
        int tier3 = 0;
        int tier4 = 0;
        int tier5 = 0;

        long storageCapacity = 0;
        ArrayList<Long> capacityPerLayer = new ArrayList<>();
        ArrayList<Long> towerHeights = new ArrayList<>();

        for (Vec3d towerLocation : getRotatedTowerLocations()) {
            var height = 0;
            var towerPos = pos.add((int) Math.round(towerLocation.x), (int) Math.round(towerLocation.y), (int) Math.round(towerLocation.z));

            if (!world.getBlockState(towerPos).isIn(ModTags.ALTAR_TOWER_BLOCKS)) {
                isValidStructure = false;
            }

            while (true) {
                BlockState block = world.getBlockState(towerPos);

                if (block.getBlock() instanceof AltarBloodContainer altarBloodContainer) {

                    int i1 = (height + 1) - capacityPerLayer.size();
                    for (int i = 0; i <= i1; i++) {
                        capacityPerLayer.add(0L);
                    }

                    capacityPerLayer.set(height, capacityPerLayer.get(height) + altarBloodContainer.getStorage());
                    storageCapacity += altarBloodContainer.getStorage();
                }

                if (!block.isIn(ModTags.ALTAR_TOWER_BLOCKS)) {
                    break;
                }

                //todo also count higher level as lower level
                if (block.isIn(ModTags.ALTAR_TIER1_BLOCKS)) tier1++;
                if (block.isIn(ModTags.ALTAR_TIER2_BLOCKS)) tier2++;
                if (block.isIn(ModTags.ALTAR_TIER3_BLOCKS)) tier3++;
                if (block.isIn(ModTags.ALTAR_TIER4_BLOCKS)) tier4++;
                if (block.isIn(ModTags.ALTAR_TIER5_BLOCKS)) tier5++;

                towerPos = towerPos.up();
                height++;
            }

            towerHeights.add((long) height);
        }

        bloodCapacity = storageCapacity + BASE_BLOOD_CAPACITY;
        var bloodToDived = fluidStorage.amount - BASE_BLOOD_CAPACITY;

        for (int height = 0; height < capacityPerLayer.size(); height++) {
            for (int i = 0; i < towerHeights.size(); i++) {
                var towerHeight = towerHeights.get(i);
                if (towerHeight == height) {
                    towerFilledHeights[i] = height;
                }
            }

            if (capacityPerLayer.get(height) <= bloodToDived) {
                bloodToDived -= capacityPerLayer.get(height);
                continue;
            }

            float filledPercentage = (float) bloodToDived / capacityPerLayer.get(height);

            for (int i = 0; i < towerHeights.size(); i++) {
                var towerHeight = towerHeights.get(i);
                if (towerHeight > height) {
                    towerFilledHeights[i] = height + filledPercentage;
                }
            }

            break;
        }

        if (!isValidStructure) {
            return 0;
        }

        if (tier5 > 10) return 5;
        if (tier4 > 10) return 4;
        if (tier3 > 10) return 3;
        if (tier2 > 10) return 2;
        if (tier1 > 10) return 1;

        return 0;
    }

    public int getAltarLevel() {
        return this.altarLevel;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.put("fluidVariant", fluidStorage.variant.toNbt());
        tag.putLong("fluidAmount", fluidStorage.amount);

        tag.put("itemVariant", itemStorage.variant.toNbt());
        tag.putLong("itemAmount", itemStorage.amount);

        tag.putInt("craftingTime", craftingTime);
        tag.putInt("extractionTick", extractionTick);

        NbtList connected = new NbtList();
        for (BlockPos connectionFunnel : connectionFunnels) {
            connected.add(NbtHelper.fromBlockPos(connectionFunnel));
        }
        tag.put("connectedFunnels", connected);

        NbtList extracting = new NbtList();
        for (BlockPos extractingFunnel : extractingFunnels) {
            extracting.add(NbtHelper.fromBlockPos(extractingFunnel));
        }
        tag.put("extractingFunnels", extracting);

        NbtList fillHeights = new NbtList();
        for (Float fillHeight : towerFilledHeights) {
            fillHeights.add(NbtFloat.of(fillHeight));
        }
        tag.put("towerFilledHeights", fillHeights);

        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        fluidStorage.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        fluidStorage.amount = tag.getLong("fluidAmount");

        itemStorage.variant = ItemVariant.fromNbt(tag.getCompound("itemVariant"));
        itemStorage.amount = tag.getLong("itemAmount");

        craftingTime = tag.getInt("craftingTime");
        extractionTick = tag.getInt("extractionTick");

        connectionFunnels.clear();
        var connected = tag.getList("connectedFunnels", NbtList.COMPOUND_TYPE);
        for (NbtElement nbtElement : connected) {
            connectionFunnels.add(NbtHelper.toBlockPos((NbtCompound) nbtElement));
        }

        extractingFunnels.clear();
        var extracting = tag.getList("extractingFunnels", NbtList.COMPOUND_TYPE);
        for (NbtElement nbtElement : extracting) {
            extractingFunnels.add(NbtHelper.toBlockPos((NbtCompound) nbtElement));
        }

        var fillHeights = tag.getList("towerFilledHeights", NbtList.FLOAT_TYPE);
        for (int i = 0; i < fillHeights.size(); i++) {
            towerFilledHeights[i] = fillHeights.getFloat(i);
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void toggleConnectedFunnel(BlockPos pos, PlayerEntity player) {
        assert world != null;

        if (connectionFunnels.contains(pos)) {
            connectionFunnels.remove(pos);
            BloodFunnelBlock.disableEffect(world, pos);
        } else {

            if (pos.isWithinDistance(getPos(), 10)) {
                connectionFunnels.add(pos);
            } else {
                BloodFunnelBlock.disableEffect(world, pos);
                player.sendMessage(Text.of("To far away"), true);
            }
        }

        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        markDirty();
    }

    public List<Vec3d> getRotatedTowerLocations() {
        Direction rotation = this.getCachedState().get(Properties.HORIZONTAL_FACING);

        return TOWER_LOCATIONS.stream()
                .map(towerLocation -> new Vec3d(towerLocation.getX(), towerLocation.getY(), towerLocation.getZ()).rotateY((float) (-rotation.asRotation() * (Math.PI) / 180 + Math.PI)))
                .collect(Collectors.toList());

    }

    public List<BlockPos> getConnectionFunnels() {
        return connectionFunnels;
    }

    public List<BlockPos> getExtractingFunnels() {
        return extractingFunnels;
    }

    public int getExtractionTick() {
        return extractionTick;
    }

    public float[] getTowerFilledHeights() {
        return towerFilledHeights;
    }
}
