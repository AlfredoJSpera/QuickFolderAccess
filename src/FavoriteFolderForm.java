import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteFolderForm extends JFrame {
	private JPanel mainPanel;
	private JPanel radiosMainContainer;
	private JButton setAsFavoriteButton;

	public FavoriteFolderForm(String title) {
		this.setTitle(title);
		this.setContentPane(mainPanel);
		this.setLocationRelativeTo(null);

		setAsFavoriteButton.setEnabled(false);

		JPanel radioButtonsPanel = new JPanel();
		radioButtonsPanel.setLayout(new BoxLayout(radioButtonsPanel, BoxLayout.Y_AXIS));
		radiosMainContainer.add(radioButtonsPanel);

		ButtonGroup buttonGroup = new ButtonGroup();
		List<JRadioButton> radioButtons = new ArrayList<>();
		PopupMenu popup = QuickFolderAccess.getPopup();

		String currentFavorite = QuickFolderAccess.getConfig().getFavoriteFolderName();
		boolean favoriteSet = false;

		for (int i = 0; i < popup.getItemCount(); i++) {
			MenuItem menuItem = popup.getItem(i);

			if (!Arrays.asList(QuickFolderAccess.NAMES_TO_AVOID).contains(menuItem.getLabel())) {
				JRadioButton radioButton = new JRadioButton(menuItem.getLabel());

				if (!favoriteSet && currentFavorite != null && menuItem.getLabel().equals(currentFavorite)) {
					radioButton.setSelected(true);
					favoriteSet = true;
				}

				radioButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						for (JRadioButton radioButton : radioButtons) {
							if (radioButton.getText().equals(currentFavorite)) {
								setAsFavoriteButton.setEnabled(false);
							} else if (radioButton.isSelected()) {
								setAsFavoriteButton.setEnabled(true);
								break;
							}
						}
					}
				});

				buttonGroup.add(radioButton);
				radioButtons.add(radioButton);
				radioButtonsPanel.add(radioButton);
			}
		}

		setAsFavoriteButton.addActionListener(e -> {
			for (JRadioButton radioButton : radioButtons) {
				if (radioButton.isSelected()) {
					QuickFolderAccess.setFavoriteFolder(radioButton.getText());

					JOptionPane.showMessageDialog(FavoriteFolderForm.this, "Folder set as favorite: " + radioButton.getText(), "QuickFolderAccess: Success", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
			dispose();
		});

		this.pack();
		this.setVisible(true);
	}
}
