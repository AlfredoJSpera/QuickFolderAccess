import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
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
	public static final String EXIT = "Exit";
	public static final String ADD_FOLDER = "Add Folder...";
	public static final String REMOVE_FOLDER = "Remove Folder...";
	public static final String SEPARATOR = "-";
	public static final String SET_FAVORITE_FOLDER = "Set Favorite Folder...";

	private static final Image image = Toolkit.getDefaultToolkit().getImage(IMAGE_PATH);
	private static SerializedConfigs config = new SerializedConfigs();
	private static SystemTray tray = SystemTray.getSystemTray();
	private static TrayIcon trayIcon = null;
	private static PopupMenu popup = new PopupMenu();
	private static MenuItem removeFolder;
	private static MenuItem setFavoriteFolder;

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
		trayIcon = new TrayIcon(
				image,
				"Open a Folder",
				popup
		);
		trayIcon.setImageAutoSize(true);
		initializePopupMenu();

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
	 * FOR INTERNAL USE ONLY. Remove folder from the right click popup menu.
	 * @param folderName Name of the folder.
	 */
	private static void removeFromPopup(String folderName) {
		boolean isNoFoldersAdded = folderName.equals(NO_FOLDER_ADDED);

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);
			if (menuItem.getLabel().equalsIgnoreCase(folderName)) {
				popup.remove(menuItem);
				config.getFolders().remove(folderName);
				config.setFavoriteFolderName(null);
				trayIcon.setToolTip("Open a Folder");
				saveConfig();
				break;
			}
		}

		if (!isNoFoldersAdded && config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);
			removeFolder.setEnabled(false);
			setFavoriteFolder.setEnabled(false);
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
			removeFolder.setEnabled(true);
			setFavoriteFolder.setEnabled(true);
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

		String favoriteFolderName = config.getFavoriteFolderName();
		if (favoriteFolderName != null) {
			setFavoriteFolder(favoriteFolderName);
		}
	}

	/**
	 * Create right click popup menu.
	 */
	private void initializePopupMenu() {
		MenuItem exit = new MenuItem(EXIT);
		exit.addActionListener(e -> System.exit(0));
		popup.add(exit);

		MenuItem addFolder = new MenuItem(ADD_FOLDER);
		addFolder.addActionListener(e -> new AddFolderForm("Add a Folder"));
		popup.add(addFolder);

		removeFolder = new MenuItem(REMOVE_FOLDER);
		removeFolder.addActionListener(e -> new RemoveFolderForm("Remove a Folder"));
		popup.add(removeFolder);

		setFavoriteFolder = new MenuItem(SET_FAVORITE_FOLDER);
		setFavoriteFolder.addActionListener(e -> new FavoriteFolderForm("Set a Favorite Folder"));
		popup.add(setFavoriteFolder);

		popup.addSeparator();

		if (config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);
			removeFolder.setEnabled(false);
			setFavoriteFolder.setEnabled(false);
		}

		addImportedFolders();
	}

	public static void setFavoriteFolder(String name) {
		for (MouseListener mouseListener : trayIcon.getMouseListeners()) {
			trayIcon.removeMouseListener(mouseListener);
		}

		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Check for double-click
					try {
						Desktop.getDesktop().open(new File(config.getFolders().get(name)));
					} catch (Exception ex) {
						showErrorDialog(ex.getMessage());
					}
				}
			}
		});

		trayIcon.setToolTip("Open " + name);
		config.setFavoriteFolderName(name);
		saveConfig();
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