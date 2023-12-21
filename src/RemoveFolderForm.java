import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveFolderForm extends JFrame {
	private JPanel mainPanel;
	private JPanel checksMainContainer;
	private JButton removeButton;

	public RemoveFolderForm(String title) {
		this.setTitle(title);
		this.setContentPane(mainPanel);
		this.setLocationRelativeTo(null);

		removeButton.setEnabled(false);

		JPanel checkBoxesPanel = new JPanel();
		checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));
		checksMainContainer.add(checkBoxesPanel);

		List<JCheckBox> checkBoxes = new ArrayList<>();
		PopupMenu popup = QuickFolderAccess.getPopup();

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);

			if (!Arrays.asList(QuickFolderAccess.NAMES_TO_AVOID).contains(menuItem.getLabel())) {
				JCheckBox checkBox = new JCheckBox(menuItem.getLabel());
				checkBox.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						boolean atLeastOneSelected = checkBoxes.stream().anyMatch(JCheckBox::isSelected);
						removeButton.setEnabled(atLeastOneSelected);
					}
				});
				checkBoxes.add(checkBox);
				checkBoxesPanel.add(checkBox);
			}
		}

		List<String> namesList = new ArrayList<>();

		removeButton.addActionListener(e -> {
			for (JCheckBox checkBox : checkBoxes) {
				if (checkBox.isSelected()) {
					namesList.add(checkBox.getText());
				}
			}
			QuickFolderAccess.removeMultipleFolders(namesList);

			StringBuilder message = new StringBuilder("Folders Removed: ");
			for (String s : namesList) {
				message.append("\n- ").append(s);
			}

			JOptionPane.showMessageDialog(RemoveFolderForm.this, message, "QuickFolderAccess: Success", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		});

		this.pack();
		this.setVisible(true);
	}
}
