package haus.man.archlight;

import lombok.Data;
import ninja.bytecode.shuriken.bench.PrecisionStopwatch;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.ChronoLatch;
import ninja.bytecode.shuriken.format.Form;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.math.M;

import java.util.List;

@Data
public class TickList
{
    private KList<Ticked> tickList;
    private long latency = 0;
    private ChronoLatch liopLimiter = new ChronoLatch(1000);
    private int liopsAvailable = 0;

    public TickList()
    {
        tickList = new KList<>();
    }

    public boolean hasLiop()
    {
        return liopsAvailable > 0;
    }

    public boolean liop()
    {
        if(liopsAvailable > 0)
        {
            liopsAvailable--;
            return true;
        }

        return false;
    }

    public int tick()
    {
        if(liopLimiter.flip())
        {
            liopsAvailable = ArchConfiguration.get().getSettings().getMaxLIOPS();
        }

        return tick(ArchConfiguration.get().getSettings().getMaxLIOPChunkSize());
    }

    private int tick(int maxItems)
    {
        int actual = 0;
        PrecisionStopwatch p = PrecisionStopwatch.start();
        sort();

        if(tickList.hasElements())
        {
            latency = tickList.get(0).getStaleness();
        }


        for(int i = 0; i < Math.min(maxItems, tickList.size()); i++)
        {

            // But dont be too busy
            if(tickList.get(i).getStaleness() < ArchConfiguration.get().getSettings().getTargetLatency())
            {
                continue;
            }

            PrecisionStopwatch pp = PrecisionStopwatch.start();
            tickList.get(i).onTick();

            if(pp.getMilliseconds() > (60000 / ArchConfiguration.get().getSettings().getFabricTPM()))
            {
                L.w("Took " + Form.duration(p.getMilliseconds(), 0) + " to tick " + tickList.get(i).getId());
            }

            actual++;

            if(p.getMilliseconds() > (60000 / ArchConfiguration.get().getSettings().getFabricTPM()) / 1.25)
            {
                break;
            }
        }

        return actual;
    }

    public void sort()
    {
        tickList.sort((o1, o2) -> (int) (o2.getStaleness() - o1.getStaleness()));
    }

    public void addBasic(Runnable r)
    {
        addBasic(r, 0L);
    }

    public void addBasic(Runnable r, long intervalDelay)
    {
        Ticked g = new Ticked() {
            private long last = 0;

            @Override
            public String getId() {
                return "basic-" + r.hashCode();
            }

            @Override
            public long getStaleness() {
                return (M.ms() - last) - intervalDelay;
            }

            @Override
            public void onTick() {
                last = M.ms();
                r.run();
            }
        };
        add(g);
    }

    public void add(Ticked t)
    {
        tickList.add(t);
    }
}
