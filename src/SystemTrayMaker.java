import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SystemTrayMaker {
	private static final String CONFIG_PATH = "C:\\Users\\Alfredo\\Desktop\\test.ser";
	private static SerializedConfigs config = new SerializedConfigs();

	public static void main(String[] args) {
		if (!SystemTray.isSupported()) {
			showErrorDialog("SystemTray is not supported");
			System.exit(1);
		}

		try {
			getConfigFile();
		} catch (IOException | ClassNotFoundException e) {
			showErrorDialog(e.getMessage());
			System.exit(1);
		}

		if (config.getFolders().isEmpty()) {
			new FolderForm();
		}

		config.getFolders().put("Desktop", "C:\\Users\\Alfredo\\Desktop");
		config.getFolders().put("Downloads", "C:\\Users\\Alfredo\\Downloads");

		TrayIcon trayIcon = new TrayIcon(
				createImage("icon.png"),
				"Open a Folder",
				createPopupMenu(config.getFolders())
		);
		trayIcon.setImageAutoSize(true);

		setFavoriteFolder("C:\\Users\\Alfredo\\Desktop", trayIcon);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			showErrorDialog(e.getMessage());
			System.err.println(e.getMessage());
		}

	}

	private static void getConfigFile() throws IOException, ClassNotFoundException {
		File configFile = new File(CONFIG_PATH);

		if (!configFile.exists()) {
			ObjectSerializer.serialize(CONFIG_PATH, config);
		} else {
			config = ObjectSerializer.deserialize(CONFIG_PATH, SerializedConfigs.class);
		}
	}

	/**
	 * Show error message dialog.
	 */
	private static void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		System.err.println("[Error] => " + message);
	}

	/**
	 * Create image from path.
	 */
	private static Image createImage(String path) {
		return Toolkit.getDefaultToolkit().getImage(path);
	}

	/**
	 * Create right click popup menu.
	 * @param folders Map of folders to add (name, paths).
	 */
	private static PopupMenu createPopupMenu(Map<String, String> folders) {
		PopupMenu popup = new PopupMenu();

		for (Map.Entry<String, String> entry: folders.entrySet()) {
			MenuItem menuItem = createMenuItem(entry.getKey(), entry.getValue());
			popup.add(menuItem);
		}

		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(e -> System.exit(0));
		popup.add(exit);

		return popup;
	}

	private static void setFavoriteFolder(String folderPath, TrayIcon trayIcon) {
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Check for double-click
					try {
						Desktop.getDesktop().open(new java.io.File(folderPath));
					} catch (Exception ex) {
						System.err.println(ex.getMessage());
					}
				}
			}
		});
	}

	/**
	 * Create right click menu item.
	 */
	private static MenuItem createMenuItem(String folderName, String folderPath) {
		MenuItem openFolder = new MenuItem(folderName);

		openFolder.addActionListener(e -> {
			try {
				Desktop.getDesktop().open(new java.io.File(folderPath));
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		});

		return openFolder;
	}
}