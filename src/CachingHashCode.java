import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CachingHashCode {

	public static void main(String[] args) {
		try {
			new CachingHashCode(args[0]);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public CachingHashCode(String file) throws IOException {
		this.problemStatement = new ProblemStatement(file);
		this.problemStatement.readProblem();

		System.err.println("read all");
		calcSolution();
		output(problemStatement.cacheServers);
		System.err.println(calcScore(problemStatement.cacheServers));
	}

	public long calcIncrementalScore(CacheServer[] solution, Video video, CacheServer cache, long currentScore) {
		long score = 0;
		for (Request request : video.getRequestsForVideo()) {
			int minLatency = Integer.MAX_VALUE;
			for (Pair<CacheServer, Integer> pairCacheLatency : request.endpoint.connectedCachesWithLatency) {
				if (solution[pairCacheLatency.getFirst().id].containsVideo(request.video)) {
					minLatency = Math.min(pairCacheLatency.getSecond(), minLatency);
				}
			}
			if (minLatency < Integer.MAX_VALUE) {
				int datacenterLatency = request.endpoint.latencyToDatacenter;
				score += (datacenterLatency - minLatency) * request.requests;
			}
		}
		return score;
	}

	public long calcScore(CacheServer[] solution) {
		long score = 0;
		for (Request request : problemStatement.requests) {
			int minLatency = Integer.MAX_VALUE;
			for (Pair<CacheServer, Integer> pairCacheLatency : request.endpoint.connectedCachesWithLatency) {
				if (solution[pairCacheLatency.getFirst().id].containsVideo(request.video)) {
					minLatency = Math.min(pairCacheLatency.getSecond(), minLatency);
				}
			}
			if (minLatency < Integer.MAX_VALUE) {
				int datacenterLatency = request.endpoint.latencyToDatacenter;
				score += (datacenterLatency - minLatency) * request.requests;
			}
		}
		return (long) ((1000 * score) / (double) this.problemStatement.totalNrRequests);
	}

	public long latencyProfit(Video video, CacheServer cache) {
		long profit = 0;
		int latencyOfCacheForEndPoint;
		int tmpLatency;
		for (Request request : video.videoRequests) {
			if (!request.endpoint.connectedCaches.contains(cache))
				continue;
			latencyOfCacheForEndPoint = request.endpoint.getLatency(cache);
			int currentLongestLatency = request.endpoint.latencyToDatacenter;
			for (CacheServer otherCache : video.caches) {
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

	public void calcSolution() {
		ArrayList<Video> videos = new ArrayList<Video>(Arrays.asList(problemStatement.videos));
		Collections.sort(videos, new Comparator<Video>() {

			@Override
			public int compare(Video arg0, Video arg1) {
				return -Double.compare((double) arg0.totalVideoRequests / arg0.size,
						(double) arg1.totalVideoRequests / arg1.size);
			}
		});
		int videosPlaced = 0;

		PriorityQueue<Video> p = new PriorityQueue<Video>(videos);
		Video video;
		while (!p.isEmpty()) {
			video = p.poll();
//			System.err.println(video.id + " " + video.totalLatencyFromVideo);
			long bestScore = -1;
			CacheServer bestCache = null;
			for (CacheServer cache : problemStatement.cacheServers) {
				if (cache.canAddVideo(video)) {
					long newScore = latencyProfit(video, cache);
					if (newScore > bestScore) {
						bestScore = newScore;
						bestCache = cache;
					}
				}
			}
			if (bestCache != null) {
				videosPlaced++;
				bestCache.addVideo(video, bestScore);
				p.add(video);
			}
			System.err.println("Videos placed: " + videosPlaced + "\tPQueue size: " + p.size());
		}
	}

	public static void output(CacheServer[] solution) {
		int size = 0;
		for (CacheServer cache : solution) {
			if (cache.videosInCache.size() > 0) {
				size++;
			}
		}
		System.out.println(size);
		for (CacheServer cache : solution) {
			if (cache.videosInCache.size() > 0) {
				System.out.print(cache.id);
				for (Video video : cache.videosInCache) {
					System.out.print(" " + video.id);
				}
				System.out.print("\n");
			}
		}
	}

	public ProblemStatement problemStatement;

}
