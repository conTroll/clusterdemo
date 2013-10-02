package net.rpeti.clusterdemo.data.spi;

import java.util.List;

public interface DataReceiver {
	public void addAttribute(String attribute);
	public void addData(List<String> row);
}
