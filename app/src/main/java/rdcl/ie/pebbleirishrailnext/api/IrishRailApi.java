package rdcl.ie.pebbleirishrailnext.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Main interface to the Irish Rail API
 */
public class IrishRailApi
{
	/// URL for Station Requests
	private static final String STATION_REQUEST_URL = "http://api.irishrail.ie/realtime/realtime.asmx/getAllStationsXML_WithStationType?StationType=";
	/// URL for Train Information Requests
	private static final String TRAIN_REQUEST_URL = "http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML?StationCode=";

	/**
	 * Different station types that can be requested during a station listing
	 */
	public enum StationType
	{
		ALL( "A" ),
		MAIN_LINE( "M" ),
		DART( "D" );

		private String code;

		private StationType( String code )
		{
			this.code = code;
		}

		/**
		 * Get the station type code for this station type
		 * @return	Station Type Code
		 */
		public String getCode()
		{
			return code;
		}
	}

	/**
	 * Class containing information of a specific train station.
	 */
	public static class StationInfo implements Comparable<StationInfo>
	{
		/// Station Name
		public String name;
		/// Station Short Code
		public String code;
		/// Station Latitude
		public double latitude;
		/// Station Longitude
		public double longitude;

		@Override
		public int compareTo( StationInfo another )
		{
			return name.compareTo( another.name );
		}
	}

	/**
	 * Class containing information on a specific train, including time until it
	 * arrives at its next station and its ultimate destination.
	 */
	public static class TrainInfo implements Comparable<TrainInfo>
	{
		@Override
		public int compareTo( TrainInfo another )
		{
			return due - another.due;
		}

		public enum Direction
		{
			NORTH,
			SOUTH
		}

		/// Destination Station
		public String 		destination;
		/// Number of minutes before the train is due at the subject station
		public int 		due;
		/// Direction of Travel
		public Direction 	direction;
	}

	/// Request Queue
	private RequestQueue queue;

	/**
	 * Default Constructor
	 * @param context	Application Context
	 */
	public IrishRailApi( Context context )
	{
		queue = Volley.newRequestQueue( context );
	}

	/**
	 * Listener interface for requests for station lists
	 */
	public interface OnStationListReceivedListener
	{
		/**
		 * Called when the station list is received from the Irish Rail API
		 * @param api		API Instance
		 * @param stations	List of Stations
		 */
		public void onStationListReceived( IrishRailApi api, List<StationInfo> stations );

		/**
		 * Called when an error occurs while retrieving station information
		 * @param api		API Instance
		 * @param exception	Exception thrown during the request to the API
		 */
		public void onStationListingError( IrishRailApi api, Exception exception );
	}

	/**
	 * Listener interface for requests for train lists on specified stations
	 */
	public interface OnTrainListReceivedListener
	{
		/**
		 * Called when the requested data on arriving trains on a specific station is received
		 * @param api		API Instance
		 * @param station	Subject Station
		 * @param trains	List of Trains
		 */
		public void onTrainListReceived( IrishRailApi api, StationInfo station, List<TrainInfo> trains );

		/**
		 * Called when an error occurs while retrieving train listing information
		 * @param api		API Instance
		 * @param exception	Exception thrown during the request to the API
		 */
		public void onTrainListingError( IrishRailApi api, Exception exception );
	}

	/**
	 * Attempt to retrieve a list of stations from the Irish Rail API. The resulting list can be
	 * used to retrieve data about specific stations. The request will occur in the background and
	 * the result will be passed to the listener on the UI thread.
	 * @param type		Possible Station Types
	 * @param listener	Result Listener
	 */
	public void getStationList( StationType type, final OnStationListReceivedListener listener )
	{
		queue.add( new StringRequest( Request.Method.GET, getStationRequestUrl( type ), new Response.Listener<String>()
		{
			@Override
			public void onResponse( String response )
			{
				try
				{
					listener.onStationListReceived( IrishRailApi.this, StationListingXMLParser.parse( response ) );
				}
				catch( Exception e )
				{
					listener.onStationListingError( IrishRailApi.this, e );
				}
			}
		},
		new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse( VolleyError error )
			{
				listener.onStationListingError( IrishRailApi.this, error );
			}
		} ) );
	}

	/**
	 * Attempt to retrieve a list of trains arriving at the specified station from the Irish Rail API.
	 * The request will occur in the background and the result will be passed to the listener on the UI thread.
	 * @param station	Subject Station
	 * @param listener	Result Listener
	 */
	public void getTrainList( final StationInfo station, final OnTrainListReceivedListener listener )
	{
		queue.add( new StringRequest( Request.Method.GET, getTrainRequestUrl( station ), new Response.Listener<String>()
		{
			@Override
			public void onResponse( String response )
			{
				try
				{
					listener.onTrainListReceived( IrishRailApi.this, station, TrainListingXMLParser.parse( response ) );
				}
				catch( Exception e )
				{
					listener.onTrainListingError( IrishRailApi.this, e );
				}
			}
		},
		new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse( VolleyError error )
			{
				listener.onTrainListingError( IrishRailApi.this, error );
			}
		} ) );
	}

	/**
	 * Form the URL for a station listing request for stations of the specified type
	 * @param type	Possible Station Typese
	 * @return	Station Request URL
	 */
	private static String getStationRequestUrl( StationType type )
	{
		return STATION_REQUEST_URL + type.getCode();
	}

	/**
	 * Form the URL for a train request for the specified station
	 * @param station	Station
	 * @return		Train Request URL
	 */
	private static String getTrainRequestUrl( StationInfo station )
	{
		return TRAIN_REQUEST_URL + station.code;
	}
}
