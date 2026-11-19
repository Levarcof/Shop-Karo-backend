package ecommerce.app.Ecommerce.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public  class Money {
    private Money(){}

    public  static  Long toPaise(BigDecimal rupees){
        return rupees.setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();
    }
}
