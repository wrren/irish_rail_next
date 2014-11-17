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
public class TrainListingXMLParser
{
	/// Logger Tag
	private static final String TAG = "Train Listing XML Parser";

	/**
	 * Parse the given XML data and return a list of train info objects
	 * @param xml	XML Data
	 * @return	List of Train Info Objects
	 * @throws org.xmlpull.v1.XmlPullParserException        If an exception occurs during the parsing process
	 * @throws java.io.IOException                       	 If an exception occurs while reading XML data
	 */
	public static List<IrishRailApi.TrainInfo> parse( String xml ) throws XmlPullParserException, IOException
	{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput( new InputStreamReader( IOUtils.toInputStream( xml ) ) );
		parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );

		List<IrishRailApi.TrainInfo> trains = new ArrayList<IrishRailApi.TrainInfo>();

		Log.d( TAG, "Starting Parse Process..." );

		int event = parser.getEventType();
		IrishRailApi.TrainInfo train = new IrishRailApi.TrainInfo();

		while( event != XmlPullParser.END_DOCUMENT )
		{
			switch( event )
			{
			case XmlPullParser.START_DOCUMENT:
			break;

			case XmlPullParser.START_TAG:
			{
				String tag = parser.getName();

				if( tag.equals( "objStationData" ) )
				{
					train = new IrishRailApi.TrainInfo();
				}
				else if( tag.equals( "Destination" ) )
				{
					train.destination = parser.nextText();
				}
				else if( tag.equals( "Duein" ) )
				{
					train.due = Integer.valueOf( parser.nextText() );
				}
				else if( tag.equals( "Direction" ) )
				{
					String direction = parser.nextText();

					if( direction.equals( "Northbound" ) )
					{
						train.direction = IrishRailApi.TrainInfo.Direction.NORTH;
					}
					else
					{
						train.direction = IrishRailApi.TrainInfo.Direction.SOUTH;
					}
				}
			}
			break;

			case XmlPullParser.END_TAG:
			{
				String tag = parser.getName();

				if( tag.equals( "objStationData" ) )
				{
					trains.add( train );
				}
			}
			break;
			}

			event = parser.next();
		}

		return trains;
	}
}
