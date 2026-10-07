package gvoper.gokis.client.gui.utils;

import net.minecraft.resources.Identifier;

public class SkillTexture extends SkillResource<Identifier> {
    private final int textureWidth;
    private final int textureHeight;

    public SkillTexture(Identifier defaultImage, Identifier hoverImage, Identifier maxLevelImage, Identifier operationImage, Identifier operationHoverImage, Identifier disabledImage, int textureWidth, int textureHeight) {
        super(defaultImage, hoverImage, maxLevelImage, operationImage, operationHoverImage, disabledImage);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public static class Builder {
        private Identifier defaultImage;
        private Identifier hoverImage;
        private Identifier maxLevelImage;
        private Identifier operationImage;
        private Identifier operationHoverImage;
        private Identifier disabledImage;
        private int textureWidth;
        private int textureHeight;

        public Builder setDefaultImage(Identifier defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        public Builder setHoverImage(Identifier hoverImage) {
            this.hoverImage = hoverImage;
            return this;
        }

        public Builder setMaxLevelImage(Identifier maxLevelImage) {
            this.maxLevelImage = maxLevelImage;
            return this;
        }

        public Builder setOperationImage(Identifier operationImage) {
            this.operationImage = operationImage;
            return this;
        }

        public Builder setOperationHoverImage(Identifier operationHoverImage) {
            this.operationHoverImage = operationHoverImage;
            return this;
        }

        public Builder setDisabledImage(Identifier disabledImage) {
            this.disabledImage = disabledImage;
            return this;
        }

        public Builder setTextureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder setTextureSize(int textureSize) {
            this.textureWidth = textureSize;
            this.textureHeight = textureSize;
            return this;
        }

        public Builder setTextureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        public Builder setTextureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        public SkillTexture build() {
            if (defaultImage == null) throw new IllegalStateException("Default image must be set");
            if (textureWidth <= 0) throw new IllegalStateException("Texture width must be set and greater than 0");
            if (textureHeight <= 0) throw new IllegalStateException("Texture height must be set and greater than 0");
            return new SkillTexture(
                    defaultImage,
                    hoverImage == null ? defaultImage : hoverImage,
                    maxLevelImage == null ? defaultImage : maxLevelImage,
                    operationImage == null ? defaultImage : operationImage,
                    operationHoverImage == null ? (operationImage == null ? defaultImage : operationImage) : operationHoverImage,
                    disabledImage == null ? defaultImage : disabledImage,
                    textureWidth,
                    textureHeight
            );
        }
    }
}
