package me.strukteon.bulbybot.utils;
/*
    Created by nils on 25.08.2018 at 00:33.
    
    (c) nils 2018
*/

public interface MultiConsumer<T, U, V> {

    void accept(T t, U u, V v);

}
