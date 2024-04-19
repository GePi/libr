package com.github.gepi.libr.app;

import io.jmix.core.metamodel.annotation.DatatypeDef;
import io.jmix.core.metamodel.datatype.Datatype;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Locale;

@DatatypeDef(
        id = "mldp",
        javaClass = MyList.class,
        defaultForClass = true

)
public class MyListDataType implements Datatype<MyList> {
    @Override
    public String format(@Nullable Object value) {
        return null;
    }

    @Override
    public String format(@Nullable Object value, Locale locale) {
        return null;
    }

    @Nullable
    @Override
    public MyList parse(@Nullable String value) throws ParseException {
        return null;
    }

    @Nullable
    @Override
    public MyList parse(@Nullable String value, Locale locale) throws ParseException {
        return null;
    }
}
