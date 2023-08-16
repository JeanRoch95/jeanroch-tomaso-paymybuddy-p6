package com.paymybuddy.binder;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
@Component
public class BigDecimalBinderCustomizer {

    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(new BigDecimal(text));
                } catch (NumberFormatException e) {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                BigDecimal value = (BigDecimal) getValue();
                return (value != null ? value.toString() : "");
            }
        });
    }
}
