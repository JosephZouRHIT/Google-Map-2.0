package com.mymap.coremap.OSMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * author: Lining Pan
 */
public class OSMXMLInterpreter {
	private static final int threads = 32;
	public static OSMAbstractDataModel loadFromFile(String filename) {
		//try with resource
		ClassLoader classLoader = OSMXMLInterpreter.class.getClassLoader();

		try(InputStream is = classLoader.getResourceAsStream(filename)) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element root = doc.getDocumentElement();

			double[] bound = getBound((Element) root.getElementsByTagName("bounds").item(0));
			
			Map<Long, OSMNode> nodeMap = InterpretRawNodeList(root.getElementsByTagName("node"));
//			System.err.println(nodeMap);
			Map<Long, OSMWay> wayMap = InterpretRawWayList(root.getElementsByTagName("way"));
//			System.err.println(wayMap);

			return new OSMXMLDataModel(nodeMap, wayMap, null,bound);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static double[] getBound(Element e){
		double[] bound = new double[4];
		bound[0] = Double.parseDouble(e.getAttribute("maxlat"));
		bound[1] = Double.parseDouble(e.getAttribute("minlat"));
		bound[2] = Double.parseDouble(e.getAttribute("maxlon"));
		bound[3] = Double.parseDouble(e.getAttribute("minlon"));

		return bound;
	}

	private static Map<Long, OSMNode> InterpretRawNodeList(NodeList nl){
		//System.out.println("Finish extracing XML node");
		long startTime = System.currentTimeMillis();
		Map<Long, OSMNode> re = new ConcurrentHashMap<>();

		class NodeInterpreter implements Runnable{
			private Map<Long, OSMNode> target;
			private Element domElement;
			private CountDownLatch signal;
			private NodeInterpreter(Element dom, Map<Long, OSMNode> t, CountDownLatch sig) {
				target = t;
				domElement = dom;
				signal = sig;
			}

			@Override
			public void run() {
				long id = Long.parseLong(domElement.getAttribute("id"));
				double lat = Double.parseDouble(domElement.getAttribute("lat"));
				double lon = Double.parseDouble(domElement.getAttribute("lon"));
				HashMap<String, String> tag = new HashMap<>();
				NodeList tagList = domElement.getElementsByTagName("tag");
				int l = tagList.getLength();
				for(int i = 0; i < l; i ++) {
					Element tagE = (Element) tagList.item(i);
					tag.put(tagE.getAttribute("k"), tagE.getAttribute("v"));
				}
				target.put(id, new OSMNode(id,tag,lat,lon));
				signal.countDown();
			}
		}

		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch finishCountDown = new CountDownLatch(nl.getLength());
		for(int i = 0, l = nl.getLength(); i < l; i ++) {
			NodeInterpreter ni = new NodeInterpreter((Element) nl.item(i), re, finishCountDown);
			pool.execute(ni);
		}
		pool.shutdown();
		try {
			finishCountDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		//System.out.println("Node load takes : " + (endTime - startTime) + " milliseconds");
		return re;
	}



	private static Map<Long, OSMWay> InterpretRawWayList(NodeList nl){
		long startTime = System.currentTimeMillis();

		Map<Long, OSMWay> re = new ConcurrentHashMap<>();

		class NodeInterpreter implements Runnable{
			private Map<Long, OSMWay> target;
			private Element domElement;
			private CountDownLatch signal;
			private NodeInterpreter(Element dom, Map<Long, OSMWay> t, CountDownLatch sig) {
				target = t;
				domElement = dom;
				signal = sig;
			}
			
			@Override
			public void run() {
				long id = Long.parseLong(domElement.getAttribute("id"));
				
				List<Long> osmNodeList = new LinkedList<>();
				NodeList osmRawNDList = domElement.getElementsByTagName("nd");
				
				for(int i = 0, l = osmRawNDList.getLength(); i < l; i ++) {
					Element ndE = (Element) osmRawNDList.item(i);
					osmNodeList.add(Long.parseLong(ndE.getAttribute("ref")));
				}
				
				HashMap<String, String> tag = new HashMap<>();
				NodeList tagList = domElement.getElementsByTagName("tag");
				
				for(int i = 0, l = tagList.getLength(); i < l; i ++) {
					Element tagE = (Element) tagList.item(i);
					tag.put(tagE.getAttribute("k"), tagE.getAttribute("v"));
				}
				target.put(id, new OSMWay(id,tag,osmNodeList));
				signal.countDown();
			}
		}
		
		ExecutorService pool = Executors.newFixedThreadPool(threads); 
		CountDownLatch finishCountDown = new CountDownLatch(nl.getLength());
		for(int i = 0, l = nl.getLength(); i < l; i ++) {
			NodeInterpreter ni = new NodeInterpreter((Element) nl.item(i), re, finishCountDown);
			pool.execute(ni);
		}
		pool.shutdown();
		try {
			finishCountDown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		//System.out.println("ways load takes : " + (endTime - startTime) + " milliseconds");
		return re;
	}
	
	
}
