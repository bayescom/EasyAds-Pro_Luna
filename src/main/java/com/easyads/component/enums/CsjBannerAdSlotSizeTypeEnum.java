package com.easyads.component.enums;

public enum CsjBannerAdSlotSizeTypeEnum {
    SIZE_1(1, 600, 300),
    SIZE_2(2, 600, 400),
    SIZE_3(3, 600, 500),
    SIZE_4(4, 600, 260),
    SIZE_5(5, 600, 90),
    SIZE_6(6, 600, 150),
    SIZE_7(7, 640, 100),
    SIZE_8(8, 690, 388);

    private final int type;
    private final int width;
    private final int height;

    CsjBannerAdSlotSizeTypeEnum(int type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public static CsjBannerAdSlotSizeTypeEnum fromType(int type) {
        for (CsjBannerAdSlotSizeTypeEnum size : values()) {
            if (size.type == type) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid adSlotSizeType: " + type);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
