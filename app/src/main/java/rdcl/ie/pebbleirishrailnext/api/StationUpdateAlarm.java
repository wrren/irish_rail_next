package rdcl.ie.pebbleirishrailnext.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import rdcl.ie.pebbleirishrailnext.R;

/**
 * Created by Warren on 17/11/2014.
 */
public class StationUpdateAlarm extends BroadcastReceiver
{
	/// Alarm Manager
	private static AlarmManager alarmManager 	= null;
	/// Pending Intent
	private static PendingIntent pending 		= null;

	// Station Data Parameters
	private static final String PARAM_STATION_NAME 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_NAME";
	private static final String PARAM_STATION_CODE 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_CODE";
	private static final String PARAM_STATION_LAT 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_LAT";
	private static final String PARAM_STATION_LON 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_LON";

	private static final int MAX_DISPLAYED_TRAINS 	= 4;

	/**
	 * Begin or continue the update loop
	 * @param context	Application Context
	 * @param station	Subject Station
	 */
	public static void set( Context context, IrishRailApi.StationInfo station )
	{
		if( alarmManager == null )
		{
			alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );

		}

		alarmManager.cancel( pending );

		Intent intent = new Intent( context, StationUpdateAlarm.class );

		intent.putExtra( PARAM_STATION_NAME, station.name );
		intent.putExtra( PARAM_STATION_CODE, station.code );
		intent.putExtra( PARAM_STATION_LAT, station.latitude );
		intent.putExtra( PARAM_STATION_LON, station.longitude );

		pending = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		alarmManager.setInexactRepeating( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20000, pending );
	}

	/**
	 * Stop any pending updates, halting the update loop
	 * @param context	Application Context
	 */
	public static void stop( Context context )
	{
		if( alarmManager != null )
		{
			alarmManager.cancel( pending );
		}
		UUID uuid = UUID.fromString( context.getString( R.string.pebble_app_uuid ) );
		updatePebble( context, uuid, "Select Station", null );
	}

	public static void updatePebble( Context context, UUID uuid, String station, List<IrishRailApi.TrainInfo> trains )
	{
		PebbleDictionary dictionary = new PebbleDictionary();
		dictionary.addString( 0, station );

		if( trains == null )
		{
			trains = new ArrayList<IrishRailApi.TrainInfo>();
		}

		int index = 1;

		for( int i = 0; i < MAX_DISPLAYED_TRAINS; i++ )
		{
			if( i < trains.size() )
			{
				dictionary.addString( index++, trains.get( i ).destination );
				dictionary.addInt32( index++, trains.get( i ).due );
				dictionary.addInt32( index++, trains.get( i ).direction == IrishRailApi.TrainInfo.Direction.NORTH ? 0 : 1 );
			}
			else
			{
				dictionary.addString( index++, 	"" );
				dictionary.addInt32( index++, 	-1 );
				dictionary.addInt32( index++, 	2 );
			}
		}

		PebbleKit.sendDataToPebble( context, uuid, dictionary );
	}

	/**
	 * Send train information for the specified station to the connected Pebble Watch
	 * @param context	Application Context
	 * @param station	Subject Station
	 */
	public static void send( final Context context, IrishRailApi.StationInfo station )
	{
		IrishRailApi api = new IrishRailApi( context );

		api.getTrainList( station, new IrishRailApi.OnTrainListReceivedListener()
		{
			@Override
			public void onTrainListReceived( IrishRailApi api, IrishRailApi.StationInfo station, List<IrishRailApi.TrainInfo> trains )
			{
				if( PebbleKit.isWatchConnected( context ) == false )
				{
					stop( context );
					return;
				}

				Log.d( "Station Update Alarm", "Received " + trains.size() + " Trains!" );
				Collections.sort( trains );

				UUID uuid = UUID.fromString( context.getString( R.string.pebble_app_uuid ) );
				updatePebble( context, uuid, station.name, trains );
			}

			@Override
			public void onTrainListingError( IrishRailApi api, Exception exception )
			{}
		} );
	}

	@Override
	public void onReceive( final Context context, Intent intent )
	{
		IrishRailApi.StationInfo station = new IrishRailApi.StationInfo();
		station.name 		= intent.getStringExtra( PARAM_STATION_NAME );
		station.code 		= intent.getStringExtra( PARAM_STATION_CODE );
		station.latitude	= intent.getDoubleExtra( PARAM_STATION_LAT, 0.0 );
		station.longitude	= intent.getDoubleExtra( PARAM_STATION_LON, 0.0 );

		send( context, station );
	}
}
