package net.rpeti.clusterdemo.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.rpeti.clusterdemo.data.DataContainer;

public class HTMLWriter {

	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String DATA = "Data";
	private static final String CLUSTERING_RESULT = "Clustering Result";
	private static final String VISUALIZATION = "Visualization";
	private static final String CLUSTER = "Cluster";
	private static final String ASSIGNED_DATA = "Assigned Data Points";

	private DataContainer data;
	private File destination;
	private int[] clusteringResult;
	private int numOfClusters;
	private BufferedImage visualization;

	public HTMLWriter(File destination, DataContainer data,
			int[] clusteringResult, int numOfClusters, BufferedImage visualization) {
		this.data = data;
		this.destination = destination;
		this.clusteringResult = clusteringResult;
		this.numOfClusters = numOfClusters;
		this.visualization = visualization;
	}

	public void write() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>" + NEWLINE + "<html>" + NEWLINE + "<body>");


		sb.append("<h2>" + VISUALIZATION + "</h2>" + NEWLINE);
		File imageDestination = 
				new File(destination.getAbsolutePath().replaceFirst("[.][^.]+$", ".png"));
		ImageIO.write(visualization, "png", imageDestination);
		sb.append("<a href=\"" + imageDestination.getName() + "\">" + NEWLINE);
		sb.append("<img src=\"" + imageDestination.getName() + "\" width=\"640px\" height=\"480px\">");
		sb.append("</img>" + NEWLINE + "</a>" + NEWLINE);

		//print clustering result
		sb.append("<h2>" + CLUSTERING_RESULT + "</h2>" + NEWLINE);
		sb.append("<table border=\"1\">" + NEWLINE);
		sb.append("<tr>" + NEWLINE);
		sb.append("<th>" + CLUSTER + "</th>" + NEWLINE);
		sb.append("<th>" + ASSIGNED_DATA + "</th>");
		sb.append("</tr>");
		for(int i = 0; i < numOfClusters; i++){
			boolean first = true;
			sb.append("<tr>" + NEWLINE);
			sb.append("<td>" + i + "</td>" + NEWLINE);
			sb.append("<td>");
			for(int j = 0; j < clusteringResult.length; j++){
				if(clusteringResult[j] == i && data.getDataRow(j).size() != 0){
					sb.append(first ? Integer.toString(j) : ", " + j);
					first = false;
				}
			}
			sb.append("</td>" + NEWLINE);
			sb.append("</tr>" + NEWLINE);
		}

		sb.append("</table>" + NEWLINE);

		sb.append("<h2>" + DATA + "</h2>" + NEWLINE);
		sb.append("<table border=\"1\">" + NEWLINE);
		//assemble header from attributes
		sb.append("<tr>" + NEWLINE);
		sb.append("<th>ID</th>" + NEWLINE);
		for(String attribute : data.getAttributes()){
			sb.append("<th>");
			sb.append(attribute);
			sb.append("</th>" + NEWLINE);
		}
		sb.append("</tr>" + NEWLINE);

		//print data rows
		for(int i = 0; i < data.getNumberOfRows(); i++){
			if (data.getDataRow(i).size() == 0) continue;
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

		sb.append("</body>" + NEWLINE + "</html>");

		//write the whole thing to file
		FileWriter fw = new FileWriter(destination);
		fw.write(sb.toString());
		fw.close();
	}

}