package com.ccbfm.music.player.tool;


import androidx.lifecycle.MutableLiveData;

/**
 * @author ccbfm
 */
public final class LiveDataBus extends HashMapMode<String, MutableLiveData<?>> {

    private LiveDataBus() {
        super();
    }

    @SuppressWarnings("unchecked")
    public <T> MutableLiveData<T> with(String key) {
        return (MutableLiveData<T>) getValue(key);
    }

    private static class SingleTonHolder {
        private final static LiveDataBus INSTANCE = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingleTonHolder.INSTANCE;
    }


    @Override
    protected MutableLiveData<?> createValue() {
        return new MutableLiveData<>();
    }

}
