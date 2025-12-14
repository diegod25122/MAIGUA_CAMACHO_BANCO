Descripción del Proyecto

Este proyecto consiste en el desarrollo de una aplicación de escritorio en Java, usando Swing, que simula el funcionamiento básico de un sistema bancario.
El sistema permite que un usuario inicie sesión y realice operaciones como depósitos, retiros y transferencias, manteniendo el saldo actualizado mediante una base de datos MySQL.
El objetivo principal es aplicar los conocimientos de interfaces gráficas, conexión a bases de datos y manejo de eventos en Java.

Tecnologías Utilizadas
Java
Java Swing
MySQL
JDBC (MySQL Connector/J)
IntelliJ IDEA

estructura del proyecto
<img width="620" height="559" alt="image" src="https://github.com/user-attachments/assets/ca234eeb-3a27-463c-8ebb-1b77fe5296f7" />
forms: contiene los formularios gráficos del sistema (loginform y bancoform)
db: contiene la clase que maneja la conexión a la base de datos.
Main: clase principal desde donde se ejecuta el programa.

Conexión a la Base de Datos

La conexión con MySQL se realiza mediante la clase Conexion, la cual se encarga de establecer la comunicación con la base de datos usando JDBC.
De esta manera, la conexión se maneja en un solo lugar y puede ser utilizada por los distintos formularios del sistema.

usamos este codigo para a conexion con la base de datos en MYSQL
<img width="1017" height="660" alt="image" src="https://github.com/user-attachments/assets/48747b7e-4608-431f-91c1-e1e2f0019225" />

Formulario LoginForm (Inicio de Sesión)
Función del formulario
El formulario LoginForm permite que el usuario ingrese su usuario y contraseña para acceder al sistema.
Datos que procesa
Nombre de usuario
Contraseña
Funcionamiento
Al presionar el botón Ingresar, el sistema consulta la base de datos para verificar si las credenciales ingresadas existen y si el usuario está habilitado.
Fragmento de código usado para la validación:
<img width="983" height="110" alt="image" src="https://github.com/user-attachments/assets/54a6e966-1d4d-4883-9ad5-08c62369a8e4" />
Si las credenciales son correctas:
Se muestra un mensaje de bienvenida.
Se abre la ventana principal del sistema bancario.
Se cierra el formulario de inicio de sesión.

Formulario BancoForm (Operaciones Bancarias)
Función del formulario
El formulario BancoForm es la ventana principal del sistema.
En esta pantalla se muestra el nombre del cliente y su saldo actual, y se pueden realizar las operaciones bancarias.
Datos que procesa
Usuario autenticado
Saldo de la cuenta
Montos ingresados por el usuario

Depósito

El depósito permite ingresar un monto que se suma al saldo actual del usuario.
Luego de la operación, el saldo se actualiza y se muestra en pantalla.

Ejemplo de la lógica usada:
saldo += monto;
actualizarSaldo();

Retiro

En el retiro, el sistema solicita un monto y verifica que el saldo sea suficiente.
Si el monto es válido, se descuenta del saldo; caso contrario, se muestra un mensaje de advertencia.

Transferencia

La transferencia permite enviar dinero a otro usuario del sistema.
El sistema solicita el destinatario y el monto, y descuenta el valor del saldo del usuario que realiza la operación.

Historial de Transacciones

Cada operación realizada se muestra en un área de texto donde se puede visualizar un pequeño historial de los movimientos realizados durante la sesión.

Base de Datos
<img width="688" height="821" alt="image" src="https://github.com/user-attachments/assets/e37d4363-ad41-43ed-806b-58b79840568c" />

La base de datos MySQL utilizada contiene las siguientes tablas:
usuarios: guarda los datos de acceso.
cuentas: almacena el saldo de cada usuario.
movimientos: registra las operaciones realizadas.
Gracias a esto, la información se mantiene incluso después de cerrar el programa.

Para la ejecucion del proyecto
Importar el proyecto en IntelliJ IDEA.
Configurar la base de datos MySQL.
Agregar el conector mysql-connector-j.jar al proyecto.
Ejecutar la clase Main.
