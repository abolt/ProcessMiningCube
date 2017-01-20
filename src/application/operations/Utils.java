package application.operations;

import java.util.List;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.TreeSet;

import com.google.common.collect.Lists;

public class Utils {
	
	public static List<Integer> parseEventIDs(List<String> textList){
		Set<Integer> idSet = new TreeSet<Integer>();
		
		for(String line : textList){
			String newLine = line.substring(1, line.length()-1);
			String[] items = newLine.split(",");
			for(String i : items){
				idSet.add(Integer.parseInt(i.trim()));
			}
		}
		return Lists.newArrayList(idSet);
	}

}
