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

		JButton browseButton = getFileDialogJButton(folderTextField);

		JButton submitButton = new JButton("Add");
		submitButton.addActionListener(e -> {
			File selectedFolder = new File(folderTextField.getText());
			QuickFolderAccess.addFolder(selectedFolder.getName(), selectedFolder.getAbsolutePath());
			JOptionPane.showMessageDialog(AddFolderForm.this, "Selected Folder: " + selectedFolder.getAbsolutePath());
		});

		// Add components to the panel
		panel.add(label);
		panel.add(folderTextField);
		panel.add(browseButton);
		panel.add(submitButton);

		// Add the panel to the frame
		add(panel);

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

			int result = fileChooser.showOpenDialog(AddFolderForm.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();
				folderTextField.setText(selectedFolder);
			}
		});

		return browseButton;
	}
}
