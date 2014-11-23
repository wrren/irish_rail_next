#include "train_list.h"
#include <pebble.h>

// BEGIN AUTO-GENERATED UI CODE; DO NOT MODIFY
static Window *s_window;
static GBitmap *s_res_r_arrow_up;
static GBitmap *s_res_r_arrow_down;
static GFont s_res_gothic_18_bold;
static InverterLayer *s_inverterlayer_1;
static TextLayer *s_station_name;
static InverterLayer *s_inverterlayer_2;
static InverterLayer *s_inverterlayer_3;
static InverterLayer *s_inverterlayer_4;
static InverterLayer *s_inverterlayer_5;

static TextLayer*	s_train_times[MAX_DISPLAYED_TRAINS];
static TextLayer*	s_train_names[MAX_DISPLAYED_TRAINS];
static BitmapLayer*	s_train_directions[MAX_DISPLAYED_TRAINS];

static void initialise_ui(void) {
	s_window = window_create();
	window_set_fullscreen(s_window, false);

	s_res_r_arrow_up = gbitmap_create_with_resource(RESOURCE_ID_r_arrow_up);
	s_res_r_arrow_down = gbitmap_create_with_resource(RESOURCE_ID_r_arrow_down);
	s_res_gothic_18_bold = fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD);
  // s_inverterlayer_1
	s_inverterlayer_1 = inverter_layer_create(GRect(0, 0, 144, 20));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_inverterlayer_1);

  // s_station_name
	s_station_name = text_layer_create(GRect(0, 2, 144, 16));
	text_layer_set_background_color(s_station_name, GColorClear);
	text_layer_set_text_color(s_station_name, GColorWhite);
	text_layer_set_text(s_station_name, "Select");
	text_layer_set_text_alignment(s_station_name, GTextAlignmentCenter);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_station_name);

  // s_inverterlayer_2
	s_inverterlayer_2 = inverter_layer_create(GRect(-2, 83, 146, 5));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_inverterlayer_2);

  // s_inverterlayer_3
	s_inverterlayer_3 = inverter_layer_create(GRect(28, 20, 4, 132));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_inverterlayer_3);

  // s_inverterlayer_4
	s_inverterlayer_4 = inverter_layer_create(GRect(0, 48, 144, 5));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_inverterlayer_4);

  // s_inverterlayer_5
	s_inverterlayer_5 = inverter_layer_create(GRect(0, 117, 144, 5));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_inverterlayer_5);

  // s_train_directions[0]
	s_train_directions[0] = bitmap_layer_create(GRect(120, 23, 16, 22));
	bitmap_layer_set_bitmap(s_train_directions[0], s_res_r_arrow_up);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_directions[0]);

  // s_train_directions[1]
	s_train_directions[1] = bitmap_layer_create(GRect(119, 57, 18, 22));
	bitmap_layer_set_bitmap(s_train_directions[1], s_res_r_arrow_up);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_directions[1]);

  // s_train_directions[2]
	s_train_directions[2] = bitmap_layer_create(GRect(118, 92, 18, 22));
	bitmap_layer_set_bitmap(s_train_directions[2], s_res_r_arrow_down);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_directions[2]);

  // s_train_directions[3]
	s_train_directions[3] = bitmap_layer_create(GRect(118, 126, 18, 22));
	bitmap_layer_set_bitmap(s_train_directions[3], s_res_r_arrow_down);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_directions[3]);

  // s_train_times[0]
	s_train_times[0] = text_layer_create(GRect(3, 24, 22, 22));
	text_layer_set_text(s_train_times[0], "");
	text_layer_set_text_alignment(s_train_times[0], GTextAlignmentCenter);
	text_layer_set_font(s_train_times[0], s_res_gothic_18_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_times[0]);

  // s_train_names[0]
	s_train_names[0] = text_layer_create(GRect(36, 26, 83, 16));
	text_layer_set_text(s_train_names[0], "");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_names[0]);

  // s_train_names[1]
	s_train_names[1] = text_layer_create(GRect(36, 60, 83, 17));
	text_layer_set_text(s_train_names[1], "");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_names[1]);

  // s_train_times[1]
	s_train_times[1] = text_layer_create(GRect(3, 58, 22, 22));
	text_layer_set_text(s_train_times[1], "");
	text_layer_set_text_alignment(s_train_times[1], GTextAlignmentCenter);
	text_layer_set_font(s_train_times[1], s_res_gothic_18_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_times[1]);

  // s_train_names[2]
	s_train_names[2] = text_layer_create(GRect(36, 95, 84, 20));
	text_layer_set_text(s_train_names[2], "");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_names[2]);

  // s_train_times[2]
	s_train_times[2] = text_layer_create(GRect(3, 93, 22, 22));
	text_layer_set_text(s_train_times[2], "");
	text_layer_set_text_alignment(s_train_times[2], GTextAlignmentCenter);
	text_layer_set_font(s_train_times[2], s_res_gothic_18_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_times[2]);

  // s_train_names[3]
	s_train_names[3] = text_layer_create(GRect(36, 130, 80, 16));
	text_layer_set_text(s_train_names[3], "");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_names[3]);

  // s_train_times[3]
	s_train_times[3] = text_layer_create(GRect(3, 128, 22, 22));
	text_layer_set_text(s_train_times[3], "");
	text_layer_set_text_alignment(s_train_times[3], GTextAlignmentCenter);
	text_layer_set_font(s_train_times[3], s_res_gothic_18_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_train_times[3]);
}

static void destroy_ui(void) {
	window_destroy(s_window);
	inverter_layer_destroy(s_inverterlayer_1);
	text_layer_destroy(s_station_name);
	inverter_layer_destroy(s_inverterlayer_2);
	inverter_layer_destroy(s_inverterlayer_3);
	inverter_layer_destroy(s_inverterlayer_4);
	inverter_layer_destroy(s_inverterlayer_5);
	bitmap_layer_destroy(s_train_directions[0]);
	bitmap_layer_destroy(s_train_directions[1]);
	bitmap_layer_destroy(s_train_directions[2]);
	bitmap_layer_destroy(s_train_directions[3]);
	text_layer_destroy(s_train_times[0]);
	text_layer_destroy(s_train_names[0]);
	text_layer_destroy(s_train_names[1]);
	text_layer_destroy(s_train_times[1]);
	text_layer_destroy(s_train_names[2]);
	text_layer_destroy(s_train_times[2]);
	text_layer_destroy(s_train_names[3]);
	text_layer_destroy(s_train_times[3]);
	gbitmap_destroy(s_res_r_arrow_up);
	gbitmap_destroy(s_res_r_arrow_down);
}
// END AUTO-GENERATED UI CODE

static void handle_window_unload(Window* window) {
	destroy_ui();
}

void show_train_list(void) {
	initialise_ui();
	window_set_window_handlers(s_window, (WindowHandlers) {
		.unload = handle_window_unload,
	});
	window_stack_push(s_window, true);
}

void hide_train_list(void) {
	window_stack_remove(s_window, true);
}

void update_station( const char* name )
{
	text_layer_set_text( s_station_name, name );
}

void update_train_destination( int index, const char* destination )
{
	if( index < MAX_DISPLAYED_TRAINS )
	{
		text_layer_set_text( s_train_names[index], destination );
	}
}
void update_train_due( int index, const char* due )
{
	if( index < MAX_DISPLAYED_TRAINS )
	{
		text_layer_set_text( s_train_times[index], due );
	}
}
void update_train_direction( int index, direction_t direction )
{
	if( index < MAX_DISPLAYED_TRAINS )
	{
		if( direction == NORTH )
		{
			bitmap_layer_set_bitmap( s_train_directions[index], s_res_r_arrow_up );
		}
		else if( direction == SOUTH )
		{
			bitmap_layer_set_bitmap( s_train_directions[index], s_res_r_arrow_down );
		}
		else if( direction == NONE )
		{
			bitmap_layer_set_bitmap( s_train_directions[index], NULL );
		}
	}
}
