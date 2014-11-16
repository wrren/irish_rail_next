void show_main_window(void);
void hide_main_window(void);

// Possible Train Directions
typedef enum
{
	NORTH,
	SOUTH
} Direction;

/**
 * Update the arrival time for the train running in the specified direction
 * @param direction  Train Direction
 * @param due        Due time, in minutes
 */
void update_due_time( Direction direction, unsigned int due );

/**
 * Update the destination for the train running in the specified direction
 * @param direction    Train Direction
 * @param destination  Destination Station
 */
void update_destination( Direction direction, const char* destination );