import { WebPlugin } from '@capacitor/core';
import { SensorsPlugin, Sensor, SensorType, SensorOrThermalZone } from './definitions';

export class SensorsPluginWeb extends WebPlugin implements SensorsPlugin {
    get(_options: { type: SensorType }): Promise<SensorOrThermalZone> {
        throw new Error("Method not implemented.");
    }
    start(_options: Sensor): Promise<{values: number[]}> {
        throw new Error("Method not implemented.");
    }
    stop(_options: Sensor): Promise<void> {
        throw new Error("Method not implemented.");
    }
}

export const WebSensorsPlugin = new SensorsPluginWeb();