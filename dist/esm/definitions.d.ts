export declare const enum SensorType {
    PROXIMITY = "PROXIMITY",
    ACCELEROMETER = "ACCELEROMETER",
    GRAVITY = "GRAVITY",
    GYROSCOPE = "GYROSCOPE",
    GYROSCOPE_UNCALIBRATED = "GYROSCOPE_UNCALIBRATED",
    LINEAR_ACCELERATION = "LINEAR_ACCELERATION",
    ROTATION_VECTOR = "ROTATION_VECTOR",
    STEP_COUNTER = "STEP_COUNTER",
    GAME_ROTATION_VECTOR = "GAME_ROTATION_VECTOR",
    GEOMAGNETIC_ROTATION_VECTOR = "GEOMAGNETIC_ROTATION_VECTOR",
    MAGNETIC_FIELD = "MAGNETIC_FIELD",
    MAGNETIC_FIELD_UNCALIBRATED = "MAGNETIC_FIELD_UNCALIBRATED",
    ORIENTATION = "ORIENTATION",
    AMBIENT_TEMPERATURE = "AMBIENT_TEMPERATURE",
    LIGHT = "LIGHT",
    PRESSURE = "PRESSURE",
    RELATIVE_HUMIDITY = "RELATIVE_HUMIDITY",
    TEMPERATURE = "TEMPERATURE",
    SIGNIFICANT_MOTION = "SIGNIFICANT_MOTION",
    STEP_DETECTOR = "STEP_DETECTOR",
    HEART_RATE = "HEART_RATE",
    POSE_6DOF = "POSE_6DOF",
    STATIONARY_DETECT = "STATIONARY_DETECT",
    MOTION_DETECT = "MOTION_DETECT",
    HEART_BEAT = "HEART_BEAT",
    LOW_LATENCY_OFFBODY_DETECT = "LOW_LATENCY_OFFBODY_DETECT",
    ACCELEROMETER_UNCALIBRATED = "ACCELEROMETER_UNCALIBRATED",
    ALL = "ALL",
    UNKNOWN = "UNKNOWN",
    ALL_THERMAL_ZONE = "ALL_THERMAL_ZONE"
}
export interface ThermalZone {
    name: string;
    value: number;
}
export interface Sensor {
    id: string;
    name: string;
    type: SensorType;
}
export declare type SensorOrThermalZone = {
    thermalZones: ThermalZone[] | undefined;
    sensors: Sensor[] | undefined;
};
export interface SensorsPlugin {
    get(options: {
        type: SensorType.ALL_THERMAL_ZONE;
    }): Promise<{
        thermalZones: ThermalZone[];
    }>;
    get(options: {
        type: SensorType;
    }): Promise<{
        sensors: Sensor[];
    }>;
    get(options: {
        type: SensorType;
    }): Promise<SensorOrThermalZone>;
    start(options: Sensor): Promise<{
        values: number[];
    }>;
    stop(options: Sensor): Promise<void>;
}
