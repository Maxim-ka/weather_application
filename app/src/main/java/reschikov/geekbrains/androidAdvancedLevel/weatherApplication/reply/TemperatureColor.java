package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;

public class TemperatureColor {

    private double temperature;

    private enum Temperature{

        COLDNESS(-35, R.color.coldness), VERY_COLD(-25, R.color.very_cold), MODERATELY_COLD(-20, R.color.moderately_cold),
        COLD(-10, R.color.cold), COOL(5, R.color.cool), GOOD(10, R.color.good), NORM(15, R.color.norm),
        HEAT(20, R.color.heat), WARMER(25, R.color.warmer), VERY_WARM(30, R.color.very_warm), HOT(35, R.color.hot),
        VERY_HOT(40,  R.color.very_hot);

        private final int temp;
        private final int color;

        int getTemp() {
            return temp;
        }

        int getColor() {
            return color;
        }

        Temperature(int temp, int color) {
            this.temp = temp;
            this.color = color;
        }
    }

    TemperatureColor(double min, double max) {
        new TemperatureColor((min + max) / 2);
    }

    TemperatureColor(double temperature) {
        this.temperature = temperature;
    }

    public int getColor(){
        for (int i = 0; i < Temperature.values().length; i++) {
            if (temperature <= Temperature.values()[i].getTemp())
                return Temperature.values()[i].getColor();
        }
        return R.color.roasting;
    }
}
