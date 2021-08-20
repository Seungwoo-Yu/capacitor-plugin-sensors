import { WebPlugin } from '@capacitor/core';
export class SensorsPluginWeb extends WebPlugin {
    get(_options) {
        throw new Error("Method not implemented.");
    }
    start(_options) {
        throw new Error("Method not implemented.");
    }
    stop(_options) {
        throw new Error("Method not implemented.");
    }
}
export const WebSensorsPlugin = new SensorsPluginWeb();
//# sourceMappingURL=web.js.map