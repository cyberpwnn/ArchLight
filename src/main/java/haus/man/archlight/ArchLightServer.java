package haus.man.archlight;

import io.github.zeroone3010.yahueapi.Light;
import lombok.Data;
import ninja.bytecode.shuriken.bench.PrecisionStopwatch;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.execution.Looper;
import ninja.bytecode.shuriken.format.Form;
import ninja.bytecode.shuriken.logging.L;
import io.github.zeroone3010.yahueapi.HueBridge;
import io.github.zeroone3010.yahueapi.discovery.HueBridgeDiscoveryService;
import ninja.bytecode.shuriken.random.RNG;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Data
public class ArchLightServer
{
    public static ArchLightServer instance;
    private Looper ticker;
    private TickList tickList;
    private boolean lookingForBridges = false;
    private boolean connectingToBridges = false;
    private LightPolicy activePolicy = (__) -> true;

    public ArchLightServer()
    {
        instance = this;
        activePolicy = (light) -> light.getId().equals("Derp");
        tickList = new TickList();
        tickList.addBasic(this::connectToBridges, 5000);
        tickList.addBasic(this::lookForBridges, 60000);
        tickList.addBasic(this::scanForLights, 45000);
        ticker = new Looper() {
            @Override
            protected long loop() {
                PrecisionStopwatch p = PrecisionStopwatch.start();
                int t = tickList.tick();
                p.end();
                L.v("Ticked " + t + "/" + tickList.getTickList().size() + " items, Tick Time: " + Form.duration(p.getMilliseconds(), 0) + " Latency: " + tickList.getLatency() + " Power: " + getTotalWattage() + " Watts (" + getTotalWattHours() + " Watt Hours)");
                L.v("============================================================================================================================");
                L.flush();
                return (long) (60000D / ArchConfiguration.get().getSettings().getFabricTPM());
            }
        };
        ticker.start();

        tickList.addBasic(() -> {
            forEachLight((i) -> {
                if(activePolicy.isMutable(i))
                {
                    i.setBrightness(1);
                    i.noEffect().setColor(new Color(154, 5, 247));
                }
            });
        }, 100);
    }

    public void shaveWatts(double watts)
    {
        double w = getTotalTargetWattage();
        int attempts = 5;
        KList<ArchLight> l = allLights();

        while(getTotalTargetWattage() > watts && attempts-- > 0)
        {
            l.sort(Comparator.comparingInt((i) -> (int) (i.getW() * 1000)));
            l.forEach((i) -> {
                if(i.getTargetWattage() > 1.25)
                {
                    i.multiplyBrightness(0.90);
                }
            });
        }

        L.v("Reduced Wattage " + (int)w + "W -> " + (int)getTotalTargetWattage() + "W!");
    }

    public void addWatts(double watts)
    {
        double w = getTotalTargetWattage();
        int attempts = 5;
        KList<ArchLight> l = allLights();

        while(getTotalTargetWattage() < watts && attempts-- > 0)
        {
            l.sort(Comparator.comparingInt((i) -> (int) (i.getW() * 1000)));
            l.forEach((i) -> {
                i.multiplyBrightness(1.1);
            });
        }

        L.v("Increased Wattage " + (int)w + "W -> " + (int)getTotalTargetWattage() + "W!");
    }

    public KList<ArchLight> allLights()
    {
        KList<ArchLight> l = new KList<>();

        for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
        {
            l.addAll(i.getLightmap().values());
        }

        return l;
    }

    public void forEachLight(Consumer<ArchLight> l)
    {
        for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
        {
            for(ArchLight j : i.getLightmap().values())
            {
                l.accept(j);
            }
        }
    }

    public String getTotalWattage() {
        double w = 0;

        for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
        {
            w += i.getLightmap().values().stream().mapToDouble(ArchLight::getTargetWattage).sum();
        }

        return Form.f(w, 0);
    }

    public double getTotalTargetWattage() {
        double w = 0;

        for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
        {
            w += i.getLightmap().values().stream().mapToDouble(ArchLight::getTargetWattage).sum();
        }

        return w;
    }

    public String getTotalWattHours() {
        double w = 0;

        for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
        {
            w += i.getLightmap().values().stream().mapToDouble(ArchLight::getWattHours).sum();
        }

        return Form.f(w, 0);
    }

    private void scanForLights() {
        L.i("Scanning for Lights");
    }

    private void connectToBridges()
    {
        L.i("Connecting to Bridges");
        if(!connectingToBridges)
        {
            connectingToBridges = true;
        }

        else
        {
            return;
        }

        J.a(() -> {
            for(BridgeConnection i : ArchConfiguration.get().getState().getBridges())
            {
                if(i.getHue() == null)
                {
                    L.i("Connecting to Bridge " + i.getBridgeIP());
                    i.connect();
                    tickList.add(i);
                }
            }
            connectingToBridges = false;
        });
    }

    private void lookForBridges()
    {
        L.i("Looking for Bridges");
        if(!lookingForBridges)
        {
            lookingForBridges = true;
        }

        else
        {
            return;
        }

        J.a(() -> {
            try
            {
                List<HueBridge> found = new HueBridgeDiscoveryService().discoverBridges(__ -> {}).get();

                finding: for(HueBridge i : found)
                {
                    for(BridgeConnection j : ArchConfiguration.get().getState().getBridges())
                    {
                        if(j.getBridgeIP().equals(i.getIp()))
                        {
                            continue finding;
                        }
                    }

                    BridgeConnection c=  new BridgeConnection();
                    c.setBridgeIP(i.getIp());
                    ArchConfiguration.get().getState().getBridges().add(c);
                    L.i("Discovered new Bridge: " + i.getIp());
                }
            }

            catch(Throwable e)
            {
                e.printStackTrace();
            }

            lookingForBridges = false;
        });
    }

    public static void main(String[] a)
    {
        L.i("ArchLight Starting...");
        instance = new ArchLightServer();
    }
}
