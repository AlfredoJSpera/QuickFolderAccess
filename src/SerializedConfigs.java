import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializedConfigs implements Serializable {
	@Serial
	private static final long serialVersionUID = 5089920150690765457L;

	private Map<String, String> folders;
	private String favoriteFolderName = null;

	public SerializedConfigs() {
		this.folders = new HashMap<>();
	}

	public SerializedConfigs(Map<String, String> folders) {
		this.folders = folders;
	}

	public SerializedConfigs(Map<String, String> folders, String favoriteFolder) {
		this.folders = folders;
		this.favoriteFolderName = favoriteFolder;
	}

	public Map<String, String> getFolders() {
		return folders;
	}

	public void setFolders(Map<String, String> folders) {
		this.folders = folders;
	}

	public String getFavoriteFolderName() {
		return favoriteFolderName;
	}

	public void setFavoriteFolderName(String favoriteFolderName) {
		this.favoriteFolderName = favoriteFolderName;
	}

	@Override
	public String toString() {
		return "SerializedConfigs{" +
				"folders=" + folders +
				", favoriteFolder=" + favoriteFolderName +
				'}';
	}
}
