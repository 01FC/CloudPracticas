- La URL a donde se debe hacer el POST para evaluar.

https://08day88hn6.execute-api.us-west-2.amazonaws.com/Examen001Stage

- Descripción de las secciones que funcionan y las que no funcionan.

Al hacer pruebas con postman de POST a la url anterior, enviando de parametro un JSON con el formato especificado, regresa un JSON con 
fileName, Tags, Text que son resultado de objetos y texto dentro de la imagen.

- Descripción general de cómo trabaja el código entregado. 

Mediante un promesa se realiza la peticion a los metodos de rekognition.detectText y detectLabels enviando como parametros la imagen base 64 como Bytes.
Con la respuesta recibida se forma una variable de respuesta con los atributos fileName, Tags y Text
