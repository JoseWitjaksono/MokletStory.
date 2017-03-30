package id.sch.smktelkom_mlg.project2.xirpl61921272934.mokletstory;

import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by Jose Witjaksono on 30/03/2017.
 */

public class Upload {

	public String name;
	public String url;

	// Default constructor required for calls to
	// DataSnapshot.getValue(User.class)
	public Upload() {
	}

	public Upload(String name, String url) {
		this.name = name;
		this.url= url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
