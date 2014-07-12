package vswe.stevesvehicles.recipe.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vswe.stevesvehicles.client.gui.screen.GuiCartAssembler;
import vswe.stevesvehicles.container.slots.SlotAssemblerFuel;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataHull;
import vswe.stevesvehicles.module.data.ModuleDataItemHandler;
import vswe.stevesvehicles.module.data.ModuleType;
import vswe.stevesvehicles.old.Helpers.ResourceHelper;
import vswe.stevesvehicles.old.Helpers.TitleBox;
import vswe.stevesvehicles.old.Items.ModItems;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;

import java.util.ArrayList;
import java.util.List;


public class RecipeHandlerVehicle extends TemplateRecipeHandler {
    protected class CachedVehicleRecipe extends CachedRecipe {
        private List<PositionedStack> ingredients;
        private PositionedStack result;
        private ModuleTypeRow[] rows;
        private int assemblyTime;
        private int coalAmount;
        private int modularCost;
        private boolean valid;

        public CachedVehicleRecipe(ItemStack result) {
            int offset = (GuiCartAssembler.BIG_SLOT_SIZE - ITEM_SIZE) / 2;
            this.result = new PositionedStack(result.copy(), RESULT_X + offset, RESULT_Y + offset);
            ingredients = new ArrayList<PositionedStack>();
            List<ModuleData> modules = ModuleDataItemHandler.getModulesFromItem(result);
            if (modules != null) {
                assemblyTime = TileEntityCartAssembler.FLAT_VEHICLE_BASE_TIME;
                for (ModuleData module : modules) {
                    modularCost += module.getCost();
                    assemblyTime += TileEntityCartAssembler.getAssemblingTime(module);
                    if (module instanceof ModuleDataHull) {
                        ModuleDataHull hull = (ModuleDataHull)module;

                        rows = new ModuleTypeRow[] {
                            new ModuleTypeRow(ModuleType.ENGINE, TileEntityCartAssembler.ENGINE_BOX, hull.getEngineMaxCount(), TileEntityCartAssembler.MAX_ENGINE_SLOTS),
                            new ModuleTypeRow(ModuleType.TOOL, TileEntityCartAssembler.TOOL_BOX, TileEntityCartAssembler.MAX_TOOL_SLOTS, TileEntityCartAssembler.MAX_TOOL_SLOTS),
                            new ModuleTypeRow(ModuleType.ATTACHMENT, TileEntityCartAssembler.ATTACH_BOX, TileEntityCartAssembler.MAX_ATTACHMENT_SLOTS, TileEntityCartAssembler.MAX_ATTACHMENT_SLOTS),
                            new ModuleTypeRow(ModuleType.STORAGE, TileEntityCartAssembler.STORAGE_BOX, hull.getStorageMaxCount(), TileEntityCartAssembler.MAX_STORAGE_SLOTS),
                            new ModuleTypeRow(ModuleType.ADDON, TileEntityCartAssembler.ADDON_BOX, hull.getAddonMaxCount(), TileEntityCartAssembler.MAX_ADDON_SLOTS)
                        };

                        List<ItemStack> items = ModuleDataItemHandler.getModularItems(result);
                        if (items != null) {
                            for (ItemStack item : items) {
                                ModuleData data = ModItems.modules.getModuleData(item);
                                if (data != null) {
                                    if (data instanceof ModuleDataHull) {
                                        ingredients.add(new PositionedStack(item, HULL_X + offset, HULL_Y + offset));
                                    }else{
                                        for (int i = 0; i < rows.length; i++) {
                                            ModuleTypeRow row = rows[i];
                                            if (row.type.getClazz().isAssignableFrom(data.getModuleClass())) {
                                                if (row.length < row.availableLength) {
                                                    int id = row.length;
                                                    int x = id % 6;
                                                    int y = id / 6;
                                                    ingredients.add(new PositionedStack(item, ROW_X + x * GuiCartAssembler.SLOT_SIZE + 1, ROW_Y + i * ROW_OFFSET_Y + y * GuiCartAssembler.SLOT_SIZE + 1));
                                                    row.length++;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            valid = true;
                        }
                    }
                }
                coalAmount = (int)Math.ceil(assemblyTime / (TileEntityFurnace.getItemBurnTime(new ItemStack(Items.coal)) * SlotAssemblerFuel.FUEL_MULTIPLIER));
            }
        }


        @Override
        public List<PositionedStack> getIngredients() {
            return ingredients;
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        public boolean isValid() {
            return valid;
        }
    }

    private class ModuleTypeRow {
        private int length;
        private int availableLength;
        private int maxLength;
        private ModuleType type;
        private TitleBox box;

        private ModuleTypeRow(ModuleType type, TitleBox box, int availableLength, int maxLength) {
            this.box = box;
            this.type = type;
            this.availableLength = availableLength;
            this.maxLength = maxLength;
        }
    }

    public RecipeHandlerVehicle() {

    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }


    private static final int ITEM_SIZE = 16;
    private static final int HULL_X = 140;
    private static final int HULL_Y = -10;
    private static final int RESULT_X = 140;
    private static final int RESULT_Y = 120;

    private static final int TITLE_X = -1;
    private static final int TITLE_Y = -9;
    private static final int ROW_X = 1;
    private static final int ROW_Y = -2;
    private static final int ROW_OFFSET_Y = 28;

    private static final int COST_X = 120;
    private static final int COST_Y = 25;

    private static final int ASSEMBLY_X = 120;
    private static final int ASSEMBLY_Y = 55;

    private static final int FUEL_X = 120;
    private static final int FUEL_Y = 85;
    private static final int COAL_X = 146;
    private static final int COAL_Y = 87;

    @Override
    public String getRecipeName() {
        return "SV Vehicle recipe";
    }

    private RenderItem renderItem = new RenderItem();
    private void drawItem(ItemStack item, int x, int y) {
        renderItem.zLevel = 100;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, item, x, y);

        renderItem.zLevel = 0;
    }

    @Override
    public void drawBackground(int id) {
        CachedVehicleRecipe recipe = (CachedVehicleRecipe)arecipes.get(id);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ResourceHelper.bindResource(GuiCartAssembler.TEXTURE_EXTRA);
        drawTexturedModalRect(HULL_X, HULL_Y, GuiCartAssembler.BIG_SLOT_SRC_X, GuiCartAssembler.BIG_SLOT_SRC_Y, GuiCartAssembler.BIG_SLOT_SIZE, GuiCartAssembler.BIG_SLOT_SIZE);
        drawTexturedModalRect(RESULT_X, RESULT_Y, GuiCartAssembler.BIG_SLOT_SRC_X, GuiCartAssembler.BIG_SLOT_SRC_Y, GuiCartAssembler.BIG_SLOT_SIZE, GuiCartAssembler.BIG_SLOT_SIZE);

        for (int i = 0; i < recipe.rows.length; i++) {
            ModuleTypeRow row = recipe.rows[i];

            int baseY = ROW_Y + i * ROW_OFFSET_Y;

            for (int j = 0; j < row.maxLength; j++) {
                int x = j % 6;
                int y = j / 6;
                int targetX = ROW_X + x * GuiCartAssembler.SLOT_SIZE;
                int targetY = baseY + y * GuiCartAssembler.SLOT_SIZE;
                drawTexturedModalRect(targetX, targetY, GuiCartAssembler.SLOT_SRC_X, GuiCartAssembler.SLOT_SRC_Y, GuiCartAssembler.SLOT_SIZE, GuiCartAssembler.SLOT_SIZE);
                if (j >= row.availableLength) {
                    drawTexturedModalRect(targetX + 1, targetY, GuiCartAssembler.SLOT_DOOR_SRC_X, GuiCartAssembler.SLOT_TOP_DOOR_SRC_Y, GuiCartAssembler.SLOT_DOOR_WIDTH, GuiCartAssembler.SLOT_DOOR_HEIGHT);
                    drawTexturedModalRect(targetX + 1, targetY + GuiCartAssembler.SLOT_DOOR_HEIGHT, GuiCartAssembler.SLOT_DOOR_SRC_X, GuiCartAssembler.SLOT_BOT_DOOR_SRC_Y, GuiCartAssembler.SLOT_DOOR_WIDTH, GuiCartAssembler.SLOT_DOOR_HEIGHT);
                }
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 500);
            drawTexturedModalRect(ROW_X + TITLE_X, baseY + TITLE_Y, GuiCartAssembler.TITLE_BOX_SRC_X, GuiCartAssembler.TITLE_BOX_SRC_Y, GuiCartAssembler.TITLE_BOX_WIDTH, GuiCartAssembler.TITLE_BOX_HEIGHT);
            GL11.glPopMatrix();
        }


        drawItem(new ItemStack(Items.coal), COAL_X, COAL_Y);

        disableTextRendering();
    }

    @Override
    public void drawForeground(int id) {
        enableTextRendering();

        CachedVehicleRecipe recipe = (CachedVehicleRecipe)arecipes.get(id);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        ResourceHelper.bindResource(GuiCartAssembler.TEXTURE_EXTRA);


        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < recipe.rows.length; i++) {
            ModuleTypeRow row = recipe.rows[i];

            int targetX = ROW_X + TITLE_X + GuiCartAssembler.TITLE_BOX_TEXT_OFFSET_X;
            int targetY = ROW_Y + i * ROW_OFFSET_Y + TITLE_Y + GuiCartAssembler.TITLE_BOX_TEXT_OFFSET_Y;
            fontRenderer.drawString(row.box.getName().translate().toUpperCase(), targetX, targetY, row.box.getColor());
        }

        fontRenderer.drawString("Capacity", COST_X, COST_Y, 0x404040); //TODO localize
        fontRenderer.drawString(String.valueOf(recipe.modularCost), COST_X, COST_Y + fontRenderer.FONT_HEIGHT, 0x404040);

        fontRenderer.drawString("Time", ASSEMBLY_X, ASSEMBLY_Y, 0x404040); //TODO localize
        fontRenderer.drawString(GuiCartAssembler.formatTime(recipe.assemblyTime), ASSEMBLY_X, ASSEMBLY_Y + fontRenderer.FONT_HEIGHT, 0x404040);

        fontRenderer.drawString("Fuel", FUEL_X, FUEL_Y, 0x404040); //TODO localize
        String str = String.valueOf(recipe.coalAmount);
        fontRenderer.drawString(str, COAL_X - fontRenderer.getStringWidth(str) - 1, FUEL_Y + fontRenderer.FONT_HEIGHT, 0x404040);
    }


    private static final int TEXTURE_SIZE = 256;
    @SideOnly(Side.CLIENT)
    public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
        float multiplierX = 1F / TEXTURE_SIZE;
        float multiplierY = 1F / TEXTURE_SIZE;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(    x,      y + h,  0,  u * multiplierX,        (v + h) * multiplierY   );
        tessellator.addVertexWithUV(    x + w,  y + h,  0,  (u + w) * multiplierX,  (v + h) * multiplierY   );
        tessellator.addVertexWithUV(    x + w,  y,      0,  (u + w) * multiplierX,  v * multiplierY         );
        tessellator.addVertexWithUV(    x,      y,      0,  u * multiplierX,        v * multiplierY         );
        tessellator.draw();
    }



    @Override
    public void loadCraftingRecipes(ItemStack result) {
        CachedVehicleRecipe cache = new CachedVehicleRecipe(result);
        if (cache.isValid()) {
            arecipes.add(cache);
        }
    }

    @Override
    public String getGuiTexture() {
        return null;
    }



    /*
        ============ METHODS TO GET RID OF THE PAGE TEXT ===========
     */

    private static final ResourceLocation EMPTY_RESOURCE = ResourceHelper.getResource("/gui/blank.png");
    private boolean oldUnicodeFlag;
    private void disableTextRendering() {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        oldUnicodeFlag = fontRenderer.getUnicodeFlag();
        fontRenderer.setUnicodeFlag(true);
        ResourceLocation[] resources = getUnicodeResourceList(fontRenderer);
        for (int i = 0; i < resources.length; i++) {
            resources[i] = EMPTY_RESOURCE;
        }
    }

    private void enableTextRendering() {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.setUnicodeFlag(oldUnicodeFlag);
        ResourceLocation[] resources = getUnicodeResourceList(fontRenderer);
        for (int i = 0; i < resources.length; i++) {
            resources[i] = null;
        }
    }

    private ResourceLocation[] getUnicodeResourceList(FontRenderer fontRenderer) {
        return ReflectionHelper.getPrivateValue(FontRenderer.class, fontRenderer, 0);
    }

}