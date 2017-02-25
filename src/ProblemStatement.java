import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ProblemStatement {

	public ProblemStatement(String file) {
		this.file = file;
	}

	public void readProblem() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));

		String inputSizes = reader.readLine();
		String[] inputSizesSeperate = inputSizes.split(" ");
		nrVideos = Integer.valueOf(inputSizesSeperate[0]);
		nrEndPoints = Integer.valueOf(inputSizesSeperate[1]);
		nrRequests = Integer.valueOf(inputSizesSeperate[2]);
		nrCacheServers = Integer.valueOf(inputSizesSeperate[3]);
		cacheSize = Integer.valueOf(inputSizesSeperate[4]);

		loadVideos(reader);
		createCacheServers();
		loadEndpoints(reader);
		loadRequests(reader);

	}

	private void loadVideos(BufferedReader reader) throws IOException {
		videos = new Video[nrVideos];

		String videoSizes = reader.readLine();
		String[] videoSizesSplit = videoSizes.split(" ");
		for (int i = 0; i < videoSizesSplit.length; i++) {
			videos[i] = new Video(i, Integer.valueOf(videoSizesSplit[i]));
		}
	}

	private void createCacheServers() {
		cacheServers = new CacheServer[nrCacheServers];
		for (int i = 0; i < nrCacheServers; i++) {
			cacheServers[i] = new CacheServer(i, cacheSize);
		}
	}

	private void loadEndpoints(BufferedReader reader) throws IOException {
		endPoints = new EndPoint[nrEndPoints];
		String[] latencyAndCacheNr;
		int latency;
		int cacheNr;
		ArrayList<Pair<CacheServer, Integer>> endPointCacheServers;
		String[] cacheIdAndLatency;
		for (int i = 0; i < nrEndPoints; i++) {
			latencyAndCacheNr = reader.readLine().split(" ");
			latency = Integer.valueOf(latencyAndCacheNr[0]);
			cacheNr = Integer.valueOf(latencyAndCacheNr[1]);
			endPointCacheServers = new ArrayList<Pair<CacheServer, Integer>>(cacheNr);
			for (int j = 0; j < cacheNr; j++) {
				cacheIdAndLatency = reader.readLine().split(" ");
				endPointCacheServers.add(new Pair<CacheServer, Integer>(
						cacheServers[Integer.valueOf(cacheIdAndLatency[0])], Integer.valueOf(cacheIdAndLatency[1])));
			}
			endPoints[i] = new EndPoint(i, latency, endPointCacheServers);
		}
	}

	private void loadRequests(BufferedReader reader) throws IOException {
		requests = new Request[nrRequests];
		String[] input;
		int videoId;
		int endPointId;
		int amountRequests;
		for (int i = 0; i < nrRequests; i++) {
			input = reader.readLine().split(" ");
			videoId = Integer.valueOf(input[0]);
			endPointId = Integer.valueOf(input[1]);
			amountRequests = Integer.valueOf(input[2]);
			requests[i] = new Request(videos[videoId], endPoints[endPointId], amountRequests);
			videos[videoId].addRequest(requests[i]);
			totalNrRequests += amountRequests;
		}
	}

	public Video[] videos;
	public CacheServer[] cacheServers;
	public EndPoint[] endPoints;
	public Request[] requests;

	private String file;
	public int nrCacheServers, nrEndPoints, nrVideos, cacheSize, nrRequests;
	public long totalNrRequests;
}
