package net.sourceforge.cilib.problem.dataset;

import org.apache.log4j.Logger;

import net.sourceforge.cilib.type.types.container.Vector;

public class CachingDataSetBuilder extends AssociatedPairDataSetBuilder {
	private static final long serialVersionUID = -8098125665317527403L;
	private static Logger log = Logger.getLogger(CachingDataSetBuilder.class);

	private Vector mean = null;
	private Vector variance = null;
	private double distanceCache[] = null;
	private int cacheSize = 0;

	public CachingDataSetBuilder() {
		super();
	}
	
	@Override
	public void initialise() {
		super.initialise();
		cacheDistances();
		log.info("Caching dataset mean");
		mean = getSetMean(patterns);
		log.info("Caching dataset variance");
		variance = getSetVariance(patterns);
	}

	private void cacheDistances() {
		log.info("Caching distances between patterns");
		int numPatterns = patterns.size();
		cacheSize = (numPatterns * (numPatterns - 1)) / 2;
		distanceCache = new double[cacheSize];
		int index = 0;
		for(int y = 0; y < numPatterns - 1; y++) {
			for(int x = y + 1; x < numPatterns; x++) {
				index = x + (numPatterns * y) - (((y + 1) * (y + 2)) / 2);
				distanceCache[index] = calculateDistance(patterns.get(x).data, patterns.get(y).data);
			}
		}
	}

	@Override
	public double calculateDistance(int x, int y) {
		if(x < 0 || y < 0)
			throw new IllegalArgumentException("No pattern at (" + x + ", " + y + ")");

		if(x == y)
			return 0.0;

		if(y > x)	// swap the x and y index
		{
			int tmp = x;
			x = y;
			y = tmp;
		}
		return distanceCache[x + (patterns.size() * y) - (((y + 1) * (y + 2)) / 2)];
	}

	@Override
	public Vector getMean() {
		return mean;
	}

	@Override
	public Vector getVariance() {
		return variance;
	}
}