package at.tugraz.xp10.firebase;

import java.util.HashMap;
import at.tugraz.xp10.model.ModelBase;

public interface DatabaseListValueEventListener {
    <T extends ModelBase> void onNewData(HashMap<String, T> data);
}
