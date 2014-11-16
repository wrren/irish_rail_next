#include <pebble.h>
#include "main_window.h"

static AppSync sync;
static uint8_t sync_buffer[256];

enum DataKey {
	NORTH_TRAIN_NAME 	= 0,  	// CString 
	NORTH_TRAIN_DUE 	= 1,  	// Int
	SOUTH_TRAIN_NAME	= 2,	// CString
	SOUTH_TRAIN_DUE		= 3	// Int
};

static void sync_error_callback( DictionaryResult dict_error, AppMessageResult app_message_error, void *context )
{
	APP_LOG( APP_LOG_LEVEL_DEBUG, "App Message Sync Error: %d", app_message_error );
}

static void sync_tuple_changed_callback( const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context ) 
{
	switch( key ) 
	{
		case NORTH_TRAIN_NAME:
			update_destination( NORTH, new_tuple->value->cstring );
		break;

		case NORTH_TRAIN_DUE:
			update_due_time( NORTH, new_tuple->value->uint32 );
		break;

		case SOUTH_TRAIN_NAME:
			update_destination( SOUTH, new_tuple->value->cstring );
		break;

		case SOUTH_TRAIN_DUE:
			update_due_time( SOUTH, new_tuple->value->uint32 );
		break;
	}
}

static void init() 
{
	show_main_window();

	const int inbound_size = 256;
	const int outbound_size = 0;
	app_message_open( inbound_size, outbound_size );

	Tuplet initial_values[] = {
		TupletCString( NORTH_TRAIN_NAME, "Synchronizing..." ),
		TupletInteger( NORTH_TRAIN_DUE, ( uint32_t ) 0 ),

		TupletCString( SOUTH_TRAIN_NAME, "Synchronizing..." ),
		TupletInteger( SOUTH_TRAIN_DUE, ( uint32_t ) 0 ),
	};

	app_sync_init( 	&sync, 
			sync_buffer, sizeof( sync_buffer ), initial_values, ARRAY_LENGTH( initial_values ),
			sync_tuple_changed_callback, sync_error_callback, NULL );
}

static void deinit() 
{
	hide_main_window();
	app_sync_deinit( &sync );
}

int main( void ) 
{
	init();
	app_event_loop();
	deinit();
}
