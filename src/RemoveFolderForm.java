import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveFolderForm extends JFrame {
	private static final String[] NAMES_TO_AVOID = {"Exit", "Add Folder...", "Remove Folder...", "-"};

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

			if (!Arrays.asList(NAMES_TO_AVOID).contains(menuItem.getLabel())) {
				JCheckBox checkBox = new JCheckBox(menuItem.getLabel());
				checkBoxes.add(checkBox);
				panel.add(checkBox);
			}
		}

		List<String> namesList = new ArrayList<>();
		JButton submitButton = new JButton("Remove");
		submitButton.addActionListener(e -> {
			for (JCheckBox checkBox : checkBoxes) {
				if (checkBox.isSelected()) {
					namesList.add(checkBox.getText());
				}
			}
			QuickFolderAccess.removeMultipleFolders(namesList);
			JOptionPane.showMessageDialog(RemoveFolderForm.this, "Selected Folders Removed");
			dispose();
		});
		panel.add(submitButton);

		// Center the frame on the screen
		setLocationRelativeTo(null);

		// Set the visibility of the JFrame
		setVisible(true);
	}

}
