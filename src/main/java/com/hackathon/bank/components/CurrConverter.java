package com.hackathon.bank.components;

import com.hackathon.bank.domain.Curr;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class CurrConverter implements Converter<String, Curr> {
    @Override
    public Result<Curr> convertToModel(String value, ValueContext context) {
        // Implement conversion from string to Curr enum
        // Here, you would typically parse the string and return the corresponding Curr enum value
        // For simplicity, let's assume Curr enum has a static method for conversion
        return Result.ok(Curr.valueOf(value)); // Assuming fromString is a static method in Curr enum
    }

    @Override
    public String convertToPresentation(Curr value, ValueContext context) {
        // Implement conversion from Curr enum to string
        // Here, you would typically return the string representation of the Curr enum value
        return value.toString(); // Assuming Curr enum has a suitable toString() implementation
    }
}