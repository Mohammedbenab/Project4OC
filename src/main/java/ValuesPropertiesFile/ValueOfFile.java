package ValuesPropertiesFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ValueOfFile {

	public String getValuesProp(String value) throws IOException {
		String value1 = null;
		Properties prop = new Properties();
		String propFileName = "values.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		try {
			prop.load(inputStream);
			value1 = prop.getProperty(value);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return value1;
	}

	public String getUrl() {
		String value = null;
		try {
			value = this.getValuesProp("url");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;

	}

	public String getUser() {
		String value = null;
		try {
			value = this.getValuesProp("user");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public String getPassword() {
		String value = null;
		try {
			value = this.getValuesProp("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

}
