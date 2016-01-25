Comento como he desarrollado cada uno de los puntos pedidos:

1. Exista una pantalla inicial de login, donde el usuario pueda introducir sus credenciales para tener acceso a su cuenta Evernote. (https://evernote.com/)
Se ha probado con el servicio de evernote para testing (https://sandbox.evernote.com) y funciona perfectamente para una cuenta de este tipo. No se ha podido probar en una cuenta en producci�n (https://evernote.com/) porque no me han dado de alta a�n el API key para producci�n.
Una vez que se haya proporcionado el API key definitivo, se ha de especificar el nuevo tipo de servicio (linea 40, fichero LoginActivity.java) cambiando de "SANDBOX" a "PRODUCTION" y la modificaci�n de API key (linea 30-31, fichero LoginActivity.java) 
Ha sido necesario cambiar el REQUEST_CODE_LOGIN a 66394 (linea 24, fichero LoginActivity.java) pues el que viene por defecto desde el API de Evernote (EvernoteSession.REQUEST_CODE_LOGIN) no funciona.

EN CASO DE QUE HAYA PROBLEMAS PARA AUTENTICAR CON LOS ACTUALES CONSUMER_KEY/CONSUMER_SECRET, SIEMPRE SE PODR�N MODIFICAR DESDE EL C�DIGO CON UNOS V�LIDOS.

2. Una vez introducidos los credenciales, se mostrar�n en pantalla todas las notas creadas por el usuario.
Implementado y funciona correctamente.
Por defecto, ordena la lista de notas por la fecha de actualizaci�n (la mas reciente, la primera). Se ha propuesto un m�ximo de Notas para la lista de 30 notas.
Por cada nota de la lista, no solo se muestra el t�tulo, sino tambi�n una preview del contenido de la misma.

3. Dicha pantalla tendr� un men� desplegable con dos opciones, una de ellas ordenar� la lista por el t�tulo de la nota y la otra por fecha de creaci�n o modificaci�n.
Implementado y funciona correctamente.
Adem�s se han a�adido algunas funcionalidades m�s desde este men� como abrir la web de dedesarrolladores de evernote, cerrar sesi�n o finalizar la aplicaci�n.

4. Al hacer tap sobre una nota, se acceder� al contenido de la misma. (No es necesario que las notas sean editables).
Implementado y funciona correctamente.
Adem�s, cuando se realiza tap sobre una nota, aparece una ventana emergente donde se da la posibilidad de editar la Nota o de visualizarla.

5. Existir� un bot�n para �a�adir nota� que permitir� crear una nota (con t�tulo y cuerpo) y posteriormente guardarla.
Implementado y funciona correctamente.
El bot�n para acceder a esta funcionalidad se encuentra justo al lado del bot�n Men�.

6. Al crear una nota, se podr� elegir entre crearla mediante el teclado o bien escribir sobre la pantalla; donde un OCR convertir� la escritura en tipograf�a de computadora.
No se ha implementado por falta de tiempo.

No obstante y a grandes rasgos, se deberia dar la posibilidad al usuario a trav�s de un view avanzado de escribir por pantalla texto (sin el teclado). El resultado se guardar�a como una imagen y se adjuntar�a como una "image Resource" a la nota creada. Posteriormente, la imagen se env�a a servidores de procesamiento de im�genes en la nube que examinan la imagen para reconocer el texto escrito a mano y finalmente se insertan los datos resultantes en el cuerpo de la nota (en forma de "recoIndex" elements).

La nueva nota procesada contendr�, por tanto, uno o m�s elementos de tipo "recoIndex" ("items") al final de su contenido. Cada uno de estos elementos "items" describe el tama�o y la ubicaci�n de una o m�s posibles ocurrencias de texto dentro de la imagen inicial adjuntada.
El elemento "item" contiene cuatro atributos:

x - La coordenada x de la esquina superior izquierda del elemento.
y - La coordenada y de la esquina superior izquierda del elemento.
w - El ancho del elemento.
h - La altura del elemento.
Estos cuatro valores crean un rect�ngulo que contiene el texto.

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

La nota con estos nuevos datos se reenviar�an de nuevo al usuario en la siguiente sincronizaci�n del cliente con los servidores.

