package rdcl.ie.pebbleirishrailnext.api;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by warren on 16/11/14.
 */
public class StationListingXMLParser
{
	/// Logger Tag
	private static final String TAG = "Station Listing XML Parser";

	/**
	 * Parse the given XML data and return a list of station info objects
	 * @param xml	XML Data
	 * @return	List of Station Info Objects
	 * @throws XmlPullParserException	If an exception occurs during the parsing process
	 * @throws IOException			If an exception occurs while reading XML data
	 */
	public static List<IrishRailApi.StationInfo> parse( String xml ) throws XmlPullParserException, IOException
	{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput( new InputStreamReader( IOUtils.toInputStream( xml ) ) );
		parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );

		List<IrishRailApi.StationInfo> stations = new ArrayList<IrishRailApi.StationInfo>();

		Log.d( TAG, "Starting Parse Process..." );

		int event = parser.getEventType();
		IrishRailApi.StationInfo station = new IrishRailApi.StationInfo();

		while( event != XmlPullParser.END_DOCUMENT )
		{
			switch( event )
			{
				case XmlPullParser.START_DOCUMENT:
				break;

				case XmlPullParser.START_TAG:
				{
					String tag = parser.getName();

					if( tag.equals( "objStation" ) )
					{
						station = new IrishRailApi.StationInfo();
					}
					else if( tag.equals( "StationDesc" ) )
					{
						station.name = parser.nextText();
					}
					else if( tag.equals( "StationLatitude" ) )
					{
						station.latitude = Double.valueOf( parser.nextText() );
					}
					else if( tag.equals( "StationLongitude" ) )
					{
						station.longitude = Double.valueOf( parser.nextText() );
					}
					else if( tag.equals( "StationCode" ) )
					{
						station.code = parser.nextText();
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					String tag = parser.getName();

					if( tag.equals( "objStation" ) )
					{
						stations.add( station );
					}
				}
				break;
			}

			event = parser.next();
		}

		return stations;
	}
}
