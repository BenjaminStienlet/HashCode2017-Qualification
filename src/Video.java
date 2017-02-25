import java.util.ArrayList;
import java.util.HashMap;

public class Video implements Comparable<Video> {

	public Video(int id, int size) {
		this.id = id;
		this.size = size;
		this.videoRequests = new ArrayList<Request>();
		this.caches = new ArrayList<CacheServer>();
		this.totalLatencyFromVideo = 0;
		this.cachedLatency = new HashMap<CacheServer, Long>();
		this.cachesPossibleProfit = new ArrayList<CacheServer>();
	}

	public void addRequest(Request request) {
		videoRequests.add(request);
		totalVideoRequests += request.requests;
		totalLatencyFromVideo += request.requests * request.endpoint.latencyToDatacenter;
		for (CacheServer cache : request.endpoint.connectedCaches) {
			if (!cachesPossibleProfit.contains(cache)) {
				this.cachesPossibleProfit.add(cache);
			}
		}
	}

	public ArrayList<Request> getRequestsForVideo() {
		return videoRequests;
	}

	@Override
	public String toString() {
		return "Video [id=" + id + "]";
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
		Video other = (Video) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void addedToCache(CacheServer cacheServer, long latencyProfit) {
		caches.add(cacheServer);
		cachedLatency.put(cacheServer, latencyProfit);
		totalLatencyFromVideo -= latencyProfit;
	}

	public void removedFromCache(CacheServer cache) {
		caches.remove(cache);
		totalLatencyFromVideo += cachedLatency.remove(cache);
	}

	public ArrayList<CacheServer> caches;
	public int id, size;
	public ArrayList<Request> videoRequests;
	public long totalVideoRequests;
	public long totalLatencyFromVideo;
	public HashMap<CacheServer, Long> cachedLatency;
	public ArrayList<CacheServer> cachesPossibleProfit;

	@Override
	public int compareTo(Video o) {
		// return Double.compare((double) this.size /
		// this.totalLatencyFromVideo,
		// (double) o.size / o.totalLatencyFromVideo);

		return -Long.compare( latencyProfitCalc() / this.size, o.cachedProfit / o.size);
	}

	public long latencyProfitCalc() {
		long best = 0, tmp;
		CacheServer bestCache = null;
		for (CacheServer cache : this.cachesPossibleProfit) {
			tmp = latencyProfit(cache);
			if (tmp > best) {
				best = tmp;
				bestCache = cache;
			}
		}
		this.cachedProfit = best;
		this.cachedBestCache = bestCache;
		return best;
	}

	public long latencyProfit(CacheServer cache) {
		long profit = 0;
		int latencyOfCacheForEndPoint;
		int tmpLatency;
		for (Request request : this.videoRequests) {
			if (!request.endpoint.connectedCaches.contains(cache))
				continue;
			latencyOfCacheForEndPoint = request.endpoint.getLatency(cache);
			int currentLongestLatency = request.endpoint.latencyToDatacenter;
			for (CacheServer otherCache : this.caches) {
				if (request.endpoint.connectedCaches.contains(otherCache)
						&& currentLongestLatency > (tmpLatency = request.endpoint.getLatency(otherCache))) {
					currentLongestLatency = tmpLatency;
					if (currentLongestLatency < latencyOfCacheForEndPoint)
						break;
				}
			}
			if (currentLongestLatency > latencyOfCacheForEndPoint)
				profit += (currentLongestLatency - latencyOfCacheForEndPoint) * request.requests;
		}
		return profit;
	}

	public long cachedProfit;
	public CacheServer cachedBestCache;
}
