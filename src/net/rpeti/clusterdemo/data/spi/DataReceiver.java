package net.rpeti.clusterdemo.data.spi;

import java.util.List;

//TODO documentation
public interface DataReceiver {
	public void addAttribute(String attribute) throws UnsupportedOperationException;
	public void addData(List<String> row) throws IllegalArgumentException;
}
