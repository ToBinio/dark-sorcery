package tobinio.darksorcery.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;

/**
 * Created: 23.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarRecipe implements Recipe<Inventory> {

    public static class Type implements RecipeType<AltarRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "altar_recipe";
    }

    private final Ingredient input;
    private final ItemStack output;

    private final int bloodConsumption;
    private final int time;

    public AltarRecipe(Ingredient input, ItemStack output, int bloodConsumption, int time) {
        this.input = input;
        this.output = output;
        this.bloodConsumption = bloodConsumption;
        this.time = time;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getBloodConsumption() {
        return bloodConsumption;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return inventory.containsAny(getInput());
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AltarRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class AltarRecipeSerializer implements RecipeSerializer<AltarRecipe> {

        public static final AltarRecipeSerializer INSTANCE = new AltarRecipeSerializer();

        private static final Codec<AltarRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input")
                        .forGetter(AltarRecipe::getInput), Registries.ITEM.getCodec()
                        .xmap(ItemStack::new, ItemStack::getItem)
                        .fieldOf("output")
                        .forGetter(AltarRecipe::getOutput), Codec.INT.fieldOf("blood_consumption")
                        .forGetter(AltarRecipe::getBloodConsumption), Codec.INT.fieldOf("time").forGetter(AltarRecipe::getTime))
                .apply(instance, AltarRecipe::new));

        @Override
        public Codec<AltarRecipe> codec() {
            return CODEC;
        }

        @Override
        public AltarRecipe read(PacketByteBuf buf) {
            var input = Ingredient.fromPacket(buf);
            var output = buf.readItemStack();
            var bloodConsumption = buf.readInt();
            var time = buf.readInt();

            return new AltarRecipe(input, output, bloodConsumption, time);
        }

        @Override
        public void write(PacketByteBuf packetData, AltarRecipe recipe) {
            recipe.getInput().write(packetData);
            packetData.writeItemStack(recipe.getOutput());
            packetData.writeInt(recipe.getBloodConsumption());
            packetData.writeInt(recipe.getTime());
        }
    }
}
