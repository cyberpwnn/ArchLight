package haus.man.archlight;

public interface Ticked {
    String getId();

    long getStaleness();

    void onTick();
}
