import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteFolderForm extends JFrame {
	private static final String[] NAMES_TO_AVOID = {
			QuickFolderAccess.EXIT, QuickFolderAccess.ADD_FOLDER,
			QuickFolderAccess.REMOVE_FOLDER, QuickFolderAccess.SEPARATOR,
			QuickFolderAccess.NO_FOLDER_ADDED, QuickFolderAccess.SET_FAVORITE_FOLDER
	};

	public FavoriteFolderForm(String name) {
		setTitle(name);
		setSize(400, 300);

		JPanel panel = new JPanel();
		add(panel);

		JLabel label = new JLabel("Select a folder to set as favorite:");
		panel.add(label);

		ButtonGroup buttonGroup = new ButtonGroup();

		List<JRadioButton> radioButtons = new ArrayList<>();
		PopupMenu popup = QuickFolderAccess.getPopup();

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);

			if (!Arrays.asList(NAMES_TO_AVOID).contains(menuItem.getLabel())) {
				JRadioButton radioButton = new JRadioButton(menuItem.getLabel());
				buttonGroup.add(radioButton);
				radioButtons.add(radioButton);
				panel.add(radioButton);
			}
		}

		JButton submitButton = new JButton("Set as Favorite");
		submitButton.addActionListener(e -> {
			for (JRadioButton radioButton : radioButtons) {
				if (radioButton.isSelected()) {
					QuickFolderAccess.setFavoriteFolder(radioButton.getText());
					JOptionPane.showMessageDialog(FavoriteFolderForm.this, "Selected Folder set as favorite");
					break;
				}
			}
			dispose();
		});
		panel.add(submitButton);

		// Center the frame on the screen
		setLocationRelativeTo(null);

		// Set the visibility of the JFrame
		setVisible(true);
	}

}
