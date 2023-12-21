import com.formdev.flatlaf.FlatDarkLaf;

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
	private static final String CONFIG_PATH = "QFA_Configs.ser";
	private static final String IMAGE_PATH = "icon.png";
	private static final Image image = Toolkit.getDefaultToolkit().getImage(IMAGE_PATH);

	private static final String NO_FOLDER_ADDED = "No Folders Added";
	private static final String EXIT = "Exit";
	private static final String ADD_FOLDER = "Add Folder...";
	private static final String REMOVE_FOLDER = "Remove Folder...";
	private static final String SEPARATOR = "-";
	private static final String SET_FAVORITE_FOLDER = "Set Favorite Folder...";

	public static final String[] NAMES_TO_AVOID = {
			EXIT, ADD_FOLDER,
			REMOVE_FOLDER, SEPARATOR,
			NO_FOLDER_ADDED, SET_FAVORITE_FOLDER
	};

	private static SerializedConfigs config = new SerializedConfigs();
	private static SystemTray tray = SystemTray.getSystemTray();
	private static TrayIcon trayIcon = null;
	private static PopupMenu popup = new PopupMenu();
	private static MenuItem removeFolder;
	private static MenuItem setFavoriteFolder;

	public QuickFolderAccess() {
		// Set flatlaf look and feel
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			showWarningDialog("Failed to initialize FlatLaf. Using default Java swing look and feel.");
		}

		// Check if system tray is supported
		if (!SystemTray.isSupported()) {
			showFatalErrorDialog("SystemTray is not supported");
		}

		try {
			// Get the config file
			getConfigFile();
		} catch (IOException | ClassNotFoundException e) {
			showFatalErrorDialog(e.toString());
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
			// Add the tray icon to the system tray
			tray.add(trayIcon);
		} catch (AWTException e) {
			showFatalErrorDialog(e.toString());
		}
	}

	/**
	 * Show error message dialog and exits the program.
	 */
	private static void showFatalErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "QuickFolderAccess: Fatal Error", JOptionPane.ERROR_MESSAGE);
		System.err.println("\u001B[31m[Error] => " + message + "\u001B[0m");
		System.exit(1);
	}

	/**
	 * Show warning message dialog.
	 */
	private static void showWarningDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "QuickFolderAccess: Warning", JOptionPane.WARNING_MESSAGE);
		System.err.println("\u001B[33m[Warning] => " + message + "\u001B[0m");
	}

	/**
	 * Get the config file.
	 */
	private void getConfigFile() throws IOException, ClassNotFoundException {
		File configFile = new File(CONFIG_PATH);

		if (!configFile.exists()) {
			// Create the config file if it doesn't exist
			ObjectSerializer.serialize(CONFIG_PATH, config);
		} else {
			config = ObjectSerializer.deserialize(CONFIG_PATH, SerializedConfigs.class);
		}
	}

	/**
	 * Adds the default voices: Exit, Add Folder, Remove Folder, Set Favorite Folder, Separator.
	 * Optionally a No Folders Added voice.
	 */
	private void initializePopupMenu() {
		// EXIT
		MenuItem exit = new MenuItem(EXIT);
		exit.addActionListener(e -> System.exit(0));
		popup.add(exit);

		// ADD FOLDER
		MenuItem addFolder = new MenuItem(ADD_FOLDER);
		addFolder.addActionListener(e -> new AddFolderForm("Add a Folder"));
		popup.add(addFolder);

		// REMOVE FOLDER
		removeFolder = new MenuItem(REMOVE_FOLDER);
		removeFolder.addActionListener(e -> new RemoveFolderForm("Remove a Folder"));
		popup.add(removeFolder);

		// SET FAVORITE FOLDER
		setFavoriteFolder = new MenuItem(SET_FAVORITE_FOLDER);
		setFavoriteFolder.addActionListener(e -> new FavoriteFolderForm("Set a Favorite Folder"));
		popup.add(setFavoriteFolder);

		// SEPARATOR
		popup.addSeparator();

		// NO FOLDERS ADDED, if there are no folders in the config file
		if (config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);

			// Disable the "Remove Folder" and "Set Favorite Folder" voices
			removeFolder.setEnabled(false);
			setFavoriteFolder.setEnabled(false);

			// Open the form and ask for at least one folder
			new AddFolderForm("Set at least a folder");
		} else {
			// Otherwise, add the imported folders to the popup menu
			addImportedFolders();
		}
	}

	/**
	 * Add imported folders to the right click popup menu.
	 */
	private void addImportedFolders() {
		// Remove the "No Folders Added" item if it's present
		removeFromPopup(NO_FOLDER_ADDED);

		// Add the imported folders to the popup menu
		for (Map.Entry<String, String> folder : config.getFolders().entrySet()) {
			popup.add(
					createMenuItem(folder.getKey(), folder.getValue())
			);
		}

		// Set the favorite folder if it's present
		String favoriteFolderName = config.getFavoriteFolderName();
		if (favoriteFolderName != null) {
			setFavoriteFolder(favoriteFolderName);
		}
	}

	/**
	 * FOR INTERNAL USE ONLY. Remove folder from the right click popup menu.
	 * @param folderName Name of the folder.
	 */
	private static void removeFromPopup(String folderName) {
		boolean isNoFoldersAdded = folderName.equals(NO_FOLDER_ADDED);

		// Remove the selected folder
		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);

			// If the folder is the selected folder, remove it
			if (menuItem.getLabel().equals(folderName)) {
				popup.remove(menuItem);
				config.getFolders().remove(folderName);

				// If the folder is the favorite folder, change the favorite folder to null
				if (folderName.equals(config.getFavoriteFolderName())) {
					config.setFavoriteFolderName(null);
					trayIcon.setToolTip("Open a Folder");
					for (MouseListener mouseListener : trayIcon.getMouseListeners()) {
						trayIcon.removeMouseListener(mouseListener);
					}
				}

				saveConfig();
				break;
			}
		}

		// If there are no folders left, add the "No Folders Added" item
		if (!isNoFoldersAdded && config.getFolders().isEmpty()) {
			MenuItem item = new MenuItem(NO_FOLDER_ADDED);
			item.setEnabled(false);
			popup.add(item);

			// Disable the "Remove Folder" and "Set Favorite Folder" voices
			removeFolder.setEnabled(false);
			setFavoriteFolder.setEnabled(false);
		}
	}

	/**
	 * Save the config file.
	 */
	private static void saveConfig() {
		try {
			ObjectSerializer.serialize(CONFIG_PATH, config);
		} catch (IOException e) {
			showFatalErrorDialog(e.toString());
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
				// Open the specified folder
				Desktop.getDesktop().open(new File(folderPath));
			} catch (Exception ex) {
				showFatalErrorDialog(ex.toString());
			}
		});

		return item;
	}

	/**
	 * Add folder to the right click popup menu.
	 * @param folderName Name of the folder.
	 * @param folderPath Path of the folder.
	 */
	public static void addFolder(String folderName, String folderPath) {
		// Check if the folder is already present or if the name or path are blank
		if (config.getFolders().containsKey(folderName)) {
			System.err.println("[Error] => Folder already present");
			return;
		}

		if (folderName.isBlank() || folderPath.isBlank()) {
			System.err.println("[Error] => Folder name or path are blank");
			return;
		}

		// Remove the "No Folders Added" item if it's present
		if (config.getFolders().isEmpty()) {
			removeFromPopup(NO_FOLDER_ADDED);

			// Enable the "Remove Folder" and "Set Favorite Folder" voices
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
	}

	/**
	 * Set a favorite folder.
	 * @param name Name of the folder.
	 */
	public static void setFavoriteFolder(String name) {
		// Remove the old mouse listeners
		for (MouseListener mouseListener : trayIcon.getMouseListeners()) {
			trayIcon.removeMouseListener(mouseListener);
		}

		// Add the new mouse listener
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Check for double-click
				if (e.getClickCount() == 2) {
					try {
						// Open the favorite folder
						Desktop.getDesktop().open(new File(config.getFolders().get(name)));
					} catch (Exception ex) {
						showFatalErrorDialog(ex.toString());
					}
				}
			}
		});

		trayIcon.setToolTip("Open " + name);
		config.setFavoriteFolderName(name);
		saveConfig();
	}

	/**
	 * Get the popup menu.
	 */
	public static PopupMenu getPopup() {
		return popup;
	}

	public static SerializedConfigs getConfig() {
		return config;
	}
}