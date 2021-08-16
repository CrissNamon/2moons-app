package ru.kpekepsalt.moonsapp.lambdas;

public interface ConsumerLambda {
    static void empty(){}
    static <V> void empty(V param){}
}
