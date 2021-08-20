import { registerPlugin } from '@capacitor/core';
import * as web from './web';
const Sensors = registerPlugin('SensorsPlugin', {
    web: () => web.WebSensorsPlugin,
});
export * from './definitions';
export { Sensors };
//# sourceMappingURL=index.js.map