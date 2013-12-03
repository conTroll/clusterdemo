package net.rpeti.clusterdemo.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import net.rpeti.clusterdemo.Main;
import net.rpeti.clusterdemo.data.DataContainer;

public class HTMLWriter {

	private static final String CLUSTER_NO = "Cluster #";
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String DATA = "Data";
	private static final String STATISTICS = "Statistics";
	private static final String VISUALIZATION = "Visualization";
	private static final String CLUSTER = "Cluster";
	private static final String NUM_OF_DATA = "Number of Data";

	private DataContainer data;
	private File destination;
	private int[] clusteringResult;
	private int[] counts;
	private int numOfClusters;
	private BufferedImage visualization;

	public HTMLWriter(File destination, DataContainer data,
			int[] clusteringResult, int numOfClusters, BufferedImage visualization) {
		this.data = data;
		this.destination = destination;
		this.clusteringResult = clusteringResult;
		this.numOfClusters = numOfClusters;
		this.visualization = visualization;
		this.counts = new int[numOfClusters];
	}

	public void write() throws IOException{
		
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>" + NEWLINE + "<html>" + NEWLINE + "<head>" + NEWLINE +
				"<meta charset=\"UTF-8\">" + NEWLINE);
		
		try {
			Scanner scanner = new Scanner(new File(this.getClass().getResource("/text/css.txt").toURI()));
			scanner.useDelimiter("\\Z");
			String css = scanner.next();
			scanner.close();
			sb.append(css);
		} catch (URISyntaxException e) {
			Main.getController().getMainWindow().showUnhandledException(e);
		}
		
		sb.append("</head>" + NEWLINE + "<body>" + NEWLINE);


		sb.append("<h2>" + VISUALIZATION + "</h2>" + NEWLINE);
		if (! (destination.getAbsolutePath().endsWith(".html") ||
				destination.getAbsolutePath().endsWith(".htm"))){
			destination = new File(destination.getAbsolutePath() + ".html");
		}
		
		File imageDestination = 
				new File(destination.getAbsolutePath().replaceFirst("[.][^.]+$", ".png"));
		ImageIO.write(visualization, "png", imageDestination);
		double ratio = (double)visualization.getHeight() / (double)visualization.getWidth();
		sb.append("<a href=\"" + imageDestination.getName() + "\">" + NEWLINE);
		sb.append("<img src=\"" + imageDestination.getName() + "\" width=\"640px\" height=\""+ (640 * ratio) + "px\">");
		sb.append("</img>" + NEWLINE + "</a>" + NEWLINE);

		sb.append("<h1>" + DATA + "</h1>" + NEWLINE);
		for(int k = 0; k < numOfClusters; k++){
			sb.append("<h2>" + CLUSTER_NO + Integer.toString(k+1) + "</h2>");
			sb.append("<table class=\"gradienttable\">" + NEWLINE);
			//assemble header from attributes
			sb.append("<tr>" + NEWLINE);
			sb.append("<th>ID</th>" + NEWLINE);
			for(String attribute : data.getAttributes()){
				sb.append("<th>");
				sb.append(attribute);
				sb.append("</th>" + NEWLINE);
			}
			sb.append("</tr>" + NEWLINE);
			
			int count = 0;
			
			//print data rows
			for(int i = 0; i < data.getNumberOfRows(); i++){
				if (clusteringResult[i] != k) continue;
				if (data.getDataRow(i).size() == 0) continue;
				count++;
				sb.append("<tr>" + NEWLINE);
				sb.append("<td>" + i + "</td>" + NEWLINE);
				for(String value : data.getDataRow(i)){
					sb.append("<td>");
					sb.append(value);
					sb.append("</td>" + NEWLINE);
				}
				sb.append("</tr>" + NEWLINE);
			}
	
			sb.append("</table>" + NEWLINE + NEWLINE);
			counts[k] = count;
		}
		
		//print stats
		sb.append("<h1>" + STATISTICS + "</h1>" + NEWLINE);
		sb.append("<table class=\"gradienttable\" width=\"220px\">" + NEWLINE);
		sb.append("<tr>" + NEWLINE);
		sb.append("<th>" + CLUSTER + "</th>" + NEWLINE);
		sb.append("<th>" + NUM_OF_DATA + "</th>");
		sb.append("</tr>");
		for(int i = 0; i < numOfClusters; i++){
			sb.append("<tr>" + NEWLINE);
			sb.append("<td>" + i + "</td>" + NEWLINE);
			sb.append("<td>");
			sb.append(counts[i]);
			sb.append("</td>" + NEWLINE);
			sb.append("</tr>" + NEWLINE);
		}

		sb.append("</table>" + NEWLINE);
		
		sb.append("</body>" + NEWLINE + "</html>");

		//write the whole thing to file
		FileOutputStream fos = new FileOutputStream(destination);
		OutputStreamWriter sw = new OutputStreamWriter(fos, "UTF8");
		sw.write(sb.toString());
		sw.close();
	}

}