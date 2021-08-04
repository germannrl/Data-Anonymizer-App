package src.data;

import java.util.*;

public interface Ontologia {

	public float distance_WP(Long offset1, Long offset2);
	public List<Long> getHyponymTree(Long offset);
}
