package rdcl.ie.pebbleirishrailnext.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import rdcl.ie.pebbleirishrailnext.R;
import rdcl.ie.pebbleirishrailnext.api.IrishRailApi;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StationUpdateService extends IntentService
{
	public static class StationUpdateTask extends TimerTask
	{
		/// Subject Station
		private IrishRailApi.StationInfo 	station;
		/// API Instance
		private IrishRailApi 			api;
		/// Pebble Application UUID
		private UUID 				appUUID;
		/// Application Context
		private Context				context;

		/**
		 * Construct an update task that will communicate with the API pass the resulting
		 * information to the pebble watch app with the specified UUID
		 * @param context 	Application Context
		 * @param appUUID	Pebble Application UUID
		 */
		public StationUpdateTask( Context context, UUID appUUID )
		{
			this.api 	= new IrishRailApi( context );
			this.appUUID	= appUUID;
			this.context 	= context;
		}

		@Override
		public void run()
		{
			if( station != null )
			{
				api.getTrainList( station, new IrishRailApi.OnTrainListReceivedListener()
				{
					@Override
					public void onTrainListReceived( IrishRailApi api, IrishRailApi.StationInfo station, List<IrishRailApi.TrainInfo> trains )
					{
						if( PebbleKit.isWatchConnected( context ) == false )
						{
							cancel();
							return;
						}

						Log.d( "Station Update Timer Task", "Received " + trains.size() + " Trains!" );
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

						PebbleKit.sendDataToPebble( context, appUUID, dictionary );
					}

					@Override
					public void onTrainListingError( IrishRailApi api, Exception exception )
					{

					}
				} );
			}
		}

		/**
		 * Set the subject station for the timer task
		 * @param station	Subject Station
		 */
		public void setStation( IrishRailApi.StationInfo station )
		{
			this.station = station;
		}
	}

	/// Select a new subject station, will start the update polling process if it's not already running
	private static final String ACTION_SELECT_STATION 	= "rdcl.ie.pebbleirishrailnext.service.action.SELECT_STATION";
	/// Stop polling for updates
	private static final String ACTION_STOP_UPDATING	= "rdcl.ie.pebbleirishrailnext.service.action.STOP_UPDATING";

	// Station Data Parameters
	public static final String PARAM_STATION_NAME 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_NAME";
	private static final String PARAM_STATION_CODE 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_CODE";
	private static final String PARAM_STATION_LAT 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_LAT";
	private static final String PARAM_STATION_LON 	= "rdcl.ie.pebbleirishrailnext.service.extra.STATION_LON";

	/// Update Task Timer
	private Timer 				timer;
	/// Station Update Task
	private StationUpdateTask 		updater;
	/// Target Station
	private IrishRailApi.StationInfo	station;

	/**
	 * Starts this service to select the given station and begin the IrishRail API polling
	 * process
	 * @param context 	Application Context
	 * @param station Selected Station
	 */
	public static void selectStation( Context context, IrishRailApi.StationInfo station )
	{
		Intent intent = new Intent( context, StationUpdateService.class );
		intent.setAction( ACTION_SELECT_STATION );
		intent.putExtra( PARAM_STATION_NAME, station.name );
		intent.putExtra( PARAM_STATION_CODE, station.code );
		intent.putExtra( PARAM_STATION_LAT, station.latitude );
		intent.putExtra( PARAM_STATION_LON, station.longitude );
		context.startService( intent );
	}

	/**
	 * Send an intent that halts the API polling process
	 * @param context	Application Context
	 */
	public static void stopUpdating( Context context )
	{
		Intent intent = new Intent( context, StationUpdateService.class );
		intent.setAction( ACTION_STOP_UPDATING );
		context.startService( intent );
	}

	public StationUpdateService()
	{
		super( "StationUpdateService" );
	}

	@Override
	protected void onHandleIntent( Intent intent )
	{
		if( intent != null )
		{
			final String action = intent.getAction();
			if( ACTION_SELECT_STATION.equals( action ) )
			{
				IrishRailApi.StationInfo station = new IrishRailApi.StationInfo();
				station.name 		= intent.getStringExtra( PARAM_STATION_NAME );
				station.code 		= intent.getStringExtra( PARAM_STATION_CODE );
				station.latitude	= intent.getDoubleExtra( PARAM_STATION_LAT, 0.0 );
				station.longitude	= intent.getDoubleExtra( PARAM_STATION_LON, 0.0 );

				handleActionSelectStation( station );
			}
			else if( ACTION_STOP_UPDATING.equals( action ) )
			{
				if( timer != null )
				{
					timer.cancel();
				}

				PebbleDictionary dictionary = new PebbleDictionary();

				dictionary.addString( 0, 	"Select Station" );
				dictionary.addInt32( 1, 	0 );
				dictionary.addString( 2, 	"Select Station" );
				dictionary.addInt32( 3, 	0 );

				PebbleKit.sendDataToPebble( this, UUID.fromString( getString( R.string.pebble_app_uuid ) ), dictionary );
			}
		}
	}

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionSelectStation( IrishRailApi.StationInfo station )
	{
		if( updater == null )
		{
			UUID uuid = UUID.fromString( getString( R.string.pebble_app_uuid ) );
			updater = new StationUpdateTask( this, uuid );
		}
		else
		{
			timer.cancel();
		}

		this.station = station;
		timer 	= new Timer( "Station Update Timer" );
		updater.setStation( station );
		timer.schedule( updater, 1000L, 60 * 1000L );
	}
}
