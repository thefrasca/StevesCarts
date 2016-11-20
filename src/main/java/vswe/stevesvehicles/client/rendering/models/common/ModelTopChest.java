package vswe.stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.storage.chest.ModuleChest;

@SideOnly(Side.CLIENT)
public class ModelTopChest extends ModelVehicle {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/topChestModel.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}

	private final ModelRenderer lid;

	public ModelTopChest() {
		lid = addChest();
	}

	private ModelRenderer addChest() {
		ModelRenderer chestAnchor = new ModelRenderer(this);
		addRenderer(chestAnchor);
		chestAnchor.rotateAngleY = (float) Math.PI * 3F / 2;
		chestAnchor.setRotationPoint(-2.5F, // X
				-3, // Y
				2 // Z
				);
		ModelRenderer base = new ModelRenderer(this, 0, 19);
		fixSize(base);
		chestAnchor.addChild(base);
		base.addBox(6, // X
				2, // Y
				8F, // Z
				12, // Size X
				4, // Size Y
				16, // Size Z
				0.0F);
		base.setRotationPoint(-14.0F, // X
				-5.5F, // Y
				-18.5F // Z
				);
		ModelRenderer lid = new ModelRenderer(this, 0, 0);
		fixSize(lid);
		chestAnchor.addChild(lid);
		lid.addBox(6, // X
				-3, // Y
				-16F, // Z
				12, // Size X
				3, // Size Y
				16, // Size Z
				0.0F);
		lid.setRotationPoint(-14.0F, // X
				-2.5F, // Y
				5.5F // Z
				);
		ModelRenderer lock = new ModelRenderer(this, 0, 39);
		fixSize(lock);
		lid.addChild(lock);
		lock.addBox(1F, // X
				1.5F, // Y
				0.5F, // Z
				2, // Size X
				3, // Size Y
				1, // Size Z
				0.0F);
		lock.setRotationPoint(10.0F, // X
				-3F, // Y
				-17.5F // Z
				);
		return lid;
	}

	@Override
	public void applyEffects(ModuleBase module, float yaw, float pitch, float roll) {
		lid.rotateAngleX = module == null ? 0 : -((ModuleChest) module).getChestAngle();
	}
}
