package vswe.stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.addon.ModuleShield;

@SideOnly(Side.CLIENT)
public class ModelShield extends ModelVehicle {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/shieldModel.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}

	@Override
	protected int getTextureWidth() {
		return 8;
	}

	@Override
	protected int getTextureHeight() {
		return 4;
	}

	private final ModelRenderer[][] shieldAnchors;
	private final ModelRenderer[][] shields;

	public ModelShield() {
		shields = new ModelRenderer[4][5];
		shieldAnchors = new ModelRenderer[shields.length][shields[0].length];
		for (int i = 0; i < shields.length; i++) {
			for (int j = 0; j < shields[i].length; j++) {
				shieldAnchors[i][j] = new ModelRenderer(this);
				addRenderer(shieldAnchors[i][j]);
				shields[i][j] = new ModelRenderer(this, 0, 0);
				fixSize(shields[i][j]);
				shieldAnchors[i][j].addChild(shields[i][j]);
				shields[i][j].addBox(-1F, // X
						-1F, // Y
						-1F, // Z
						2, // Size X
						2, // Size Y
						2, // Size Z
						0.0F);
				shields[i][j].setRotationPoint(0, // X
						0, // Y
						0 // Z
						);
			}
		}
	}

	@Override
	public void render(ModuleBase module, float yaw, float pitch, float roll, float multiplier, float partialTime) {
		if (module == null || ((ModuleShield) module).hasShield()) {
			super.render(module, yaw, pitch, roll, multiplier, partialTime);
		}
	}

	@Override
	public void applyEffects(ModuleBase module, float yaw, float pitch, float roll) {
		float shieldAngle = module == null ? 0 : ((ModuleShield) module).getShieldAngle();
		float shieldDistance = module == null ? 18 : ((ModuleShield) module).getShieldDistance();
		for (int i = 0; i < shields.length; i++) {
			for (int j = 0; j < shields[i].length; j++) {
				float a = shieldAngle + (float) (Math.PI * 2) * (j / (float) shields[i].length + i / (float) shields.length);
				a = (float) (a % (Math.PI * 100));
				shieldAnchors[i][j].rotateAngleY = a;
				shields[i][j].rotationPointY = ((float) Math.sin(a / 5) * 3F + (i - (shields.length - 1) / 2F) * 5F - 5F) * shieldDistance / 18F;
				shields[i][j].rotationPointZ = shieldDistance;
			}
		}
	}
}
