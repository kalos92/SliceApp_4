package it.polito.mad17.viral.sliceapp;

import java.util.Observer;

/**
 * Created by Kalos on 27/04/2017.
 */

public interface HashMap_Subject {

    public void register(Observer subscriber);
    public void unregister(Observer unsubscriber);
    public void notityObserver();
}
