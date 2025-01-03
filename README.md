Alumno: Angel Jesus Terol Martinez
DNI: 53978578Q

Práctica: SQLite

Activities:

- MainActivity: Actividad principal donde el usuario puede loguearse, crear y restaurar un backup, y entrar a la pantalla de mantenimiento de usuarios.
- UserDataActivity: Actividad donde se muestra el username y el nombre completo del usuario que se acaba de loguear.
- UserManagementActivity: Actividad desde la cual se puede acceder a las actividades de crear un nuevo usuario o ver la lista de todos los que ya hay. Se dispone de un
spinner que muestra el username de todos los usuarios que hay en la base de datos actualmente. Al seleccionar uno, podremos actualizarlo o eliminarlo.
- UpdateUserActivity: Actividad en la que podemos actualizar los datos de un usuario.
- UserListActivity: Actividad que nos muestra todos los usuarios actualmente registrados en base de datos. Hecho a partir de un RecyclerView.

RecyclerViews:

- UserAdapter: Recycler utilizado para la parte de SQLiteOpenHelper del ejercicio.
- UserAdapterRoom: Recycler utilizado para la parte de Room del ejercicio (activo).

Otros:

- SQLManager: Clase singletone principalmente utilizada para las implementaciones del CRUD con SQLiteOpenHelper. También se han implementado en ella
las dos clases de generación/restauración de backups (tanto para la parte de SQLiteOpenHelper como Room).
- User: Entidad utilizada para la parte de SQLiteOpenHelper.
- UserEntity: Entidad que representa la tabla de base de datos de Room.
- UserDAO: DAO donde se han definido los CRUDs a utilizar.
- AppDatabase: Singletone a partir del cual poder utilizar el DAO de Room.

Dificultades encontradas:

He tenido dificultades a la hora de generar y restaurar el backup para la parte de Room. Hasta que no me aseguré de cerrar la conexión
previo a copiar y pegar el archivo, los cambios no aparecían en la aplicación. Parece algo evidente pero que no encontré reflejado
en ningún sitio.
