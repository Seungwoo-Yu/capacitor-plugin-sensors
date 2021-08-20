import { registerPlugin } from '@capacitor/core';
import * as web from './web';
import { SensorsPlugin } from './definitions';

const Sensors = registerPlugin<SensorsPlugin>('SensorsPlugin', {
    web: () => web.WebSensorsPlugin,
});

export * from './definitions';
export { Sensors };