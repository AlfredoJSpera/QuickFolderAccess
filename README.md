# QuickFolderAccess

QuickFolderAccess is a Java-based system tray application that provides quick access to your favorite folders directly from the system tray on your Windows desktop.

## Features

- **System Tray Integration:** Access your favorite folders right from the system tray.
- **FlatLaf Look and Feel:** Modern and sleek user interface powered by the FlatLaf look and feel.
- **Add, Remove, and Set Favorite Folders:** Easily manage your list of favorite folders.
- **Double-Click to Open:** Quickly open your favorite folder by double-clicking on the system tray icon.

## Getting Started

### Prerequisites

- [Java Runtime Environment (JRE)](https://www.java.com/en/download/) installed on your system.

### Installation

1. [Download the zip file](https://github.com/AlfredoJSpera/QuickFolderAccess/releases)

2. Extract the zip file and put the folder where you want. Try to avoid system directories
3. To open the program at startup:
   - Copy the jar file
   - Open run (`Win` + `R`) and type `shell:startup`
   - Paste the shortcut in the folder that opens

## Usage

1. The application runs in the system tray after launching.
2. Right-click on the system tray icon to access the menu.
3. Add, remove, and set favorite folders using the provided menu options.

## Configuration

- Configuration data is stored in the `QFA_Configs.ser` file.
- The application automatically creates the configuration file if it doesn't exist.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
