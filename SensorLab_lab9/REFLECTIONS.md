# Reflection explanations

Use these to understand the implementation and write your own answers if requested.

1. **Why can `getDefaultSensor()` return null?** Android devices have different hardware. The requested sensor may not be installed, so the lookup has to allow a null result. The app checks this before registering or displaying readings.

2. **Why unregister listeners when paused?** Sensor callbacks consume processing time and energy. Unregistering stops unnecessary work when this screen is inactive. `onResume()` registers the sensors again when the user returns.

3. **How does angular velocity differ from orientation?** Angular velocity describes how fast the device rotates around an axis. Orientation describes its attitude relative to a reference frame. A stationary phone can have any orientation while its angular velocity stays near zero. Integrating angular velocity to estimate orientation accumulates drift.

4. **What units does the gyroscope use?** Radians per second (rad/s), for rotation around the X, Y, and Z axes. These are not angles in degrees.

5. **Why not use battery temperature instead?** Battery temperature measures the battery, which is affected by charging and device workload. Ambient temperature measures the surrounding air. They represent different physical quantities, so battery temperature cannot stand in for the requested ambient sensor.

6. **How does `mutableStateOf` update the Compose UI?** Compose observes state read by a composable. When a sensor callback assigns a changed value to that state, Compose schedules recomposition of the readers and displays the new value. Here the listener is registered on the activity's main thread, so the callbacks and UI state changes occur there.
