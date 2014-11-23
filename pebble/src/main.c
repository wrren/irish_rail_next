#include <pebble.h>
#include "train_list.h"

static AppSync sync;
static uint8_t sync_buffer[16386];

enum DataKey {
	STATION_NAME		= 0,

	TRAIN_1_NAME 		= 1,  
	TRAIN_1_DUE		= 2,
	TRAIN_1_DIRECTION	= 3,

	TRAIN_2_NAME 		= 4,  
	TRAIN_2_DUE		= 5,
	TRAIN_2_DIRECTION	= 6,

	TRAIN_3_NAME 		= 7,  
	TRAIN_3_DUE		= 8,
	TRAIN_3_DIRECTION	= 9,

	TRAIN_4_NAME 		= 10,  
	TRAIN_4_DUE		= 11,
	TRAIN_4_DIRECTION	= 12
};

static char s_train_due[4][5];

static void sync_error_callback( DictionaryResult dict_error, AppMessageResult app_message_error, void *context )
{
	APP_LOG( APP_LOG_LEVEL_DEBUG, "App Message Sync Error: %d, %d:%d", 1<<7, app_message_error, dict_error );
}

static void sync_tuple_changed_callback( const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context ) 
{
	if( key == STATION_NAME )
	{
		update_station( new_tuple->value->cstring );
	}
	else if( key == TRAIN_1_NAME || key == TRAIN_2_NAME || key == TRAIN_3_NAME || key == TRAIN_4_NAME )
	{
		update_train_destination( key / 3, new_tuple->value->cstring );
	}
	else if( key == TRAIN_1_DUE || key == TRAIN_2_DUE || key == TRAIN_3_DUE || key == TRAIN_4_DUE )
	{
		int due = new_tuple->value->int32;

		if( due == -1 )
		{
			s_train_due[key/3][0] = '\n';
		}
		else
		{
			snprintf( s_train_due[key/3], 4, "%d", due );
		}
		update_train_due( key / 3, s_train_due[key/3] );
	}
	else if( key == TRAIN_1_DIRECTION || key == TRAIN_2_DIRECTION || key == TRAIN_3_DIRECTION || key == TRAIN_4_DIRECTION )
	{
		update_train_direction( key / 3, new_tuple->value->int32 );
	}

}

static void init() 
{
	show_train_list();

	const int inbound_size = 256;
	const int outbound_size = 0;
	app_message_open( inbound_size, outbound_size );
	
	Tuplet initial_values[] = {
		TupletCString( STATION_NAME, "Select Station" ),
		
		TupletCString( TRAIN_1_NAME, "" ),
		TupletCString( TRAIN_1_DUE, "" ),
		TupletInteger( TRAIN_1_DIRECTION, NONE ),

		TupletCString( TRAIN_2_NAME, "" ),
		TupletCString( TRAIN_2_DUE, "" ),
		TupletInteger( TRAIN_2_DIRECTION, NONE ),
		
		TupletCString( TRAIN_3_NAME, "" ),
		TupletCString( TRAIN_3_DUE, "" ),
		TupletInteger( TRAIN_3_DIRECTION, NONE ),
		
		TupletCString( TRAIN_4_NAME, "" ),
		TupletCString( TRAIN_4_DUE, "" ),
		TupletInteger( TRAIN_4_DIRECTION, NONE ),
	};

	APP_LOG( APP_LOG_LEVEL_DEBUG, "Sync Init with Buffer Size: %d", sizeof( sync_buffer ) );

	app_sync_init( 	&sync, 
			sync_buffer, sizeof( sync_buffer ), initial_values, ARRAY_LENGTH( initial_values ),
			sync_tuple_changed_callback, sync_error_callback, NULL );
}

static void deinit() 
{
	hide_train_list();
	app_sync_deinit( &sync );
}

int main( void ) 
{
	init();
	app_event_loop();
	deinit();
}
