# Notas Anónimas (Backend) :bust_in_silhouette::speech_balloon:
Servidor creado con Java 17 y Spring Boot 3.0.0 para proporcionar una API RESTful que funciona junto con el proyecto de front-end Notas Anónimas de React JS, utilizando una base de datos SQL con MySQL.

## Configuración
- Clonar este repositorio.
- Configurar los detalles de la base de datos en application.properties.
- Ejecutar el comando mvn spring-boot:run para iniciar el servidor.

## Autenticación JWT con Spring Security
Este servidor utiliza Spring Security para proporcionar autenticación JWT. Se debe enviar una solicitud de inicio de sesión con las credenciales correctas para obtener un token JWT que se utilizará para acceder a todas las rutas protegidas.

## Funciones disponibles
### Registro de usuario
El servidor permite el registro de un usuario. Se requiere un nombre de usuario y una contraseña para crear una cuenta.

### Modificación de credenciales
Un usuario puede cambiar sus credenciales (nombre de usuario y/o contraseña) después de iniciar sesión.

### Creación y eliminación de publicaciones
Un usuario autenticado puede crear y eliminar sus propias publicaciones. Cada publicación contiene un título y un cuerpo de texto.

### Consulta de publicaciones de otros usuarios
Un usuario puede consultar las publicaciones de otros usuarios por relevancia, es decir, se ordenarán por número de reacciones. También se puede consultar las publicaciones más antiguas o las más reaccionadas.

### Creación y eliminación de comentarios
Un usuario autenticado puede crear y eliminar sus propios comentarios en cualquier publicación.

### Reacciones a publicaciones y comentarios
Un usuario autenticado puede reaccionar a cualquier publicación o comentario con un "Me gusta". Cada usuario solo puede reaccionar una vez a cada publicación o comentario.

## Características
- Versión de Java: 17.
- Versión de Spring Boot: 3.0.0.
- Servidor XAMPP con MySQL: Ver 15.1 Distrib 10.4.24-MariaDB, for Win64 (AMD64)
