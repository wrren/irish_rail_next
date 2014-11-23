package rdcl.ie.pebbleirishrailnext.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

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

		PebbleDictionary dictionary = new PebbleDictionary();

		dictionary.addString( 0, 	"Select Station" );
		dictionary.addInt32( 1, 	0 );
		dictionary.addString( 2, 	"Select Station" );
		dictionary.addInt32( 3, 	0 );

		PebbleKit.sendDataToPebble( context, UUID.fromString( context.getString( R.string.pebble_app_uuid ) ), dictionary );
	}

	/**
	 * Send train information for the specified station to the connected Pebble Watch
	 * @param context	Application Context
	 * @param station	Subject Station
	 */
	public static void send( final Context context, IrishRailApi.StationInfo station )
	{
		IrishRailApi api = new IrishRailApi( context );
		final UUID uuid = UUID.fromString( context.getString( R.string.pebble_app_uuid ) );

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

				IrishRailApi.TrainInfo north = null, south = null;

				for( IrishRailApi.TrainInfo train : trains )
				{
					if( north != null && south != null )
					{
						break;
					}
					if( north == null && train.direction == IrishRailApi.TrainInfo.Direction.NORTH )
					{
						north = train;
					}

					if( south == null && train.direction == IrishRailApi.TrainInfo.Direction.SOUTH )
					{
						south = train;
					}
				}

				PebbleDictionary dictionary = new PebbleDictionary();

				if( north != null )
				{
					dictionary.addString( 0, north.destination );
					dictionary.addInt32( 1, north.due );
				}
				if( south != null )
				{
					dictionary.addString( 2, south.destination );
					dictionary.addInt32( 3, south.due );
				}

				PebbleKit.sendDataToPebble( context, uuid, dictionary );
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
