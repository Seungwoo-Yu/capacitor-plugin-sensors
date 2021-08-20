package com.zzlcoding.capacitor.plugin.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CapacitorPlugin(name = "SensorsPlugin")
public class SensorsPlugin extends Plugin implements SensorEventListener {


    private SensorManager sensorManager;

    private final Map<String, PluginCall> startedSensorMap = new HashMap<>();

    public SensorsPlugin() {
        super();
    }

    @PluginMethod()
    public void get(PluginCall call) {
        synchronized (this) {
            if (sensorManager == null) {
                sensorManager = (SensorManager)this.getActivity().getSystemService(Context.SENSOR_SERVICE);
            }
        }

        String type = call.getString("type");
        SensorType sensorType = SensorType.valueOf(type);
        if (sensorType != SensorType.ALL_THERMAL_ZONE) {
            List<Sensor> sensors = sensorManager.getSensorList(sensorType.getValue());

            JSArray sensorArray = new JSArray();
            for (Sensor sensor: sensors) {
                String id = getSensorId(sensor);
                String name = getSensorName(sensor);
                SensorType st = getSensorType(sensor);
                JSObject sensorObj = new JSObject();
                sensorObj.put("id", id);
                sensorObj.put("name", name);
                sensorObj.put("type", st.toString());
                sensorArray.put(sensorObj);
            }

            JSObject result = new JSObject();
            result.put("sensors", sensorArray);

            call.resolve(result);
        } else {
            final ThermalZone[] thermalZones = getThermalZones();

            JSArray thermalArray = new JSArray();
            for (ThermalZone thermalZone: thermalZones) {
                JSObject thermalObj = new JSObject();
                thermalObj.put("name", thermalZone.getName());
                thermalObj.put("value", thermalZone.getValue());
                thermalArray.put(thermalObj);
            }

            JSObject result = new JSObject();
            result.put("thermalZones", thermalArray);

            call.resolve(result);
        }
    }

    @PluginMethod()
    public void start(PluginCall call) {
        String id = call.getString("id");
        String name = call.getString("name");
        String type = call.getString("type");

        if (startedSensorMap.containsKey(id)) {
            call.reject(String.format("sensor is already started: %s", id));
            return;
        }

        SensorType sensorType = SensorType.valueOf(type);
        Sensor sensor = getSensor(sensorType, name);
        if (sensor == null) {
            call.reject(String.format("can not find sensor with name (%s) for type (%s)", name, type));
            return;
        }

        call.setKeepAlive(true);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        startedSensorMap.put(id, call);
    }

    @PluginMethod()
    public void stop(PluginCall call) {
        String id = call.getString("id");
        String name = call.getString("name");
        String type = call.getString("type");

        if (!startedSensorMap.containsKey(id)) {
            call.reject(String.format("sensor is not started: %s", id));
            return;
        }

        PluginCall startCall = startedSensorMap.get(id);
        startCall.release(this.getBridge());
        startedSensorMap.remove(id);

        SensorType sensorType = SensorType.valueOf(type);
        Sensor sensor = getSensor(sensorType, name);
        if (sensor == null) {
            call.reject(String.format("can not find sensor with name (%s) for type (%s)", name, type));
            return;
        }

        sensorManager.unregisterListener(this, sensor);
        call.resolve();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String sensorId = getSensorId(event.sensor);
        if (startedSensorMap.containsKey(sensorId)) {
            PluginCall call = startedSensorMap.get(sensorId);
            JSArray values = new JSArray();
            for (float value : event.values) {
                try {
                    values.put(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSObject object = new JSObject();
            object.put("values", values);
            call.resolve(object);
            call.setKeepAlive(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getSensorId(Sensor sensor) {
        return sensor.toString();
    }

    private String getSensorName(Sensor sensor) {
        return sensor.getName();
    }

    private SensorType getSensorType(Sensor sensor) {
        int type = sensor.getType();
        SensorType[] values = SensorType.values();
        for (SensorType st : values) {
            if (st.getValue() == type) {
                return st;
            }
        }
        return SensorType.UNKNOWN;
    }

    private Sensor getSensor(SensorType type, String name) {
        List<Sensor> sensors = sensorManager.getSensorList(type.getValue());
        for (Sensor sensor : sensors) {
            if (sensor.getName().equals(name)) {
                return sensor;
            }
        }
        return null;
    }

    public ThermalZone[] getThermalZones() {
        final ArrayList<ThermalZone> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Float value = getThermalValue(i);
            if (value > 0.0f) {
                String type = getThermalType(i);
                if (type != null) {
                    data.add(new ThermalZone(type, value));
                }
            }
        }

        return data.toArray(new ThermalZone[0]);
    }

    public Float getThermalValue(int i) {
        try {
            Process process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            float temp = 0f;
            if (line != null) {
                temp = Float.parseFloat(line);
            }
            reader.close();
            process.destroy();
            if (temp > 0.0f) {
                if (temp > 10000.0f) {
                    return Float.valueOf(String.valueOf((Math.floor(temp / 100) / 10)));
                } else if (temp > 1000.0f) {
                    return Float.valueOf(String.valueOf((Math.floor(temp / 10) / 10)));
                } else if (temp > 100.0f) {
                    return Float.valueOf(String.valueOf((Math.floor(temp) / 10)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0f;
    }

    public String getThermalType(int i) {
        try {
            Process process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/type");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                return line;
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
