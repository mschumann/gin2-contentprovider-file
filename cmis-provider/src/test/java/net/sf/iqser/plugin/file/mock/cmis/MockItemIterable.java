package net.sf.iqser.plugin.file.mock.cmis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.ItemIterable;

public class MockItemIterable<T> implements ItemIterable<T> {
	
	List<T> data = new ArrayList<T>();
	
	public MockItemIterable(List<T> data){
		this.data = data;
	}

	public ItemIterable<T> skipTo(long l) {
		return null;
	}

	public ItemIterable<T> getPage() {
		return null;
	}

	public ItemIterable<T> getPage(int i) {
		return null;
	}

	public Iterator<T> iterator() {
		return data.iterator();
	}

	public long getPageNumItems() {
		return 0;
	}

	public boolean getHasMoreItems() {
		return false;
	}

	public long getTotalNumItems() {
		return data.size();
	}

}
