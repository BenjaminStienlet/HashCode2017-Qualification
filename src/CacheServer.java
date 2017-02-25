import java.util.HashSet;

public class CacheServer implements Comparable<CacheServer> {

	public CacheServer(int id, int size) {
		this.id = id;
		this.size = size;
		this.sizeLeft = size;
		this.videosInCache = new HashSet<Video>();
	}

	public int id, size, sizeLeft;

	public boolean canAddVideo(Video video) {
		return sizeLeft >= video.size;
	}

	public boolean addVideo(Video video, long latencyProfit) {
		if (!canAddVideo(video)) {
			return false;
		}
		videosInCache.add(video);
		sizeLeft -= video.size;

		video.addedToCache(this, latencyProfit);
		return true;
	}

	public void removeVideo(Video video) {
		boolean inCache = videosInCache.remove(video);

		if (inCache) {
			sizeLeft += video.size;
			video.removedFromCache(this);
		}
	}

	@Override
	public int compareTo(CacheServer o) {
		return Integer.compare(sizeLeft, o.sizeLeft);
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
		CacheServer other = (CacheServer) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheServer [id=" + id + ", size=" + size + ", sizeLeft=" + sizeLeft + "]";
	}

	public HashSet<Video> videosInCache;

	public boolean containsVideo(Video video) {
		return videosInCache.contains(video);
	}
}
