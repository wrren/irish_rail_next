#include "main_window.h"
#include <pebble.h>

// BEGIN AUTO-GENERATED UI CODE; DO NOT MODIFY
static Window *s_window;
static GFont s_res_gothic_24_bold;
static GBitmap *s_res_r_arrow_down;
static GFont s_res_gothic_14;
static GBitmap *s_res_r_arrow_up;
static TextLayer *s_south_name;
static BitmapLayer *s_arrow_down;
static TextLayer *s_south_due;
static InverterLayer *s_south_inverter;
static TextLayer *s_southbound;
static InverterLayer *s_north_inverter;
static TextLayer *s_northbound;
static BitmapLayer *s_arrow_up;
static TextLayer *s_north_name;
static TextLayer *s_north_due;

static char northDueBuffer[32];
static char southDueBuffer[32];

static void initialise_ui(void) 
{
	s_window = window_create();
	window_set_fullscreen(s_window, false);

	s_res_gothic_24_bold = fonts_get_system_font(FONT_KEY_GOTHIC_24_BOLD);
	s_res_r_arrow_down = gbitmap_create_with_resource(RESOURCE_ID_r_arrow_down);
	s_res_gothic_14 = fonts_get_system_font(FONT_KEY_GOTHIC_14);
	s_res_r_arrow_up = gbitmap_create_with_resource(RESOURCE_ID_r_arrow_up);
// s_south_name
	s_south_name = text_layer_create(GRect(8, 98, 106, 29));
	text_layer_set_text(s_south_name, "Graystones");
	text_layer_set_font(s_south_name, s_res_gothic_24_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_south_name);

// s_arrow_down
	s_arrow_down = bitmap_layer_create(GRect(111, 110, 18, 22));
	bitmap_layer_set_bitmap(s_arrow_down, s_res_r_arrow_down);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_arrow_down);

// s_south_due
	s_south_due = text_layer_create(GRect(8, 127, 100, 20));
	text_layer_set_text(s_south_due, "100 Minutes");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_south_due);

// s_south_inverter
	s_south_inverter = inverter_layer_create(GRect(0, 76, 144, 15));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_south_inverter);

// s_southbound
	s_southbound = text_layer_create(GRect(0, 76, 144, 20));
	text_layer_set_background_color(s_southbound, GColorClear);
	text_layer_set_text_color(s_southbound, GColorWhite);
	text_layer_set_text(s_southbound, "South Bound");
	text_layer_set_text_alignment(s_southbound, GTextAlignmentCenter);
	text_layer_set_font(s_southbound, s_res_gothic_14);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_southbound);

// s_north_inverter
	s_north_inverter = inverter_layer_create(GRect(0, 0, 144, 15));
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_north_inverter);

// s_northbound
	s_northbound = text_layer_create(GRect(0, 0, 144, 20));
	text_layer_set_background_color(s_northbound, GColorClear);
	text_layer_set_text_color(s_northbound, GColorWhite);
	text_layer_set_text(s_northbound, "North Bound");
	text_layer_set_text_alignment(s_northbound, GTextAlignmentCenter);
	text_layer_set_font(s_northbound, s_res_gothic_14);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_northbound);

// s_arrow_up
	s_arrow_up = bitmap_layer_create(GRect(113, 33, 18, 22));
	bitmap_layer_set_bitmap(s_arrow_up, s_res_r_arrow_up);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_arrow_up);

// s_north_name
	s_north_name = text_layer_create(GRect(8, 23, 106, 29));
	text_layer_set_text(s_north_name, "Malahide");
	text_layer_set_font(s_north_name, s_res_gothic_24_bold);
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_north_name);

// s_north_due
	s_north_due = text_layer_create(GRect(8, 52, 100, 20));
	text_layer_set_text(s_north_due, "100 Minutes");
	layer_add_child(window_get_root_layer(s_window), (Layer *)s_north_due);
}

static void destroy_ui( void ) 
{
	window_destroy(s_window);
	text_layer_destroy(s_south_name);
	bitmap_layer_destroy(s_arrow_down);
	text_layer_destroy(s_south_due);
	inverter_layer_destroy(s_south_inverter);
	text_layer_destroy(s_southbound);
	inverter_layer_destroy(s_north_inverter);
	text_layer_destroy(s_northbound);
	bitmap_layer_destroy(s_arrow_up);
	text_layer_destroy(s_north_name);
	text_layer_destroy(s_north_due);
	gbitmap_destroy(s_res_r_arrow_down);
	gbitmap_destroy(s_res_r_arrow_up);
}
// END AUTO-GENERATED UI CODE

static void handle_window_unload( Window* window )
{
	destroy_ui();
}

void show_main_window( void ) 
{
	initialise_ui();
	window_set_window_handlers(s_window, (WindowHandlers) {
		.unload = handle_window_unload,
	});
	window_stack_push(s_window, true);
}

void hide_main_window( void ) 
{
	window_stack_remove(s_window, true);
}

void update_due_time( Direction direction, unsigned int due )
{
	if( direction == NORTH )
	{
		snprintf( northDueBuffer, 32, "%u Minutes", due );
		text_layer_set_text( s_north_due, northDueBuffer );
	}
	else
	{
		snprintf( southDueBuffer, 32, "%u Minutes", due );
		text_layer_set_text( s_south_due, southDueBuffer );
	}
}


void update_destination( Direction direction, const char* destination )
{
	if( direction == NORTH )
	{
		text_layer_set_text( s_north_name, destination );
	}
	else
	{
		text_layer_set_text( s_south_name, destination );
	}
}
