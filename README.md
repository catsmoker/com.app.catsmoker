# Catsmoker App

Catsmoker is a simple Android app that displays a web page using a `WebView` component. The web page shown is hosted at [catsmoker.github.io](https://catsmoker.github.io). It also serves as a utility aimed at unlocking higher FPS in games by spoofing your device as a different model for specific games.

For more information, visit the [GitHub repository](https://github.com/Xposed-Modules-Repo/com.app.catsmoker).

# Updated Pro Version

For the New Pro version check here: [Store](https://payhip.com/b/oVHNt)

## Features

- Spoof device information for improved gaming performance in supported games.
- Compatible with a wide variety of popular gaming apps.
- Simple installation with no configuration needed.
- Displays a web page within the app.

## Table of Contents

- [Getting Started](#getting-started)
- [Usage](#usage)
- [Supported Games](#supported-games)
- [License](#license)
- [Contributing](#contributing)

## Getting Started

### Installation Guide

1. **Check Root Access**  
   Ensure that your Android device is rooted. You can verify this using the [Root Checker](https://play.google.com/store/apps/details?id=com.joeykrim.rootcheck&hl=en). If you don't have root access, follow the instructions [here](https://topjohnwu.github.io/Magisk/).

2. **Install Magisk**  
   Download and install [Magisk Canary](https://github.com/topjohnwu/Magisk/releases/tag/canary-27008) if it's not already installed on your device.

3. **(Optional) Install Shamiko Module**  
   To hide root, consider installing the [Shamiko](https://github.com/LSPosed/LSPosed.github.io/releases) module.

4. **Install LSPosed_mod Module**  
   Through the Magisk app, install the [LSPosed_mod](https://github.com/mywalkb/LSPosed_mod/releases) module.

5. **Open LSPosed_mod Manager**  
   Launch the `LSPosed_mod` Manager.

6. **Enable the Module**  
   - Navigate to the `Modules` section.
   - Search for `catsmoker` and enable the module.

7. **Manage Supported Games**  
   The supported games will be automatically added to the scope.

8. **Force Stop the Game**  
   Manually force stop the added game to apply the changes.


## Usage

Once you've installed and activated the Catsmoker module, it will automatically spoof your device information for supported gaming applications. There's no additional configuration required. Simply force stop and open the games you want to play, and enjoy the benefits of device spoofing.

You can untick the games you don't want to spoof in the lsposed app. A reboot may be required.

**Note:** Device spoofing may violate the terms of service for some games or apps.

## Supported Games

- **Activision Games**
  - `com.activision.callofduty.shooter`
  - `com.activision.callofduty.warzone`
  - `com.garena.game.codm`
  - `com.tencent.tmgp.kr.codm`
  - `com.vng.codmvn`
  - `com.tencent.tmgp.cod`
- **Tencent Games**
  - `com.tencent.ig`
  - `com.pubg.imobile`
  - `com.pubg.krmobile`
  - `com.rekoo.pubgm`
  - `com.vng.pubgmobile`
  - `com.tencent.tmgp.pubgmhd`
- **Garena Games**
  - `com.dts.freefiremax`
  - `com.dts.freefireth`
- **Epic Games**
  - `com.epicgames.fortnite`

For any additional games you’d like to see supported, please open an issue with the APK name at [issues](https://github.com/catsmoker/com.app.catsmoker/issues).

## Note

The updates are temporarily on hold due to a lack of devices available for experimentation.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

![app](https://github.com/user-attachments/assets/5f0812d4-5636-434c-8ec0-1dd7fc427015)

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any bugs, improvements, or new features.
