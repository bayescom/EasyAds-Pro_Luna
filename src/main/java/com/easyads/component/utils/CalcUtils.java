package com.easyads.component.utils;

public class CalcUtils {
    private static final float DELTA = 1e-8f;

    public static int calcTrend(Number base, Number compare) {
        if(null == base || null == compare) {
            return 0;
        }

        if(base instanceof Double || compare instanceof Double) {
            double delta = base.doubleValue() - compare.doubleValue();
            if(Math.abs(delta) <= DELTA) {
                return 0;
            } else {
                return delta > 0 ? 1 : -1;
            }
        } else if (base instanceof Float || compare instanceof Float) {
            float delta = base.floatValue() - compare.floatValue();
            if(Math.abs(delta) <= DELTA) {
                return 0;
            } else {
                return delta > 0 ? 1 : -1;
            }
        } else if (base instanceof Long || compare instanceof Long) {
            long delta = base.longValue() - compare.longValue();
            if(0 == delta) {
                return 0;
            } else {
                return delta > 0 ? 1 : -1;
            }
        } else {
            int delta = base.intValue() - compare.intValue();
            if(0 == delta) {
                return 0;
            } else {
                return delta > 0 ? 1 : -1;
            }
        }
    }

    // 指标计算百分比统一的函数
    public static String calcRate(Number numerator, Number denominator, int digits) {
        // 百分比保留的小数位，要在浮点数的基础上加2位
        float rate = calcRateFloat(numerator, denominator, digits + 2);
        return String.format("%." + digits + "f%%", rate * 100f);
    }

    // 默认百分比保留两位小数
    public static String calcRate(Number numerator, Number denominator) {
        return calcRate(numerator, denominator, 2);
    }

    // 指标计算小数统一的函数
    public static float calcRateFloat(Number numerator, Number denominator, int digits) {
        float rate = 0.0f;
        if(null == numerator || null == denominator || 0 == denominator.floatValue()) {
            return rate;
        }

        float multiplier = (float) Math.pow(10.0f, digits);
        rate = (Math.round(multiplier * numerator.floatValue() / denominator.floatValue()) / multiplier);

        return rate;
    }

    // 默认小数计算保留4位小数，用来计算百分比后两位
    public static float calcRateFloat(Number numerator, Number denominator) {
        return calcRateFloat(numerator, denominator, 4);
    }

    public static float calcEcpm(Float income, Long imp) {
        if (null != imp) {
            return calcRateFloat(income, 1.0 * imp / 1000.0f,2);
        }
        return 0.0f;
    }

    // 请求ecpm保留3位小数
    public static float calcReqEcpm(Float income, Long req) {
        if (null != req) {
            return calcRateFloat(income, 1.0 * req / 1000.0f,3);
        }
        return 0.0f;
    }

    public static float calcEcpc(Float income, Long click) {
        return calcRateFloat(income, click,2);
    }

    public static Float calcGapPercentValue(Number base, Number compare) {
        if (null == base || null == compare) {
            return null;
        }

        if (0 == compare.floatValue()) {
            return 0f;
        } else {
            return Math.round(1000.0f * (base.floatValue() - compare.floatValue()) / compare.floatValue()) / 1000.0f;
        }
    }

    public static String calcGapPercent(Number base, Number compare) {
        if(null == base || null == compare) {
            return "-";
        }

        if(0 == compare.floatValue()) {
            return "-";
        } else {
            return String.format("%.2f%%", 100.0f * (base.floatValue() - compare.floatValue()) / compare.floatValue());
        }
    }

    private static float decimalNumber(Number base, Number compare) {
        // 根据两个数的小数点位数，获得要基于这两个数的计算结果要保留的小数位数n，返回10^n，用于计算
        String compareString, baseString;
        if(base instanceof Double || compare instanceof Double) {
            baseString = String.valueOf(base.doubleValue());
            compareString = String.valueOf(compare.doubleValue());
        } else {
            baseString = String.valueOf(base.floatValue());
            compareString = String.valueOf(compare.floatValue());
        }

        int baseDecimal = baseString.split("\\.")[1].length();
        int compareDecimal = compareString.split("\\.")[1].length();
        int maxDecimal = Math.max(compareDecimal, baseDecimal);
        return 1.0f * Math.round(Math.pow(10, Math.min(maxDecimal, 3))); // 这里这样做的原因是怕有溢出风险，最多也就保留3位小数了
    }

    public static Number calcContrastSubValue(Number base, Number compare) {
        if(null == compare && null == base) {
            return 0;
        }

        if(null == compare) {
            return base;
        }

        if(null == base) {
            return calcContrastSubValue(0, compare);
        }

        if (base instanceof Double || compare instanceof Double) {
            float enlarge = decimalNumber(base, compare);
            return Math.round(enlarge * (base.doubleValue() - compare.doubleValue())) / enlarge;
        } else if (base instanceof Float || compare instanceof Float) {
            float enlarge = decimalNumber(base, compare);
            return Math.round(enlarge * (base.floatValue() - compare.floatValue())) / enlarge;
        } else if (base instanceof Long || compare instanceof Long) {
            return base.longValue() - compare.longValue();
        } else {
            return base.intValue() - compare.intValue();
        }
    }

    public static String calcContrastSub(Number base, Number compare) {
        Number sub;
        if (null == compare && null == base) {
            sub = 0;
        } else if (null == compare) {
            sub = base;
        } else if (null == base) {
            sub = calcContrastSubValue(0, compare);
        } else {
            // 保留4位小数，目的是小百分比做差值的时候（例如1.81% - 0.99%）避免舍入成0.00%
            float enlarge = 10000f;
            if (base instanceof Double || compare instanceof Double) {
                sub =  Math.round(enlarge * (base.doubleValue() - compare.doubleValue())) / enlarge;
            } else if (base instanceof Float || compare instanceof Float) {
                sub =  Math.round(enlarge * (base.floatValue() - compare.floatValue())) / enlarge;
            } else if (base instanceof Long || compare instanceof Long) {
                sub =  base.longValue() - compare.longValue();
            } else {
                sub =  base.intValue() - compare.intValue();
            }
        }
        return String.format("%.2f%%", sub.doubleValue() * 100);
    }

    public static Long nullToZero(Long n) {
        if (null == n) {
            return 0L;
        }
        return n;
    }

    public static Float nullToZero(Float n) {
        if (null == n) {
            return 0f;
        }
        return n;
    }
}
