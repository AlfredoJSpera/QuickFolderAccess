import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class ItemsContainer {
	private JCheckBox checkBox;
	private MenuItem menuItem;

	public ItemsContainer(JCheckBox checkBox, MenuItem menuItem) {
		this.checkBox = checkBox;
		this.menuItem = menuItem;
	}

	public JCheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(JCheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}
}

public class RemoveFolderForm extends JFrame {
	public RemoveFolderForm(String name) {
		setTitle(name);
		setSize(400, 300);

		JPanel panel = new JPanel();
		add(panel);

		JLabel label = new JLabel("Select a folder to remove:");
		panel.add(label);

		List<JCheckBox> checkBoxes = new ArrayList<>();
		PopupMenu popup = QuickFolderAccess.getPopup();

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);

			JCheckBox checkBox = new JCheckBox(menuItem.getLabel());

			checkBoxes.add(checkBox);
		}

		JButton submitButton = new JButton("Remove");
		submitButton.addActionListener(e -> {
			for (JCheckBox checkBox : checkBoxes) {
				if (checkBox.isSelected()) {
					QuickFolderAccess.removeFolder(checkBox.getText());
				}
			}
			JOptionPane.showMessageDialog(RemoveFolderForm.this, "Selected Folders Removed");
		});



		// Add the panel to the frame


		panel.add(checkBox);

		// Center the frame on the screen
		setLocationRelativeTo(null);

		// Set the visibility of the JFrame
		setVisible(true);
	}

	private JButton getFileDialogJButton(JTextField folderTextField) {
		JButton browseButton = new JButton("Browse");

		browseButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int result = fileChooser.showOpenDialog(RemoveFolderForm.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();
				folderTextField.setText(selectedFolder);
			}
		});

		return browseButton;
	}
}
