# Gestor-de-inventario-por-usuarios
Con ayuda de Spring se hace una pagina local de gestión de un inventario, que dependiendo del usuario se podrá modificar la base de datos. Usa MySQL.

Manual de usuarios:
El primer paso antes de empezar es descargar la versión a utilizar de java, en este caso, java 17(Amazon corretto), también, usaremos mysql 8.0.33 y como interfaz gráfica se usará heidiSQL.
Se recomienda usar el puerto 3308, y agregar un usuario con nombre: Luisa y contraseña: root, esto obviamente puede ser cambiado de forma sencilla, pero en el caso de querer simplemente instalar y no mover nada de codigo, se puede tomar esta opción (Más adelante se dirá dónde se configuró esto.)
Una vez instalado esto, es cuestión de abrir el codigo, dejar que se descarguen las librerías y en el navegador se entrará a la dirección http://localhost:8080/login
En este lugar se deberá ingresar un usuario y una contraseña.

Estas pueden ser añadidas en la base de datos Desarrollo, tabla users, nos vamos al apartado de Datos y agregamos el user id, en dado caso de tener un email, nombre de usuario, contraseña (Encriptada con BCrypt, en el programa hay un apartado dentro de Database, con una clase llamada psswGen, solo es cuestión de cambiar el String prueba y se imprimirá el String encriptado para hacer que el usuario esté funcionando, enabled se deja en 1, después, la id que se le añadió a usuario se agrega en users_roles con su respectivo role_id, siendo 1=usuario, 2=creator, 3=editor y 4=admin, en esta parte se pueden poner varios roles, solo es cuestión de agregar otra fila con el user id deseado)
Una vez agregada las credenciales, nos aparecerá la tabla de inventario, una bienvenida al nombre del usuario, y 4 opciones.

Borrar: Solo puede ser accedida por administradores, te pide el codigo de el producto y lo eliminará, no sin antes pedir una confirmación.

Crear: Solo puede ser accedida por creators, crea un nuevo producto, donde se le puede añadir codigo, producto, cantidad, descripción y precio.

Editar: Solo puede ser accedida por editors, admite cambiar la cantidad y el precio de los productos mostrados al dar click en editar.

Logout: Te saca de tu sesión y redirige a home.

Se debe tener en cuenta que si un usuario quiere entrar a una de las opciones con las cuales no cumple su permiso, le aparecerá una pantalla de error con la opción de volver a home.
Además, este error puede aparecer debido a un uso inapropiado de la tabla, es decir, agregar contenido que no soporta (Números con longitud mayor a 9).

Ojo, se debe tener en cuenta que el número máximo de caracteres soportado por números es de 8, mientras que de producto 15 y descripción 20, en el dado caso de que se sobrepase este número, la información obtenida será recortada.

Información técnica:

Tenemos una tabla de users con los datos:
User_id, tipo int, primary key y auto incremento (Para así no tener que preocuparnos de duplicados)
Username, tipo varchar
Password, tipo varchar
Role, tipo varchar
Enabled, tipo tinyint

Las librerías usadas están en el pom.xml
Algunas a destacar son:
Spring boot, la versión 2.7.9
(Con artefactos, como web, thymeleaf, security y jpa)
Conector de mysql para la versión 8.0.23
Plugin de Maven para springboot
Y se puede agregar lombok para un mejor uso en cuanto a getters, setters y constructores.

En resources, modificaremos la información de application.properties
spring.datasource.url=jdbc:mysql://localhost:3308
spring.datasource.username=Luisa
spring.datasource.password=root
Esto sirve para conectarse a la base de datos, siendo el conector jdbc, mysql la base, localhost:3308 el puerto, y Desarrollo la base de datos. Luisa es el nombre de usuario con acceso a admin, y root la contraseña.
Algunos datos para tener en cuenta a lo largo de este programa y entenderlo mejor, son sus anotaciones de tipo @, algunos ejemplos usados son los siguientes:
@Override , transcribe la configuración por default para así crear la propia.
@Param , usados con ayuda de @RequestParam para obtener información que se obtiene de una de las paginas del html.
@Controller es la anotación que se le da a la clase del homecontroller, este sirve para que el programa tenga entendido que dentro de la clase se hará uso de requests en Http, algunos metodos que se usaron fueron el @PostMapping para aquellos que se usaban forms y obtener su información, y @RequestMapping (Utilizado mas que nada gracias a una mayor versatilidad que @GetMapping) en donde se invoca cuando se crea una request del usuario para entrar a ese endpoint.
@Bean esta anotación es para producir un vean, un objeto el que puede ser inyectado a otros objetos,
@Autowired este se usa junto a vean, de esta forma se inyecta el vean a otro objeto, se hace uso de setters.
Se debe tener en cuenta que Bean se utiliza más en clases de configuración, mientras que wired en controllers, services o cualquier otro que necesite de una dependencia.

Ahora con esto en cuenta, se pasa a la parte de configuración.
Se crea una clase de User marcadas con @Entity y @Table para que sean reconocidos como una base de datos, donde se darán sus propiedades mas importantes, el id, username, password y si está enabled, se crean sus getters y setters, y además se agrega @Id para marcar como la primary key que habíamos puesto al inicio, @Column para buscar la columna de user_id, y el @GeneratedValue es para que vaya acorde al autoincremento.

También se utiliza @ManyToMany para que tenga la relación de user_id con los roles junto con lo que se busca adjuntar, es decir, users_roles, user_id y role_id.

También generamos una clase parecida a User, pero para los Roles.
Creamos nuestro UserRepository, donde se hará un Query para obtener los parámetros de username.
Esto se hará uso en UserDetailsService, donde se le inyecta el UserRepository y con el Override cambiamos la configuración para usar el userRepository.

También creamos una clase de MyUserDetails implementando UserDetails, aquí se configuran algunas pautas a seguir para su función, como ver que las credentials funcionen, el usuario esté habilitado, no se haya expirado la request de entrar, así como obtener el usuario, su contraseña y poder darle la autoridad de su rol.
Una vez configurado la información de los usuarios, se puede pasar al siguiente punto.

Se crea una clase @Configuration @EnableWebSecurity que extienda WebSecurityConfigurerAdapter, en esta clase basicamente se controlan los permisos para entradas y su autenticación.
Crearemos algunos beans, un UserDetailsService, un encoder de BCrypt y un DaoAuthentication Provider, donde los UserDetailsService y el PasswordEncoder sean llamados con los beans que creamos.
Ahora con estos beans, se pueden hacer unas configuraciones de la seguridad.
Se cambia el authenticationManager por el que creamos con daoAuthentication.

También, se configura la seguridad de Http, algunos puntos importantes son:
.formLogin.loginPage, donde se agrega que /login sea el punto de acceso inicial de la pagina, a la hora de un default succed, se dirige a /home, se agrega un permitAll que no se necesita autenticación para entrar, un proceso parecido se hace con /logout.
Para casos mas específicos como las urls para borrar, editar, cambiar y sus derivados, con antMatchers se busca la url en cuestión, y con el .hasAuthority le damos los permisos que deseamos, dejamos que se pueda pedir este request a cualquier usuario autenticado, en dado caso que un usuario no autorizado entra, se le redirigirá a la pagina de blank error que creamos.
Y por último pero no menos importante (De hecho, muy importante) deshabilitamos el csrf, ya que interfiere con nuestros metodos de post.

Ahora se hablará de MyService, que es la forma en que nos conectamos a la base de datos en los metodos, basicamente se obtiene de forma privada lo obtenido de application.properties, se hace un setter y se pone la información obtenida en un DriverManager.getConnection.

Los metodos son bastantes sencillos, la mayoría se conforman de statements que en la mayoría de casos, toma información del usuario para completar un statement y al final ejecutarlo.
El único caso que no es así, es el de showDatabase, que simplemente devuelve el string de la base de datos formada.
Por último(Sin contar el uso del html y css) en homeController tenemos una inyección del MyService, junto con los @RequestMapping y @PostMapping, donde unos piden parámetros, otros solo devuelven el html, y otros añaden atributos con el add.Attribute.
Todo esto se hila gracias a los metodos y los templates .html, donde se hacen uso de forms y botones para obtener y dar información.
Todo esto se le da un mejor acabado con la template de styles.css, donde se modifica la apariencia de algunas propiedades, como botones o letras, además de que hay información adicional dentro de <style>’s.
