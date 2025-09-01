package com.conductor.adapter;

//no the right place for this interface
public interface Adapter {
    /**
     * {@code init} is invoked when applications starts. This must be
     * used to initialise any resources that an adapter might need.
     *
     * @return status of an adapter
     */
    AdapterStatus init();

    /**
     * @return name of the adapter. Must be unique otherwise
     * a runtime exception is thrown when this adapter is loaded
     * in the core.
     */
    String getName();
}
