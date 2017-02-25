import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class EndPoint {

	public EndPoint(int id, int latencyToDatacenter, ArrayList<Pair<CacheServer, Integer>> connectedCaches) {
		this.id = id;
		this.latencyToDatacenter = latencyToDatacenter;
		this.connectedCachesWithLatency = connectedCaches;
		this.connectedCaches = new HashSet<CacheServer>(connectedCaches.size());
		this.cacheOfLatency = new HashMap<CacheServer, Integer>(connectedCaches.size());
		this.connectedCachesList = new ArrayList<CacheServer>();
		Collections.sort(this.connectedCachesWithLatency);
		for (Pair<CacheServer, Integer> pair : connectedCaches) {
			this.connectedCaches.add(pair.getFirst());
			this.connectedCachesList.add(pair.getFirst());
			this.cacheOfLatency.put(pair.getFirst(), pair.getSecond());
		}
	}

	public int getLatency(CacheServer cache) {
		return cacheOfLatency.get(cache);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EndPoint other = (EndPoint) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EndPoint [id=" + id + ", latencyToDatacenter=" + latencyToDatacenter + ", connectedCachesWithLatency="
				+ connectedCachesWithLatency + "]";
	}

	public int id, latencyToDatacenter;
	public ArrayList<Pair<CacheServer, Integer>> connectedCachesWithLatency;
	public HashSet<CacheServer> connectedCaches;
	public ArrayList<CacheServer> connectedCachesList;
	public HashMap<CacheServer, Integer> cacheOfLatency;

}
