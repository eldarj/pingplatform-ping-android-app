package com.eldarja.ping.helpers;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public abstract class MyAbstractRunnable<T> implements Serializable {

    public abstract void run(T t);

    public void error(@Nullable Integer statusCode, @Nullable String errorMessage) {
        throw new UnsupportedOperationException("Error not implemented ie. overriden");
    }

    public Class<T> getGenericType()
    {
        Class<T> persistentClass = (Class<T>)
                ((ParameterizedType)getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];

        return persistentClass;
    }

}
