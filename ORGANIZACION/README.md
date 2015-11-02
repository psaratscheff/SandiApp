# Manejo de archivos ajenos a la aplicación


- Para obtener nombre de usuario: (static)
	String nombre = LoginActivity.userName;

- Para obtener id de usuario: (static)
	String id = LoginActivity.userID;

- Para poner un marcador en el mapa en la posición actual del usuario:
	MapsActivity.addPinToCurrentLoc(String titulo, String descripcion);

- Para obtener la ubicacion actual del usuario: (static)
	LatLng loc = MapsActivity.getCurrentLocation();


