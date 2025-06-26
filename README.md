📍 Compass App
A sleek and modern Android compass app built with Jetpack Compose that utilizes real-time sensor data to display accurate directional information, magnetic strength, and acceleration values. The interface features a rotating compass dial, cardinal directions, degree markers, and dynamic heading updates with clean, customizable UI support for both light and dark themes.

🧭 Features
✅ Real-Time Compass using device magnetometer and accelerometer

🎯 Displays heading in degrees (0°–360°) and corresponding direction label (e.g., North, Southwest)

🧲 Shows magnetic field strength in microteslas (µT)

📡 Displays current acceleration (gravity + motion) in m/s²

🌗 Support for dark mode and light mode

🌀 Rotating compass dial with:

Degree tick marks (every 15°)

Labeled major degrees (every 30°)

Bold N, E, S, W cardinal labels

📌 A fixed pointer arrow always aligned to the top, showing the device’s orientation

🛠️ Tech Stack
Jetpack Compose for modern declarative UI

ViewModel and StateFlow for reactive state management

SensorManager to access magnetic field and accelerometer data

Custom Canvas Drawing for compass dial and pointer

Material 3 theming support

🧪 Preview
Supports full Jetpack Compose Preview via @Preview for design-time rendering.

🚀 How It Works
CompassViewModel collects sensor data via SensorRepository.

Data is observed as state using collectAsState().

CompassComponent shows the heading, direction, and sensor values.

RotatingCompassDial uses Canvas to draw a dial that rotates with the heading.

A fixed arrow is overlaid on top to indicate the device's forward direction.

📱 Screenshots

<img src="https://github.com/user-attachments/assets/63934744-9826-4aad-96f7-e7a9bc93d772" alt="Compass Screenshot 1" width="300"/>
<img src="https://github.com/user-attachments/assets/a1f6b8a4-1c3f-4a2c-ba9f-46508370907a" alt="Compass Screenshot 2" width="300"/>
<img src="https://github.com/user-attachments/assets/95fb7f7e-d7f5-4fcc-9832-7f34e9ca85ee" alt="Compass Screenshot 3" width="300"/>

