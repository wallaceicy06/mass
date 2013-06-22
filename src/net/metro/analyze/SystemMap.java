/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.Service;
import net.metro.systemobjects.ServiceStop;
import net.metro.systemobjects.Stop;
import net.metro.systemobjects.WayPoint;
import net.metro.systemobjects.dataobjects.Data;
import net.metro.systemobjects.dataobjects.DataType;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;

/**
 * The hub for everything map-related in MASS. It is the component placed within
 * {@code MapFrame}s which paints a map with {@code RoutePath}s, {@code Service}
 * s, and corresponding data. It extends the {@code JMapViewer} class to add
 * functionality such as highlighting points and coloring route paths according
 * to numerical values.
 * 
 * @author Sean Harger
 * 
 */
public class SystemMap extends JMapViewer implements MarkerListener {
	private static final long serialVersionUID = -422170301841004026L;

	private static final double LOSANGELES_LAT = 34.052219;
	private static final double LOSANGELES_LONG = -118.243347;

	private static final double CLOSE_DISTANCE = 6;
	private static final int HIGHLIGHTED_MARKER_SIZE = 5;
	private static final int REGULAR_MARKER_SIZE = 3;

	private static final int BACKGROUNDLINEWIDTH = 1;
	private static final int SELECTEDLINEWIDTH = 3;
	private static final int SEGMENTDATALINEWIDTH = 5;
	private static final int POINTDATALINEWIDTH = 1;

	private ArrayList<Service> selectedServices;
	private ArrayList<RoutePath> selectedRoutePaths;
	private ArrayList<Route> allRoutes;
	private DataType selectedDataType;

	private ArrayList<Stop> visibleStopMarkers;
	private ArrayList<Stop> stopsWithData;
	private WayPoint highlightedMapMarker;
	private Point mousePosition;
	private boolean isHighlightedMarkerMoving;
	private boolean backgroundRoutesVisible;

	private MapFrame mapFrame;
	private MetroMapController controller;

	private Color[] colorScale;
	private Color storedHighlighedMarkerColor;
	private boolean isScaleFixed;
	private int scaleMinValOverride;
	private int scaleMaxValOverride;
	private static final Color BACKGROUNDSTOPCOLOR = new Color(228, 228, 228);
	private static final Color BACKGROUNDLINECOLOR = new Color(187, 187, 187);
	private static final Color SELECTEDSTOPCOLOR = new Color(17, 177, 255);
	private static final Color SELECTEDLINECOLOR = Color.BLACK;

	/**
	 * Constructs a {@code SystemMap}
	 * 
	 * @param mpFrm
	 *            {@code MapFrame} to which this {@code SystemMap} belongs
	 */
	public SystemMap(MapFrame mpFrm) {
		super(new MemoryTileCache(), 4);
		this.setDisplayPositionByLatLon(SystemMap.LOSANGELES_LAT,
				SystemMap.LOSANGELES_LONG, 10);

		mapFrame = mpFrm;

		controller = new MetroMapController(this);
		controller.addMarkerListener(this);

		allRoutes = new ArrayList<Route>();
		selectedServices = new ArrayList<Service>();
		selectedRoutePaths = new ArrayList<RoutePath>();
		stopsWithData = new ArrayList<Stop>();
		selectedDataType = DataType.BOARDINGS;
		backgroundRoutesVisible = true;

		visibleStopMarkers = new ArrayList<Stop>();
		highlightedMapMarker = null;
		isHighlightedMarkerMoving = false;

		mousePosition = new Point();

		isScaleFixed = false;
		scaleMinValOverride = -1;
		scaleMaxValOverride = -1;
		setUpColorScale();
	}

	/**
	 * Sets up color scale for representation of data. Default color scale is
	 * red for maximum and green for minimum.
	 */
	public void setUpColorScale() {
		final int RED_MIN = 0;
		final int RED_MAX = 255;
		final int GREEN_MIN = 0;
		final int GREEN_MAX = 255;
		colorScale = new Color[(RED_MAX - RED_MIN + 1)
				+ (GREEN_MAX - GREEN_MIN + 1)];

		int index = 0;

		for (int red = RED_MIN; red <= RED_MAX; red++) {
			colorScale[index] = new Color(red, GREEN_MAX, 0);
			index++;
		}

		for (int green = GREEN_MAX; green >= GREEN_MIN; green--) {
			colorScale[index] = new Color(RED_MAX, green, 0);
			index++;
		}
	}

	/**
	 * Returns the {@code MapFrame} to which this {@code SystemMap} belongs.
	 * 
	 * @return the {@code MapFrame} to which this {@code SystemMap} belongs.
	 */
	protected MapFrame getMapFrame() {
		return mapFrame;
	}

	/**
	 * Returns the color scale minimum numerical value. If the scale is not
	 * fixed, then this method finds the relative minimum value according to the
	 * currently selected services and returns it; if the scale is fixed, then
	 * it returns the overridden scale minimum specified from the
	 * {@code DataControlPalette}.
	 * 
	 * @return the current color scale minimum value.
	 */
	protected int getScaleMin() {
		if (isScaleFixed) {
			return scaleMinValOverride;
		} else {
			return getRelativeMin();
		}
	}

	/**
	 * Returns the color scale maximum numerical value. If the scale is not
	 * fixed, then this method finds the relative maximum value according to the
	 * currently selected services and returns it; if the scale is fixed, then
	 * it returns the overridden scale maximum specified from the
	 * {@code DataControlPalette}.
	 * 
	 * @return the current color scale maximum value.
	 */
	protected int getScaleMax() {
		if (isScaleFixed) {
			return scaleMaxValOverride;
		} else {
			return getRelativeMax();
		}
	}

	/**
	 * Returns the relative minimum data value according to the currently
	 * selected services.
	 * 
	 * @return the relative minimum data value
	 */
	protected int getRelativeMin() {
		return Service
				.findMinData(getSelectedDataType(), getSelectedServices());
	}

	/**
	 * Returns the relative maximum data value according to the currently
	 * selected services.
	 * 
	 * @return the relative maximum data value
	 */
	protected int getRelativeMax() {
		return Service
				.findMaxData(getSelectedDataType(), getSelectedServices());
	}

	/**
	 * Calculates the relative minimum data value within the currently viewable
	 * map area.
	 * 
	 * @return the relative minimum data value within the currently viewable map
	 *         area.
	 */
	protected int getViewableMin() {
		ArrayList<ServiceStop> visibleServiceStops = new ArrayList<ServiceStop>();

		for (Stop st : visibleStopMarkers) {
			if (isStopWithData(st)) {
				visibleServiceStops.add(findServiceStop(st));
			}
		}

		return ServiceStop.findMinData(getSelectedDataType(),
				visibleServiceStops);
	}

	/**
	 * Calculates the relative maximum data value within the currently viewable
	 * map area.
	 * 
	 * @return the relative maximum data value within the currently viewable map
	 *         area.
	 */
	protected int getViewableMax() {
		ArrayList<ServiceStop> visibleServiceStops = new ArrayList<ServiceStop>();

		for (Stop st : visibleStopMarkers) {
			if (isStopWithData(st)) {
				visibleServiceStops.add(findServiceStop(st));
			}
		}

		return ServiceStop.findMaxData(getSelectedDataType(),
				visibleServiceStops);
	}

	/**
	 * Sets the color scale to fixed. This means that the data coloring will
	 * remain static and will not update when new services are added or the
	 * period changes.
	 * 
	 * @param fixed
	 *            {@code true} to enable fixed scale
	 */
	protected void setScaleFixed(boolean fixed) {
		isScaleFixed = fixed;
		System.out.println("Set scale fixed: " + fixed);
	}

	/**
	 * Overrides the minimum scale value with the specified integer.
	 * 
	 * @param integer
	 *            value desired to be set as the fixed minimum value.
	 */
	protected void overrideScaleMin(int min) {
		scaleMinValOverride = min;
		repaint();
		System.out.println("Overrode minimum to " + min);
	}

	/**
	 * Overrides the maximum scale value with the specified integer.
	 * 
	 * @param integer
	 *            value desired to be set as the fixed maximum value.
	 */
	protected void overrideScaleMax(int max) {
		scaleMaxValOverride = max;
		repaint();
		System.out.println("Overrode maximum to " + max);
	}

	/**
	 * Returns all viewable routes in this {@code SystemMap}.
	 * 
	 * @return all viewable routes in this {@code SystemMap}.
	 */
	protected ArrayList<Route> getViewableRoutes() {
		return allRoutes;
	}

	/**
	 * Returns the {@code MetroMapController} that listens for user actions.
	 * 
	 * @return the {@code MetroMapController} that listens for user actions.
	 */
	protected MetroMapController getController() {
		return controller;
	}

	/**
	 * Returns the array of {@code Color} objects in the color scale.
	 * 
	 * @return the array of {@code Color} objects in the color scale.
	 */
	protected Color[] getColorScale() {
		return colorScale;
	}

	/**
	 * Returns the selected services on the map. These are services that matched
	 * the {@code RoutePath} and {@code Service} constraints specified in the
	 * {@code DataControlPalette} and are available to display data on the map.
	 * 
	 * @return the selected services on the map.
	 */
	protected ArrayList<Service> getSelectedServices() {
		return selectedServices;
	}

	/**
	 * Returns the currently selected {@code DataType}.
	 * 
	 * @return the currently selected {@code DataType}.
	 */
	protected DataType getSelectedDataType() {
		return selectedDataType;
	}

	/**
	 * Sets the viewable routes to the list of specified routes. Any routes not
	 * passed to the {@code SystemMap} via this method will NOT be available for
	 * data viewing.
	 * 
	 * @param rtes
	 *            list of {@code Route}s to be made available to the user for
	 *            data viewing
	 */
	protected void setViewableRoutes(ArrayList<Route> rtes) {
		allRoutes = rtes;
		repaint();
	}

	/**
	 * Sets the visibility of the background routes and stops. Normally,
	 * background routes and stops are painted gray, but they can become
	 * intrusive during data interpretation.
	 * 
	 * @param visible
	 *            {@code true} to set the background routes visible
	 */
	protected void setBackgroundRoutesVisible(boolean visible) {
		backgroundRoutesVisible = visible;
	}

	/**
	 * Sets the selected {@code Service} objects to be colored according to
	 * their data values.
	 * 
	 * @param svcs
	 *            desired services to be selected for data interpretation
	 */
	protected void setSelectedServices(ArrayList<Service> svcs) {
		selectedServices = svcs;
		repaint();
	}

	/**
	 * Sets one selected {@code Service } object to be colored according to its
	 * data values.
	 * 
	 * @param svc
	 *            desired service to be selected for data interpretation
	 */
	protected void setSelectedService(Service svc) {
		ArrayList<Service> oneSvc = new ArrayList<Service>();
		oneSvc.add(svc);
		setSelectedServices(oneSvc);
	}

	/**
	 * Sets the selected {@code RoutePath} objects to be painted on the map.
	 * Since RoutePaths are not associated with data, only a single color will
	 * be painted showing the path of the routes. This is typically used by the
	 * {@code LineEditor} for editing a route path.
	 * 
	 * @param rtePths
	 *            list of {@code RoutePath} objects to select
	 */
	protected void setSelectedRoutePaths(ArrayList<RoutePath> rtePths) {
		if (rtePths == null) {
			clearSelectedRoutePaths();
		} else {
			selectedRoutePaths = rtePths;
			repaint();
		}
	}

	/**
	 * Sets the selected {@code RoutePath} object to be painted on the map.
	 * Since RoutePaths are not associated with data, only a single color will
	 * be painted showing the path of this route. This is typically used by the
	 * {@code LineEditor} for editing a route path.
	 * 
	 * @param rtePths
	 *            {@code RoutePath} object to select
	 */
	protected void setSelectedRoutePath(RoutePath rtePth) {
		if (rtePth == null) {
			clearSelectedRoutePaths();
		} else {
			ArrayList<RoutePath> onePath = new ArrayList<RoutePath>();
			onePath.add(rtePth);
			setSelectedRoutePaths(onePath);
		}
	}

	/**
	 * Clears the selected {@code RoutePath} objects list.
	 */
	protected void clearSelectedRoutePaths() {
		selectedRoutePaths.clear();
		repaint();
	}

	/**
	 * Sets the selected {@code DataType} to be used to paint the selected
	 * {@code Service}s.
	 * 
	 * @param typ
	 *            {@code DataType} to use
	 */
	protected void setSelectedDataType(DataType typ) {
		selectedDataType = typ;
	}

	/**
	 * Determines whether a {@code RoutePath} is selected or not.
	 * 
	 * @param pth
	 *            {@code RoutePath} to test for selection
	 * @return {@code true} if the specified {@code RoutePath} is selected.
	 */
	private boolean isSelectedRoutePath(RoutePath pth) {
		for (RoutePath rtePth : selectedRoutePaths) {
			if (rtePth == pth) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether a {@code Service} is selected or not.
	 * 
	 * @param pth
	 *            {@code Service} to test for selection
	 * @return {@code true} if the specified {@code Service} is selected.
	 */
	private boolean isSelectedService(RoutePath pth) {
		for (Service svc : selectedServices) {
			if (svc.getRoutePath() == pth) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether {@code Stop} is located close to a specified point.
	 * 
	 * @param pt
	 *            {@code Point} to look for a stop nearby
	 * @return {@code true} if a {@code Stop} was found nearby.
	 */
	protected boolean isStop(Point pt) {
		ArrayList<Stop> closeMarkers = getCloseStops(pt);
		if (closeMarkers.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the selected {@code WayPoint}.
	 * 
	 * @return the selected {@code WayPoint}.
	 */
	protected WayPoint getSelectedMarker() {
		return highlightedMapMarker;
	}

	/**
	 * Sets the selected marker to the specified {@code WayPoint}.
	 * 
	 * @param selectedMarker
	 *            {@code WayPoint} to select
	 */
	protected void setSelectedMapMarker(WayPoint selectedMarker) {
		highlightedMapMarker = selectedMarker;
		repaint();
	}

	/**
	 * Determines whether the highlighted marker is currently being moved.
	 * 
	 * @return {@code true} if the highlighted marker is currently moving
	 */
	protected boolean isHighlightedMarkerMoving() {
		return isHighlightedMarkerMoving;
	}

	/**
	 * Sets the current moving state of the highlighted map marker.
	 * 
	 * @param moving
	 *            {@code boolean} value specifying whether the map marker is
	 *            moving or not
	 */
	protected void setHighlightedMarkerMoving(boolean moving) {
		isHighlightedMarkerMoving = moving;
	}

	/**
	 * Updates the current mouse position to the specified point.
	 * 
	 * @param {@code Point} of the current mouse position
	 */
	protected void updateMousePosition(Point p) {
		mousePosition = p;
	}

	/**
	 * Sets the status bar text to information regarding the specified marker.
	 * 
	 * @param mkr
	 *            {@code WayPoint} to show information about
	 */
	private void showMarkerInformation(WayPoint mkr) {
		setSelectedMapMarker(mkr);
		mapFrame.setStatusMessage(mkr.getStatusMessage());
	}

	/**
	 * Finds a corresponding {@code ServiceStop} for the specified {@code Stop}.
	 * Since {@code ServiceStop}s are data objects while {@code Stop}s are route
	 * objects, a method is needed to bridge the gap between the two.
	 * 
	 * @param st
	 *            {@code Stop} to look for corresponding {@code ServiceStop}
	 * @return corresponding {@code ServiceStop} to the specified {@code Stop}
	 *         or {@code null} if none was found.
	 */
	private ServiceStop findServiceStop(Stop st) {
		for (Service svc : selectedServices) {
			ServiceStop svcStop = svc.getServiceStop(st);
			if (svcStop != null) {
				return svcStop;
			}
		}
		return null;
	}

	/**
	 * Determines whether a specified stop has data to be viewed. This
	 * information is cached when stops are painted on the map.
	 * 
	 * @param stop
	 *            {@code Stop} to inquire about data presence
	 * @return {@code true} if data exists for the specified stop
	 */
	private boolean isStopWithData(Stop stop) {
		for (Stop st : stopsWithData) {
			if (st == stop) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the closest stop to the specified point (within a close radius).
	 * 
	 * @param pt
	 *            {@code Point} to look for stops nearby
	 * @return closest {@code Stop} to the specified point
	 */
	protected Stop getClosestStop(Point pt) {
		Stop closestStop = null;
		Double closestDistance = SystemMap.CLOSE_DISTANCE;

		for (Stop mkr : getCloseStops(pt)) {
			Point mkrPt = this.getMapPosition(mkr.getLat(), mkr.getLon());
			if (pt.distance(mkrPt) < closestDistance) {
				closestStop = mkr;
			}
		}

		return closestStop;
	}

	/**
	 * Finds all the stops within a close radius to the specified {@code Point}.
	 * 
	 * @param pt
	 *            {@code Point} to look for stops around
	 * @return list of stops within a close radius to the specified location.
	 */
	protected ArrayList<Stop> getCloseStops(Point pt) {
		ArrayList<Stop> closeStops = new ArrayList<Stop>();

		for (Stop st : visibleStopMarkers) {
			Point mkrPt = getMapPosition(st.getLat(), st.getLon());
			if (mkrPt != null) {
				if (pt.distance(mkrPt) < SystemMap.CLOSE_DISTANCE) {
					closeStops.add(st);
				}
			}
		}

		return closeStops;
	}

	/**
	 * Determines a color value to represent a data value. Using the minimum and
	 * maximum scale data values, this method computes the relative location of
	 * the specified data value in the color range and returns that color.
	 * 
	 * @param val
	 *            integer value to find a color to represent
	 * @param minVal
	 *            minimum scale value
	 * @param maxVal
	 *            maximum scale value
	 * @return {@code Color} representing the passed data value.
	 */
	private Color interpolateColor(int val, int minVal, int maxVal) {
		int colorIndex = -1;
		if (val < minVal) {
			colorIndex = 0;
		} else if (val > maxVal) {
			colorIndex = colorScale.length - 1;
		} else {
			double pct = (double) (val - minVal) / (maxVal - minVal + 1);
			colorIndex = (int) (pct * (colorScale.length));
		}
		return colorScale[colorIndex];
	}

	/**
	 * Prepares an array of colors for a series of {@code ServiceStop}s. This is
	 * intended for "point style" data, colors that are intended to be local to
	 * a stop or waypoint.
	 * 
	 * @param svcStops
	 *            list of {@code ServieStop}s to prepare color representations
	 *            for
	 * @param dType
	 *            data type to extract from the {@code ServiceStop}s
	 * @param minVal
	 *            minimum scale value
	 * @param maxVal
	 *            maximum scale value
	 * @return array of {@code Color} values representing the list of
	 *         {@code ServiceStop}s.
	 */
	private Color[] preparePointColors(List<ServiceStop> svcStops,
			DataType dType, int minVal, int maxVal) {
		Color[] colors = new Color[svcStops.size()];

		for (int index = 0; index < svcStops.size(); index++) {

			Data dt = svcStops.get(index).getData(dType);
			if (dt != null) {
				colors[index] = interpolateColor(dt.getValue(), minVal, maxVal);
			} else {
				colors[index] = SELECTEDSTOPCOLOR;
			}

		}
		return colors;
	}

	/**
	 * Prepares an array of colors for a series of {@code ServiceStop}s. This
	 * method is intended for "segment style" data that is to be painted between
	 * stops and waypoints.
	 * 
	 * @param svcStops
	 *            list of {@code ServieStop}s to prepare color representations
	 *            for
	 * @param wpts
	 *            list of {@code WayPoint}s in between the list of
	 *            {@code ServiceStop}s
	 * @param dType
	 *            data type to extract from the {@code ServiceStop}s
	 * @param minVal
	 *            minimum scale value
	 * @param maxVal
	 *            maximum scale value
	 * @return array of {@code Color} values representing the list of
	 *         {@code ServiceStop}s.
	 */
	private Color[] prepareConnectingLineColors(List<ServiceStop> svcStops,
			List<WayPoint> wpts, DataType dType, int minVal, int maxVal) {
		Color[] colors = new Color[wpts.size()];

		for (int index = 0; index < svcStops.size(); index++) {
			ServiceStop svcStop = svcStops.get(index);
			int indexOfSvcStop = wpts.indexOf(svcStop.getStop());
			int dtVal = svcStop.getData(dType).getValue();
			colors[indexOfSvcStop] = interpolateColor(dtVal, minVal, maxVal);
		}

		return colors;
	}

	/**
	 * Paints the map onto this component. The process is broken into several
	 * steps: <br>
	 * 1. the default implementation from the {@code JMapViewer} class is run to
	 * draw the background map <br>
	 * 2. the background route paths are painted using the method
	 * {@code paintBackgroundRoutePaths()} <br>
	 * 3. the selected route paths are painted using the method
	 * {@code paintSelectedRoutePaths()} <br>
	 * 4. the selected services are painted using the method
	 * {@code paintSelectedServices} <br>
	 * 5. the highlighted map marker is painted using the method
	 * {@code paintHighlightedMapMarker()}
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		visibleStopMarkers.clear();
		if (backgroundRoutesVisible) {
			paintBackgroundRoutePaths(g);
		}
		paintSelectedRoutePaths(g);
		paintSelectedServices(g, selectedDataType);
		paintHighlightedMarker(g);
	}

	/**
	 * Paints the background {@code RoutePaths} onto the map.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 */
	private void paintBackgroundRoutePaths(Graphics g) {
		for (Route rte : allRoutes) {
			for (RoutePath rtePth : rte.getPaths()) {
				if (!isSelectedRoutePath(rtePth) && !isSelectedService(rtePth)) {
					try {
						paintConnectingLines(g, rtePth.getWayPoints(),
								BACKGROUNDLINEWIDTH, BACKGROUNDLINECOLOR);
						paintStopMarkers(g, rtePth.getStops(),
								BACKGROUNDSTOPCOLOR);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Paints the selected {@code RoutePath}s onto the map in a more prominent
	 * color than the background {@code RoutePath}s.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 */
	private void paintSelectedRoutePaths(Graphics g) {
		for (RoutePath rtePth : selectedRoutePaths) {
			if (!isSelectedService(rtePth)) {
				try {
					paintConnectingLines(g, rtePth.getWayPoints(),
							SELECTEDLINEWIDTH, SELECTEDLINECOLOR);
					paintStopMarkers(g, rtePth.getStops(), SELECTEDSTOPCOLOR);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Paints the selected {@code Service}s on the map. Since services contain
	 * data objects, this method prepares the color values for each service's
	 * path and then paints the corresponding colors onto the map. If the
	 * currently selected data type is a "point" style data type
	 * {@code DataType.POINT}, then data colors are painted on top of stops. If
	 * the currently selected data type is a "segment" style data type
	 * {@code DataType.SEGMENT}, then data colors are painted on top of and in
	 * between stop markers.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param dType
	 *            {@code DataType} to interpret and paint colors according to
	 */
	private void paintSelectedServices(Graphics g, DataType dType) {
		stopsWithData.clear();
		for (Service svc : selectedServices) {
			ArrayList<WayPoint> wpts = svc.getServicePath();
			ArrayList<ServiceStop> svcStops = svc.getServiceStops();

			if (dType.getType() == DataType.POINT) {
				Color[] colors = preparePointColors(svcStops, dType,
						getScaleMin(), getScaleMax());
				try {
					paintConnectingLines(g, wpts, POINTDATALINEWIDTH,
							Color.BLACK);
					paintStopMarkers(g, svc.getStops(), colors);
					stopsWithData.addAll(svc.getStops());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (dType.getType() == DataType.SEGEMENT) {
				Color[] lineColors = prepareConnectingLineColors(svcStops,
						wpts, dType, getScaleMin(), getScaleMax());
				Color[] pointColors = preparePointColors(svcStops, dType,
						getScaleMin(), getScaleMax());
				try {
					paintConnectingLines(g, wpts, SEGMENTDATALINEWIDTH,
							lineColors);
					paintStopMarkers(g, svc.getStops(), pointColors);
					stopsWithData.addAll(svc.getStops());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Paints a route path according to a series of {@code WayPoint}s and a
	 * corresponding array of colors.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param wayPoints
	 *            series of WayPoints indicating the path of the route
	 * @param lnWidth
	 *            width of the connecting lines
	 * @param colors
	 *            array of {@code Color} values to paint the corresponding stops
	 *            in the {@code WayPoint} list
	 * @throws Exception
	 */
	private void paintConnectingLines(Graphics g,
			ArrayList<WayPoint> wayPoints, int lnWidth, Color[] colors)
			throws Exception {
		Graphics2D g2d = (Graphics2D) g;
		Stroke originalStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(lnWidth));

		Point prevPt = null;
		for (int index = 0; index < wayPoints.size(); index++) {
			WayPoint wpt = wayPoints.get(index);
			Point pt = getMapPosition(wpt.getLat(), wpt.getLon(), false);

			if (prevPt != null) {
				Color col = colors[index - 1];
				if (col != null) {
					g.setColor(col);
				}
				g.drawLine(prevPt.x, prevPt.y, pt.x, pt.y);
			}
			prevPt = pt;
		}

		g2d.setStroke(originalStroke);
	}

	/**
	 * Paints route path according to a series of {@code WayPoint}s in one
	 * color.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param wayPoints
	 *            series of WayPoints indicating the path of the route
	 * @param lnWidth
	 *            width of the connecting lines
	 * @param color
	 *            {@code Color} to paint the connecting lines between stops
	 * @throws Exception
	 */
	private void paintConnectingLines(Graphics g,
			ArrayList<WayPoint> wayPoints, int lnWidth, Color color)
			throws Exception {
		Color[] colors = new Color[wayPoints.size()];
		for (int index = 0; index < colors.length; index++) {
			colors[index] = color;
		}
		paintConnectingLines(g, wayPoints, lnWidth, colors);
	}

	/**
	 * Paints a series of stop markers onto the map and colors them according to
	 * a specified array of {@code Color}s.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param stops
	 *            list of {@code Stop}s to paint on the map
	 * @param colors
	 *            array of corresponding {@code Color}s to paint stops with
	 * @throws Exception
	 */
	private void paintStopMarkers(Graphics g, ArrayList<Stop> stops,
			Color[] colors) throws Exception {
		for (int index = 0; index < stops.size(); index++) {
			Stop st = stops.get(index);

			if (st == highlightedMapMarker) {
				storedHighlighedMarkerColor = colors[index];
			} else {
				Point mkrPos = getMapPosition(st.getLat(), st.getLon(), true);
				if (mkrPos != null) {
					visibleStopMarkers.add(st);
					st.paint(g, mkrPos, SystemMap.REGULAR_MARKER_SIZE,
							colors[index]);
				}
			}
		}
	}

	/**
	 * Paints a series of stop markers onto the map and colors them according to
	 * the specified {@code Color}.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param stops
	 *            list of {@code Stop}s to be painted
	 * @param color
	 *            single {@code Color} to paint stops
	 * @throws Exception
	 */
	private void paintStopMarkers(Graphics g, ArrayList<Stop> stops, Color color)
			throws Exception {
		Color[] colors = new Color[stops.size()];
		for (int index = 0; index < colors.length; index++) {
			colors[index] = color;
		}
		paintStopMarkers(g, stops, colors);
	}

	/**
	 * Paints the highlighted marker on the map. A highlighted maker takes the
	 * same color as the marker dot that was painted originally on the map (with
	 * the exception of {@code WayPoint}s which are normally invisible). If the
	 * marker is a {@code Stop}, then a bubble is also painted containing
	 * information about the stop's name, route path, and data values.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 */
	private void paintHighlightedMarker(Graphics g) {
		if (highlightedMapMarker != null) {
			Point hltMkrPos = getMapPosition(highlightedMapMarker.getLat(),
					highlightedMapMarker.getLon(), false);
			if (isHighlightedMarkerMoving) {
				highlightedMapMarker.paint(g, mousePosition,
						SystemMap.HIGHLIGHTED_MARKER_SIZE,
						storedHighlighedMarkerColor);
			} else {
				highlightedMapMarker.paint(g, hltMkrPos,
						SystemMap.HIGHLIGHTED_MARKER_SIZE,
						storedHighlighedMarkerColor);
			}

			if (highlightedMapMarker instanceof Stop) {
				Stop st = (Stop) highlightedMapMarker;
				ArrayList<String> boxLines = new ArrayList<String>();
				boxLines.add(st.getName());
				boxLines.add("Route: "
						+ st.getRoutePath().getRoute().getRouteId() + "-"
						+ st.getRoutePath().toString().substring(0, 1)
						+ "  Stop: " + st.getStationId());

				String data;
				if (isStopWithData(st)) {
					ServiceStop svcStop = findServiceStop(st);
					DataType dType = getSelectedDataType();
					int dataVal = svcStop.getData(dType).getValue();
					data = new String(dType.getName() + ": " + dataVal);
				} else {
					data = new String("No Data");
				}
				boxLines.add(data);

				int numLines = boxLines.size();
				int linePadding = 5;
				int borderPadding = 5;
				int error = 2;

				FontMetrics metrics = g.getFontMetrics();
				int fontHeight = metrics.getHeight();
				int strWidth = getLongestStringLengh(metrics, boxLines);
				int strHeight = numLines * fontHeight + (numLines - 1)
						* linePadding;

				int boxWidth = strWidth + borderPadding * 2;
				int boxHeight = strHeight + borderPadding * 2;

				int upperLeftX = hltMkrPos.x - boxWidth / 2;
				int upperLeftY = hltMkrPos.y - boxHeight - 10;

				g.setColor(storedHighlighedMarkerColor);
				g.fillRect(upperLeftX, upperLeftY, boxWidth, boxHeight);
				Polygon triangle = new Polygon();
				triangle.addPoint(hltMkrPos.x, hltMkrPos.y - 5);
				triangle.addPoint(hltMkrPos.x - 5, hltMkrPos.y - 10);
				triangle.addPoint(hltMkrPos.x + 5, hltMkrPos.y - 10);
				g.fillPolygon(triangle);
				g.setColor(Color.BLACK);
				g.drawRect(upperLeftX, upperLeftY, boxWidth, boxHeight);
				g.drawPolygon(triangle);
				for (int index = 0; index < numLines; index++) {
					g.drawString(boxLines.get(index), upperLeftX
							+ borderPadding, upperLeftY + borderPadding
							+ fontHeight * (index + 1) + linePadding * index
							- error);
				}
			}
		}
	}

	/**
	 * Determines the longest String length (in pixels) from the specified array
	 * of {@code String}s.
	 * 
	 * @param metrics
	 *            {@code FontMetrics} variable for the font to be used to paint
	 *            the {@code String}s
	 * @param strings
	 *            {@code String}s from which to find the longest one
	 * @return the length of the longest {@code String} (in pixels).
	 */
	private int getLongestStringLengh(FontMetrics metrics,
			ArrayList<String> strings) {
		int maxLength = 0;
		for (String s : strings) {
			int len = metrics.stringWidth(s);
			if (len > maxLength) {
				maxLength = len;
			}
		}
		return maxLength;
	}

	/**
	 * If a marker is selected by the user, then that maker is set as the
	 * highlighted map marker. It also sets the status bar message to
	 * information about the selected marker.
	 */
	public void markerSelected(MarkerEvent e) {
		showMarkerInformation(e.getMapMarker());
		setSelectedMapMarker(e.getMapMarker());
		repaint();
	}

	/**
	 * If a point on the map is selected that does not contain a marker, then
	 * the highlighted map marker is reset to {@code null} and the status bar
	 * message is set to the default {@code MapFrame.EMPTY_MESSAGE}.
	 */
	public void markerNotSelected() {
		setSelectedMapMarker(null);
		setSelectedRoutePath(null);
		mapFrame.setStatusMessage(MapFrame.EMPTY_MESSAGE);
		repaint();
	}

	/**
	 * If a marker is moved by the user, then the highlighted map marker is set
	 * to the marker that is moving, and marker movement is set to {@code false}
	 * .
	 */
	public void markerMoved(MarkerEvent e) {
		setSelectedMapMarker(e.getMapMarker());
		setHighlightedMarkerMoving(false);
		repaint();
	}

	/**
	 * If a marker is currently moving, then marker movement is set to
	 * {@code true} and the current mouse position is set according to the
	 * location passed by the {@code MarkerEvent}.
	 */
	public void markerMoving(MarkerEvent e) {
		setHighlightedMarkerMoving(true);
		updateMousePosition(e.getPointMovedTo());
		repaint();
	}

	/**
	 * If the user's mouse is currently hovering over a map marker, then the
	 * status bar is set to display information about that marker.
	 */
	public void mouseHoveringOverMarker(MarkerEvent e) {
		mapFrame.setStatusMessage(e.getMapMarker().getStatusMessage());
	}

	/**
	 * If the user's mouse is currently not hovering over any marker, then the
	 * status bar is set to display the message {@code SystemMap.EMPTY_MESSAGE}.
	 */
	public void mouseHoveringOverNothing() {
		if (highlightedMapMarker == null) {
			mapFrame.setStatusMessage(MapFrame.EMPTY_MESSAGE);
		} else {
			mapFrame.setStatusMessage(highlightedMapMarker.getStatusMessage());
		}
	}

}
