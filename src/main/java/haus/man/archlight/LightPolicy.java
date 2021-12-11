package haus.man.archlight;

@FunctionalInterface
public interface LightPolicy
{
    boolean isMutable(ArchLight light);
}
