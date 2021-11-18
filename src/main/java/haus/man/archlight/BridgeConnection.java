package haus.man.archlight;

import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.State;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.logging.L;
import io.github.zeroone3010.yahueapi.Hue;
import lombok.Data;
import ninja.bytecode.shuriken.math.M;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

@Data
public class BridgeConnection implements Ticked
{
    private transient Hue hue;
    private String bridgeIP;
    private String apiKey;
    private KMap<String, ArchLight> lightmap = new KMap<>();
    private long last = 0;

    private void addToLightMap(Light i) {
        String id = i.getId();
        if(lightmap.containsKey(id) && lightmap.get(id).getLight() == null)
        {
            lightmap.get(id).setLight(i);
            ArchLightServer.instance.getTickList().add(lightmap.get(id));
            L.i("Found " + i.getName() + " [" + id + "] from " + bridgeIP);
        }

        else if(!lightmap.containsKey(id))
        {
            ArchLight a = new ArchLight();
            a.setId(id);
            a.setLight(i);
            a.setTargetState(new State(i.getState()));
            ArchLightServer.instance.getTickList().add(a);
            lightmap.put(id, a);
            ArchLightServer.instance.getTickList().add(a);
            L.i("Found NEW " + i.getName() + " [" + id + "] from " + bridgeIP);
        }

        else
        {

        }
    }

    private String getId(Light i)
    {
        try
        {
            Field f = i.getClass().getField("id");
            f.setAccessible(true);
            return f.get(i).toString();
        }

        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return "Error!id--hc=" + i.hashCode();
    }

    public void connect()
    {
        if(hue != null)
        {
            L.w("Already Connected!");
            return;
        }

        if(bridgeIP == null)
        {
            throw new RuntimeException("Can't connect to bridge because IP is missing!");
        }

        if(apiKey == null)
        {
            try {
                L.v("Grabbing API Key");
                apiKey = Hue.hueBridgeConnectionBuilder(bridgeIP).initializeApiConnection("ArchLight").get();
                L.v("Obtained API Key for new Bridge " + getBridgeIP() + ": " + getApiKey());
            } catch(InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(apiKey != null)
        {
            L.v("Connecting with " + getApiKey() + " API Key");
            hue = new Hue(getBridgeIP(), getApiKey());
            L.i("Connected to Bridge " + bridgeIP);
            ArchConfiguration.get().save();
        }

        else
        {
            throw new RuntimeException("Can't connect to bridge because we cant get an api key!");
        }
    }

    @Override
    public String getId() {
        return "br-" + bridgeIP;
    }

    @Override
    public long getStaleness() {
        return (M.ms() - last) - 60000;
    }

    @Override
    public void onTick() {
        last = M.ms();
        if(hue == null)
        {
            return;
        }

        for(Light i : hue.getUnassignedLights())
        {
            addToLightMap(i);
        }

        for(Room i : hue.getRooms())
        {
            for(Light j : i.getLights())
            {
                addToLightMap(j);
            }
        }
    }
}
