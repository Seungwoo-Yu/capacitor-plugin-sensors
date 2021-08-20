import { WebPlugin } from '@capacitor/core';
import { SensorsPlugin, Sensor, SensorType, SensorOrThermalZone } from './definitions';
export declare class SensorsPluginWeb extends WebPlugin implements SensorsPlugin {
    get(_options: {
        type: SensorType;
    }): Promise<SensorOrThermalZone>;
    start(_options: Sensor): Promise<{
        values: number[];
    }>;
    stop(_options: Sensor): Promise<void>;
}
export declare const WebSensorsPlugin: SensorsPluginWeb;
