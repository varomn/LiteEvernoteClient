UPDATE 27/01/2016

-Soporte para cuentas Evernote en producción (https://evernote.com/). API key personal ha sido dada de alta.
Para ello, hay que cerrar sesión desde el menu y loguearse de nuevo con una cuenta de https://evernote.com/.
-Pequeñas mejoras y optimizaciones.

Comento como he desarrollado cada uno de los puntos pedidos:

1. Exista una pantalla inicial de login, donde el usuario pueda introducir sus credenciales para tener acceso a su cuenta Evernote. (https://evernote.com/)
Se ha probado con el servicio de evernote para testing (https://sandbox.evernote.com) y funciona perfectamente para una cuenta de este tipo. No se ha podido probar en una cuenta en producción (https://evernote.com/) porque no me han dado de alta aún el API key para producción.
Una vez que se haya proporcionado el API key definitivo, se ha de especificar el nuevo tipo de servicio (linea 40, fichero LoginActivity.java) cambiando de "SANDBOX" a "PRODUCTION" y la modificación de API key (linea 30-31, fichero LoginActivity.java)
Ha sido necesario cambiar el REQUEST_CODE_LOGIN a 66394 (linea 24, fichero LoginActivity.java) pues el que viene por defecto desde el API de Evernote (EvernoteSession.REQUEST_CODE_LOGIN) no funciona.

EN CASO DE QUE HAYA PROBLEMAS PARA AUTENTICAR CON LOS ACTUALES CONSUMER_KEY/CONSUMER_SECRET, SIEMPRE SE PODRÁN MODIFICAR DESDE EL CÓDIGO CON UNOS VÁLIDOS.

2. Una vez introducidos los credenciales, se mostrarán en pantalla todas las notas creadas por el usuario.
Implementado y funciona correctamente.
Por defecto, ordena la lista de notas por la fecha de actualización (la mas reciente, la primera). Se ha propuesto un máximo de Notas para la lista de 30 notas.
Por cada nota de la lista, no solo se muestra el título, sino también una preview del contenido de la misma.

3. Dicha pantalla tendrá un menú desplegable con dos opciones, una de ellas ordenará la lista por el título de la nota y la otra por fecha de creación o modificación.
Implementado y funciona correctamente.
Además se han añadido algunas funcionalidades más desde este menú como abrir la web de dedesarrolladores de evernote, cerrar sesión o finalizar la aplicación.

4. Al hacer tap sobre una nota, se accederá al contenido de la misma. (No es necesario que las notas sean editables).
Implementado y funciona correctamente.
Además, cuando se realiza tap sobre una nota, aparece una ventana emergente donde se da la posibilidad de editar la Nota o de visualizarla.

5. Existirá un botón para “añadir nota” que permitirá crear una nota (con título y cuerpo) y posteriormente guardarla.
Implementado y funciona correctamente.
El botón para acceder a esta funcionalidad se encuentra justo al lado del botón Menú.

6. Al crear una nota, se podrá elegir entre crearla mediante el teclado o bien escribir sobre la pantalla; donde un OCR convertirá la escritura en tipografía de computadora.
No se ha implementado por falta de tiempo.

No obstante y a grandes rasgos, se deberia dar la posibilidad al usuario a través de un view avanzado de escribir por pantalla texto (sin el teclado). El resultado se guardaría como una imagen y se adjuntaría como una "image Resource" a la nota creada. Posteriormente, la imagen se envía a servidores de procesamiento de imágenes en la nube que examinan la imagen para reconocer el texto escrito a mano y finalmente se insertan los datos resultantes en el cuerpo de la nota (en forma de "recoIndex" elements).

La nueva nota procesada contendrá, por tanto, uno o más elementos de tipo "recoIndex" ("items") al final de su contenido. Cada uno de estos elementos "items" describe el tamaño y la ubicación de una o más posibles ocurrencias de texto dentro de la imagen inicial adjuntada.
El elemento "item" contiene cuatro atributos:

x - La coordenada x de la esquina superior izquierda del elemento.
y - La coordenada y de la esquina superior izquierda del elemento.
w - El ancho del elemento.
h - La altura del elemento.
Estos cuatro valores crean un rectángulo que contiene el texto.

Para acceder a esta funcionalidad desde las APIs de android hay dos maneras: utilizando NoteStore.getNote o NoteStore.getResourceRecognition.
Un ejemplo con NoteStore.getResourceRecognition:
-------------------------------------------------------------------
client = EvernoteClient(token=dev_token, sandbox=False)
noteStore = client.get_note_store()

note_guid = '5056b825-e447-4eeb-bfda-cf7a86241f49'
note = noteStore.getNote(dev_token, note_guid, False, True, True, True)
for r in note.resources:
    recogData = noteStore.getResourceRecognition(r.guid)
    print recogData
-------------------------------------------------------------------

La nota con estos nuevos datos se reenviarían de nuevo al usuario en la siguiente sincronización del cliente con los servidores.