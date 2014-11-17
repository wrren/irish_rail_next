package rdcl.ie.pebbleirishrailnext;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import rdcl.ie.pebbleirishrailnext.api.IrishRailApi;
import rdcl.ie.pebbleirishrailnext.service.StationUpdateService;


public class MainActivity extends ActionBarActivity
{
	public static class StationListAdapter extends ArrayAdapter<IrishRailApi.StationInfo>
	{
		public static class ViewContainer
		{
			public IrishRailApi.StationInfo station;
			public CheckedTextView		text;
		}

		/// Selected Station
		private IrishRailApi.StationInfo 	selected;
		/// API
		private IrishRailApi 			api;
		/// Checked View
		private CheckedTextView			checked;

		/**
		 * Default Constructor
		 * @param context	Application Context
		 */
		public StationListAdapter( Context context )
		{
			super( context, R.layout.station_list_item );
			api = new IrishRailApi( context );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			IrishRailApi.StationInfo station 	= getItem( position );
			ViewContainer container 		= null;

			if( convertView == null )
			{
				LayoutInflater inflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				convertView 		= inflater.inflate( R.layout.station_list_item, null );
				container		= new ViewContainer();

				container.station 	= station;
				container.text 		= ( CheckedTextView ) convertView.findViewById( R.id.station );

				convertView.setTag( container );
			}
			else
			{
				container = ( ViewContainer ) convertView.getTag();
			}

			container.text.setText( station.name );
			container.station = station;

			if( selected != null && selected.name.equals( station.name ) )
			{
				container.text.setChecked( true );
				checked = container.text;
			}
			else
			{
				container.text.setChecked( false );
			}

			return convertView;
		}

		/**
		 * Refresh the station list
		 */
		public void refresh()
		{
			api.getStationList( IrishRailApi.StationType.DART, new IrishRailApi.OnStationListReceivedListener()
			{
				@Override
				public void onStationListReceived( IrishRailApi api, List<IrishRailApi.StationInfo> stations )
				{
					StationListAdapter.this.clear();

					Collections.sort( stations );

					StationListAdapter.this.addAll( stations );
					StationListAdapter.this.notifyDataSetChanged();
				}

				@Override
				public void onStationListingError( IrishRailApi api, Exception exception )
				{
					Toast.makeText( getContext(), "Error During Station Request: " + exception.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
				}
			} );
		}

		/**
		 * Set the selected station for this adapter
		 * @param view 		Checked View
		 * @param station	Selected Station
		 */
		public boolean setCheckedStation( View view, IrishRailApi.StationInfo station )
		{
			CheckedTextView checkedTextView = ( CheckedTextView ) view.findViewById( R.id.station );

			if( checkedTextView != null )
			{
				if( checked != null && checked == checkedTextView )
				{
					checkedTextView.setChecked( false );
					this.selected = null;

					return false;
				}
				else
				{
					if( checked != null )
					{
						checked.setChecked( false );
					}
					checkedTextView.setChecked( true );
					checked = checkedTextView;
					selected = station;

					return true;
				}
			}

			return false;
		}
	}

	/// List Adapter
	private StationListAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.station_list );
		setTitle( R.string.station_list_title );

		adapter = new StationListAdapter( this );

		ListView stationList = ( ListView ) findViewById( R.id.station_list_view );
		stationList.setAdapter( adapter );

		stationList.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				IrishRailApi.StationInfo station = ( ( StationListAdapter.ViewContainer ) view.getTag() ).station;

				if( adapter.setCheckedStation( view, station ) )
				{
					StationUpdateService.selectStation( getApplicationContext(), station );
				}
				else
				{
					StationUpdateService.stopUpdating( getApplicationContext() );
				}
			}
		} );

		adapter.refresh();


	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if( id == R.id.action_settings )
		{
			return true;
		}
		else if( id == R.id.action_refresh )
		{
			adapter.refresh();
		}

		return super.onOptionsItemSelected( item );
	}
}
