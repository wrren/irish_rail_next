void show_train_list(void);
void hide_train_list(void);

#define MAX_DISPLAYED_TRAINS 4

typedef enum
{
	NORTH,
	SOUTH,
	NONE
} direction_t;

void update_station( 		const char* name );

void update_train_destination( 	int index, const char* destination );
void update_train_due( 		int index, const char* due );
void update_train_direction( 	int index, direction_t direction );