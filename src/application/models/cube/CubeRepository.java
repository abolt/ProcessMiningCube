package application.models.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CubeRepository implements Serializable {

	/**
	 * This class is used to store a collection of cubes, that will be presented
	 * to the user in the cube repository
	 */
	private static final long serialVersionUID = 1569592154362738299L;

	private SortedMap<String, Cube> repository;

	public CubeRepository() {
		repository = new TreeMap<String, Cube>();
		lookForExistingCubes();
	}
	
	private void lookForExistingCubes() {
		// TODO check if there are cubes stored in disk in the user folder
		
	}

	public boolean cubeExists(Cube cube) {
		return repository.containsKey(cube.getCubeInfo().getName());
	}

	public void addCube(Cube cube) {
		repository.put(cube.getCubeInfo().getName(), cube);
	}

	public void removeCube(Cube cube) {
		repository.remove(cube.getCubeInfo().getName());
	}

	public List<String> getCubeNames() {
		List<String> cubeNames = new ArrayList<String>();
		for (String name : repository.keySet())
			cubeNames.add(name);
		return cubeNames;
	}

	public Cube getCube(String name) {
		return repository.get(name);
	}

}
