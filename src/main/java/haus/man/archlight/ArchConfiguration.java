package haus.man.archlight;

import com.google.gson.Gson;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.ChronoLatch;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.logging.L;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class ArchConfiguration
{
    private static ArchConfiguration instance;
    private static int hash;
    private static ChronoLatch saveLatch;

    private ArchState state = new ArchState();
    private ArchSettings settings = new ArchSettings();

    @Data
    static class ArchSettings
    {
        // Ticks per minute
        private double fabricTPM = 75;

        // Maximum Light Operations per second (to avoid RF crowding)
        private int maxLIOPS = 4;

        // Maximum Light Operations per ticklist cycle
        private int maxLIOPChunkSize = 8;

        // The target latency to hit
        private double targetLatency = 1000;
    }

    @Data
    static class ArchState
    {
        private KList<BridgeConnection> bridges = new KList<>();
    }

    public static ArchConfiguration get() {
        if(instance == null)
        {
            instance = load();
            saveLatch = new ChronoLatch(60000);
            hash = instance.hashCode();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> instance.save()));
        }

        if(saveLatch.flip() && hash != instance.hashCode())
        {
            instance.save();
            L.v("Saved archlight.json");
        }

        return instance;
    }

    public void save()
    {
        try {
            IO.writeAll(new File("archlight.json"), new Gson().toJson(this));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static ArchConfiguration load()
    {
        try
        {
            if(!new File("archlight.json").exists())
            {
                new ArchConfiguration().save();
            }

            return new Gson().fromJson(IO.readAll(new File("archlight.json")), ArchConfiguration.class);
        }

        catch(Throwable e)
        {
            e.printStackTrace();
            L.f("Try deleting the archlight.json file and restart.");
            System.exit(1);
        }

        return null;
    }
}
