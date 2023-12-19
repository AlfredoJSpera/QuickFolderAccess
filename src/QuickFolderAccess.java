import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QuickFolderAccess {
	private static final String CONFIG_PATH = "C:\\Users\\Alfredo\\Desktop\\test.ser";
	private static final String IMAGE_PATH = "icon.png";
	public static final String NO_FOLDER_ADDED = "No Folders Added";

	private static final Image image = Toolkit.getDefaultToolkit().getImage(IMAGE_PATH);
	private static SerializedConfigs config = new SerializedConfigs();
	private static SystemTray tray = SystemTray.getSystemTray();
	private static TrayIcon trayIcon = null;
	private static PopupMenu popup = null;

	public QuickFolderAccess() {
		// Check if system tray is supported
		if (!SystemTray.isSupported()) {
			showErrorDialog("SystemTray is not supported");
			System.exit(1);
		}

		// Get the config file
		try {
			getConfigFile();
		} catch (IOException | ClassNotFoundException e) {
			showErrorDialog(e.toString());
			System.exit(1);
		}

		// If there are no folders, open the form and ask for at least one folder
		if (config.getFolders().isEmpty()) {
			new AddFolderForm("Set at least a folder");
		}

		// Add default folders
		initializePopupMenu();
		trayIcon = new TrayIcon(
				image,
				"Open a Folder",
				popup
		);
		trayIcon.setImageAutoSize(true);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			showErrorDialog(e.toString());
		}
	}

	/**
	 * Get the config file.
	 */
	private void getConfigFile() throws IOException, ClassNotFoundException {
		File configFile = new File(CONFIG_PATH);

		if (!configFile.exists()) {
			ObjectSerializer.serialize(CONFIG_PATH, config);
		} else {
			config = ObjectSerializer.deserialize(CONFIG_PATH, SerializedConfigs.class);
		}
	}

	/**
	 * Create right click menu item with the action listener.
	 * @param folderName Name of the folder.
	 * @param folderPath Path of the folder.
	 */
	private static MenuItem createMenuItem(String folderName, String folderPath) {
		MenuItem item = new MenuItem(folderName);

		item.addActionListener(e -> {
			try {
				Desktop.getDesktop().open(new java.io.File(folderPath));
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		});

		return item;
	}

	/**
	 * Remove folder from the right click popup menu.
	 * @param folderName Name of the folder.
	 */
	private static void removeFromPopup(String folderName) {
		boolean isNoFoldersAdded = folderName.equals(NO_FOLDER_ADDED);

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);
			if (menuItem.getLabel().equalsIgnoreCase(folderName)) {
				popup.remove(menuItem);
				config.getFolders().remove(folderName);
				break;
			}
		}

		if (!isNoFoldersAdded && config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);
		}
	}

	/**
	 * Add folder to the right click popup menu.
	 * @param folderName Name of the folder.
	 * @param folderPath Path of the folder.
	 */
	public static void addFolder(String folderName, String folderPath) {
		if (config.getFolders().isEmpty()) {
			removeFromPopup(NO_FOLDER_ADDED);
		}

		popup.add(createMenuItem(folderName, folderPath));
		config.getFolders().put(folderName, folderPath);
		saveConfig();
	}

	/**
	 * Remove folders from the right click popup menu.
	 * @param nameslist List of folder names.
	 */
	public static void removeMultipleFolders(List<String> nameslist) {
		for (String folderName : nameslist) {
			removeFromPopup(folderName);
		}
		saveConfig();
	}

	/**
	 * Add imported folders to the right click popup menu.
	 */
	private void addImportedFolders() {
		if (!config.getFolders().entrySet().isEmpty()) {
			removeFromPopup(NO_FOLDER_ADDED);
		}

		for (Map.Entry<String, String> folder : config.getFolders().entrySet()) {
			popup.add(createMenuItem(folder.getKey(), folder.getValue()));
		}
	}

	/**
	 * Create right click popup menu.
	 */
	private void initializePopupMenu() {
		popup = new PopupMenu();

		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(e -> System.exit(0));
		popup.add(exit);

		MenuItem addFolder = new MenuItem("Add Folder...");
		addFolder.addActionListener(e -> new AddFolderForm("Add Folder"));
		popup.add(addFolder);

		MenuItem removeFolder = new MenuItem("Remove Folder...");
		removeFolder.addActionListener(e -> new RemoveFolderForm("Remove Folder"));
		popup.add(removeFolder);

		popup.addSeparator();

		if (config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);
		}

		addImportedFolders();
	}

	private void setFavoriteFolder(String folderPath) {
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
	 * Show error message dialog.
	 */
	private static void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "QuickFolderAccess: Error", JOptionPane.ERROR_MESSAGE);
		System.err.println("[Error] => " + message);
	}

	/**
	 * Save config file.
	 */
	private static void saveConfig() {
		try {
			ObjectSerializer.serialize(CONFIG_PATH, config);
		} catch (IOException e) {
			showErrorDialog(e.toString());
		}
	}

	public static PopupMenu getPopup() {
		return popup;
	}
}