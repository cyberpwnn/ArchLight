package haus.man.archlight;

import io.github.zeroone3010.yahueapi.AlertType;
import io.github.zeroone3010.yahueapi.EffectType;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;
import lombok.Data;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.math.M;

import java.awt.Color;
import java.util.Objects;

@Data
public class ArchLight implements Ticked {
    private String id;
    private State targetState;
    private State knownState;
    private transient long last = M.ms();
    private transient Light light;
    private transient boolean dirty = true;
    private int lastColor = 0;
    private double wh = 0;
    private double w = 0;
    private long lastChange = M.ms();

    @Override
    public String getId() {
        return id + "(" + (getLight() != null ? getLight().getName() : "???") + ")";
    }

    @Override
    public long getStaleness() {
        if(light == null)
        {
            return (M.ms() - last) - 60000;
        }

        return (M.ms() - last) - (getState().getTransitiontime() == null ? 0 : getState().getTransitiontime());
    }

    private State getState()
    {
        if(knownState == null)
        {
            L.v("Reading " + getLight().getName() + "'s Initial State");
            knownState = light.getState();
        }

        return knownState;
    }

    public void setBrightness(double brightness)
    {
        brightness = brightness < 0 ? 0 : brightness;
        brightness = brightness > 1 ? 1 : brightness;
        targetState.setBri((int) (brightness * 254));
        lastChange = M.ms();
        w = Math.max(0.5, Math.pow(targetState.getBri() / 255D, 2) * 8.5);
        targetState.setTransitiontime(100);
    }

    public void multiplyBrightness(double multiplier)
    {
        setBrightness(getBrightness() * multiplier);
    }

    public double getBrightness()
    {
        return (double)knownState.getBri() / 255D;
    }

    public Color getColor()
    {
        return new Color(lastColor);
    }

    public double getTargetWattage()
    {
        return Math.max(0.5, Math.pow(targetState.getBri() / 255D, 2) * 8.5);
    }

    public void updateWattage()
    {
        if(light.isOn() && light.isReachable())
        {
            w = Math.max(0.5, Math.pow(knownState.getBri() / 255D, 2) * 8.5);
        }

        else
        {
            w = 0;
        }
    }

    public void setColor(Color c)
    {
        targetState.setHue(null);
        targetState.setXy(null);
        targetState.setCt(null);
        targetState.setScene(null);
        double[] normalizedToOne = new double[3];
        normalizedToOne[0] = (c.getRed() / 255D);
        normalizedToOne[1] = (c.getGreen() / 255D);
        normalizedToOne[2] = (c.getBlue() / 255D);
        float red = normalizedToOne[0] > 0.04045 ? (float) Math.pow((normalizedToOne[0] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[0] / 12.92);
        float green = normalizedToOne[1] > 0.04045 ? (float) Math.pow((normalizedToOne[1] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[1] / 12.92);
        float blue = normalizedToOne[2] > 0.04045 ? (float) Math.pow((normalizedToOne[2] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[2] / 12.92);
        float X = (float) (red * 0.649926 + green * 0.103455 + blue * 0.197109);
        float Y = (float) (red * 0.234327 + green * 0.743075 + blue * 0.022598);
        float Z = (float) (red * 0.0000000 + green * 0.053077 + blue * 1.035763);
        float x = X / (X + Y + Z);
        float y = Y / (X + Y + Z);
        targetState.setXy(new KList<>(x, y));
        lastColor = c.getRGB();
        targetState.setTransitiontime(100);
        targetState.setSat((int) Math.round(255 * (M.max((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen()) - M.min((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen())) / M.max((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen())));
    }

    public double getWattHours()
    {
        return wh + (w * ((double) (M.ms() - lastChange) / 1000D / 60D / 60D));
    }

    public ArchLight rgb()
    {
        targetState.setEffect(EffectType.COLOR_LOOP);
        knownState.setAlert(AlertType.LONG_ALERT);
        return this;
    }

    public ArchLight noEffect()
    {
        targetState.setEffect(EffectType.NONE);
        return this;
    }

    public ArchLight noAlert()
    {
        targetState.setAlert(AlertType.NONE);
        return this;
    }

    @Override
    public void onTick() {
        last = M.ms();
        if(light == null)
        {
            return;
        }

        if(!light.isReachable())
        {
            return;
        }

        if(!ArchLightServer.instance.getTickList().hasLiop())
        {
            return;
        }

        if(dirty) {
            updateWattage();
        }

        if(targetState != null)
        {
            targetState.setTransitiontime(100);
        }

        State s = getState();

        if(!s.equals(targetState))
        {
            if(ArchLightServer.instance.getTickList().liop()) {
                String k = "[" + light.getName() + "]: ";

                if(s.getOn() != targetState.getOn()) {
                    if(targetState.getOn()) {
                        L.i(k + "Turned On");
                    } else {
                        L.i(k + "Turned Off");
                    }
                }

                if(!Objects.equals(s.getBri(), targetState.getBri())) {
                    L.i(k + "Brightness " + s.getBri() + " -> " + targetState.getBri());
                }

                if(!Objects.equals(s.getCt(), targetState.getCt())) {
                    L.i(k + "Ct " + s.getCt() + " -> " + targetState.getCt());
                }

                if(!Objects.equals(s.getHue(), targetState.getHue())) {
                    L.i(k + "Hue " + s.getHue() + " -> " + targetState.getHue());
                }

                if(!Objects.equals(s.getSat(), targetState.getSat())) {
                    L.i(k + "Saturation " + s.getSat() + " -> " + targetState.getSat());
                }

                if(!Objects.equals(s.getScene(), targetState.getScene())) {
                    L.i(k + "Scene " + s.getScene() + " -> " + targetState.getScene());
                }

                if(!Objects.equals(s.getAlert(), targetState.getAlert())) {
                    L.i(k + "Alert " + s.getAlert() + " -> " + targetState.getAlert());
                }

                light.setState(targetState);
                knownState = new State(targetState);
                long lt = M.ms() - lastChange;
                wh += w * ((double) lt / 1000D / 60D / 60D);
                lastChange = M.ms();
                updateWattage();
            }
        }
    }
}
