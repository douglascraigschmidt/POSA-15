package vandy.mooc.model.aidl;

/**
 * AIDL definition for the WeatherData class, which the AIDL compiler
 * needs to integrate the code for marshaling/demarshaling WeatherData
 * objects.  An .aidl file is needed for any parcelable class used in
 * any other .aidl file, even if they are defined in the same package
 * as an interface.
 */
parcelable WeatherData;
