import javax.swing.*;
import java.io.File;

public class AddFolderForm extends JFrame {

	public AddFolderForm(String name) {
		setTitle(name);
		setSize(400, 300);

		JPanel panel = new JPanel();

		JLabel label = new JLabel("Select a folder to add:");

		JTextField folderTextField = new JTextField(20);
		folderTextField.setEditable(false);

		JCheckBox setAsFavoriteCheckBox = new JCheckBox("Set as Favorite");

		JButton submitButton = new JButton("Add");
		submitButton.setEnabled(false);
		submitButton.addActionListener(e -> {
			File selectedFolder = new File(folderTextField.getText());
			QuickFolderAccess.addFolder(selectedFolder.getName(), selectedFolder.getAbsolutePath());

			if (setAsFavoriteCheckBox.isSelected()) {
				QuickFolderAccess.setFavoriteFolder(selectedFolder.getName());
			}

			JOptionPane.showMessageDialog(AddFolderForm.this, "Selected Folder: " + selectedFolder.getAbsolutePath());
		});

		JButton browseButton = getFileDialogJButton(folderTextField, submitButton);

		panel.add(label);
		panel.add(folderTextField);
		panel.add(browseButton);
		panel.add(setAsFavoriteCheckBox);
		panel.add(submitButton);

		add(panel);

		setLocationRelativeTo(null);

		setVisible(true);
	}

	private JButton getFileDialogJButton(JTextField folderTextField, JButton submitButton) {
		JButton browseButton = new JButton("Browse");

		browseButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int result = fileChooser.showOpenDialog(AddFolderForm.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();
				folderTextField.setText(selectedFolder);
				submitButton.setEnabled(true);
			}
		});

		return browseButton;
	}
}
