import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializedConfigs implements Serializable {
	private Map<String, String> folders = null;

	public SerializedConfigs() {
		this.folders = new HashMap<>();
	}

	public SerializedConfigs(Map<String, String> folders) {
		this.folders = folders;
	}

	public Map<String, String> getFolders() {
		return folders;
	}

	public void setFolders(Map<String, String> folders) {
		this.folders = folders;
	}

	@Override
	public String toString() {
		return "SerializedConfigs{" + folders + '}';
	}
}
