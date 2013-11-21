package com.example.base.parser;

import com.example.base.exception.ParseException;
import com.example.base.type.IType;

public interface IParser<T extends IType> {
	public static String SUCCESS_CODE = "200";

    public abstract T parse(String jsonString) throws ParseException;


}
