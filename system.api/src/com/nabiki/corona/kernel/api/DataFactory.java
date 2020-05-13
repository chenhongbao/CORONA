package com.nabiki.corona.kernel.api;

public interface DataFactory {
    <T> T create(Class<T> clz) throws KerError;

    <T> T create(Class<T> clz, T param) throws KerError;
}
